package com.eirs.duplicate.repository;

import com.eirs.duplicate.repository.entity.Duplicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuplicateRepository extends JpaRepository<Duplicate, Long> {

    public List<Duplicate> findByImei(String imei);

    public List<Duplicate> findByImeiAndImsie(String imei, String imsie);
}
