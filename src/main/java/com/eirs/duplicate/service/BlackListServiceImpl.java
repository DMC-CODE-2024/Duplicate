package com.eirs.duplicate.service;

import com.eirs.duplicate.constants.DeviceSyncOperation;
import com.eirs.duplicate.repository.BlacklistDeviceHisRepository;
import com.eirs.duplicate.repository.BlacklistDeviceRepository;
import com.eirs.duplicate.repository.entity.BlacklistDevice;
import com.eirs.duplicate.repository.entity.BlacklistDeviceHis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlackListServiceImpl implements BlackListService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BlacklistDeviceHisRepository blacklistHisRepository;

    @Autowired
    BlacklistDeviceRepository blacklistRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public BlacklistDevice save(BlacklistDevice blacklist) {
        blacklist = blacklistRepository.save(blacklist);
        log.info("Saved in to Blacklist:{}", blacklist);
        return blacklist;
    }

    @Override
    public List<BlacklistDevice> save(List<BlacklistDevice> blacklists) {
        blacklists = blacklistRepository.saveAll(blacklists);
        log.info("Saved in to Blacklist:{}", blacklists);
        return blacklists;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public BlacklistDeviceHis save(BlacklistDeviceHis blacklistHis) {
        blacklistHis = blacklistHisRepository.save(blacklistHis);
        log.info("Saved in to BlacklistHis:{}", blacklistHis);
        return blacklistHis;
    }

    @Override
    @Transactional
    public void delete(List<BlacklistDevice> blacklists) {
        blacklistRepository.deleteAll(blacklists);
        log.info("Deleted blacklists:{}", blacklists);
        for (BlacklistDevice bl : blacklists) {
            BlacklistDeviceHis blacklistHis = new BlacklistDeviceHis();
            blacklistHis.setOperation(DeviceSyncOperation.DELETE.ordinal());
            blacklistHis.setImei(bl.getImei());
            blacklistHis.setActualImei(bl.getActualImei());
//        blacklistHis.setImsi(fileDataDto.getImsi());
            blacklistHis.setCreatedOn(LocalDateTime.now());
            blacklistHis.setMsisdn(bl.getMsisdn());
            blacklistHis.setOperatorId(null);
            blacklistHis.setOperatorName(bl.getOperatorName());
            blacklistHis.setSource("DUPLICATE");
            blacklistHis.setTac(bl.getTac());
            blacklistHisRepository.save(blacklistHis);
            log.info("Added in History blacklistHis:{}", blacklistHis);
        }

    }

    @Override
    public List<BlacklistDevice> getByImei(String imei) {
        log.info("going to find BlackList imei:{}", imei);
        List<BlacklistDevice> blacklists = blacklistRepository.findByImei(imei);
        log.info("Found Blacklist for Imei:{} blacklists:{}", imei, blacklists);
        return blacklists;
    }
}
