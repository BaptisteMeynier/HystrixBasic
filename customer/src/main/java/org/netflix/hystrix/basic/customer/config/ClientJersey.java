package org.netflix.hystrix.basic.customer.config;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Component
public class ClientJersey {

    public Client getClient() {
        final ClientConfig config = new ClientConfig();
        return ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .property(ClientProperties.READ_TIMEOUT, 5000)
                .property(ClientProperties.CONNECT_TIMEOUT, 5000)
                .build();
    }
}
