package com.changgou.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * 记录用户请求ip
 */
@Component
public class IPFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取ip地址
        InetSocketAddress remoteAddress= exchange.getRequest().getRemoteAddress();
        assert remoteAddress != null;
        String ip = remoteAddress.getHostName();

        // todo 记录到数据库
        System.out.println("用户ip:" + ip);

        // 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
