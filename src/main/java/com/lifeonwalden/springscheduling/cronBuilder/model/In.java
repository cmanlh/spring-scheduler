package com.lifeonwalden.springscheduling.cronBuilder.model;

import com.lifeonwalden.springscheduling.cronBuilder.time.CronTime;

/**
 * 指定值范围
 * 
 * @author luhong
 *
 */
public class In implements FieldDefinition {
    private CronTime[] times;

    public In(CronTime... times) {
        this.times = times;
    }

    @Override
    public String toExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append(times[0].toCronTime());
        for (int i = 1; i < times.length; i++) {
            sb.append(",").append(times[i].toCronTime());
        }

        return sb.toString();
    }

    @Override
    public boolean isValid(Class<?> clazz) {
        for (CronTime time : times) {
            if (!clazz.isInstance(time)) {
                return false;
            }
        }

        return true;
    }

}
