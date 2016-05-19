package com.lifeonwalden.springscheduling.task;

import java.util.Map;

public interface Worker {
  public void doJob(Map<String, Object> context);
}
