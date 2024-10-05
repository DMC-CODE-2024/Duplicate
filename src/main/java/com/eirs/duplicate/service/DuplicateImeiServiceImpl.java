package com.eirs.duplicate.service;

import com.eirs.duplicate.repository.DuplicateImeiRepository;
import com.eirs.duplicate.repository.entity.DuplicateImei;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DuplicateImeiServiceImpl implements DuplicateImeiService {
    @Autowired
    private DuplicateImeiRepository duplicateImeiRepository;

    private Map<String, Boolean> cache = new HashMap<>();

    @Override
    public DuplicateImei save(DuplicateImei duplicateImei) {
        if (!isPresentFromCache(duplicateImei.getImei())) {
            duplicateImei.setStatus("DUPLICATE");
            duplicateImei = duplicateImeiRepository.save(duplicateImei);
            cache.put(duplicateImei.getImei(), Boolean.TRUE);
            return duplicateImei;
        } else {
            log.info("DuplicateImei Already exist {}", duplicateImei);
        }
        return duplicateImei;
    }

    @Override
    public Boolean isPresent(String imei) {
        long start = System.currentTimeMillis();
        log.info("Checking in DuplicateImei  exist imei:{}", imei);
        DuplicateImei existing = duplicateImeiRepository.findByImei(imei);
        log.info("Result in DuplicateImei  exist imei:{} existing:{} TimeTaken:{}", imei, existing, (System.currentTimeMillis() - start));
        if (existing == null) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean isPresentFromCache(String imei) {
        return BooleanUtils.isTrue(cache.get(imei));
    }
}
