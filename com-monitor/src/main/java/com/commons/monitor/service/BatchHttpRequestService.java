package com.commons.monitor.service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface BatchHttpRequestService {

    List<?> invokeHttpRequest(List<HttpInvoker> invokers)
            throws InterruptedException, ExecutionException, TimeoutException;
}
