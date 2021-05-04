package webflux;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.HttpHandlerDecoratorFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements WebFilter, HttpHandlerDecoratorFactory {
    public static final String CORRELATION_ID_HEADER_NAME = "X-correlationId";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().beforeCommit(() -> Mono.deferContextual(ctx -> {
            logger.info("### Setting response header");
            exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER_NAME, ctx.get(CORRELATION_ID_HEADER_NAME));
            return Mono.empty();
        }));
        return chain.filter(exchange);
    }

    @Override
    public HttpHandler apply(HttpHandler httpHandler) {
        return (request, response) ->
                httpHandler.handle(request, response)
                        .contextWrite(ctx -> {
                            String correlationId = UUID.randomUUID().toString();
                            logger.info("### CorrelationId generated: {}", correlationId);
                            return ctx.put(CORRELATION_ID_HEADER_NAME, correlationId);
                        });
    }

}
