package com.lifeonwalden.springscheduling.cronBuilder;


import org.junit.Assert;
import org.junit.Test;

import com.lifeonwalden.springscheduling.cronBuilder.model.Between;
import com.lifeonwalden.springscheduling.cronBuilder.model.Every;
import com.lifeonwalden.springscheduling.cronBuilder.model.In;
import com.lifeonwalden.springscheduling.cronBuilder.model.When;
import com.lifeonwalden.springscheduling.cronBuilder.time.Minute;
import com.lifeonwalden.springscheduling.cronBuilder.time.Second;


public class CronBuilderTest {
    @Test
    public void test() {
        String exp = CronBuilder.build().toCronExpression();

        System.out.println(exp);
        Assert.assertEquals("* * * * * *", exp);
    }

    @Test
    public void testSecondWhen() {
        String exp = CronBuilder.build().second(new When(Second.S0)).toCronExpression();

        System.out.println(exp);
        Assert.assertEquals("0 * * * * *", exp);
    }

    @Test
    public void testSecondBetween() {
        String exp = CronBuilder.build().second(new Between(Second.S0, Second.S36)).toCronExpression();

        System.out.println(exp);
        Assert.assertEquals("0-36 * * * * *", exp);
    }

    @Test
    public void testSecondEvery() {
        String exp = CronBuilder.build().second(new Every(new Between(Second.S0, Second.S36), 15)).toCronExpression();

        System.out.println(exp);
        Assert.assertEquals("0-36/15 * * * * *", exp);
    }

    @Test
    public void testSecondIn() {
        String exp = CronBuilder.build().second(new In(Second.S0, Second.S36)).toCronExpression();

        System.out.println(exp);
        Assert.assertEquals("0,36 * * * * *", exp);
    }

    @Test
    public void testSecondError() {
        CronBuilder.build().second(new In(Minute.M0, Minute.M36)).toCronExpression();
    }
}
