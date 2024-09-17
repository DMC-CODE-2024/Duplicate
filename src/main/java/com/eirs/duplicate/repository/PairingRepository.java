package com.eirs.duplicate.repository;

import com.eirs.duplicate.constants.GSMAStatus;
import com.eirs.duplicate.repository.entity.Pairing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PairingRepository extends JpaRepository<Pairing, Long> {

    List<Pairing> findByImeiAndGsmaStatus(String imei, GSMAStatus gsmaStatus);

    List<Pairing> findByImei(String imei);

    List<Pairing> findByActualImei(String actualImei);

    List<Pairing> findByImsi(String imsi);

    Pairing findByImeiAndMsisdn(String imei, String msisdn);

    Pairing findByImeiAndImsi(String imei, String imsi);

    Pairing findByActualImeiAndImsi(String actualImei, String imsi);

    Pairing findByActualImeiAndMsisdn(String imei, String msisdn);

    List<Pairing> findByMsisdn(String msisdn);
}
