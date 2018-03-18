 package org.netflix.hystrix;
 
 
 
 public class PTHystrixCommand extends HystrixCommand<Response> {

        private final Invocation.Builder request;

        private Response response = null;

        public PTHystrixCommand(Invocation.Builder request) {
            super(
                    Setter.withGroupKey(PT_HYSTRIX_COMMAND_GROUP_KEY)
                            .andCommandKey(PT_HYSTRIX_COMMAND_KEY)
                            .andThreadPoolKey(PT_HYSTRIX_POOL_KEY)
                            .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                                    .withCoreSize(10))
                            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                    .withExecutionTimeoutInMilliseconds(2000)
                                    .withCircuitBreakerRequestVolumeThreshold(5)
                                    .withCircuitBreakerErrorThresholdPercentage(50)
                                    .withFallbackEnabled(false)
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
            boolean partialResponse = true;
            Book defaultBook = new Book();
            
            return Response.ok(new Book(partialResponse)).build();
        }

        public void closeConnection() {
            if (response != null) {
                response.close();
            }
        }
    }