package com.commons.utils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 */
public interface JsonCallback<T> {

    T execute(JsonNode node);
}
