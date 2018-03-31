package mainproj;

import java.net.URL;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.control.*;
import model.*;

public class MainWindowController implements Initializable {
	
	/////////////////  FXML variables
	@FXML
	private TextArea codeTextArea;
	@FXML
	private ListView<String> opcodeListView, errorsListView;
	@FXML 
	private Button opcodeButton;
	
	/////////////////  OTHER variables
	public static ArrayList<Instruction> instructions = new ArrayList<Instruction>();
	public static ArrayList<Integer> registers = new ArrayList<Integer>(31);
	ObservableList<String> errors = FXCollections.observableArrayList();
	String codeRead[];
	
	////////////////   FXML functions
	@FXML
	public void processTextArea() {

		instructions.clear();
		
		int datastart = 0;
		int dataend = 0; 
		int codestart =0; 
		int codeend =0;
		
		codeRead = codeTextArea.getText().split("[\\r\\n]+");
		// ^ splits the code by every newline (removes blank lines)
		
		for (int i = 0; i < codeRead.length; i++)	{
			if (codeRead[i].replaceAll("\\s+","").equalsIgnoreCase(".data")) {
				//contains memory declarations & initializations
				datastart = i+1;
				System.out.println(".data range start " + datastart);
								
			} else if (codeRead[i].replaceAll("\\s+","").equalsIgnoreCase(".code") || codeRead[i].replaceAll("\\s+","").equalsIgnoreCase(".text")) {
				//contains the instructions
				dataend = i-1;
				codestart = i+1;
				codeend = codeRead.length-1;
				System.out.println(".data range end " + dataend);
				System.out.println(".code/.text range start " + codestart);
				System.out.println(".code/.text range end " + codeend);
			} 
		}
		initializeInstructions(datastart, dataend, codestart, codeend);
		errorsListView.setItems(errors);
	}

	////////////////   OTHER functions
	public void initialize(URL url, ResourceBundle rb) {
	}
	
	public void initializeInstructions(int ds, int de, int cs, int ce) {

		for (int i = cs; i <= ce ; i++) {
			String code = codeRead[i].toUpperCase();
			
			if (code.contains("BC")) {
				JType j = new JType(code);
				instructions.add(j); 
			} else if (code.contains("DADDU")) {
				RType r = new RType(code); 
				instructions.add(r);
			} else if (code.contains("DADDIU") || code.contains("XORI") || code.contains("LD") ||
					    code.contains("SD") || code.contains("BLTC")) {
				IType it = new IType(code); 
				instructions.add(it);
			} else {
				int j = i + 1;
				errors.add("There is an invalid instruction on line " + j);
			}
		}
	}
	
	
}
