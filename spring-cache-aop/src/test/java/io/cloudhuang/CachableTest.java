package io.cloudhuang;

/**
 * @author cloudhuang
 */

import io.cloudhuang.service.DummyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:beans.xml", "classpath*:test-beans.xml"})
public class CachableTest {
    @Autowired
    private DummyService dummyService;

    @Test
    public void testCacheString() throws Exception {
        dummyService.getDummy();
        dummyService.getDummy();
        dummyService.getDummy();
    }

    @Test
    public void testCacheMap() throws Exception {
        dummyService.getMap();
        dummyService.getMap();

        Map<String, String> map = dummyService.getMap();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("-----------------------> " + entry.getKey() + ": " + entry.getValue());
        }
    }

    @Test
    public void testCacheList() throws Exception {
        dummyService.getList();
        dummyService.getList();
        List<String> list = dummyService.getList();

        for (String s : list) {
            System.out.println("---------> " + s);
        }
    }
}
