package com.mission.global.rsData;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RsData<T> {

    private final String resultCode;
    private final String msg;
    private final T data;

    // Data 안넘어가는 구조
    public RsData(String resultCode, String msg) {
        this(resultCode, msg, null);
    }
}