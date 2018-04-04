package mainproj;

import java.net.URL;
import java.util.*;

import javax.swing.JOptionPane;

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
	private Button runButton;

	@FXML
	private GridPane GPGrid, MemCodeGrid, MemDataGrid;
	@FXML
	private ScrollPane GPPane, MemCodePane, MemDataPane;
	
	/////////////////  OTHER variables
	public static ArrayList<Instruction> instructions = new ArrayList<Instruction>();
	String codeRead[]; 
	
	////////////////   FXML functions
	@FXML
	public void processTextArea() {
		Lists.clearErrors();
		Lists.clearOpcode();
		Lists.clearMemoryCodes();
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
		errorsListView.setItems(Lists.getErrors());
		opcodeListView.setItems(Lists.getOpcodes());
		initializeMemoryCode();
	}

	////////////////   OTHER functions
	public void initialize(URL url, ResourceBundle rb) {
		initializeRegisters();
		//initializeMemoryData();
	}
	
	public void initializeRegisters() {
		for(int i = 0 ; i < 31 ; i++) {
			Register r = new Register(i+1, "0000000000000000");
			Lists.addRegister(r);
			
			Button b1 = new Button("R" + r.getNumber() + " =  "+ r.getContent());
			b1.setMinWidth(GPPane.getWidth());
			b1.setStyle("-fx-background-color: transparent");

			b1.setOnMouseClicked(new EventHandler<MouseEvent>() { 
				public void handle(MouseEvent event) {
					String buttonText[] = b1.getText().replaceAll("\\s+","").split("=");
					String register = JOptionPane.showInputDialog("Type content for register " + buttonText[0] );
					
					//ERROR CHECKING BEFORE CHANGING REGISTER CONTENT(length, all numbers etc)
				}
			});	
			b1.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					b1.setStyle("-fx-border-color: blue");
				
				}
				
			});
			b1.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					b1.setStyle("-fx-background-color: transparent");
				}
				
			});
			GPGrid.add(b1, 0, i);
		}
	}

	public void initializeInstructions(int ds, int de, int cs, int ce) {

		for (int i = cs; i <= ce ; i++) {
			String code = codeRead[i].toUpperCase();
			
			if (code.contains("BC")) {
				JType j = new JType(code);
				instructions.add(j);
				Lists.addMemoryCode(new MemoryCode(j.getFinalhexopcode(), code));
			} else if (code.contains("DADDU")) {
				RType r = new RType(code); 
				instructions.add(r);
				Lists.addMemoryCode(new MemoryCode(r.getFinalhexopcode(), code));
			} else if (code.contains("DADDIU") || code.contains("XORI") || code.contains("LD") ||
					    code.contains("SD") || code.contains("BLTC")) {
				IType it = new IType(code); 
				instructions.add(it);
				Lists.addMemoryCode(new MemoryCode(it.getFinalhexopcode(), code));
			} else {
				int j = i + 1;
				Lists.addError("Invalid instruction on line " + j);
			}
		}
	}

	public void initializeMemoryCode() {
			int currentAddress = 256; 
			MemCodeGrid.getChildren().clear();
			for (int i = 0; i< Lists.getMemoryCodes().size(); i++) {
				MemoryCode currentItem = Lists.getMemoryCodes().get(i);
				currentItem.setAddress("0" + Integer.toHexString(currentAddress));
				
				Button b1 = new Button(currentItem.getAddress().toUpperCase() + " " + currentItem.getOpcode() + "  " + currentItem.getInstruction());
				b1.setMinWidth(MemCodePane.getWidth());
				b1.setStyle("-fx-background-color: transparent");

				b1.setOnMouseEntered(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						b1.setStyle("-fx-border-color: blue");
					
					}
					
				});
				b1.setOnMouseExited(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						b1.setStyle("-fx-background-color: transparent");
					}
					
				});
				MemCodeGrid.add(b1, 0, i);  
				currentAddress += 4;
			}			
	}
}
