package com.bclould.tea.model;

/**
 * Created by GIjia on 2018/12/21.
 */

public class ExternalTokenInfo {
    /**
     * access_token : 17a4fe0750ee33c5c933c2690a98ab81ddbd7db4
     * expires_in : 3600
     * token_type : Bearer
     * scope : cyc
     * authorized : true
     */

    private String access_token;
    private int expires_in;
    private String token_type;
    private String scope;
    private boolean authorized;
    private String error;
    private String error_description;

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
