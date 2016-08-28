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

import java.util.List;
import java.util.Map;

import com.lifeonwalden.springscheduling.monitor.Monitor;

/**
 * worker do the job one after another, but with dependences between them
 * 
 * @author HongLu
 *
 */
public class DependentChainTask extends Task {
  private List<Worker> workerList;

  private int retryIndex = -1;

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
    int _retryIndex = 0;
    if (canRetry && !alwaysFromBeginning && -1 < retryIndex) {
      _retryIndex = retryIndex;
    }

    retryIndex = -1;
    int size = workerList.size();
    for (int i = _retryIndex; i < size; i++) {
      try {
        workerList.get(i).doJob(param);
      } catch (Throwable e) {
        retryIndex = i;

        throw e;
      }
    }

    return null;
  }

}
