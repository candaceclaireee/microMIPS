package model;

public class Instruction {
	protected String name;	
	protected String offset; 
	protected String memoryoffset;
	protected int base; 
	protected int rt; 
	protected int rs; 
	protected int rd; 
	protected int immediate; 
	
	protected String finalopcode; 
	protected String finalhexopcode;
		
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
	public int getImmediate() {
		return immediate;
	}
	public void setImmediate(int immediate) {
		this.immediate = immediate;
	}
}
