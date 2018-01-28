package org.netflix.hystrix.basic.book.finder.resource;


import org.netflix.hystrix.basic.book.finder.service.BookService;
import org.netflix.hystrix.basic.common.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.util.List;
import java.util.Optional;

@Component
@Path("books")
@Produces(MediaType.APPLICATION_JSON)
public class BookResource {

	@Autowired
	private BookService bookService;

	@GET
	public Response findAll() {
		List<Book> books = bookService.findAll();
		return Response.ok(books).build();
	}

	@GET
	public Response findByName(@QueryParam("name") final String name) {
		ResponseBuilder res = Response.noContent();
		Optional<Book> book = bookService.findByName(name);
		if(book.isPresent()) {
			res =Response.ok(book.get());	
		}
		return res.build();    	
	}
}