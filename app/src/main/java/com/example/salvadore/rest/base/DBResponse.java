package com.example.salvadore.rest.base;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DBResponse<T> {

    private String code;
    private String msg;
    private String requestId;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setCode( String code ) {
        this.code = code;
    }

    public void setMsg( String msg ) {
        this.msg = msg;
    }

    public void setRequestId( String requestId ) {
        this.requestId = requestId;
    }
}
