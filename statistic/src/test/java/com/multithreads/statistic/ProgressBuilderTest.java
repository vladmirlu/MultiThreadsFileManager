package com.multithreads.statistic;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProgressBuilderTest {

    private ReportsAdjuster reportsAdjuster;

    private ProgressBuilder progressBuilder;

    @Before
    public void setUp(){
        reportsAdjuster = new ReportsAdjuster();
        progressBuilder = new ProgressBuilder(reportsAdjuster);
    }

    @Test
    public void callTest(){
        String progress = progressBuilder.call();
        assertEquals("Total progress: 0%, Total spent time: 0ns " ,progress);
    }

    @Test
    public void calculateProgressTest(){
        int progress = progressBuilder.calculateProgress(50,100);
        assertEquals(50, progress);
    }
}
