package net.ulrice.sample.module.databinding;

public class Person {

    public String firstName;
    public String lastName;
    public String address;
    
    public int age;
    
    public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
    
    public void setLastName(String lastName) {
		this.lastName = lastName;
	}
    
    public String getFirstName() {
		return firstName;
	}
    
    public String getLastName() {
		return lastName;
	}
    
    public int getAge() {
		return age;
	}
    
    public void setAge(int age) {
		this.age = age;
	}
    
    public String getAddress() {
		return address;
	}
    
    public void setAddress(String address) {
		this.address = address;
	}
}