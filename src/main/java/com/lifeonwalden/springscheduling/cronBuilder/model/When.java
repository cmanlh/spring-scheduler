package com.lifeonwalden.springscheduling.cronBuilder.model;

import com.lifeonwalden.springscheduling.cronBuilder.time.CronTime;

/**
 * 当
 * 
 * @author luhong
 *
 */
public class When implements FieldDefinition {
    private CronTime time;

    public When(CronTime time) {
        this.time = time;
    }

    @Override
    public String toExpression() {
        return Integer.toString(time.toCronTime());
    }

    @Override
    public boolean isValid(Class<?> clazz) {
        return clazz.isInstance(time);
    }
}
