package org.netflix.hystrix.customer.client;


import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.netflix.hystrix.basic.common.model.Book;
import org.netflix.hystrix.basic.customer.MainBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThat;

import java.util.stream.Stream;




@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainBootstrap.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookResourceTest {


	@Autowired
	private TestRestTemplate restTemplate;

	@Before
	 public void init() {
   	restTemplate.getRestTemplate().getMessageConverters().add(new StringHttpMessageConverter());
	}
	
    @Test
    public void serverCallTest() {   	
    	ResponseEntity<Object> responseEntity  = restTemplate.getForEntity("/api/v1/books", Object.class);
    //	ResponseEntity<Book[]> responseEntity  = restTemplate.getForEntity("/api/v1/books", Book[].class);
		assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    }


    

}
