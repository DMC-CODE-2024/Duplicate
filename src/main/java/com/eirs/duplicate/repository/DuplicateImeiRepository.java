package com.eirs.duplicate.repository;

import com.eirs.duplicate.repository.entity.Duplicate;
import com.eirs.duplicate.repository.entity.DuplicateImei;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuplicateImeiRepository extends JpaRepository<DuplicateImei, Long> {

    DuplicateImei findByImei(String imei);
}
