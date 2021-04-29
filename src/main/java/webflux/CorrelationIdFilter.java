package webflux;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements WebFilter {
    public static final String CORRELATION_ID_HEADER_NAME = "X-correlationId";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        logger.info("### Filter called");
        exchange.getResponse().beforeCommit(() -> Mono.deferContextual(ctx -> {
            exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER_NAME, ctx.get(CORRELATION_ID_HEADER_NAME));
            return Mono.empty();
        }));

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(CORRELATION_ID_HEADER_NAME, UUID.randomUUID().toString()));
    }

}
