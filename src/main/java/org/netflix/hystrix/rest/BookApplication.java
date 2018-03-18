package org.netflix.hystrix.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.netflix.hystrix.rest.BookEndpoint;





@ApplicationPath("/api")
public class BookApplication extends Application{
	@Override
	public Set<Class<?>> getClasses() {
	    Set<Class<?>> s = new HashSet<Class<?>>();
	    s.add(BookEndpoint.class);
	    return s;
	}
	
}
