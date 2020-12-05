package com.changgou.service;

import java.util.Map;

public interface ElasticSearchSearchService {
    Map<String, Object> search(Map<String, String> conditionMap);
}
