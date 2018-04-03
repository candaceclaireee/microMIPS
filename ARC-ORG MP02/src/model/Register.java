package model;

public class Register {
	
	private int number; 
	private String content;
	
	public Register (int number, String content) {
		this.number = number; 
		this.content = content; 
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	} 

}
