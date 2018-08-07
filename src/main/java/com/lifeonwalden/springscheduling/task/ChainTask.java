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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * work do the job one after another, but no dependences between them
 *
 * @author HongLu
 */
public class ChainTask extends Task {
    private final static Logger logger = LogManager.getLogger(ChainTask.class);

    private List<Work> workList;

    private List<Work> retryList = new ArrayList<Work>();

    public ChainTask(String id, String name, TaskTriggerContext triggerContext, Monitor monitor, List<Work> workList) {
        super(id, name, triggerContext, monitor);
        this.workList = workList;
    }

    public ChainTask(String id, String name, TaskTriggerContext triggerContext, List<Work> workList) {
        super(id, name, triggerContext);
        this.workList = workList;
    }

    public List<Work> getWorkList() {
        return workList;
    }

    @Override
    public List<String> doJob(Map<String, Object> param, boolean isOneTimeExecution) {
        if (isOneTimeExecution) {
            return runOneTimeJob(param);
        } else {
            return runPlanJob(param);
        }
    }

    private List<String> runOneTimeJob(Map<String, Object> param) {
        List<String> failPrintList = new ArrayList<>();

        for (Work work : workList) {
            try {
                logger.info("Work [{}] Start", work.getName());
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                work.doJob(param);
                stopWatch.stop();
                logger.info("Work [{}] End, the task cost time :  {}", work.getName(), stopWatch.getTime());
            } catch (Exception e) {
                logger.error("Work failed", e);

                failPrintList.add(e.getMessage());
            }
        }

        return failPrintList.isEmpty() ? null : failPrintList;
    }

    private List<String> runPlanJob(Map<String, Object> param) {
        List<String> failPrintList = new ArrayList<>();

        List<Work> _workList = workList;
        if (canRetry && !alwaysFromBeginning && !retryList.isEmpty()) {
            if (retryTimes < maxRetryTimes) {
                _workList = retryList;
            } else {
                retryList.clear();
            }
        }

        retryList = new ArrayList<>();
        for (Work work : _workList) {
            try {
                logger.info("Work [{}] Start", work.getName());
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                work.doJob(param);
                stopWatch.stop();
                logger.info("Work [{}] End, the task cost time :  {}", work.getName(), stopWatch.getTime());
            } catch (Exception e) {
                logger.error("Work failed", e);

                retryList.add(work);
                failPrintList.add(e.getMessage());
            }
        }

        return failPrintList.isEmpty() ? null : failPrintList;
    }
}
