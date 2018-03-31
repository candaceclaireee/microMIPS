package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Errors {

	private static ObservableList<String> errors = FXCollections.observableArrayList();

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
}
