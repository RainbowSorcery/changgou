package com.changgou.service;

import java.util.Map;

public interface CartService {
    void addCard(String username, String skuId, Integer num);

    Map<String, Object> cardList(String username);

    void deleteCart(String username, String skuId);

    void updateChecked(String username, String skuId, boolean checked);
}
