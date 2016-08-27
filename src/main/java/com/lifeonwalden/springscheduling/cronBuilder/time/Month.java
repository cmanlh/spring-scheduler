package com.lifeonwalden.springscheduling.cronBuilder.time;

public enum Month implements CronTime {

    /**
     * The singleton instance for the month of January with 31 days.
     */
    JANUARY,
    /**
     * The singleton instance for the month of February with 28 days, or 29 in a leap year.
     */
    FEBRUARY,
    /**
     * The singleton instance for the month of March with 31 days.
     */
    MARCH,
    /**
     * The singleton instance for the month of April with 30 days.
     */
    APRIL,
    /**
     * The singleton instance for the month of May with 31 days.
     */
    MAY,
    /**
     * The singleton instance for the month of June with 30 days.
     */
    JUNE,
    /**
     * The singleton instance for the month of July with 31 days.
     */
    JULY,
    /**
     * The singleton instance for the month of August with 31 days.
     */
    AUGUST,
    /**
     * The singleton instance for the month of September with 30 days.
     */
    SEPTEMBER,
    /**
     * The singleton instance for the month of October with 31 days.
     */
    OCTOBER,
    /**
     * The singleton instance for the month of November with 30 days.
     */
    NOVEMBER,
    /**
     * The singleton instance for the month of December with 31 days.
     */
    DECEMBER;

    @Override
    public int toCronTime() {
        return ordinal() + 1;
    }

}
