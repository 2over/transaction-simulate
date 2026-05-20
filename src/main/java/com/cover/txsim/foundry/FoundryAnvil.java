package com.cover.txsim.foundry;

import com.cover.txsim.config.SimulationConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoundryAnvil implements AutoCloseable {

    private final SimulationConfig config;
    private Process process;

    private FoundryAnvil(SimulationConfig config) {
        this.config = config;
    }

    public static FoundryAnvil fromConfig(SimulationConfig config) {
        return new FoundryAnvil(config);
    }

    public void startIfNeeded() {
        if (!config.isAutoStartAnvil()) {
            return;
        }
        if (config.getForkUrl() == null || config.getForkUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("foundry.fork.url is required when foundry.anvil.autoStart=true");
        }

        List<String> command = new ArrayList<String>();
        command.add(config.getAnvilPath());
        command.add("--fork-url");
        command.add(config.getForkUrl());
        command.add("--port");
        command.add(String.valueOf(config.getPort()));
        if (config.getForkBlockNumber() != null) {
            command.add("--fork-block-number");
            command.add(String.valueOf(config.getForkBlockNumber()));
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        processBuilder.inheritIO();

        try {
            process = processBuilder.start();
            Thread.sleep(2_000L);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start Foundry Anvil. Is Foundry installed and on PATH?", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while starting Anvil", e);
        }
    }

    @Override
    public void close() {
        if (process != null && process.isAlive()) {
            process.destroy();
        }
    }
}
