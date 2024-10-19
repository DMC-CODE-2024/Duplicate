package com.eirs.duplicate.orchestrator;

import com.eirs.duplicate.config.AppConfig;
import com.eirs.duplicate.dto.FileDataDto;
import com.eirs.duplicate.repository.entity.ModuleAuditTrail;
import com.eirs.duplicate.service.ModuleAlertService;
import com.eirs.duplicate.service.ModuleAuditTrailService;
import com.eirs.duplicate.service.QueryExecutorService;
import com.eirs.duplicate.service.SystemConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RecordDuplicateProcessor implements DuplicateProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private CheckDuplicateOrch checkDuplicateOrch;

    @Autowired
    QueryExecutorService queryExecutorService;

    AtomicInteger counter = new AtomicInteger(0);


    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;

    @Autowired
    SystemConfigurationService systemConfigurationService;

    @Autowired
    private ModuleAlertService moduleAlertService;

    @Autowired
    private AppConfig appConfig;

    final String MODULE_NAME = "duplicate";

    @Override
    public void process(LocalDate localDate) {
        if (!moduleAuditTrailService.previousDependentModuleExecuted(localDate, appConfig.getDependentFeatureName())) {
            log.info("Process:{} will not execute as already Dependent Module:{} Not Executed for the day {}", appConfig.getFeatureName(), appConfig.getDependentFeatureName(), localDate);
            return;
        }
        if (!moduleAuditTrailService.runProcess(localDate, appConfig.getFeatureName())) {
            log.info("Process:{} will not execute it may already Running or Completed for the day {}", appConfig.getFeatureName(), localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().createdOn(LocalDateTime.of(localDate, LocalTime.now())).moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build());
        Long start = System.currentTimeMillis();
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(appConfig.getFeatureName()).build();
        int saved = insertIntoDuplicateDeviceFromEdr(localDate);
        counter.set(saved);
        String query = "SELECT id,edr_date_time,actual_imei,imsi,msisdn,operator_name,file_name,is_gsma_valid,is_custom_paid,tac,device_type from app.edr_" + localDate.format(dateTimeFormatter) + " where device_type in (" + getAllowedDeviceTypes() + ") and is_gsma_valid=1 and is_duplicate=0 order by edr_date_time";
        log.info("Selecting Records with Query:[{}]", query);
        try {
            jdbcTemplate.setFetchSize(Integer.MIN_VALUE);
            jdbcTemplate.query(query, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    FileDataDto fileData = new FileDataDto();
                    fileData.setDate(rs.getTimestamp("edr_date_time").toLocalDateTime());
                    fileData.setFilename(rs.getString("file_name"));
                    fileData.setActualImei(rs.getString("actual_imei"));
                    fileData.setImsie(rs.getString("imsi"));
                    fileData.setMsisdn(rs.getString("msisdn"));
                    fileData.setOperator(rs.getString("operator_name"));
                    log.info("Picked Record: {}", fileData);
                    if (!StringUtils.isNumeric(fileData.getActualImei())) {
                        log.info("Not Processing this due to Alpha Numeric of actual_imei recordDataDto:{}", fileData);
                    }
                    if (StringUtils.isBlank(fileData.getImsie())) {
                        log.info("Not Processing record as Imsi is null or Blank recordDataDto:{}", fileData);
                    } else if (fileData.getImsie().startsWith("456")) {
                        if (fileData.getActualImei().length() >= 14)
                            fileData.setImei(fileData.getActualImei().substring(0, 14));
                        else
                            fileData.setImei(fileData.getActualImei());

                        checkDuplicateOrch.process(fileData);
                    } else {
                        log.info("Not Processing record as Imsi is not starting with 456 recordDataDto:{}", fileData);
                    }
                    counter.getAndIncrement();
                }
            });
            updateModuleAuditTrail.setStatusCode(200);
        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            log.error("Error {}", e.getCause().getMessage(), e);
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), appConfig.getFeatureName());
            updateModuleAuditTrail.setStatusCode(500);
        } catch (Exception e) {
            moduleAlertService.sendModuleExecutionAlert(e.getMessage(), appConfig.getFeatureName());
            log.error("Error while Processing Query:{} Error:{} ", query, e.getMessage(), e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(counter.get());
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
    }

    private Integer insertIntoDuplicateDeviceFromEdr(LocalDate localDate) {
        String query = "SELECT id,edr_date_time,actual_imei,imsi,msisdn,operator_name,file_name,is_gsma_valid,is_custom_paid,tac,device_type from app.edr_" + localDate.format(dateTimeFormatter) + " where is_duplicate=1";
        log.info("Selecting Records with Query:[{}]", query);

        Set<FileDataDto> set = new HashSet<>();
        jdbcTemplate.query(query, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                FileDataDto fileData = new FileDataDto();
                fileData.setDate(rs.getTimestamp("edr_date_time").toLocalDateTime());
                fileData.setFilename(rs.getString("file_name"));
                fileData.setActualImei(rs.getString("actual_imei"));
                fileData.setImsie(rs.getString("imsi"));
                fileData.setMsisdn(rs.getString("msisdn"));
                fileData.setOperator(rs.getString("operator_name"));
                if (fileData.getActualImei().length() >= 14)
                    fileData.setImei(fileData.getActualImei().substring(0, 14));
                else
                    fileData.setImei(fileData.getActualImei());
                set.add(fileData);
            }
        });

        return checkDuplicateOrch.batchInsertDuplicate(new ArrayList<>(set));
    }

    private String getAllowedDeviceTypes() {
        StringBuilder nameBuilder = new StringBuilder();
        for (String n : systemConfigurationService.getAllowedDeviceTypes()) {
            nameBuilder.append("'").append(n.replace("'", "\\'")).append("',");
        }
        nameBuilder.deleteCharAt(nameBuilder.length() - 1);
        return nameBuilder.toString();

    }
}
