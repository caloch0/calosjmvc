package org.caloch.core;


public enum HttpMethod {

    GET, //
    POST, //
    PUT, //
    DELETE, //
    HEAD;

    public String getName() {
        return name();
    }

    @Override
    public String toString() {
        return name();
    }

}