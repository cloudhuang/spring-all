package io.cloudhuang.service;

import io.cloudhuang.Cachable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cloudhuang
 */
public class DummyService {
    @Cachable
    public String getDummy() {
        return "Test Caching";
    }

    @Cachable
    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("Name", "CloudHuang");
        return map;
    }

    @Cachable(value = "listCache")
    public List<String> getList() {
        List<String> list = new ArrayList<String>();
        list.add("Name");
        list.add("CloudHuang");
        return list;
    }
}
