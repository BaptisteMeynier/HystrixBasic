package org.netflix.hystrix.basic.book.finder.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.netflix.hystrix.basic.book.finder.resource.BookResource;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        this.registerEndpoints();
    }

    private void registerEndpoints() {
        this.register(BookResource.class);
    }

}