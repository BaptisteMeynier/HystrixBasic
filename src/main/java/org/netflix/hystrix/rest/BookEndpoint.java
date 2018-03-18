package org.netflix.hystrix.rest;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.netflix.hystrix.domain.Book;




@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookEndpoint {

	@Inject
	private Logger log;

	private List<Book> library = 
			Stream.of(
					new Book("La Metamorphose",10),
					new Book("La Colonie penitentiaire",8),
					new Book("Le Proces",5),
					new Book("Le Chateau",5),
					new Book("L'Amerique",2)
					).collect(Collectors.toList()); 


	@GET
	public Response findAllBooks() {
		log.info("findAllBooks");
		if (this.library == null)
			return Response.status(NOT_FOUND).build();

		return Response.ok(this.library).build();
	}

	@GET
	@Path("/{name}")
	public Response findBookByName(@PathParam("name") @NotNull final String name) {
		log.info("findBookByName");
		Optional<Book> book = this.library.stream().filter(aBook->{return aBook.getName().equalsIgnoreCase(name);}).findFirst();

		if (!book.isPresent())
			return Response.status(NOT_FOUND).build();

		return Response.ok(book.get()).build();
	}

}
