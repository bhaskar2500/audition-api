package com.audition.configuration;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

@Component
public class ResponseHeaderInjector implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        SpanContext currentSpan = Span.current().getSpanContext();
        if (currentSpan.isValid()) {
            HttpHeaders headers = request.getHeaders();
            headers.add("trace_id", currentSpan.getTraceId());
            headers.add("span_id", currentSpan.getSpanId());
        }
        return execution.execute(request, body);
    }

}