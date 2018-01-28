package org.netflix.hystrix.basic.customer.client;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.netflix.hystrix.basic.common.constantes.Constantes;

import org.netflix.hystrix.basic.common.model.Book;
import org.netflix.hystrix.basic.common.constantes.*;
import org.netflix.hystrix.basic.common.exceptions.RemoteCallException;
import org.netflix.hystrix.basic.customer.config.ClientJersey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

@Component
public class BookBorrower {
	
	@Autowired
	ClientJersey clientJersey;

	public Book search(String name) {
		Book book=null;
		Response response = null;
		BookHystrixCommand hystrixCommand = null;
		try {
			UriBuilder uriBuilder = UriBuilder.fromUri("books").queryParam("name", name);
			
			final WebTarget target = clientJersey.getClient().target(uriBuilder);
			final Invocation.Builder request = target.request(APPLICATION_JSON_TYPE);
			hystrixCommand = new BookHystrixCommand(request);
			response = hystrixCommand.execute();
		} catch (HystrixRuntimeException e) {
			if (hystrixCommand != null) {
				hystrixCommand.closeConnection();
			}
			switch (e.getFailureType()) {
			case TIMEOUT:
				throw new RemoteCallException(RemoteCallException.ExceptionType.TIMEOUT, hystrixCommand.getExecutionTimeInMilliseconds(), e);
			case SHORTCIRCUIT:
				throw new RemoteCallException(RemoteCallException.ExceptionType.HYSTRIX_OPEN_CIRCUIT, hystrixCommand.getExecutionTimeInMilliseconds(), e);
			case REJECTED_THREAD_EXECUTION:
				throw new RemoteCallException(RemoteCallException.ExceptionType.HYSTRIX_REJECTED_THREAD_EXECUTION, hystrixCommand.getExecutionTimeInMilliseconds(), e);
			default:
				throw new RemoteCallException(RemoteCallException.ExceptionType.OTHER, hystrixCommand.getExecutionTimeInMilliseconds(), e);
			}
		}

		if (response.getEntity() instanceof Book) {
			book = (Book) response.getEntity();
		} /*else {
			book = adapteRemoteResponse(response.readEntity(Book.class));
		}*/
		return book;
	}

	private class BookHystrixCommand extends HystrixCommand<Response> {

		private final Invocation.Builder request;

		private Response response = null;

		public BookHystrixCommand(Invocation.Builder request) {
			super(
					Setter.withGroupKey(Constantes.BOOK_HYSTRIX_COMMAND_GROUP_KEY)
					.andCommandKey(Constantes.BOOK_HYSTRIX_COMMAND_KEY)
					.andThreadPoolKey(Constantes.BOOK_HYSTRIX_POOL_KEY)
					.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
							.withCoreSize(10))
					.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
							.withExecutionTimeoutInMilliseconds(2000)
							.withCircuitBreakerRequestVolumeThreshold(5)
							.withCircuitBreakerErrorThresholdPercentage(50)
							.withMetricsRollingStatisticalWindowInMilliseconds(10000)
							.withCircuitBreakerSleepWindowInMilliseconds(5000)));

			this.request = request;
		}

		@Override
		protected Response run() throws Exception {
			response = request.get();
			return response;
		}

		@Override
		protected Response getFallback() {
			return Response.ok(new Book("INCONNUE",0,0)).build();
		}

		public void closeConnection() {
			if (response != null) {
				response.close();
			}
		}
	}
	
}
