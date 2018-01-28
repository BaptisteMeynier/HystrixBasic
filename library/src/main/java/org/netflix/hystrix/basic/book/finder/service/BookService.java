package org.netflix.hystrix.basic.book.finder.service;


import org.netflix.hystrix.basic.common.model.Book;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class BookService {

	private static List<Book> library = new ArrayList(); 

	static {
		library.add(new Book("OEdipe",15,1718));
		library.add(new Book("La Henriade",5,1723));
		library.add(new Book("Histoire de Charles XII",25,1731));
		library.add(new Book("Zaïre",10,1732));
		library.add(new Book("Le Temple du Goût",2,1733));
		library.add(new Book("Lettres Philosophiques",8,1734));
		library.add(new Book("Traité de métaphysique",10,1735));
	}

	public Optional<Book> findByName(String name) {
		Optional<Book> result = Optional.empty();
		if(Objects.nonNull(name)) {
		result = library
				.stream()
				.filter(book->{return name.equalsIgnoreCase(book.getName());})
				.findFirst();
		}
		return result;
	}

	public List<Book> findAll() {
		return library;
	}

}

