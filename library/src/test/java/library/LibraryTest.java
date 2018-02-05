package library;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.netflix.hystrix.basic.book.finder.BookBootstrap;
import org.netflix.hystrix.basic.common.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;



@RunWith(SpringRunner.class)
@SpringBootTest(classes =BookBootstrap.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LibraryTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void shouldFindAllBooks()throws Exception {
		ResponseEntity<Book[]> responseEntity  = this.restTemplate.getForEntity("/api/v1/books", Book[].class);
		Book[] books = responseEntity.getBody();
		assertThat(books).isNotEmpty();
		Stream.of(books).forEach(System.out::println);
	}
	
	@Test
	public void shouldFindBook()throws Exception {
		Book book = this.restTemplate.getForObject("/api/v1/books/Zaire", Book.class);
		assertThat(book.getName()).isEqualTo("Zaire");
		System.out.println(book.toString());
	}


}
