package com.eirs.duplicate.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
public class EdrCurrentRecordTime {

    private LocalDateTime time;
}
