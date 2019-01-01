package com.order.order.util;

public interface TrxIdGenerator {
    long getCurrentTrxId(String busCode);
}
