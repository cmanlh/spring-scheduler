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
    protected List<Throwable> doJob(Map<String, Object> param) {
        worker.doJob(param);

        return null;
    }
}
