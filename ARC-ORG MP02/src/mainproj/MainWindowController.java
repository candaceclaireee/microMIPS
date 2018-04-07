package mainproj;

import java.net.URL;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.geometry.Pos;
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
	private Button runButton, gotoButton;

	@FXML
	private GridPane GPGrid, MemCodeGrid, MemDataGrid;
	@FXML
	private ScrollPane GPPane, MemCodePane, MemDataPane;
	
	/////////////////  OTHER variables
	public String codeRead[]; 
	public String fullLines;
	public int currentAddress = 256; 
	public Utilities util;
	
	public MainWindowController (){
		util = new Utilities ();
	}
	
	////////////////   FXML functions
	@FXML
	public void processTextArea() {
		Lists.clearErrors();
		Lists.clearOpcode();
		Lists.clearMemoryCodes();
		Lists.clearInstructions();
		
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
		initializeMemoryCode();
		initializeData(datastart, dataend);
		executeCode();
	}

	private void initializeData(int datastart, int dataend) {
		String type = "";
		String label = "";
		String code = "";

//		System.out.println(datastart + " to "+ dataend);
		for (int i=datastart; i<=dataend; i++){ 
			String [] parts = codeRead[i].split(": "); //split every line 
												 	  //0=label, 1=code
			label = parts[0];
			code =  parts[1];
			
			String [] data = null;
			
//			System.out.println(parts[0] + " AND "+ parts[1]);
			if (code.contains(",")){
				//BETA: .word 0x40, 0x50, 0x60
				data = code.split(",");
				type = data[0].substring(data[0].indexOf("."), data[0].indexOf(" ")+1);
				data[0] = data[0].replaceFirst(type, " ");
			}
			else {
				//ALPHA: .byte 0x30
				data = code.split(" ");
				type = code.substring(code.indexOf("."), code.indexOf(" ")+1);
				data[0] = data[1];
			}
			
			System.out.println("Type: " + type );
			System.out.println("label: " + label );
			System.out.println("code: " + code );
			
			//assign to memorydatalist
			for (int m=0; m<Lists.getMemoryData().size(); m++){
				if (Lists.getMemoryData().get(m).isTaken() == false){ //not yet taken
					Lists.getMemoryData().get(m).setCode(code);
					Lists.getMemoryData().get(m).setLabel(label);
					Lists.getMemoryData().get(m).setType(type);
					
					data[0]= util.padZeros(util.removeExtrasHex(data[0]), 16);
					Lists.getMemoryData().get(m).setData(data[0]);
					Lists.getMemoryData().get(m).setTaken(true);
					break;
				}
			}
			
		}
		
		//REFRESH GUI MEMORY DATA
		MemDataGrid.getChildren().clear();
		for (int k=0; k<Lists.getMemoryData().size(); k++){				
			Button b1 = new Button(Lists.getMemoryData().get(k).getAddress().toUpperCase() + " || " + 
								   Lists.getMemoryData().get(k).getData() + " || " + 
								   Lists.getMemoryData().get(k).getLabel() + " || " + 
								   Lists.getMemoryData().get(k).getCode());
			b1.setMinWidth(MemCodePane.getWidth());
			b1.setAlignment(Pos.CENTER_LEFT);
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
			
			MemDataGrid.add(b1, 0, k); 
		}
	}

	////////////////   OTHER functions
	public void initialize(URL url, ResourceBundle rb) {
		initializeRegisters();
		//initializeMemoryCode(); 
		initializeMemoryData();
	}
	
	public void initializeInstructions(int ds, int de, int cs, int ce) {
		
		for (int i = cs; i <= ce ; i++) {
			String code = codeRead[i].toUpperCase();
		
			if (code.contains("BC")) {
				JType j = new JType(code);
				Lists.addInstruction(j);
			} else if (code.contains("DADDU")) {
				RType r = new RType(code); 
				Lists.addInstruction(r);
			} else if (code.contains("DADDIU") || code.contains("XORI") || code.contains("LD") ||
						    code.contains("SD") || code.contains("BLTC")) {
				IType it = new IType(code); 
				Lists.addInstruction(it);
			} else {
				int j = i + 1;
				Lists.addError("Invalid instruction on line " + j);
			}
		}
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
					String register = JOptionPane.showInputDialog("Type content for register " + buttonText[0]);
					
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
	
	public void initializeMemoryCode() {
			MemCodeGrid.getChildren().clear();
			
			//SET JUMP ADDRESSES FOR BC & BLTC
			for(int k =0; k < Lists.getInstructions().size(); k++) {
				String jumpCode;
				boolean isBC;
				
				if(Lists.getInstructions().get(k).getCode().contains("BC") || Lists.getInstructions().get(k).getCode().contains("BLTC")) {
					if(Lists.getInstructions().get(k).getCode().contains("BC")) {
						jumpCode = Lists.getInstructions().get(k).getCode().split(" ")[1];
						isBC = true;
					}
					else {
						jumpCode = Lists.getInstructions().get(k).getCode().split(",")[3];
						isBC = false;
					}
					for (int i = 0; i< Lists.getMemoryCodes().size(); i++) {
						if(Lists.getMemoryCodes().get(i).getStruct().getCodeLine().contains(jumpCode) && !Lists.getMemoryCodes().get(i).getStruct().getCode().equalsIgnoreCase(Lists.getInstructions().get(k).getCode())) {
							Lists.getInstructions().get(k).setJumpAddress(Lists.getMemoryCodes().get(i).getAddress());
							
							if(isBC)
								((JType)Lists.getInstructions().get(k)).buildOpcode();
							else
								((IType)Lists.getInstructions().get(k)).buildOpcode();
							System.out.println(Lists.getInstructions().get(k).getJumpAddress());
						}
					}	
				} 
			}
			
			setMemoryCode();
			currentAddress+=4;
			
			// ADD TO GUI
			for (int i = 0; i< Lists.getMemoryCodes().size(); i++){
				MemoryCode currentItem = Lists.getMemoryCodes().get(i);
			
				Button b1 = new Button(currentItem.getAddress().toUpperCase() + " / " + currentItem.getOpcode() + " / " + currentItem.getInstruction());
				b1.setMinWidth(MemCodePane.getWidth());
				b1.setAlignment(Pos.CENTER_LEFT);
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
				errorsListView.setItems(Lists.getErrors());
				opcodeListView.setItems(Lists.getOpcodes());
			}	
		}

	public void setMemoryCode() {
		Lists.getMemoryCodes().clear();
		currentAddress = 256;
		
		//INIT MEMORYCODE
		for(int k =0; k < Lists.getInstructions().size(); k++) {
			Lists.addMemoryCode(new MemoryCode(Lists.getInstructions().get(k)));
		}
		
		//SET ADDRESS FOR MEMORY CODE
		for (int i = 0; i< Lists.getMemoryCodes().size(); i++) {
			MemoryCode currentItem = Lists.getMemoryCodes().get(i);
			currentItem.setAddress("0" + Integer.toHexString(currentAddress));
			currentAddress+=4;
		}
	}
	
	public void initializeMemoryData(){
		//FOR .DATA
		Lists.getMemoryData().clear();
		
		//SET ADDRESS FOR MEMORY CODE
		int j=0;
		for (int i = 0; i< 32; i++) {
			String address = "0" + Integer.toHexString(j);
			
			if (address.length() == 1){
				address = util.padZeros(address, 4);
			}
			else if (address.length() == 2) {
				address = util.padZeros(address, 4);
			}
			else if (address.length() == 3) {
				address = util.padZeros(address, 4);
			}
			
			Lists.addMemoryData(new MemoryData(address));
			j+=8;
		}
		
		
		for (int k=0; k<Lists.getMemoryData().size(); k++){			
			Button b1 = new Button(Lists.getMemoryData().get(k).getAddress().toUpperCase() + " || " + 
								   Lists.getMemoryData().get(k).getData() + " || " + 
								   " label " + " || " + 
								   " code ");
			b1.setMinWidth(MemCodePane.getWidth());
			b1.setAlignment(Pos.CENTER_LEFT);
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
			
			MemDataGrid.add(b1, 0, k); 
		}
	}
	
	public void executeCode() {
		//manipulate the memorycode and memorydata observablelists HERE
		
		for (int i=0; i<Lists.getMemoryCodes().size(); i++) {
			MemoryCode m = Lists.getMemoryCodes().get(i);
			
		}		
	}

}


