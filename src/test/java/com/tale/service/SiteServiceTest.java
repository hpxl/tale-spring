package com.tale.service;

import com.tale.BaseTest;
import com.tale.model.Bo.StatisticsBo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hpxl on 2/5/18.
 */
public class SiteServiceTest extends BaseTest {

    @Autowired
    private SiteService siteService;

    @Test
    public void getStatisticsTest() {
        StatisticsBo statisticsBo = siteService.getStatistics();
        System.out.println(statisticsBo);
    }

}
