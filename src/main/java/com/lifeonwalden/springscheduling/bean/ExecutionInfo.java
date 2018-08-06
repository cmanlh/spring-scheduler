/*
 *    Copyright 2018 CManLH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.lifeonwalden.springscheduling.bean;

import java.util.Date;

public class ExecutionInfo {

    private Date nextExecutionTime;

    private Date actualExecutionTime;

    private Date completionTime;

    private boolean isSuccess;

    public Date getNextExecutionTime() {
        return nextExecutionTime;
    }

    public ExecutionInfo setNextExecutionTime(Date nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;

        return this;
    }

    public Date getActualExecutionTime() {
        return actualExecutionTime;
    }

    public ExecutionInfo setActualExecutionTime(Date actualExecutionTime) {
        this.actualExecutionTime = actualExecutionTime;

        return this;
    }

    public Date getCompletionTime() {
        return completionTime;
    }

    public ExecutionInfo setCompletionTime(Date completionTime) {
        this.completionTime = completionTime;

        return this;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public ExecutionInfo setSuccess(boolean success) {
        isSuccess = success;

        return this;
    }
}
