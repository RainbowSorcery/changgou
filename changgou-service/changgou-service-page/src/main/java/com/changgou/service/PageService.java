package com.changgou.service;

import java.util.Map;

public interface PageService {
    Map<String, Object> buildPageData(String spuId);

    void createPageHtml(String spuId);
}
