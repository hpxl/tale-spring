package com.tale.dao;

import com.tale.model.entity.Contents;
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
@ContextConfiguration(locations = {"classpath:spring/spring-dao.xml"})
public class ContentsDaoTest {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    //注入Dao实现类依赖
    @Resource
    private ContentsDao contentsDao;

    @Test
    public void testQueryObject() throws Exception {
        Contents contents;
        contents = contentsDao.queryObject(1);
        System.out.println(contents);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("slug", "customize/about");
        int count = contentsDao.queryTotal(map);
        System.out.println(count);
    }
}
