package com.cover.txsim.service;

import com.cover.txsim.model.SimulationRequest;
import com.cover.txsim.model.SimulationResult;
import com.cover.txsim.rpc.AnvilRpcClient;
import java.io.IOException;
import java.util.Map;

public class TransactionSimulator {

    private final AnvilRpcClient rpcClient;

    public TransactionSimulator(org.web3j.protocol.Web3j web3j, AnvilRpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public SimulationResult simulate(SimulationRequest request) throws IOException {
        String snapshotId = rpcClient.snapshot();
        Object trace = null;

        try {
            rpcClient.impersonateAccount(request.getFrom());
            rpcClient.setBalance(request.getFrom(), request.getFromBalance());

            Map<String, String> callObject = request.toRpcCallObject();
            AnvilRpcClient.JsonRpcStringResponse ethCall = rpcClient.ethCall(callObject, request.getBlockTag());

            if (request.isTrace()) {
                trace = rpcClient.debugTraceCall(callObject, request.getBlockTag());
            }

            if (ethCall.hasError()) {
                return SimulationResult.failed(
                        ethCall.getResult(),
                        String.valueOf(ethCall.getError().getCode()),
                        ethCall.getError().getMessage(),
                        trace
                );
            }

            return SimulationResult.ok(ethCall.getResult(), trace);
        } finally {
            try {
                rpcClient.stopImpersonatingAccount(request.getFrom());
            } finally {
                rpcClient.revert(snapshotId);
            }
        }
    }
}
