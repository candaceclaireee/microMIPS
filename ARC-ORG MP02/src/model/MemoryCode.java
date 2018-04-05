package model;

public class MemoryCode {
	private String address; 
	private String opcode; 
	private String instruction;
	private Instruction struct;
	
	public MemoryCode(Instruction struct) {
		this.struct = struct;
		this.opcode = struct.getFinalhexopcode();
		this.instruction = struct.getCode();
	}
	
	public Instruction getStruct() {
		return struct;
	}
	public void setStruct(Instruction struct) {
		this.struct = struct;
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
