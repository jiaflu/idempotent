package com.ljf.idempotentsdk.util;

public interface TrxIdGenerator {
    long getCurrentTrxId(String busCode);
}
