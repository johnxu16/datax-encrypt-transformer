package com.alibaba.datax.transformer.support;


public enum AlgorithmType {
    SM4_ECB("SM4", "SM4");

    private final String code;
    private final String description;

    private AlgorithmType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "AlgorithmType{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
