package com.lifeonwalden.springscheduling.cronBuilder.time;

public enum Weekday implements CronTime {
  /**
   * The singleton instance for the day-of-week of Sunday.
   */
  SUNDAY,
  /**
   * The singleton instance for the day-of-week of Monday.
   */
  MONDAY,
  /**
   * The singleton instance for the day-of-week of Tuesday.
   */
  TUESDAY,
  /**
   * The singleton instance for the day-of-week of Wednesday.
   */
  WEDNESDAY,
  /**
   * The singleton instance for the day-of-week of Thursday.
   */
  THURSDAY,
  /**
   * The singleton instance for the day-of-week of Friday.
   */
  FRIDAY,
  /**
   * The singleton instance for the day-of-week of Saturday.
   */
  SATURDAY;

  @Override
  public int toCronTime() {
    return ordinal();
  }

}
