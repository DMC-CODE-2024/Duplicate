package com.eirs.duplicate.service;

import com.eirs.duplicate.repository.entity.Duplicate;

import java.util.List;

public interface DuplicateService {

    Duplicate save(Duplicate duplicate);

    List<Duplicate> save(List<Duplicate> duplicates);

    List<Duplicate> getByImei(String imei);
    Boolean isAvailable(String imei);

    Boolean isNotAvailable(String imei);
}
