package com.springboot.app.gateway.server.filters.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class ExampleGatewayFilterFactory
		extends AbstractGatewayFilterFactory<ExampleGatewayFilterFactory.Configuration> {

	private final Logger logger = LoggerFactory.getLogger(ExampleGatewayFilterFactory.class);

	public ExampleGatewayFilterFactory() {
		super(Configuration.class);
	}

	@Override
	public GatewayFilter apply(Configuration config) {
		return (exchange, chain) -> {
			logPreFilterExecution(config);
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				addCookieToResponse(exchange, config);
				logPostFilterExecution(config);
			}));
		};
	}

	private void logPreFilterExecution(Configuration config) {
		logger.info("Ejecutando pre gateway filter factory: {}", config.getMessage());
	}

	private void logPostFilterExecution(Configuration config) {
		logger.info("Ejecutando post gateway filter factory: {}", config.getMessage());
	}

	private void addCookieToResponse(ServerWebExchange exchange, Configuration config) {
		Optional.ofNullable(config.getCookieValue()).ifPresent(cookieValue -> {
			exchange.getResponse().addCookie(ResponseCookie.from(config.getCookieName(), cookieValue).build());
		});
	}

	@Override
	public String name() {
		return "cookies";
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList("message", "cookieName", "cookieValue");
	}

	public static class Configuration {
		@NotBlank
		private String message;
		private String cookieName;
		private String cookieValue;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getCookieName() {
			return cookieName;
		}

		public void setCookieName(String cookieName) {
			this.cookieName = cookieName;
		}

		public String getCookieValue() {
			return cookieValue;
		}

		public void setCookieValue(String cookieValue) {
			this.cookieValue = cookieValue;
		}
	}
}
