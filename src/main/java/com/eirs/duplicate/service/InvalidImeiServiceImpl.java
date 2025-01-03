package com.eirs.duplicate.service;

import com.eirs.duplicate.repository.InvalidImeiRepository;
import com.eirs.duplicate.repository.entity.InvalidImei;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvalidImeiServiceImpl implements InvalidImeiService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InvalidImeiRepository invalidImeiRepository;

    public Boolean isPresent(String imei) {
        long start = System.currentTimeMillis();
        log.info("Checking in InvalidImei for imei:{}", imei);
        Optional<InvalidImei> invalidImei = invalidImeiRepository.findByImei(imei);
        log.info("Found in InvalidImei for imei:{} found:{} TimeTaken:{}", imei, invalidImei.isPresent(), (System.currentTimeMillis() - start));
        return invalidImei.isPresent();
    }

    public InvalidImei save(InvalidImei invalidImei) {
        long start = System.currentTimeMillis();
        if (!isPresent(invalidImei.getImei())) {
            log.info("Going to save into invalidImei:{}", invalidImei);
            invalidImei = invalidImeiRepository.save(invalidImei);
            log.info("Saved into invalidImei:{} TimeTaken:{}", invalidImei, (System.currentTimeMillis() - start));
        }
        return invalidImei;
    }

    @Override
    public Boolean isPresent(String[] imeis) {
        if (imeis != null) {
            for (String imei : imeis) {
                if (imei != null) {
                    Optional<InvalidImei> optional = invalidImeiRepository.findByImei(imei);
                    if (optional.isPresent())
                        return true;
                }
            }
        }
        return false;
    }
}
