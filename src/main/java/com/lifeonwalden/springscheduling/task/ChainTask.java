/**
 * Copyright 2016 HongLu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.lifeonwalden.springscheduling.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lifeonwalden.springscheduling.monitor.Monitor;

/**
 * worker do the job one after another, but no dependences between them
 * 
 * @author HongLu
 *
 */
public class ChainTask extends Task {
  private List<Worker> workerList;

  private List<Worker> retryList = new ArrayList<Worker>();

  public ChainTask(String id, String name, TaskTriggerContext triggerContext, Monitor monitor,
      List<Worker> workerList) {
    super(id, name, triggerContext, monitor);
    this.workerList = workerList;
  }

  public ChainTask(String id, String name, TaskTriggerContext triggerContext, List<Worker> workerList) {
    super(id, name, triggerContext);
    this.workerList = workerList;
  }

  public List<Worker> getWorkerList() {
    return workerList;
  }

  @Override
  protected List<Throwable> doJob(Map<String, Object> param) {
    List<Throwable> failPrintList = new ArrayList<>();

    List<Worker> _workerList = workerList;
    if (canRetry && !alwaysFromBeginning && !retryList.isEmpty()) {
      _workerList = retryList;
    }

    retryList = new ArrayList<Worker>();
    for (Worker worker : _workerList) {
      try {
        worker.doJob(param);
      } catch (Throwable e) {
        retryList.add(worker);

        failPrintList.add(e);
      }
    }
    return failPrintList.isEmpty() ? null : failPrintList;
  }

}
