package model;

import java.math.BigInteger;

public class Instruction {
	protected String name;	
	protected String offset; 
	protected String memoryoffset;
	protected int base; 
	protected int rt; 
	protected int rs; 
	protected int rd; 
	protected String immediate; 
	
	protected String finalopcode; 
	protected String finalhexopcode;
	
	// UTILSSS
	public String padZeros(String binary, int n) { 
		if (binary.length() < n){
			for (int i = binary.length(); i<n; i++)
				binary = "0" + binary;
		}
		return binary;		
	}
	
	public String convertBinary(int n) {
		return Integer.toBinaryString(n);
	}
	
	public String convertHex(String bin) {
		return new BigInteger(bin, 2).toString(16);
	}
	
	public int convertInt(String n) {
		return Integer.decode("0x"+n);
	}
	
	// GETTERS AND SETTERS
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}
	public String getMemoryoffset() {
		return memoryoffset;
	}
	public void setMemoryoffset(String memoryoffset) {
		this.memoryoffset = memoryoffset;
	}
	public int getBase() {
		return base;
	}
	public void setBase(int base) {
		this.base = base;
	}
	public int getRt() {
		return rt;
	}
	public void setRt(int rt) {
		this.rt = rt;
	}
	public int getRs() {
		return rs;
	}
	public void setRs(int rs) {
		this.rs = rs;
	}
	public int getRd() {
		return rd;
	}
	public void setRd(int rd) {
		this.rd = rd;
	}
	public String getImmediate() {
		return immediate;
	}
	public void setImmediate(String immediate) {
		this.immediate = immediate;
	}
}
