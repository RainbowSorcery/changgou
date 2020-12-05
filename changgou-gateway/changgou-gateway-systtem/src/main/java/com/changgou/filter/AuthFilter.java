package com.changgou.filter;

import com.changgou.utils.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;

@Component
public class AuthFilter implements GlobalFilter, Ordered  {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求
        ServerHttpRequest request = exchange.getRequest();
        // 获取响应
        ServerHttpResponse response = exchange.getResponse();

        // 获取uri 并判断是否为登录操作 如果是登录操作 放行 否则 拦截
        String hostString = request.getURI().getPath();

        if (hostString.contains("/admin/login")) {
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst("token");

        if (StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);

            return response.setComplete();
        }

        try {
            JwtUtils.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }


        return chain.filter(exchange);
    }



    @Override
    public int getOrder() {
        return 0;
    }
}
