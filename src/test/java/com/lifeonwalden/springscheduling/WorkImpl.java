package com.lifeonwalden.springscheduling;

import java.util.Map;

import com.lifeonwalden.springscheduling.task.Worker;

public class WorkImpl implements Worker {

  @Override
  public void doJob(Map<String, Object> context) {
    System.out.println("I am running");
  }

}
