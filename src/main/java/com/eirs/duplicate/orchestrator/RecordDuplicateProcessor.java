package com.eirs.duplicate.orchestrator;

import com.eirs.duplicate.alerts.AlertConfig;
import com.eirs.duplicate.config.AppConfig;
import com.eirs.duplicate.constants.AuditQueriesConstant;
import com.eirs.duplicate.constants.DBType;
import com.eirs.duplicate.dto.FileDataDto;
import com.eirs.duplicate.repository.entity.ModuleAuditTrail;
import com.eirs.duplicate.repository.entity.SystemConfigKeys;
import com.eirs.duplicate.service.ModuleAlertService;
import com.eirs.duplicate.service.ModuleAuditTrailService;
import com.eirs.duplicate.service.QueryExecutorService;
import com.eirs.duplicate.service.SystemConfigurationService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    AppConfig appConfig;

    AtomicInteger counter = new AtomicInteger(0);

    String MODULE_NAME;

    final String DEPENDENT_MODULE_NAME = "P4_PROCESS";

    @Autowired
    ModuleAuditTrailService moduleAuditTrailService;

    @Autowired
    SystemConfigurationService systemConfigurationService;

    @Autowired
    private ModuleAlertService moduleAlertService;

    @Autowired
    private AlertConfig alertConfig;
    @PostConstruct
    public void init() {
        MODULE_NAME = alertConfig.getProcessId();
    }

    @Override
    public void process(LocalDate localDate) {
        if (!moduleAuditTrailService.previousDependentModuleExecuted(localDate, DEPENDENT_MODULE_NAME)) {
            log.info("Process:{} will not execute as already Dependent Module:{} Not Executed for the day {}", MODULE_NAME, DEPENDENT_MODULE_NAME, localDate);
            return;
        }
        if (!moduleAuditTrailService.runProcess(localDate, MODULE_NAME)) {
            log.info("Process:{} will not execute it may already Running or Completed for the day {}", MODULE_NAME, localDate);
            return;
        }
        moduleAuditTrailService.createAudit(ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(MODULE_NAME).build());
        Long start = System.currentTimeMillis();
        ModuleAuditTrail updateModuleAuditTrail = ModuleAuditTrail.builder().moduleName(MODULE_NAME).featureName(MODULE_NAME).build();
        String query = "SELECT id,edr_date_time,actual_imei,imsi,msisdn,operator_name,file_name,is_gsma_valid,is_custom_paid,tac,device_type from app.edr_" + localDate.format(dateTimeFormatter) + " where device_type in (" + getAllowedDeviceTypes() + ") and is_gsma_valid=1 order by edr_date_time";
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
            moduleAlertService.sendDatabaseAlert(e.getCause().getMessage(), MODULE_NAME);
            updateModuleAuditTrail.setStatusCode(500);
        } catch (Exception e) {
            moduleAlertService.sendModuleExecutionAlert(e.getMessage(), MODULE_NAME);
            log.error("Error while Processing Query:{} Error:{} ", query, e.getMessage(), e);
            updateModuleAuditTrail.setStatusCode(500);
        }
        updateModuleAuditTrail.setTimeTaken(System.currentTimeMillis() - start);
        updateModuleAuditTrail.setCount(counter.get());
        moduleAuditTrailService.updateAudit(updateModuleAuditTrail);
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