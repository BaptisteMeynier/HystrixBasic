package org.netflix.hystrix.basic.book.finder.resource;


import org.netflix.hystrix.basic.book.finder.service.BookService;
import org.netflix.hystrix.basic.common.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



import java.util.List;


@RestController
@RequestMapping("/api/v1/books")
public class BookResource {

	@Autowired
	private BookService bookService;

	@RequestMapping(value = "/{name}",	method = RequestMethod.GET)
	public @ResponseBody Book findByName(@PathVariable("name") final String name) {
		return bookService.findByName(name).get();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody List<Book> findAll() {
		return bookService.findAll();
	}
}