package com.eirs.duplicate.constants;

public enum SmsPlaceHolders {
    IMEI("<IMEI>"), ACTUAL_IMEI("<ACTUAL_IMEI>"), REQUEST_ID("<REQUEST_ID>"), MSISDN("<MSISDN>"), IMSI("<IMSI>"), OPERATOR("<OPERATOR>"), OTP("<OTP>"), DATE_DD_MMM_YYYY("<DATE_DD_MMM_YYYY>"),
    ;
    private String placeHolder;

    SmsPlaceHolders(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public String getPlaceHolder() {
        return this.placeHolder;
    }
}
