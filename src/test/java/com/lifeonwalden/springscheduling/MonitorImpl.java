package com.lifeonwalden.springscheduling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeonwalden.springscheduling.monitor.Monitor;
import com.lifeonwalden.springscheduling.monitor.TaskEvent;

public class MonitorImpl implements Monitor {

  @Override
  public void notificate(TaskEvent event) {
    try {
      System.out.println(new ObjectMapper().writeValueAsString(event));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

}
