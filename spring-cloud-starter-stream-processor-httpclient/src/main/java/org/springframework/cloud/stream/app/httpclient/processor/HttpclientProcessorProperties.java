/*
 * Copyright 2015-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.httpclient.processor;

import java.time.Duration;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.expression.Expression;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the Http Client Processor module.
 *
 * @author Waldemar Hummer
 * @author Mark Fisher
 * @author Christian Tzolov
 * @author Artem Bilan
 */
@ConfigurationProperties("httpclient")
@Validated
public class HttpclientProcessorProperties {

	private static final HttpMethod DEFAULT_HTTP_METHOD = HttpMethod.GET;

	private static final Class<?> DEFAULT_RESPONSE_TYPE = String.class;

	/**
	 * The URL to issue an http request to, as a static value.
	 */
	private String url;

	/**
	 * A SpEL expression against incoming message to determine the URL to use.
	 */
	private Expression urlExpression;

	/**
	 * The kind of http method to use.
	 */
	private HttpMethod httpMethod = DEFAULT_HTTP_METHOD;


	/**
	 * A SpEL expression to derive the request method from the incoming message.
	 */
	private Expression httpMethodExpression;

	/**
	 * The (static) request body; if neither this nor bodyExpression is provided, the payload will be used.
	 */
	private Object body;

	/**
	 * A SpEL expression to derive the request body from the incoming message.
	 */
	private Expression bodyExpression;

	/**
	 * A SpEL expression used to derive the http headers map to use.
	 */
	private Expression headersExpression;

	/**
	 * The type used to interpret the response.
	 */
	private Class<?> expectedResponseType = DEFAULT_RESPONSE_TYPE;

	/**
	 * A SpEL expression used to compute the final result, applied against the whole http response.
	 */
	private Expression replyExpression = new SpelExpressionParser().parseExpression("body");

	private final Retry retry = new Retry();

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

	public Expression getUrlExpression() {
		return urlExpression != null ? urlExpression
				: new LiteralExpression(this.url);
	}

	public void setUrlExpression(Expression urlExpression) {
		this.urlExpression = urlExpression;
	}

	@NotNull
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public Expression getHttpMethodExpression() {
		return httpMethodExpression;
	}

	public void setHttpMethodExpression(Expression httpMethodExpression) {
		this.httpMethodExpression = httpMethodExpression;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public Expression getBodyExpression() {
		return bodyExpression;
	}

	public void setBodyExpression(Expression bodyExpression) {
		this.bodyExpression = bodyExpression;
	}

	public Expression getHeadersExpression() {
		return headersExpression;
	}

	public void setHeadersExpression(Expression headersExpression) {
		this.headersExpression = headersExpression;
	}

	@NotNull
	public Class<?> getExpectedResponseType() {
		return expectedResponseType;
	}

	public void setExpectedResponseType(Class<?> expectedResponseType) {
		this.expectedResponseType = expectedResponseType;
	}

	@NotNull
	public Expression getReplyExpression() {
		return replyExpression;
	}

	public void setReplyExpression(Expression replyExpression) {
		this.replyExpression = replyExpression;
	}

	public Retry getRetry() {
		return this.retry;
	}

	@AssertTrue(message = "Exactly one of 'url' or 'urlExpression' is required")
	public boolean isExactlyOneUrl() {
		return url == null ^ urlExpression == null;
	}

	@AssertTrue(message = "At most one of 'body' or 'bodyExpression' is allowed")
	public boolean isAtMostOneBody() {
		return body == null || bodyExpression == null;
	}


	public static class Retry {

		/**
		 * Whether retries are enabled around HTTP requests.
		 */
		private boolean enabled;

		/**
		 * Maximum number of attempts to deliver a message.
		 */
		private int maxAttempts = 3;

		/**
		 * Duration between the first and second attempt to deliver a message.
		 */
		private Duration initialInterval = Duration.ofMillis(1000);

		/**
		 * Multiplier to apply to the previous retry interval.
		 */
		private double multiplier = 1.0;

		/**
		 * Maximum duration between attempts.
		 */
		private Duration maxInterval = Duration.ofMillis(10000);

		public boolean isEnabled() {
			return this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public int getMaxAttempts() {
			return this.maxAttempts;
		}

		public void setMaxAttempts(int maxAttempts) {
			this.maxAttempts = maxAttempts;
		}

		public Duration getInitialInterval() {
			return this.initialInterval;
		}

		public void setInitialInterval(Duration initialInterval) {
			this.initialInterval = initialInterval;
		}

		public double getMultiplier() {
			return this.multiplier;
		}

		public void setMultiplier(double multiplier) {
			this.multiplier = multiplier;
		}

		public Duration getMaxInterval() {
			return this.maxInterval;
		}

		public void setMaxInterval(Duration maxInterval) {
			this.maxInterval = maxInterval;
		}

	}

}
