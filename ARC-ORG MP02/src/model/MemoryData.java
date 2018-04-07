package model;

public class MemoryData {
	private String address; 
	private String data; 
	private String label;
	private String code;
	private String type;
	private Instruction struct;
	private boolean taken;
	
	public MemoryData (String address) {
		//insert needs
		this.address = address;
		this.data = "0000000000000000";
		this.label = "label";
		this.code = "code";
		this.taken = false;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Instruction getStruct() {
		return struct;
	}
	public void setStruct(Instruction struct) {
		this.struct = struct;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isTaken() {
		return taken;
	}

	public void setTaken(boolean taken) {
		this.taken = taken;
	}
	
	
}
