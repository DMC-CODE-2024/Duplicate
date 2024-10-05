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
        if (isNotAvailable(duplicate.getImei(), duplicate.getImsie())) {
            long start = System.currentTimeMillis();
            log.info("Saving Duplicate:{}", duplicate);
            duplicate = duplicateRepository.save(duplicate);
            log.info("Saved Duplicate:{} TimeTaken:{}", duplicate, (System.currentTimeMillis() - start));
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
    public List<Duplicate> saveAll(List<Duplicate> duplicates) {
        long start = System.currentTimeMillis();
        log.info("Saving to Duplicate Batch duplicates:{}", duplicates);
        duplicates = duplicateRepository.saveAll(duplicates);
        log.info("Saved to Duplicate Batch duplicates:{} TimeTaken:{}", duplicates, (System.currentTimeMillis() - start));
        return duplicates;
    }

    @Override
    public List<Duplicate> getByImei(String imei) {
        long start = System.currentTimeMillis();
        log.info("Finding in duplicate table using imei : {}", imei);
        List<Duplicate> duplicates = duplicateRepository.findByImei(imei);
        log.info("Get Duplicate for Imei:{} Duplicate:{} TimeTaken:{}", imei, duplicates, (System.currentTimeMillis() - start));
        return duplicates;
    }

    @Override
    public Boolean isAvailable(String imei, String imsie) {
        long start = System.currentTimeMillis();
        log.info("Finding in duplicate table using imei : {} imsie:{}", imei, imsie);
        List<Duplicate> duplicates = duplicateRepository.findByImeiAndImsie(imei, imsie);
        log.info("Get Duplicate for Imei:{} imsie:{} Duplicate:{} TimeTaken:{}", imei, imsie, duplicates, (System.currentTimeMillis() - start));
        if (CollectionUtils.isEmpty(duplicates))
            return false;
        return true;
    }

    @Override
    public Boolean isNotAvailable(String imei, String imsie) {
        return !isAvailable(imei, imsie);
    }
}
