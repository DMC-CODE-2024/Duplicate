package com.eirs.duplicate.service;

import com.eirs.duplicate.constants.GSMAStatus;
import com.eirs.duplicate.repository.PairingRepository;
import com.eirs.duplicate.repository.entity.Pairing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PairingServiceImpl implements PairingService {
    @Autowired
    private PairingRepository pairingRepository;

    @Override
    public Pairing save(Pairing pairing) {
        log.info("Going to Add for Pair : {} is saved ", pairing);
        Pairing pair = pairingRepository.save(pairing);
        log.info("Pair : {} is saved ", pair);
        return pair;
    }

    @Override
    public Pairing delete(Pairing pairing) {
        log.info("Going to Delete Pair : {} ", pairing);
        Pairing pair = pairingRepository.save(pairing);
        log.info("Pair : {} is Deleted ", pair);
        return pair;
    }

    @Override
    @Transactional
    public List<Pairing> saveAll(List<Pairing> pairings) {
        log.info("Going to Add for pairings : {} is saved ", pairings);
        pairings = pairingRepository.saveAll(pairings);
        log.info("Pair : {} is saved ", pairings);
        return pairings;
    }

    @Override
    public List<Pairing> getPairsByImeiAndGsmaStatus(String imei, GSMAStatus gsmaStatus) {
        log.info("Find in Pairing table imei : {}, gsma status : {}", imei, gsmaStatus);
        List<Pairing> pairs = pairingRepository.findByImeiAndGsmaStatus(imei, gsmaStatus);
        log.info("pairs found by IMEI : {} gsmaStatus:{} count : {}", imei, gsmaStatus, pairs.size());
        return pairs;
    }

    @Override
    public List<Pairing> getPairsByImei(String imei) {
        log.info("Find in Pairing table imei : {}", imei);
        List<Pairing> pairs = pairingRepository.findByImei(imei);
        log.info("pairs found by IMEI : {} count : {}", imei, pairs.size());
        return pairs;
    }

    @Override
    public List<Pairing> getPairsByActualImei(String actualImei) {
        log.info("Find in pairing table  actual imei : {}", actualImei);
        List<Pairing> pairs = pairingRepository.findByActualImei(actualImei);
        log.info("pairs found by actualImei : {} count : {}", actualImei, pairs.size());
        return pairs;
    }

    @Override
    public Pairing getPairsByMsisdn(String imei, String msisdn) {
        log.info("Find in Pairing table imei : {}, msisdn : {}", imei, msisdn);
        Pairing pairing = pairingRepository.findByImeiAndMsisdn(imei, msisdn);
        if (pairing == null) {
            log.info("Pair not found by IMEI:{} msisdn:{}", imei, msisdn);
            return null;
        } else {
            log.info("Pair found by IMEI:{} msisdn:{}", imei, msisdn);
            return pairing;
        }
    }

    @Override
    public List<Pairing> getPairsByImsie(String imsie) {
        log.info("Find in PPairing table imsie : {}", imsie);
        List<Pairing> pairs = pairingRepository.findByImsi(imsie);
        log.info("pairs found by imsi : {} count : {}", imsie, pairs.size());
        return pairs;
    }

    @Override
    public Pairing getByImeiAndImsie(String imei, String imsie) {
        log.info("Find in pairing table imei : {} , imsi : {}", imei, imsie);
        Pairing pairing = pairingRepository.findByImeiAndImsi(imei, imsie);
        if (pairing == null) {
            log.info("Pair not found by IMEI:{} IMSI:{}", imei, imsie);
            return null;
        } else {
            log.info("Pair found by IMEI:{} IMSI:{}", imei, imsie);
            return pairing;
        }
    }

    @Override
    public Pairing getByActualImeiAndImsie(String actualImei, String imsie) {
        log.info("Find in Pairing table actual imei: {}, imsie : {}", actualImei, imsie);
        Pairing pairing = pairingRepository.findByActualImeiAndImsi(actualImei, imsie);
        if (pairing == null) {
            log.info("Pair not found by actualImei:{} IMSI:{}", actualImei, imsie);
            return null;
        } else {
            log.info("Pair found by actualImei:{} IMSI:{}", actualImei, imsie);
            return pairing;
        }
    }

    @Override
    public Pairing getPairsActualImeiByMsisdn(String actualImei, String msisdn) {
        log.info("Find in Pairing table actual imei : {}, msisdn : {}", actualImei, msisdn);
        Pairing pairing = pairingRepository.findByActualImeiAndMsisdn(actualImei, msisdn);
        if (pairing == null) {
            log.info("Pair not found by actualImei:{} msisdn:{}", actualImei, msisdn);
            return null;
        } else {
            log.info("Pair found by actualImei:{} msisdn:{}", actualImei, msisdn);
            return pairing;
        }
    }

    @Override
    public List<Pairing> getPairsByMsisdn(String msisdn) {
        log.info("Find in Pairing table msisdn : {}", msisdn);
        List<Pairing> pairs = pairingRepository.findByMsisdn(msisdn);
        log.info("pairs found by msisdn : {} count : {}", msisdn, pairs.size());
        return pairs;
    }

}
