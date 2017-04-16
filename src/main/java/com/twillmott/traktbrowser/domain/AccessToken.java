package com.twillmott.traktbrowser.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * Entity relating to {@link com.uwetrottmann.trakt5.entities.AccessToken}.
 */
@Entity
public class AccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String refresh_token;
    private String scope;

    public AccessToken(){}

    public AccessToken(String access_token, String token_type, Integer expires_in, String refresh_token, String scope){
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.scope = scope;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }


    /**
     * Map from an {@link com.uwetrottmann.trakt5.entities.AccessToken} to this type of {@link AccessToken}.
     */
    public com.uwetrottmann.trakt5.entities.AccessToken mapToTraktToken() {
        return mapToTraktToken(this);
    }

    /**
     * Map from an {@link com.uwetrottmann.trakt5.entities.AccessToken} to this type of {@link AccessToken}.
     */
    public static com.uwetrottmann.trakt5.entities.AccessToken mapToTraktToken(AccessToken input) {
        com.uwetrottmann.trakt5.entities.AccessToken output = new com.uwetrottmann.trakt5.entities.AccessToken();
        output.access_token = input.getAccess_token();
        output.expires_in = input.getExpires_in();
        output.refresh_token = input.getRefresh_token();
        output.scope = input.getScope();
        output.token_type = input.getToken_type();
        return output;
    }


    /**
     * Map from an @{@link AccessToken} to a {@link com.uwetrottmann.trakt5.entities.AccessToken}.
     */
    public static AccessToken mapFromTraktToken(com.uwetrottmann.trakt5.entities.AccessToken input) {
        AccessToken output = new AccessToken();
        output.setAccess_token(input.access_token);
        output.setExpires_in(input.expires_in);
        output.setRefresh_token(input.refresh_token);
        output.setScope(input.scope);
        output.setToken_type(input.token_type);
        return output;
    }
}
