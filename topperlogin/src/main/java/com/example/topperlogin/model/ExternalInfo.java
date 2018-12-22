package com.example.topperlogin.model;

/**
 * Created by GIjia on 2018/12/21.
 */

public class ExternalInfo {
    private String client_id;
    private String client_secret;
    private String scope;


    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
