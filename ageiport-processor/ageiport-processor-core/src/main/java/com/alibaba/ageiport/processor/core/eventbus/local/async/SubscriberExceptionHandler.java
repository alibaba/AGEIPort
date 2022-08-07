package com.alibaba.ageiport.processor.core.eventbus.local.async;

public interface SubscriberExceptionHandler {
  void handleException(Throwable exception, SubscriberExceptionContext context);
}