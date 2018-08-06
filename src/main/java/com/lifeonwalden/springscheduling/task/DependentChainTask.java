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

import java.util.List;
import java.util.Map;

/**
 * work do the job one after another, but with dependences between them
 *
 * @author HongLu
 */
public class DependentChainTask extends Task {
    private final static Logger logger = LogManager.getLogger(DependentChainTask.class);

    private List<Work> workList;

    private int retryIndex = -1;

    public DependentChainTask(String id, String name, TaskTriggerContext triggerContext, Monitor monitor, List<Work> workList) {
        super(id, name, triggerContext, monitor);
        this.workList = workList;
    }

    public DependentChainTask(String id, String name, TaskTriggerContext triggerContext, List<Work> workList) {
        super(id, name, triggerContext);
        this.workList = workList;
    }

    public List<Work> getWorkList() {
        return workList;
    }

    @Override
    public List<String> doJob(Map<String, Object> param, boolean isOneTimeExecution) {
        int _retryIndex = 0;
        if (canRetry && !alwaysFromBeginning && -1 < retryIndex) {
            _retryIndex = retryIndex;
        }

        retryIndex = -1;
        int size = workList.size();
        Work work = null;
        for (int i = _retryIndex; i < size; i++) {
            try {
                work = workList.get(i);

                logger.info("Work [{}] Start", work.getName());
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                work.doJob(param);
                stopWatch.stop();
                logger.info("Work [{}] End, the task cost time :  {}", work.getName(), stopWatch.getTime());
            } catch (Throwable e) {
                logger.error("Work failed", e);

                if (this.maxRetryTimes <= this.retryTimes) {
                    retryIndex = -1;
                } else {
                    retryIndex = i;
                }
                throw e;
            }
        }

        return null;
    }
}
