package com.eirs.duplicate.repository;

import com.eirs.duplicate.constants.NotificationLanguage;
import com.eirs.duplicate.constants.SmsTag;
import com.eirs.duplicate.repository.entity.SmsConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsConfigurationEntityRepository extends JpaRepository<SmsConfigurationEntity, Long> {

    SmsConfigurationEntity findByTagAndLanguage(SmsTag tag, NotificationLanguage language);

}
