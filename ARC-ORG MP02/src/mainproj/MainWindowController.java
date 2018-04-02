package mainproj;

import java.net.URL;
import java.util.*;

import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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
	String codeRead[];
	
	//////////////// PUTANG INA MO GIT//////////////////////////////////////////////
	@FXML
	private GridPane GPGrid;
	@FXML
	private GridPane FPGrid;
	@FXML
	private ScrollPane GPPane;
	@FXML
	private ScrollPane FPPane;
	
	private ArrayList<String> GPregList = new ArrayList<String>();
	private ArrayList<String> FPregList = new ArrayList<String>();
	 
	
	////////////////   FXML functions
	@FXML
	public void processTextArea() {
		Errors.clearErrors();
		Opcodes.clearOpcode();
		instructions.clear();
		
		int datastart = 0;
		int dataend = 0; 
		int codestart =0; 
		int codeend =0;
		
		codeRead = codeTextArea.getText().split("[\\r\\n]+");
		// ^ splits the code by every newline (removes blank lines)
		
		for (int i = 0; i < codeRead.length; i++)	{
			if (codeRead[i].replaceAll("\\s+","").equalsIgnoreCase(".data")) {
				// range of .data
				datastart = i+1;								
			} else if (codeRead[i].replaceAll("\\s+","").equalsIgnoreCase(".code") || codeRead[i].replaceAll("\\s+","").equalsIgnoreCase(".text")) {
				// range of .code or .text
				dataend = i-1;
				codestart = i+1;
				codeend = codeRead.length-1;
			} 
		}
		initializeInstructions(datastart, dataend, codestart, codeend);
		errorsListView.setItems(Errors.getErrors());
		opcodeListView.setItems(Opcodes.getOpcodes());
	}

	////////////////   OTHER functions
	public void initialize(URL url, ResourceBundle rb) {
		initializeRegisters();
	}
	
	public void initializeRegisters() {
		for(int i = 0 ; i < 32 ; i++) {
			GPregList.add("0000000000000000");
			FPregList.add("0000000000000000");
			
			Button b1 = new Button("R" +i+ " = "+GPregList.get(i));
			Button b2 = new Button("F"+i+  "=" +FPregList.get(i));
			
			b1.setMinWidth(GPPane.getWidth());
			b1.setStyle("-fx-background-color: transparent");
			setButtonMouseAction(b1);
			
			b2.setMinWidth(FPPane.getWidth());
			b2.setStyle("-fx-background-color: transparent");
			setButtonMouseAction(b2);
			
			GPGrid.add(b1, 0, i);
			FPGrid.add(b2, 0, i);
		}
		
	}
	public void setButtonMouseAction(Button b) {
		b.setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				b.setStyle("-fx-border-color: blue");
			
			}
			
		});
		b.setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				b.setStyle("-fx-background-color: transparent");
			}
			
		});
		
		
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
				Errors.addError("Invalid instruction on line " + j);
			}
		}
	}
}
