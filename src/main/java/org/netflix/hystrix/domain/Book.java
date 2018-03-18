package org.netflix.hystrix.domain;

public class Book {

	private String name;
	private float price;
	
	public Book() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Book(String name,float price) {
		super();
		this.name = name;
		this.price = price;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	
	
}
