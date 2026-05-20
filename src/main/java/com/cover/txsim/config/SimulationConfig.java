package com.cover.txsim.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SimulationConfig {

    private final String rpcUrl;
    private final boolean autoStartAnvil;
    private final String anvilPath;
    private final String forkUrl;
    private final Integer port;
    private final Long forkBlockNumber;
    private final String defaultFrom;
    private final String defaultTo;

    private SimulationConfig(Properties properties) {
        this.autoStartAnvil = readBoolean(properties, "foundry.anvil.autoStart", false);
        this.anvilPath = read(properties, "foundry.anvil.path", "anvil");
        this.forkUrl = read(properties, "foundry.fork.url", "");
        this.port = readInt(properties, "foundry.anvil.port", 8545);
        this.forkBlockNumber = readLong(properties, "foundry.fork.blockNumber", null);
        this.rpcUrl = read(properties, "foundry.rpc.url", "http://127.0.0.1:" + port);
        this.defaultFrom = read(properties, "simulate.default.from", "0x0000000000000000000000000000000000000001");
        this.defaultTo = read(properties, "simulate.default.to", "0x0000000000000000000000000000000000000000");
    }

    public static SimulationConfig load(String resourceName) {
        Properties properties = new Properties();
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + resourceName, e);
        }
        overlayEnv(properties, "FOUNDRY_RPC_URL", "foundry.rpc.url");
        overlayEnv(properties, "FOUNDRY_FORK_URL", "foundry.fork.url");
        overlayEnv(properties, "ANVIL_AUTO_START", "foundry.anvil.autoStart");
        overlayEnv(properties, "ANVIL_PATH", "foundry.anvil.path");
        overlayEnv(properties, "ANVIL_PORT", "foundry.anvil.port");
        overlayEnv(properties, "FORK_BLOCK_NUMBER", "foundry.fork.blockNumber");
        overlayEnv(properties, "SIM_FROM", "simulate.default.from");
        overlayEnv(properties, "SIM_TO", "simulate.default.to");
        return new SimulationConfig(properties);
    }

    private static void overlayEnv(Properties properties, String envName, String propertyName) {
        String value = System.getenv(envName);
        if (value != null && value.trim().length() > 0) {
            properties.setProperty(propertyName, value.trim());
        }
    }

    private static String read(Properties properties, String key, String defaultValue) {
        String value = properties.getProperty(key);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    private static boolean readBoolean(Properties properties, String key, boolean defaultValue) {
        String value = read(properties, key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    private static Integer readInt(Properties properties, String key, Integer defaultValue) {
        String value = properties.getProperty(key);
        return value == null || value.trim().isEmpty() ? defaultValue : Integer.valueOf(value.trim());
    }

    private static Long readLong(Properties properties, String key, Long defaultValue) {
        String value = properties.getProperty(key);
        return value == null || value.trim().isEmpty() ? defaultValue : Long.valueOf(value.trim());
    }

    public String getRpcUrl() {
        return rpcUrl;
    }

    public boolean isAutoStartAnvil() {
        return autoStartAnvil;
    }

    public String getAnvilPath() {
        return anvilPath;
    }

    public String getForkUrl() {
        return forkUrl;
    }

    public Integer getPort() {
        return port;
    }

    public Long getForkBlockNumber() {
        return forkBlockNumber;
    }

    public String getDefaultFrom() {
        return defaultFrom;
    }

    public String getDefaultTo() {
        return defaultTo;
    }
}
