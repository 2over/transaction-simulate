package com.cover.txsim.rpc;

import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnvilRpcClient {

    private final Web3jService web3jService;

    public AnvilRpcClient(Web3jService web3jService) {
        this.web3jService = web3jService;
    }

    public String snapshot() throws IOException {
        JsonRpcStringResponse response = request("evm_snapshot", Collections.emptyList(), JsonRpcStringResponse.class).send();
        failOnError(response);
        return response.getResult();
    }

    public void revert(String snapshotId) throws IOException {
        JsonRpcBooleanResponse response = request("evm_revert", Collections.singletonList(snapshotId), JsonRpcBooleanResponse.class).send();
        failOnError(response);
    }

    public void impersonateAccount(String address) throws IOException {
        if (address == null || address.trim().isEmpty()) {
            return;
        }
        JsonRpcBooleanResponse response = request("anvil_impersonateAccount", Collections.singletonList(address), JsonRpcBooleanResponse.class).send();
        failOnError(response);
    }

    public void stopImpersonatingAccount(String address) throws IOException {
        if (address == null || address.trim().isEmpty()) {
            return;
        }
        JsonRpcBooleanResponse response = request("anvil_stopImpersonatingAccount", Collections.singletonList(address), JsonRpcBooleanResponse.class).send();
        failOnError(response);
    }

    public void setBalance(String address, BigInteger balanceWei) throws IOException {
        if (address == null || balanceWei == null) {
            return;
        }
        JsonRpcObjectResponse response = request(
                "anvil_setBalance",
                Arrays.asList(address, "0x" + balanceWei.toString(16)),
                JsonRpcObjectResponse.class
        ).send();
        failOnError(response);
    }

    public Object debugTraceCall(Map<String, String> callObject, String blockTag) throws IOException {
        Map<String, Object> options = new LinkedHashMap<>();
        options.put("tracer", "callTracer");
        options.put("timeout", "20s");
        JsonRpcObjectResponse response = request(
                "debug_traceCall",
                Arrays.asList(callObject, blockTag == null ? "latest" : blockTag, options),
                JsonRpcObjectResponse.class
        ).send();
        failOnError(response);
        return response.getResult();
    }

    public JsonRpcStringResponse ethCall(Map<String, String> callObject, String blockTag) throws IOException {
        return request(
                "eth_call",
                Arrays.asList(callObject, blockTag == null ? "latest" : blockTag),
                JsonRpcStringResponse.class
        ).send();
    }

    private <T extends Response<?>> Request<Object, T> request(String method, java.util.List<?> params, Class<T> responseType) {
        return new Request<Object, T>(method, (java.util.List<Object>) params, web3jService, responseType);
    }

    private void failOnError(Response<?> response) {
        if (response.hasError()) {
            throw new IllegalStateException(response.getError().getMessage());
        }
    }

    public static class JsonRpcStringResponse extends Response<String> {
    }

    public static class JsonRpcBooleanResponse extends Response<Boolean> {
    }

    public static class JsonRpcObjectResponse extends Response<Object> {
    }
}
