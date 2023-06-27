package com.todo.apigateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.todo.apigateway.util.JwtUtil;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    @Autowired
    private RouteValidator validator;

    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest()
                        .getHeaders()
                        .containsKey(HttpHeaders.AUTHORIZATION)) {
                    return buildUnauthorizedResponse(exchange, "Token Not Found");
                }

                String authHeader = exchange.getRequest()
                        .getHeaders()
                        .get(HttpHeaders.AUTHORIZATION)
                        .get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
//                    //REST call to AUTH service
//                    template.getForObject("http://IDENTITY-SERVICE//validate?token" + authHeader, String.class);
                    jwtUtil.validateToken(authHeader);

                } catch (Exception e) {
                    return buildUnauthorizedResponse(exchange, "Invalid or Expired Token");
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }

    private Mono<Void> buildUnauthorizedResponse(ServerWebExchange exchange, String responseBody) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

}
