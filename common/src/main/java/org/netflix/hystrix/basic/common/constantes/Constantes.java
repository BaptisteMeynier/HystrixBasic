package org.netflix.hystrix.basic.common.constantes;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

public interface Constantes {
	int HTTP_PORT= 8080;
    String BOOK_REMOTE_URI = "http://localhost:"+HTTP_PORT;
    String BOOK_PATH = "api/v1/books";

    HystrixCommandGroupKey BOOK_HYSTRIX_COMMAND_GROUP_KEY = HystrixCommandGroupKey.Factory.asKey("Book");
    HystrixCommandKey BOOK_HYSTRIX_COMMAND_KEY = HystrixCommandKey.Factory.asKey("Book");
    HystrixThreadPoolKey BOOK_HYSTRIX_POOL_KEY = HystrixThreadPoolKey.Factory.asKey("Book");

    HystrixCommandGroupKey PT_HYSTRIX_COMMAND_GROUP_KEY = HystrixCommandGroupKey.Factory.asKey("Library");
    HystrixCommandKey PT_HYSTRIX_COMMAND_KEY = HystrixCommandKey.Factory.asKey("Library");
    HystrixThreadPoolKey PT_HYSTRIX_POOL_KEY = HystrixThreadPoolKey.Factory.asKey("Library");

}
