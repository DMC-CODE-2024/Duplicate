package com.eirs.duplicate.repository;

import com.eirs.duplicate.repository.entity.SysParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigRepository extends JpaRepository<SysParam, Long> {

    public List<SysParam> findByConfigKeyAndModule(String configKey, String module);

    public List<SysParam> findByConfigKey(String configKey);
}
