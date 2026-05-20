package com.cover.txsim.model;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimulationRequest {

    private final String from;
    private final String to;
    private final BigInteger value;
    private final String data;
    private final BigInteger gas;
    private final BigInteger gasPrice;
    private final String blockTag;
    private final boolean trace;
    private final BigInteger fromBalance;

    private SimulationRequest(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.value = builder.value;
        this.data = builder.data;
        this.gas = builder.gas;
        this.gasPrice = builder.gasPrice;
        this.blockTag = builder.blockTag;
        this.trace = builder.trace;
        this.fromBalance = builder.fromBalance;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, String> toRpcCallObject() {
        Map<String, String> call = new LinkedHashMap<>();
        putIfPresent(call, "from", from);
        putIfPresent(call, "to", to);
        putIfPresent(call, "value", toHex(value));
        putIfPresent(call, "data", data);
        putIfPresent(call, "gas", toHex(gas));
        putIfPresent(call, "gasPrice", toHex(gasPrice));
        return call;
    }

    private static void putIfPresent(Map<String, String> map, String key, String value) {
        if (value != null && value.trim().length() > 0) {
            map.put(key, value);
        }
    }

    private static String toHex(BigInteger value) {
        return value == null ? null : "0x" + value.toString(16);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public BigInteger getValue() {
        return value;
    }

    public String getData() {
        return data;
    }

    public BigInteger getGas() {
        return gas;
    }

    public String getBlockTag() {
        return blockTag == null ? "latest" : blockTag;
    }

    public boolean isTrace() {
        return trace;
    }

    public BigInteger getFromBalance() {
        return fromBalance;
    }

    public static class Builder {
        private String from;
        private String to;
        private BigInteger value;
        private String data = "0x";
        private BigInteger gas;
        private BigInteger gasPrice;
        private String blockTag = "latest";
        private boolean trace = true;
        private BigInteger fromBalance;

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder value(BigInteger value) {
            this.value = value;
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        public Builder gas(BigInteger gas) {
            this.gas = gas;
            return this;
        }

        public Builder gasPrice(BigInteger gasPrice) {
            this.gasPrice = gasPrice;
            return this;
        }

        public Builder blockTag(String blockTag) {
            this.blockTag = blockTag;
            return this;
        }

        public Builder trace(boolean trace) {
            this.trace = trace;
            return this;
        }

        public Builder fromBalance(BigInteger fromBalance) {
            this.fromBalance = fromBalance;
            return this;
        }

        public SimulationRequest build() {
            return new SimulationRequest(this);
        }
    }
}
