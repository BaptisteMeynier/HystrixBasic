package org.netflix.hystrix.basic.common.constantes;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

public interface Constantes {
    String BOOK_REMOTE_URI = "http://localhost:8090";
    String BOOK_JERSEY_PATH = "books";

    HystrixCommandGroupKey BOOK_HYSTRIX_COMMAND_GROUP_KEY = HystrixCommandGroupKey.Factory.asKey("Book");
    HystrixCommandKey BOOK_HYSTRIX_COMMAND_KEY = HystrixCommandKey.Factory.asKey("Book");
    HystrixThreadPoolKey BOOK_HYSTRIX_POOL_KEY = HystrixThreadPoolKey.Factory.asKey("Book");

    HystrixCommandGroupKey PT_HYSTRIX_COMMAND_GROUP_KEY = HystrixCommandGroupKey.Factory.asKey("Library");
    HystrixCommandKey PT_HYSTRIX_COMMAND_KEY = HystrixCommandKey.Factory.asKey("Library");
    HystrixThreadPoolKey PT_HYSTRIX_POOL_KEY = HystrixThreadPoolKey.Factory.asKey("Library");
}
