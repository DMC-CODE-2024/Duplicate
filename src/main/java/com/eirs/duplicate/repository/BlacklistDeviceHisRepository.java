package com.eirs.duplicate.repository;

import com.eirs.duplicate.repository.entity.BlacklistDeviceHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BlacklistDeviceHisRepository extends JpaRepository<BlacklistDeviceHis, Long> {

}
