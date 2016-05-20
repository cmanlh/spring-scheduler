package com.lifeonwalden.springscheduling.task;

import java.util.List;
import java.util.Map;

import com.lifeonwalden.springscheduling.monitor.Monitor;

public class IndependentTask extends Task {
  private Worker worker;

  public IndependentTask(String id, String name, TaskTriggerContext triggerContext, Worker worker) {
    super(id, name, triggerContext);
    this.worker = worker;
  }

  public IndependentTask(String id, String name, TaskTriggerContext triggerContext, Monitor monitor, Worker worker) {
    super(id, name, triggerContext, monitor);
    this.worker = worker;
  }

  public Worker getWorker() {
    return worker;
  }

  @Override
  public List<Throwable> doJob(Map<String, Object> param) {
    worker.doJob(param);

    return null;
  }
}
