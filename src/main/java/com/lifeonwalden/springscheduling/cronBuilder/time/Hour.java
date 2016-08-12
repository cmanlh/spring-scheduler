package com.lifeonwalden.springscheduling.cronBuilder.time;

public enum Hour implements CronTime {
    H0, H1, H2, H3, H4, H5, H6, H7, H8, H9, H10, H11, H12, H13, H14, H15, H16, H17, H18, H19, H20, H21, H22, H23;

    @Override
    public int toCronTime() {
        return ordinal();
    }

}
