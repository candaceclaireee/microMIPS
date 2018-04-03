package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Lists {

	private static ObservableList<String> errors = FXCollections.observableArrayList();
	private static ObservableList<String> opcodes = FXCollections.observableArrayList();

	private static ObservableList<Register> registers = FXCollections.observableArrayList();
	private static ObservableList<MemoryCode> memorycodeaddresses = FXCollections.observableArrayList();
	
	///////////////////////////////////////////////ERRORS
	public static void addError(String e) {
		errors.add(e);
	}
	
	public static void removeError(String e) {
		errors.remove(e);
	}
	
	public static ObservableList<String> getErrors(){
		return errors;
	}

	public static void clearErrors() {
		errors.clear();
	}
	///////////////////////////////////////////////OPCODES
	public static void addOpcode(String e) {
		opcodes.add(e);
	}
	
	public static void removeOpcode(String e) {
		opcodes.remove(e);
	}
	
	public static ObservableList<String> getOpcodes(){
		return opcodes;
	}

	public static void clearOpcode() {
		opcodes.clear();
	}
	///////////////////////////////////////////////REGISTERS
	public static void addRegister(Register r) {
		registers.add(r);
	}
	
	public static void removeRegister(Register r) {
		registers.remove(r);
	}
	
	public static ObservableList<Register> getRegisters(){
		return registers;
	}

	public static void clearRegisters() {
		registers.clear();
	}
	///////////////////////////////////////////////MEMORY CODE ADDRESSES
	public static void addMemoryCode(MemoryCode m) {
		memorycodeaddresses.add(m);
	}
	
	public static void removeMemoryCode(MemoryCode m) {
		memorycodeaddresses.remove(m);
	}
	
	public static ObservableList<MemoryCode> getMemoryCodes(){
		return memorycodeaddresses;
	}

	public static void clearMemoryCodes() {
		memorycodeaddresses.clear();
	}
}
