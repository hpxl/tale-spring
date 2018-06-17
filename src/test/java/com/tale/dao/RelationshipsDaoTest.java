package com.tale.dao;

import com.tale.service.RelationshipsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置Spring和Junit整合,junit启动时加载springIOC容器
 * spring-test,junit
 */

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:spring/spring-dao.xml")
@ContextConfiguration(locations = {"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class RelationshipsDaoTest {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    //注入Dao实现类依赖
    @Resource
    private RelationshipsDao relationshipsDao;

    @Resource
    private RelationshipsService relationshipsService;

    @Test
    public void testDelete() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cid", 5);

        int num;
        //num = relationshipsDao.delete(map);
        //System.out.println(num);

        num = relationshipsService.deleteById(4, null);
        System.out.println(num);
    }
}
