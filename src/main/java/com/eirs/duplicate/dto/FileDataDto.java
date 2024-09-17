package com.eirs.duplicate.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Data
public class FileDataDto {

    private LocalDateTime date;
    private String actualImei;
    private String imei;
    private String imsie;
    private String filename;
    private String msisdn;
    private String operator;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FileDataDto that = (FileDataDto) object;
        return Objects.equals(imei, that.imei) && Objects.equals(imsie, that.imsie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imei, imsie);
    }
}
