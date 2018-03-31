package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Opcodes {

	private static ObservableList<String> opcodes = FXCollections.observableArrayList();

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

}
