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

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lifeonwalden.springscheduling.monitor.Monitor;

public class IndependentTask extends Task {
    private final static Logger logger = LogManager.getLogger(IndependentTask.class);

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
    public List<String> doJob(Map<String, Object> param) {
        String clzName = worker.getClass().getSimpleName();
        logger.info("Worker [{}] Start", clzName);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        worker.doJob(param);
        stopWatch.stop();
        logger.info("Worker [{}] End, the task cost time :  {}", clzName, stopWatch.getTime());

        return null;
    }
}
