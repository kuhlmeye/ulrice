package net.ulrice.sample.module.databinding;

public class Person {

    public String firstName;
    public String lastName;
    
    public int age;
    
    public Person() {
    	
    }
    
    public Person(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
}