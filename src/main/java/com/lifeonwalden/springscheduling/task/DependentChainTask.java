package com.lifeonwalden.springscheduling.task;

import java.util.List;
import java.util.Map;

import com.lifeonwalden.springscheduling.monitor.Monitor;

/**
 * worker do the job one after another, but no dependences between them
 * 
 * @author HongLu
 *
 */
public class DependentChainTask extends Task {
  private List<Worker> workerList;

  public DependentChainTask(String id, String name, TaskTriggerContext triggerContext, Monitor monitor,
      List<Worker> workerList) {
    super(id, name, triggerContext, monitor);
    this.workerList = workerList;
  }

  public DependentChainTask(String id, String name, TaskTriggerContext triggerContext, List<Worker> workerList) {
    super(id, name, triggerContext);
    this.workerList = workerList;
  }

  public List<Worker> getWorkerList() {
    return workerList;
  }

  @Override
  public List<Throwable> doJob(Map<String, Object> param) {
    for (Worker worker : workerList) {
      worker.doJob(param);
    }

    return null;
  }

}
