package mainproj;

import java.net.URL;
import java.util.*;
import javax.swing.JOptionPane;
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
	private GridPane GPGrid, MemCodeGrid, MemDataGrid, CyclesGrid;
	@FXML
	private ScrollPane GPPane, MemCodePane, MemDataPane, CyclesPane;
	
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
		try {
			initializeData(datastart, dataend);
			initializeInstructions(datastart, dataend, codestart, codeend);
			initializeMemoryCode();
			runCycles();
		} catch(Exception e) {
			System.out.println("Code empty");
		}
	}

	private void initializeData(int datastart, int dataend) {
		String type = "";
		String label = "";
		String code = "";
		String newData = "";
		boolean invalid = false;
		String nextStr = "";

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
				data[1] = "DNE";
			}
			
//			System.out.println("Type: " + type );
//			System.out.println("label: " + label );
//			System.out.println("code: " + code );
			
			//assign to memorydatalist
			for (int m=0; m<Lists.getMemoryData().size(); m++){
				if (Lists.getMemoryData().get(m).isTaken() == false){ //not yet taken
					//check data type
					if (type.trim().equalsIgnoreCase(".byte")){
						System.out.println("enter byte");
						
						//check if multiple or single data
						if (data[1].equalsIgnoreCase("DNE")){ //single
//							System.out.println("enter DNE");
							
							if (data[0].contains("0x")){
								data[0]= util.removeExtrasHex(data[0]);
							}
							else {
								data[0]= util.decToHex(data[0].replaceAll("\\s+","")).toUpperCase();
							}
							
							if (data[0].trim().length() >= 3){
								Lists.addError("Invalid data found!");
//								System.out.println(data[0]+" Invalid data found!");
							}
							else {
								Lists.getMemoryData().get(m).setCode(code);
								Lists.getMemoryData().get(m).setLabel(label);
								Lists.getMemoryData().get(m).setType(type);
//								System.out.println("newData: "+data[0]);
//								System.out.println("added to "+Lists.getMemoryData().get(m));
								Lists.getMemoryData().get(m).setData(util.padZeros(data[0], 16));
								Lists.getMemoryData().get(m).setTaken(true);
//								System.out.println("added to "+Lists.getMemoryData().get(m));
							}
							break;
						}
						else { //multiple
//							System.out.println("enter multiple ");
//							for (int n=0; n<data.length; n++){
//								System.out.println(data[n]);
//							}
							
							for (int d=(data.length-1); d>=0; d--){ //assign every data
								if (data[d].contains("0x")){
									data[d]= util.removeExtrasHex(data[d]);
								}
								else {
									data[d]= util.padZeros(util.decToHex(data[d].replaceAll("\\s+","")).toUpperCase(), 2);
								}
								
								if (data[d].length() >= 3){
									Lists.addError("Invalid data found!");
//									System.out.println(data[d]+" Invalid data found!");
									invalid = true;
									break;
								}
								else {
									newData = newData + data[d];
//									System.out.println("newData: "+newData);
								}
							}
							
							if (invalid){
								
							}
							else{
								if(newData.length()>16){
									do {									
										nextStr = newData.substring(0, newData.length()-16); 	//next
										newData = newData.substring(newData.length()-16, newData.length());					//first
										
										//assign first
										Lists.getMemoryData().get(m).setCode(code);
										Lists.getMemoryData().get(m).setLabel(label);
										Lists.getMemoryData().get(m).setType(type);
										Lists.getMemoryData().get(m).setData(newData);
										Lists.getMemoryData().get(m).setTaken(true);
										m++;
										newData = nextStr;
										
										if (nextStr.length()<16){
											//assign first
											Lists.getMemoryData().get(m).setCode(code);
											Lists.getMemoryData().get(m).setLabel(label);
											Lists.getMemoryData().get(m).setType(type);
											Lists.getMemoryData().get(m).setData(util.padZeros(newData, 16));
											Lists.getMemoryData().get(m).setTaken(true);
										}
									} while (nextStr.length()>16);	
									newData = "";
								}
								else {
									Lists.getMemoryData().get(m).setCode(code);
									Lists.getMemoryData().get(m).setLabel(label);
									Lists.getMemoryData().get(m).setType(type);
									Lists.getMemoryData().get(m).setData(util.padZeros(newData, 16));
									Lists.getMemoryData().get(m).setTaken(true);
									newData= "";
								}
							}							
							break;
						}
						
					}
					else if (type.trim().equalsIgnoreCase(".word") || type.trim().equalsIgnoreCase(".word64")){
						System.out.println("enter word");
						//check if multiple or single data
						if (data[1].equalsIgnoreCase("DNE")){ //single
							System.out.println("enter DNE");
							
							if (data[0].contains("0x")){
								data[0]= util.removeExtrasHex(data[0]);
							}
							else {
								data[0]= util.decToHex(data[0].replaceAll("\\s+","")).toUpperCase();
							}
							
							if (data[0].trim().length() >= 17){
								Lists.addError("Invalid data found!");
//								System.out.println(data[0]+" Invalid data found!");
							}
							else {
								Lists.getMemoryData().get(m).setCode(code);
								Lists.getMemoryData().get(m).setLabel(label);
								Lists.getMemoryData().get(m).setType(type);
//								System.out.println("newData: "+data[0]);
//								System.out.println("added to "+Lists.getMemoryData().get(m));
								Lists.getMemoryData().get(m).setData(util.padZeros(data[0], 16));
								Lists.getMemoryData().get(m).setTaken(true);
//								System.out.println("added to "+Lists.getMemoryData().get(m));
							}
							break;
						}
						else { //multiple
							
							for (int d=0; d<data.length; d++){
								if (data[d].contains("0x")){
									data[d]= util.removeExtrasHex(data[d]);
								}
								else {
									data[d]= util.decToHex(data[d].replaceAll("\\s+","")).toUpperCase();
								}
								
								if (data[d].trim().length() >= 17){
									Lists.addError("Invalid data found!");
//									System.out.println(data[0]+" Invalid data found!");
									break;
								}
								
								//assign first
								Lists.getMemoryData().get(m).setCode(code);
								Lists.getMemoryData().get(m).setLabel(label);
								Lists.getMemoryData().get(m).setType(type);
								Lists.getMemoryData().get(m).setData(util.padZeros(data[d], 16));
								Lists.getMemoryData().get(m).setTaken(true);
								m++;
							}
							
							break;
						}
					}
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
		for(int i = 0 ; i < 32 ; i++) {
			Register r = new Register(i, "0000000000000000");
			Lists.addRegister(r);
		}
		initializeRegisters();
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
		
		for (int j=0; j<Lists.getInstructions().size(); j++) {
			Instruction i = Lists.getInstructions().get(j);
			
			if(i instanceof JType) {
				((JType) i).buildOpcode();
			} else if (i instanceof IType) {
				if (((IType) i).checkForErrors() == false)
					((IType) i).buildOpcode();
			} else if (i instanceof RType) {
				if (((RType) i).checkForErrors() == false)
					((RType) i).buildOpCode();
			}
		}
	}
	
	public void initializeRegisters() {
		GPGrid.getChildren().clear();
		
		for(int i = 0 ; i < Lists.getRegisters().size() ; i++) {
			Register r = Lists.getRegisters().get(i);
			
			Button b1 = new Button("R" + r.getNumber() + " =  "+ r.getContent());
			b1.setMinWidth(GPPane.getWidth());
			b1.setAlignment(Pos.CENTER_LEFT);
			b1.setStyle("-fx-background-color: transparent");
	
			b1.setOnMouseClicked(new EventHandler<MouseEvent>() { 
				public void handle(MouseEvent event) {
					String buttonText[] = b1.getText().replaceAll("\\s+","").split("=");
					int registerNum = Integer.parseInt(buttonText[0].replace("R", ""));
					String registerContent = JOptionPane.showInputDialog("Type content for register " + registerNum);
					
					if (registerContent.replaceAll("\\s+","").length() > 16 || !registerContent.matches("[0-9A-F]+")) { 
						JOptionPane.showMessageDialog(null, "Please enter a valid input! ", "Error", JOptionPane.ERROR_MESSAGE);		
					} else {
						registerContent = util.padZeros(registerContent, 16);
						
						for(int j=0; j<Lists.getRegisters().size(); j++) {
							Register r = Lists.getRegisters().get(j);
							if (r.getNumber() == registerNum) {
								r.setContent(registerContent);
							}
						}
						processTextArea();
						initializeRegisters();
					}
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
		setMemoryCode();
		currentAddress+=4;
						
		for (int i = 0; i< Lists.getMemoryCodes().size(); i++){
			MemoryCode currentItem = Lists.getMemoryCodes().get(i);
		
			Button b1 = new Button(currentItem.getAddress().toUpperCase() + " || " + currentItem.getOpcode() + " || " + currentItem.getInstruction());
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
	public void runCycles() {
		for(int i = 0;i <= Lists.getMemoryCodes().size() ; i++) {
			//List
			
			
		}
		
		
	}
	
	
}