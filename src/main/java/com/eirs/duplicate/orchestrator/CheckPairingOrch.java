package com.eirs.duplicate.orchestrator;

import com.eirs.duplicate.constants.ModuleNames;
import com.eirs.duplicate.constants.SmsPlaceHolders;
import com.eirs.duplicate.constants.SmsTag;
import com.eirs.duplicate.dto.DuplicateDataDto;
import com.eirs.duplicate.dto.FileDataDto;
import com.eirs.duplicate.dto.NotificationDetailsDto;
import com.eirs.duplicate.mapper.DuplicateMapper;
import com.eirs.duplicate.repository.entity.Duplicate;
import com.eirs.duplicate.repository.entity.Pairing;
import com.eirs.duplicate.service.*;
import com.eirs.duplicate.utils.DateFormatterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckPairingOrch {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CheckDuplicateService checkDuplicateService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DuplicateService duplicateService;
    @Autowired
    DuplicateMapper duplicateMapper;
    @Autowired
    InvalidImeiService invalidImeiService;

    @Autowired
    PairingService pairingService;

    public List<Duplicate> checkPairs(FileDataDto fileData) {
        List<Pairing> pairings = pairingService.getPairsByImei(fileData.getImei());
        if (CollectionUtils.isEmpty(pairings)) {
            log.info("No Pairs found for {}", fileData);
            return null;
        }
        return duplicateService.save(duplicateMapper.toDuplicate(pairings));
    }

}
