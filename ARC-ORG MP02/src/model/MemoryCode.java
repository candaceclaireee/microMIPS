package model;

public class MemoryCode {
	private String address; 
	private String opcode; 
	private String instruction;
	
	
	public MemoryCode(String address, String opcode, String instruction) {
		this.address = address; 
		this.opcode = opcode; 
		this.instruction = instruction;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getOpcode() {
		return opcode;
	}
	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	
}
