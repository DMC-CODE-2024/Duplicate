package com.eirs.duplicate.service;

import com.eirs.duplicate.repository.DuplicateImeiRepository;
import com.eirs.duplicate.repository.entity.DuplicateImei;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DuplicateImeiServiceImpl implements DuplicateImeiService {
    @Autowired
    private DuplicateImeiRepository duplicateImeiRepository;

    @Override
    public DuplicateImei save(DuplicateImei duplicate) {
        DuplicateImei existing = duplicateImeiRepository.findByImei(duplicate.getImei());
        if (existing == null) {
            duplicate.setStatus("DUPLICATE");
            existing = duplicateImeiRepository.save(duplicate);
        } else {
            log.info("DuplicateImei Already exist {}", existing);
        }
        return existing;
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
}
