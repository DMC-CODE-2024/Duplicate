package com.eirs.duplicate.service;

import com.eirs.duplicate.repository.DuplicateRepository;
import com.eirs.duplicate.repository.entity.Duplicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DuplicateServiceImpl implements DuplicateService {
    @Autowired
    private DuplicateRepository duplicateRepository;

    @Override
    public Duplicate save(Duplicate duplicate) {
        if (isNotAvailable(duplicate.getImei())) {
            log.info("Saving Duplicate:{}", duplicate);
            duplicate = duplicateRepository.save(duplicate);
            log.info("Saved Duplicate:{}", duplicate);
        }
        return duplicate;
    }

    @Override
    public List<Duplicate> save(List<Duplicate> duplicates) {
        List<Duplicate> savedEntities = new ArrayList<>();
        log.info("Saving to Duplicate duplicates:{}", duplicates);
        duplicates.forEach(d -> savedEntities.add(save(d)));
//        return duplicateRepository.saveAll(duplicates);
        return savedEntities;
    }

    @Override
    public List<Duplicate> getByImei(String imei) {
        log.info("Finding in duplicate table using imei : {}", imei);
        List<Duplicate> duplicates = duplicateRepository.findByImei(imei);
        log.info("Get Duplicate for Imei:{} Duplicate:{}", imei, duplicates);
        return duplicates;
    }

    @Override
    public Boolean isAvailable(String imei) {
        log.info("Finding in duplicate table using imei : {}", imei);
        List<Duplicate> duplicates = duplicateRepository.findByImei(imei);
        log.info("Get Duplicate for Imei:{} Duplicate:{}", imei, duplicates);
        if (CollectionUtils.isEmpty(duplicates))
            return false;
        return true;
    }

    @Override
    public Boolean isNotAvailable(String imei) {
        return !isAvailable(imei);
    }
}
