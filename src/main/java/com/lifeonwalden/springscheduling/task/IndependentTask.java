/**
 * Copyright 2016 HongLu
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.lifeonwalden.springscheduling.task;

import com.lifeonwalden.springscheduling.monitor.Monitor;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IndependentTask extends Task {
    private final static Logger logger = LogManager.getLogger(IndependentTask.class);

    private Work work;

    public IndependentTask(String id, String name, TaskTriggerContext triggerContext, Work work) {
        super(id, name, triggerContext);
        this.work = work;
    }

    public IndependentTask(String id, String name, TaskTriggerContext triggerContext, Monitor monitor, Work work) {
        super(id, name, triggerContext, monitor);
        this.work = work;
    }

    public Work getWork() {
        return work;
    }

    @Override
    public List<String> doJob(Map<String, Object> param, boolean isOneTimeExecution) {
        logger.info("Work [{}] Start", work.getName());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        work.doJob(param);
        stopWatch.stop();
        logger.info("Work [{}] End, the task cost time :  {}", work.getName(), stopWatch.getTime());

        return null;
    }

    @Override
    public List<Work> getWorkList() {
        return Arrays.asList(work);
    }
}
