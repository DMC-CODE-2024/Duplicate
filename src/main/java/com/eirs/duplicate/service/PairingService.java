package com.eirs.duplicate.service;

import com.eirs.duplicate.constants.GSMAStatus;
import com.eirs.duplicate.repository.entity.Pairing;

import java.util.List;

public interface PairingService {

    Pairing save(Pairing pairing);

    Pairing delete(Pairing pairing);

    List<Pairing> saveAll(List<Pairing> pairings);

    List<Pairing> getPairsByImeiAndGsmaStatus(String imei, GSMAStatus gsmaStatus);

    List<Pairing> getPairsByImei(String imei);

    List<Pairing> getPairsByActualImei(String actualImei);

    Pairing getPairsByMsisdn(String imei, String msisdn);

    List<Pairing> getPairsByImsie(String imsie);

    Pairing getByImeiAndImsie(String imei, String imsie);

    Pairing getByActualImeiAndImsie(String actualImei, String imsie);

    Pairing getPairsActualImeiByMsisdn(String actualImei, String msisdn);

    List<Pairing> getPairsByMsisdn(String msisdn);
}
