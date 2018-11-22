package com.multithreads.statistic;

import com.multithreads.statistic.model.TaskReport;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ReportsAdjusterTest {

    private ReportsAdjuster reportsAdjuster;

    @Before
    public void setUp(){
        reportsAdjuster  = new ReportsAdjuster();
    }

    @Test
    public void adjustThreadReportsOnTrueTest(){
        boolean adjustReports = reportsAdjuster.adjustThreadReports(-1,"test",1);
        assertTrue(adjustReports);
    }

    @Test
    public void adjustThreadReportsOnFalseTest(){
        boolean adjustReports = reportsAdjuster.adjustThreadReports(0,"test",0);
        assertFalse( adjustReports);
    }

    @Test
    public void getGeneralReportTest(){
        TaskReport taskReport = reportsAdjuster.getGeneralReport();
        assertTrue(taskReport instanceof TaskReport);
    }

    @Test
    public void getThreadReportsTest(){
        Map<String, TaskReport> reports = reportsAdjuster.getThreadReports();
        assertNotNull(reports);
    }
}
