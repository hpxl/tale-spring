package com.tale.service;

import com.tale.BaseTest;
import com.tale.dto.Types;
import com.tale.model.entity.Metas;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 4/3/18.
 */
public class ContentsServiceTest extends BaseTest {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ContentsService contentsService;

    @Autowired
    private MetasService metasService;

    @Test
    public void testQueryList() {
        Map<String, Object> params = new HashMap<String, Object>();
        System.out.println(contentsService.queryList(params));
        LOGGER.info("test");
    }

    @Test
    public void testGetMetas() {
        List<Metas> metasList = metasService.getMetas(Types.CATEGORY);
        System.out.println(metasList);
    }
}
