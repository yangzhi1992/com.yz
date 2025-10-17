package com.commons.monitor.service;

import com.commons.monitor.dto.HttpRequestResponse;

public interface Invoker {

    HttpRequestResponse invoke();
}
