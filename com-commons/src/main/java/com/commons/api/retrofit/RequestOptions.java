package com.commons.api.retrofit;

import java.io.Serializable;

/**
 * @description:
 * @author: allan
 * @date: 18/5/15 下午8:04
 */
public class RequestOptions implements Serializable {

    private static final long serialVersionUID = 9219345626002935808L;

    private final int connectTimeoutMillis;

    private final int readTimeoutMillis;

    private final int writeTimeoutMillis;

    private final int retries;

    public RequestOptions(int connectTimeoutMillis, int readTimeoutMillis, int writeTimeoutMillis, int retries) {
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.writeTimeoutMillis = writeTimeoutMillis;
        this.retries = retries;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public int getWriteTimeoutMillis() {
        return writeTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public int getRetries() {
        return retries;
    }
}
