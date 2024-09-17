package com.eirs.duplicate.service;

import com.eirs.duplicate.repository.entity.BlacklistDevice;
import com.eirs.duplicate.repository.entity.BlacklistDeviceHis;

import java.util.List;

public interface BlackListService {

    BlacklistDevice save(BlacklistDevice blacklist);

    List<BlacklistDevice> save(List<BlacklistDevice> blacklists);


    BlacklistDeviceHis save(BlacklistDeviceHis blacklistHis);

    void delete(List<BlacklistDevice> blacklistHis);

    List<BlacklistDevice> getByImei(String imei);
}
