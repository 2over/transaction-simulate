package com.cover.txsim;

import com.cover.txsim.config.SimulationConfig;
import com.cover.txsim.foundry.FoundryAnvil;
import com.cover.txsim.model.SimulationRequest;
import com.cover.txsim.model.SimulationResult;
import com.cover.txsim.rpc.AnvilRpcClient;
import com.cover.txsim.service.TransactionSimulator;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

public class App {

    public static void main(String[] args) throws Exception {
        SimulationConfig config = SimulationConfig.load("application.properties");

        try (FoundryAnvil anvil = FoundryAnvil.fromConfig(config)) {
            anvil.startIfNeeded();

            Web3jService web3jService = new HttpService(config.getRpcUrl());
            Web3j web3j = Web3j.build(web3jService);
            AnvilRpcClient rpcClient = new AnvilRpcClient(web3jService);
            TransactionSimulator simulator = new TransactionSimulator(web3j, rpcClient);

            SimulationRequest request = sampleRequest(config);
            SimulationResult result = simulator.simulate(request);

            System.out.println(result.toHumanString());
            web3j.shutdown();
        }
    }

    private static SimulationRequest sampleRequest(SimulationConfig config) {
        return SimulationRequest.builder()
                .from(config.getDefaultFrom())
                .to(config.getDefaultTo())
                .value(BigInteger.ZERO)
                .data("0x")
                .gas(BigInteger.valueOf(3_000_000L))
                .blockTag("latest")
                .trace(true)
                .build();
    }
}
