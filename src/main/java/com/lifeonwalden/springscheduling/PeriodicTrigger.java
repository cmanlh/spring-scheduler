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
package com.lifeonwalden.springscheduling;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.TriggerContext;
import org.springframework.util.Assert;

/**
 * A trigger for periodic task execution. The period may be applied as either fixed-rate or fixed-delay, and an initial
 * delay value may also be configured. The default initial delay is 0, and the default behavior is fixed-delay (i.e. the
 * interval between successive executions is measured from each <i>completion</i> time). To measure the interval between
 * the scheduled <i>start</i> time of each execution instead, set the 'fixedRate' property to {@code true} .
 *
 * <p>
 * Note that the TaskScheduler interface already defines methods for scheduling tasks at fixed-rate or with fixed-delay. Both also support an optional
 * value for the initial delay. Those methods should be used directly whenever possible. The value of this Trigger implementation is that it can be
 * used within components that rely on the Trigger abstraction. For example, it may be convenient to allow periodic triggers, cron-based triggers, and
 * even custom Trigger implementations to be used interchangeably.
 *
 * @author Mark Fisher
 * @since 3.0
 */
public class PeriodicTrigger extends BaseTrigger {

    private long period;

    private TimeUnit timeUnit;

    private volatile long initialDelay = 0;

    private volatile boolean fixedRate = false;

    /**
     * Create a trigger with the given period in milliseconds.
     * 
     * @param id id
     * @param name name
     * @param period period
     */
    public PeriodicTrigger(String id, String name, long period) {
        this(id, name, period, null);
    }

    /**
     * Create a trigger with the given period and time unit. The time unit will apply not only to the period but also to
     * any 'initialDelay' value, if configured on this Trigger later via {@link #setInitialDelay(long)}.
     * 
     * @param id id
     * @param name name
     * @param period period
     * @param timeUnit timeUnit
     */
    public PeriodicTrigger(String id, String name, long period, TimeUnit timeUnit) {
        Assert.isTrue(period >= 0, "period must not be negative");
        this.timeUnit = (timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS);
        this.period = this.timeUnit.toMillis(period);
        this.id = id;
        this.name = name;
    }

    /**
     * 
     * @param period period
     */
    public void updateTrigger(long period) {
        updateTrigger(period, null);
    }

    /**
     * 
     * @param period period
     * @param timeUnit timeUnit
     */
    public void updateTrigger(long period, TimeUnit timeUnit) {
        this.timeUnit = (timeUnit != null ? timeUnit : TimeUnit.MILLISECONDS);
        this.period = this.timeUnit.toMillis(period);
    }


    /**
     * Specify the delay for the initial execution. It will be evaluated in terms of this trigger's {@link TimeUnit}. If
     * no time unit was explicitly provided upon instantiation, the default is milliseconds.
     * 
     * @param initialDelay initialDelay
     */
    public void setInitialDelay(long initialDelay) {
        this.initialDelay = this.timeUnit.toMillis(initialDelay);
    }

    /**
     * Specify whether the periodic interval should be measured between the scheduled start times rather than between
     * actual completion times. The latter, "fixed delay" behavior, is the default.
     * 
     * @param fixedRate fixedRate
     */
    public void setFixedRate(boolean fixedRate) {
        this.fixedRate = fixedRate;
    }


    /**
     * Returns the time after which a task should run again.
     */
    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        if (triggerContext.lastScheduledExecutionTime() == null) {
            return new Date(System.currentTimeMillis() + this.initialDelay);
        } else if (this.fixedRate) {
            return new Date(triggerContext.lastScheduledExecutionTime().getTime() + this.period);
        }
        return new Date(triggerContext.lastCompletionTime().getTime() + this.period);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PeriodicTrigger)) {
            return false;
        }
        PeriodicTrigger other = (PeriodicTrigger) obj;
        return (this.fixedRate == other.fixedRate && this.initialDelay == other.initialDelay && this.period == other.period);
    }

    @Override
    public int hashCode() {
        return (this.fixedRate ? 17 : 29) + (int) (37 * this.period) + (int) (41 * this.initialDelay);
    }

}
