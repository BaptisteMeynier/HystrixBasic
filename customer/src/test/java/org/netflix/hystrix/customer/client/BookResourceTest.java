package org.netflix.hystrix.customer.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.netflix.hystrix.basic.common.model.Book;
import org.netflix.hystrix.basic.customer.config.ClientJersey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.netflix.hystrix.basic.common.constantes.Constantes.BOOK_PATH;
import static org.netflix.hystrix.basic.common.constantes.Constantes.BOOK_REMOTE_URI;
import static org.netflix.hystrix.basic.common.constantes.Constantes.HTTP_PORT;

import java.util.stream.Stream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

public class BookResourceTest {

   
    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(HTTP_PORT);

    @Rule
    public WireMockClassRule mockedService = wireMockRule;

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
	private String url = "http://localhost:8080/api/v1/books";
	private RestTemplate restTemplate;
	
	@Before
	 public void init() {
		restTemplate = new RestTemplate();
    	// Add the String message converter
    	restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
	}
	
    @Test
    public void test() {
    	mockedService.stubFor(WireMock.any(WireMock.urlMatching(""))
        		.willReturn(WireMock.aResponse().withStatus(Response.Status.OK.getStatusCode()).withBody("ABook")));
    }

    @Test
    public void serverCallTest() {
    	mockedService.stubFor(WireMock.any(WireMock.urlMatching(".*/api/.*"))
        		.willReturn(WireMock.aResponse().withStatus(Response.Status.OK.getStatusCode())));	
    	
    	ResponseEntity<Book[]> responseEntity  = restTemplate.getForEntity(url, Book[].class);
		assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    }
    
 
    @Test
    public void serverCallTestWithTimeout() {

    	mockedService.stubFor(WireMock.any(WireMock.urlMatching(".*/api/.*"))
        		.willReturn(WireMock.aResponse().withStatus(Response.Status.OK.getStatusCode()).withFixedDelay(2000))
        );
		    	
    	ResponseEntity<Book[]> responseEntity  = restTemplate.getForEntity(url, Book[].class);
		assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void serverCallTestUriFull(){

    	mockedService.stubFor(WireMock.any(WireMock.urlMatching(".*api/v1/books.*"))
        		.willReturn(WireMock.aResponse().withStatus(Response.Status.OK.getStatusCode()))
        );
		    	
    	ResponseEntity<Book[]> responseEntity  = restTemplate.getForEntity(url, Book[].class);
		assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    }
    
    @Test
    public void serverCallTestUriEmpty(){
    	 exception.expect(HttpClientErrorException.class);
    	mockedService.stubFor(WireMock.any(WireMock.urlMatching(""))
        		.willReturn(WireMock.aResponse().withStatus(Response.Status.OK.getStatusCode()))
        );
		    	
    	ResponseEntity<Book[]> responseEntity  = restTemplate.getForEntity(url, Book[].class);
		assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
    
    @Test
    public void serverCallTestUriWildCard(){

    	mockedService.stubFor(WireMock.any(WireMock.urlMatching(".*"))
        		.willReturn(WireMock.aResponse().withStatus(Response.Status.OK.getStatusCode()))
        );
		    	
    	ResponseEntity<Book[]> responseEntity  = restTemplate.getForEntity(url, Book[].class);
		assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    }
    
    @Test
    public void serverCallTestTwiceMock(){

    	mockedService.stubFor(WireMock.any(WireMock.urlMatching(".*"))
        		.willReturn(WireMock.aResponse().withStatus(Response.Status.OK.getStatusCode()))
        );
		    	
    	ResponseEntity<Book[]> responseEntity  = restTemplate.getForEntity(url, Book[].class);
		assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
		
    	ResponseEntity<Book[]> responseEntity2  = restTemplate.getForEntity(url, Book[].class);
		assertThat(responseEntity2.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    }
    
    
    @Test
    public void serverCallTestUriBuilder(){

    	mockedService.stubFor(WireMock.any(WireMock.urlMatching(".*"))
        		.willReturn(WireMock.aResponse().withStatus(Response.Status.OK.getStatusCode()))
        );
    	Client client = ClientBuilder.newClient();
    	final WebTarget target = client.target(BOOK_REMOTE_URI).path(BOOK_PATH);
    	Response response = target.request(APPLICATION_JSON_TYPE).get();

		assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(HttpStatus.OK.value());

    }
}
