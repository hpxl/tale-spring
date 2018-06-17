package com.tale;

import org.junit.Test;

import java.util.*;

/**
 * Created by hpxl on 4/3/18.
 */
public class javaTest {
    @Test
    public void name() throws Exception {
        Map<String, Object> linkMap = new LinkedHashMap<String, Object>();
        linkMap.put("a", "1");
        linkMap.put("b", "2");
        System.out.println(linkMap);
    }
}
