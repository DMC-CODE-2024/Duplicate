
package com.eirs.duplicate.service;

import com.eirs.duplicate.repository.entity.Duplicate;
import com.eirs.duplicate.repository.entity.DuplicateImei;

import java.util.List;

public interface DuplicateImeiService {

    DuplicateImei save(DuplicateImei duplicate);

    Boolean isPresent(String imei);
}