package com.lifeonwalden.springscheduling.cronBuilder.model;

import com.lifeonwalden.springscheduling.cronBuilder.time.CronTime;

/**
 * 区间范围
 * 
 * @author luhong
 *
 */
public class Between implements FieldDefinition {
  private CronTime start;
  private CronTime end;

  public Between(CronTime start, CronTime end) {
    if (start.toCronTime() >= end.toCronTime()) {
      throw new RuntimeException("Invalid range.");
    }

    this.start = start;
    this.end = end;
  }

  @Override
  public String toExpression() {
    return Integer.toString(start.toCronTime()) + "-" + Integer.toString(end.toCronTime());
  }

  @Override
  public boolean isValid(Class<?> clazz) {
    return clazz.isInstance(start) && clazz.isInstance(end);
  }
}
