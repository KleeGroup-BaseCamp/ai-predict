package io.vertigo.ai.dataset;

public class Hero {
	
	private int id;
	private String name;
	private int age;
	
	public Hero() {
		super();
	}
	
	public Hero(int id, String name, int age) {
		setId(id);
		setName(name);
		setAge(age);
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public int getAge() {
		return age;
	}
}
