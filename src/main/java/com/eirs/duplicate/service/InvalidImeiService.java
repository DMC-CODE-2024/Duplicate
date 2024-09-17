package com.eirs.duplicate.service;

import com.eirs.duplicate.repository.entity.InvalidImei;

public interface InvalidImeiService {

    Boolean isPresent(String imei);

    InvalidImei save(InvalidImei imei);

    Boolean isPresent(String[] imeis);
}
