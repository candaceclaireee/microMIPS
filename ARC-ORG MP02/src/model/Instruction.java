package model;

import java.math.BigInteger;

public class Instruction {
	protected String code;
	protected String codeLine;
	protected String name;	
	
	protected String offset; 
	protected int base; 
	protected int rt; 
	protected int rs; 
	protected int rd; 
	protected String immediate; 
	protected String jumpAddress = "0000";

	protected String finalopcode; 
	protected String finalhexopcode;
	
	protected Utilities util;
	
	public Instruction () {
		util = new Utilities();
	}

	// GETTERS AND SETTERS
	public String getCodeLine() {
		return codeLine;
	}
	public void setCodeLine(String codeLine) {
		this.codeLine = codeLine;
	}
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getJumpAddress() {
		return jumpAddress;
	}
	public void setJumpAddress(String address) {
		jumpAddress = util.hexToBin(address);
	}
	public String getFinalopcode() {
		return finalopcode;
	}
	public void setFinalopcode(String finalopcode) {
		this.finalopcode = finalopcode;
	}
	public String getFinalhexopcode() {
		return finalhexopcode;
	}
	public void setFinalhexopcode(String finalhexopcode) {
		this.finalhexopcode = finalhexopcode;
	}
}
