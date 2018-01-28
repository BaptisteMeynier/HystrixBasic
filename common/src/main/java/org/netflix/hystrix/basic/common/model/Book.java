package org.netflix.hystrix.basic.common.model;



public class Book {

	private String name;
	private float price;
	private int publication;

	public Book() {}

	public Book(String name, float price, int publication) {
		super();
		this.name = name;
		this.price = price;
		this.publication = publication;
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
	public int getPublication() {
		return publication;
	}
	public void setPublication(int publication) {
		this.publication = publication;
	}

}
