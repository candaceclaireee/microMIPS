package mainproj;

import java.net.URL;
import java.util.*;
import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
		Lists.clearMemoryData();
		Lists.clearErrors();
		Lists.clearOpcode();
		Lists.clearMemoryCodes();
		Lists.clearCycles();
		Lists.clearInstructions();
		Lists.clearRegisters();

		
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
			for(int i = 0 ; i < 32 ; i++) {
			Register r = new Register(i, "0000000000000000");
			Lists.addRegister(r);
			}
			initializeRegisters();
			initializeMemoryData();
			initializeData(datastart, dataend);
			initializeInstructions(datastart, dataend, codestart, codeend);
			initializeMemoryCode();
			runCycles();
		} catch(Exception e) {
			System.out.println("Code empty");
		}
	}

	public void specialProcessTextArea() { //FOR MANUALLY INPUT OF REGISTERS
		Lists.clearMemoryData();
		Lists.clearErrors();
		Lists.clearOpcode();
		Lists.clearMemoryCodes();
		Lists.clearCycles();
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
			for(int i = 0 ; i < 32 ; i++) {
			Register r = new Register(i, "0000000000000000");
			Lists.addRegister(r);
			}
			initializeRegisters();
			initializeMemoryData();
			initializeData(datastart, dataend);
			initializeInstructions(datastart, dataend, codestart, codeend);
			initializeMemoryCode();
			runCycles();
		} catch(Exception e) {
			System.out.println("Code empty");
		}
	}
	
	////////////////   OTHER functions
	public void initialize(URL url, ResourceBundle rb) {
		gotoButton.setOnMouseClicked(new EventHandler<MouseEvent>() { 
			public void handle(MouseEvent event) {
				String memoryAddress = JOptionPane.showInputDialog("Type memory address ");
				try {
					if (memoryAddress.replaceAll("\\s+","").length() > 4) {
						JOptionPane.showMessageDialog(null, "Please enter a valid input! ", "Error", JOptionPane.ERROR_MESSAGE);		
					} else {
						for (int i=0; i<Lists.getMemoryData().size(); i++) {
							MemoryData m = Lists.getMemoryData().get(i);
							
							if (memoryAddress.equals(m.getAddress())) {
								JOptionPane.showMessageDialog(null, "Address: " + m.getAddress() + "\nData: " + m.getData()
								 	+ "\n Label: " + m.getLabel() + "\nCode: " + m.getCode(), "\nError", JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}
				} catch (Exception e) {
					System.out.println("Exited/Cancelled");
				}
			}
		});	
		
		for(int i = 0 ; i < 32 ; i++) {
			Register r = new Register(i, "0000000000000000");
			Lists.addRegister(r);
		}
		initializeRegisters();
		initializeMemoryData();
		initCyclesTable();
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
				Lists.addError("ERROR: There is an invalid instruction"); 
			}
		}
		
		for (int j=0; j<Lists.getInstructions().size(); j++) {
			Instruction i = Lists.getInstructions().get(j);
			
			if(i instanceof JType) {
				if (((JType) i).checkForErrors() == false)
				    ((JType) i).buildOpcode();
			} if (i instanceof IType) {
				if (((IType) i).checkForErrors() == false)
					((IType) i).buildOpcode();
			}if (i instanceof RType) {
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
					try {
						if (registerContent.replaceAll("\\s+","").length() > 16 || !registerContent.matches("[0-9A-F]+")) { 
							JOptionPane.showMessageDialog(null, "Please enter a valid input! ", "Error", JOptionPane.ERROR_MESSAGE);		
						} else if (registerNum == 0) {
							JOptionPane.showMessageDialog(null, "Cannot put value in R0! ", "Error", JOptionPane.ERROR_MESSAGE);		
						} else {
							registerContent = util.padZeros(registerContent, 16);
							
							for(int j=0; j<Lists.getRegisters().size(); j++) {
								Register r = Lists.getRegisters().get(j);
								if (r.getNumber() == registerNum) {
									r.setContent(registerContent);
								}
							}
							specialProcessTextArea();
							initializeRegisters();
						}
					} catch (Exception e) {
						System.out.println("Exited/Cancelled");
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
			b1.setMinWidth(MemDataPane.getWidth());
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
	public String xorFunc(String a, String b) {
		String A = (util.hexToBin(a));
		int AinInt = util.binToInt(A);
		String B = (util.hexToBin(b));
		int BinInt = util.binToInt(B);
		return (util.intToHex(BinInt ^ AinInt).toUpperCase());
	}
	public void runCycles() {

		
		String currPointer = "0000000000000100";
		for(int i = 0;i <= Lists.getMemoryCodes().size() ; i++) {
			Cycle curCycle = new Cycle();
			System.out.println(currPointer);
			if(Lists.getMemoryCodes().get(i).getStruct().getName().equalsIgnoreCase("DADDU") && Lists.getMemoryCodes().get(i).getAddress().equals(currPointer.substring(currPointer.length() - 4))) {
				System.out.println("DADDU REACHED");
				MemoryCode m = Lists.getMemoryCodes().get(i);
				curCycle.setINSTRUCTION(m.getStruct().getCode());
				curCycle.setIR(m.getOpcode());
				curCycle.setNPC(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(m.getAddress()) + 4)).toUpperCase(), 16));
				curCycle.setA(Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRs()))).getContent());
				curCycle.setB(Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRt()))).getContent());
				curCycle.setIMM(util.padZeros(m.getOpcode().substring(m.getOpcode().length() - 4), 16));
				curCycle.setALUOUPUT(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(curCycle.getA()) + util.hexToDec(curCycle.getB()))).toUpperCase(), 16));
				curCycle.setCOND(false);
				curCycle.setPC(curCycle.getNPC());
				curCycle.setLMD("N/A");
				curCycle.setRANGE("N/A");
				curCycle.setRN(util.padZeros(curCycle.getALUOUPUT(), 16) + "in R" + m.getStruct().getRd());
				Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRd()))).setContent(util.padZeros(curCycle.getALUOUPUT().toUpperCase(), 16));
				currPointer = curCycle.getPC();
				Lists.addCyles(curCycle);

			} else if (Lists.getMemoryCodes().get(i).getStruct().getName().trim().equalsIgnoreCase("DADDIU") && Lists.getMemoryCodes().get(i).getAddress().equals(currPointer.substring(currPointer.length() - 4))) {
				System.out.println("DADDIU REACHED");
				MemoryCode m = Lists.getMemoryCodes().get(i);
				curCycle.setINSTRUCTION(m.getStruct().getCode());
				curCycle.setIR(m.getOpcode());
				curCycle.setNPC(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(m.getAddress()) + 4)).toUpperCase(), 16));
				curCycle.setA(Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRs()))).getContent());
				curCycle.setB(Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRt()))).getContent());
				curCycle.setIMM(util.padZeros(m.getOpcode().substring(m.getOpcode().length() - 4), 16));
				curCycle.setALUOUPUT(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(curCycle.getA()) + util.hexToDec(curCycle.getIMM()))).toUpperCase(), 16));
				curCycle.setCOND(false);
				curCycle.setPC(curCycle.getNPC());
				curCycle.setLMD("N/A");
				curCycle.setRANGE("N/A");
				curCycle.setRN(util.padZeros(curCycle.getALUOUPUT(), 16)+ "in R" + m.getStruct().getRt());
				Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRt()))).setContent(util.padZeros(curCycle.getALUOUPUT().toUpperCase(), 16));
				currPointer = curCycle.getPC();
				Lists.addCyles(curCycle);
			}else if(Lists.getMemoryCodes().get(i).getStruct().getName().equalsIgnoreCase("XORI") && Lists.getMemoryCodes().get(i).getAddress().equals(currPointer.substring(currPointer.length() - 4))) {
				System.out.println("XORI REACHED");
				MemoryCode m = Lists.getMemoryCodes().get(i);

				curCycle.setINSTRUCTION(m.getStruct().getCode());
				curCycle.setIR(m.getOpcode());
				curCycle.setNPC(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(m.getAddress()) + 4)).toUpperCase(), 16));
				curCycle.setA(Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRs()))).getContent());
				curCycle.setB(Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRt()))).getContent());
				curCycle.setIMM(util.padZeros(m.getStruct().getImmediate(), 16));
				curCycle.setALUOUPUT(util.padZeros(xorFunc(curCycle.getA(), curCycle.getIMM()).toUpperCase(), 16));
				curCycle.setCOND(false);
				curCycle.setPC(curCycle.getNPC());
				curCycle.setLMD("N/A");
				curCycle.setRANGE("N/A");
				curCycle.setRN(util.padZeros(curCycle.getALUOUPUT(), 16)+ "in R" + m.getStruct().getRt());

				Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRt()))).setContent(util.padZeros(curCycle.getALUOUPUT().toUpperCase(), 16));
				currPointer = curCycle.getPC();
				Lists.addCyles(curCycle);


			} else if(Lists.getMemoryCodes().get(i).getStruct().getName().equalsIgnoreCase("SD") && Lists.getMemoryCodes().get(i).getAddress().equals(currPointer.substring(currPointer.length() - 4))) {
				System.out.println("SD REACHED");
				MemoryCode m = Lists.getMemoryCodes().get(i);
				String label = "";

				curCycle.setINSTRUCTION(m.getStruct().getCode());
				curCycle.setIR(m.getOpcode());
				curCycle.setNPC(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(m.getAddress()) + 4)).toUpperCase(), 16));
				curCycle.setA(Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getBase()))).getContent()); //base
				curCycle.setB(Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRt()))).getContent()); //rt
				curCycle.setIMM(util.padZeros(m.getOpcode().substring(m.getOpcode().length() - 4), 16));

				//a, b, imm
				System.out.println("a :"   + Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getBase()))).getContent());
				System.out.println("b :"   + Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRt()))).getContent());
				System.out.println("imm :" + util.padZeros(m.getOpcode().substring(m.getOpcode().length() - 4), 16));

				curCycle.setALUOUPUT(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(curCycle.getA()) + util.hexToDec(curCycle.getIMM()))).toUpperCase(), 16));
				curCycle.setCOND(false);
				curCycle.setPC(curCycle.getNPC());
				curCycle.setLMD("N/A");
				curCycle.setRANGE(m.getStruct().getOffset()+" - " +util.decToHex(Integer.toString(util.hexToDec(m.getStruct().getOffset()) + 7)));
				curCycle.setRN("N/A in R" + m.getStruct().getRt());

				for (int l=0; l<Lists.getMemoryData().size(); l++){
					System.out.println(util.padZeros(Lists.getMemoryData().get(l).getAddress(), 16)+" vs "+curCycle.getALUOUPUT());
					if (util.padZeros(Lists.getMemoryData().get(l).getAddress(), 16).equalsIgnoreCase(curCycle.getALUOUPUT())){
						System.out.println("assign memory address "+Lists.getMemoryData().get(l).getAddress()+" to "+curCycle.getB());
						Lists.getMemoryData().get(l).setData(curCycle.getB());
						System.out.println("updated data: "+Lists.getMemoryData().get(l).getData());
						break;
					}
				}
				currPointer = curCycle.getPC();
				Lists.addCyles(curCycle);
			} else if(Lists.getMemoryCodes().get(i).getStruct().getName().equalsIgnoreCase("LD") && Lists.getMemoryCodes().get(i).getAddress().equals(currPointer.substring(currPointer.length() - 4))) {
				System.out.println("LD REACHED");
				MemoryCode m = Lists.getMemoryCodes().get(i);
				String label = "";

				curCycle.setINSTRUCTION(m.getStruct().getCode());
				curCycle.setIR(m.getOpcode());
				curCycle.setNPC(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(m.getAddress()) + 4)).toUpperCase(), 16));
				curCycle.setA(Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getBase()))).getContent()); //base
				curCycle.setB(Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRt()))).getContent()); //rt
				curCycle.setIMM(util.padZeros(m.getOpcode().substring(m.getOpcode().length() - 4), 16));
				curCycle.setALUOUPUT(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(curCycle.getA()) + util.hexToDec(curCycle.getIMM()))).toUpperCase(), 16));
				curCycle.setCOND(false);
				curCycle.setPC(curCycle.getNPC());

				for (int l=0; l<Lists.getMemoryData().size(); l++){
					if (Lists.getMemoryData().get(l).getAddress().equalsIgnoreCase(m.getStruct().getOffset())){
						curCycle.setLMD(Lists.getMemoryData().get(l).getData());
						break;
					}
				}

				currPointer = curCycle.getPC();
				curCycle.setRANGE("N/A");
				curCycle.setRN(curCycle.getLMD()+" in R"+m.getStruct().getRt());
				Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRt()))).setContent(util.padZeros(curCycle.getLMD().toUpperCase(), 16));

				Lists.addCyles(curCycle);
			} else if(Lists.getMemoryCodes().get(i).getStruct().getName().equalsIgnoreCase("BC") && Lists.getMemoryCodes().get(i).getAddress().equals(currPointer.substring(currPointer.length() - 4))) {
				System.out.println("BC REACHED");
				MemoryCode m = Lists.getMemoryCodes().get(i);

				curCycle.setINSTRUCTION(m.getStruct().getCode());
				curCycle.setIR(m.getOpcode());
				curCycle.setNPC(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(m.getAddress()) + 4)).toUpperCase(), 16));
				curCycle.setA(util.padZeros(util.decToHex(m.getStruct().getFinalopcode().substring(21, 25)), 16));
				curCycle.setB(util.padZeros(util.decToHex(m.getStruct().getFinalopcode().substring(16, 20)), 16));
				curCycle.setIMM(util.padZeros(m.getOpcode().substring(m.getOpcode().length() - 4), 16));
				curCycle.setALUOUPUT(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(m.getAddress()) +  (util.hexToDec(curCycle.getIMM())*4) + 4)), 16));
				curCycle.setCOND(true);
				curCycle.setLMD("N/A");
				curCycle.setRANGE("N/A");
				curCycle.setRN("N/A");
				currPointer = curCycle.getPC();
				if (curCycle.getCOND()) {
					curCycle.setPC(curCycle.getALUOUPUT());
					System.out.println("NEXT IS: "+ curCycle.getALUOUPUT());
				}
				else {
					curCycle.setPC(curCycle.getNPC());
				}
				currPointer = curCycle.getPC();
				Lists.addCyles(curCycle);
			} else if(Lists.getMemoryCodes().get(i).getStruct().getName().equalsIgnoreCase("BLTC") && Lists.getMemoryCodes().get(i).getAddress().equals(currPointer.substring(currPointer.length() - 4))) {
				System.out.println("BLTC REACHED");
				MemoryCode m = Lists.getMemoryCodes().get(i);

				curCycle.setINSTRUCTION(m.getStruct().getCode());
				curCycle.setIR(m.getOpcode());
				curCycle.setNPC(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(m.getAddress()) + 4)).toUpperCase(), 16));
				curCycle.setA(Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRs()))).getContent());
				curCycle.setB(Lists.getRegisters().get(util.hexToDec(Integer.toString(m.getStruct().getRt()))).getContent());
				curCycle.setIMM(util.padZeros(m.getOpcode().substring(m.getOpcode().length() - 4), 16));
				curCycle.setALUOUPUT(util.padZeros(util.decToHex(Integer.toString(util.hexToDec(m.getAddress()) +  (util.hexToDec(curCycle.getIMM())*4) + 4)), 16));

				if(util.hexToDec(curCycle.getA()) > util.hexToDec((curCycle.getB()))) {
					curCycle.setCOND(true);
					curCycle.setPC(curCycle.getALUOUPUT());

				}
				else {
					curCycle.setCOND(false);
					curCycle.setPC(curCycle.getNPC());
				}
				curCycle.setLMD("N/A");
				curCycle.setRANGE("N/A");
				curCycle.setRN("N/A");

				currPointer = curCycle.getPC();
				Lists.addCyles(curCycle);
			}
			showCycles();
			initializeRegisters();

			//REFRESH GUI MEMORY DATA
			MemDataGrid.getChildren().clear();
			for (int k=0; k<Lists.getMemoryData().size(); k++){
				Button b1 = new Button(Lists.getMemoryData().get(k).getAddress().toUpperCase() + " || " +
						Lists.getMemoryData().get(k).getData() + " || " +
						Lists.getMemoryData().get(k).getLabel() + " || " +
						Lists.getMemoryData().get(k).getCode());
				b1.setMinWidth(MemDataPane.getWidth());
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

	}
	public void showCycles(){
		initCyclesTable();
		int j  = 1;

		for(int i = 0; i < Lists.getCycles().size() ; i++){
			Cycle c = Lists.getCycles().get(i);
			CyclesGrid.add(new Label(c.getINSTRUCTION()), j, 0);
			CyclesGrid.add(new Label(c.getIR()), j, 1);
			CyclesGrid.add(new Label(c.getNPC()), j, 2);
			CyclesGrid.add(new Label(c.getA()), j, 3);
			CyclesGrid.add(new Label(c.getB()), j, 4);
			CyclesGrid.add(new Label(c.getIMM()), j, 5);
			CyclesGrid.add(new Label(c.getALUOUPUT()), j, 6);
			CyclesGrid.add(new Label(c.getPC()), j, 7);
			CyclesGrid.add(new Label(c.getLMD()), j, 8);
			CyclesGrid.add(new Label(c.getRANGE()), j, 9);
			CyclesGrid.add(new Label(c.getRN()), j, 10);
			j++;
		}


		ObservableList<Node> childrens = CyclesGrid.getChildren();
		for (Node node : childrens) {
			CyclesGrid.setMargin(node, new Insets(5, 5, 5, 5));

		}


	}
	public void initCyclesTable(){

		CyclesGrid.setPadding(new Insets(2, 2, 2, 2));
		CyclesGrid.setMinSize(0,0);

		CyclesGrid.getChildren().retainAll(CyclesGrid.getChildren().get(0));

		CyclesGrid.add(new Label(" Instruction"), 0, 0);
		CyclesGrid.add(new Label(" IR"), 0, 1);
		CyclesGrid.add(new Label(" NPC"), 0, 2);
		CyclesGrid.add(new Label(" A"), 0, 3);
		CyclesGrid.add(new Label(" B"), 0, 4);
		CyclesGrid.add(new Label(" IMM"), 0, 5);
		CyclesGrid.add(new Label(" ALUOUTPUT"), 0, 6);
		CyclesGrid.add(new Label(" PC"), 0, 7);
		CyclesGrid.add(new Label(" LMD"), 0, 8);
		CyclesGrid.add(new Label(" RANGE"), 0, 9);
		CyclesGrid.add(new Label(" RN"), 0, 10);

		CyclesGrid.setGridLinesVisible(true);
	}



}