package com.cover.txsim.model;

public class SimulationResult {

    private final boolean success;
    private final String returnData;
    private final String errorCode;
    private final String errorMessage;
    private final Object trace;

    private SimulationResult(boolean success, String returnData, String errorCode, String errorMessage, Object trace) {
        this.success = success;
        this.returnData = returnData;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.trace = trace;
    }

    public static SimulationResult ok(String returnData, Object trace) {
        return new SimulationResult(true, returnData, null, null, trace);
    }

    public static SimulationResult failed(String returnData, String errorCode, String errorMessage, Object trace) {
        return new SimulationResult(false, returnData, errorCode, errorMessage, trace);
    }

    public String toHumanString() {
        StringBuilder builder = new StringBuilder();
        builder.append("success=").append(success).append('\n');
        if (returnData != null) {
            builder.append("returnData=").append(returnData).append('\n');
        }
        if (errorMessage != null) {
            builder.append("errorCode=").append(errorCode).append('\n');
            builder.append("errorMessage=").append(errorMessage).append('\n');
        }
        if (trace != null) {
            builder.append("trace=").append(trace).append('\n');
        }
        return builder.toString();
    }
}
