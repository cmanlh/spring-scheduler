package com.lifeonwalden.springscheduling;

import com.alibaba.fastjson.JSON;
import com.lifeonwalden.springscheduling.monitor.Monitor;
import com.lifeonwalden.springscheduling.monitor.TaskEvent;

public class MonitorImpl implements Monitor {

  @Override
  public void notificate(TaskEvent event) {
    System.out.println(JSON.toJSONString(event));
  }

}
