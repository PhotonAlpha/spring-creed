package com.ethan.cloud.filter;

import brave.Span;
import brave.Tracer;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
// @Order(TraceWebServletAutoConfiguration.TRACING_FILTER_ORDER + 1)
public class CustomTracingFilter extends GenericFilterBean {
  private final Tracer tracer;

  public CustomTracingFilter(Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
    Span currentSpan = this.tracer.currentSpan();
    // for readability we're returning trace id in a hex form
    String traceId = currentSpan.context().traceIdString();
    MDC.put("X-B3-TraceId", traceId);
    MDC.put("X-B3-SpanId", traceId);
    ((HttpServletResponse) servletResponse).addHeader("X-B3-TraceId", traceId);

    // we can also add some custom tags
    currentSpan.tag("gebng", "tag");

    chain.doFilter(servletRequest, servletResponse);
  }
}
