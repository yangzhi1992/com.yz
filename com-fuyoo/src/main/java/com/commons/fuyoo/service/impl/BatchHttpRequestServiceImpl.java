package com.commons.fuyoo.service.impl;

import com.commons.fuyoo.dto.HttpRequestResponse;
import com.commons.fuyoo.service.BatchHttpRequestService;
import com.commons.fuyoo.service.HttpInvoker;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BatchHttpRequestServiceImpl implements BatchHttpRequestService {

    @Override
    public List<?> invokeHttpRequest(List<HttpInvoker> invokers) throws
            InterruptedException, ExecutionException, TimeoutException {
        List<Object> resultList = new ArrayList<>();
        if (invokers == null) {
            return resultList;
        }

        List<CompletableFuture<HttpRequestResponse>> requestFutures = invokers.stream()
                                                                              .map(invoker -> CompletableFuture.supplyAsync(invoker::invoke))
                                                                              .collect(Collectors.toList());

        CompletableFuture<Void> allHealthCheckFutures = CompletableFuture.allOf(
                requestFutures.toArray(new CompletableFuture[0]));

        CompletableFuture<List<Object>> listCompletableFuture = allHealthCheckFutures.thenApply(
                v -> requestFutures.stream().map(requestFuture -> {
                    try {
                        return requestFuture.get(30, TimeUnit.SECONDS);
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        HttpRequestResponse exceptionResult =
                                HttpRequestResponse.builder().success(false).exception(e.getMessage()).build();
                        return CompletableFuture.completedFuture(exceptionResult);
                    }
                }).collect(Collectors.toList()));

        return listCompletableFuture.get(60, TimeUnit.SECONDS);
    }
}
