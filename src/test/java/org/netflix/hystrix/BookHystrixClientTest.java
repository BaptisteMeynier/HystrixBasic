package org.netflix.hystrix;




import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.netflix.hystrix.domain.Book;
import org.netflix.hystrix.producer.LoggerProducer;
import org.netflix.hystrix.rest.BookApplication;
import org.netflix.hystrix.rest.BookEndpoint;

import com.github.tomakehurst.wiremock.junit.WireMockRule;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.Assert.assertEquals;


@RunWith(Arquillian.class)
@RunAsClient
public class BookHystrixClientTest {

    // ======================================
    // =             Attributes             =
    // ======================================
    private Client client;
    private WebTarget bookTarget;
	
    @Rule
	public WireMockRule wireMockRule = new WireMockRule(8089); // No-args constructor defaults to port 8080

    // ======================================
    // =          Injection Points          =
    // ======================================

    @ArquillianResource
    private URI baseURL;

    // ======================================
    // =         Deployment methods         =
    // ======================================

    @Deployment(testable = false)
    public static WebArchive createDeployment() {

        return ShrinkWrap.create(WebArchive.class)
                .addClasses(BookApplication.class,BookEndpoint.class, Book.class)
                .addClasses(LoggerProducer.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    // ======================================
    // =          Lifecycle methods         =
    // ======================================

    @Before
    public void initWebTarget() {
        client = ClientBuilder.newClient();
        
        bookTarget = client.target(baseURL).path("api").path("books");
    }

    // ======================================
    // =            Test methods            =
    // ======================================

    @Test
    public void shouldFindAll() throws Exception {
    	stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200).withFixedDelay(2000)));
        Response response = bookTarget
        		.request(MediaType.APPLICATION_JSON_TYPE)
        		.get();

        assertEquals(200, response.getStatus());
    }

}