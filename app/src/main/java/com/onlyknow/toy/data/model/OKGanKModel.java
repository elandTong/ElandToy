package com.onlyknow.toy.data.model;

import java.util.List;

public class OKGanKModel {
    private boolean error;

    private String msg;

    private List<OKGanKResultModel> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<OKGanKResultModel> getResults() {
        return results;
    }

    public void setResults(List<OKGanKResultModel> results) {
        this.results = results;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
