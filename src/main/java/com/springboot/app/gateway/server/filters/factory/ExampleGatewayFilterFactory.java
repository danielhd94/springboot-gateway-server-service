package com.springboot.app.gateway.server.filters.factory;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

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

			logger.info("Ejecutando pre gateway filter factory: " + config.message);
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {

				Optional.ofNullable(config.cookieValue).ifPresent((cookie) -> {
					exchange.getResponse().addCookie(ResponseCookie.from(config.cookieName, cookie).build());
				});
				logger.info("Ejecutando post gateway filter factory: " + config.message);
			}));
		};
	}
	
	
	public class Configuration {

		private String message;
		private String cookieValue;
		private String cookieName;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getCookieValue() {
			return cookieValue;
		}

		public void setCookieValue(String cookieValue) {
			this.cookieValue = cookieValue;
		}

		public String getCookieName() {
			return cookieName;
		}

		public void setCookieName(String cookieName) {
			this.cookieName = cookieName;
		}

	}

}
