package model;

import java.math.BigInteger;

public class IType extends Instruction{
	
	public static final String DADDIU_OPCODE = "011001";  // DADDIU OK
	public static final String XORI_OPCODE = "001110"; 
	public static final String LD_OPCODE = "110111"; 
	public static final String SD_OPCODE = "111111";
	public static final String BLTC_OPCODE = "010111";
	
	String codeLine;
		
	public IType(String code) {
		codeLine = code;
		checkForErrors();
		buildOpcode();
	}
	
	public void checkForErrors() {
		if (codeLine.contains("DADDIU") || codeLine.contains("XORI")) {
			String parameter[] = codeLine.replaceAll("\\s+","").split(",");

			if (parameter.length != 3) 
				Errors.addError("Invalid parameters");
			if (!parameter[0].contains("R"))
				Errors.addError("First parameter is not a register");
			if (!parameter[1].contains("R")) 
				Errors.addError("Second parameter is not a register");
			if ( !parameter[2].contains("0X") && !parameter[2].contains("#"))
				Errors.addError("Third parameter has a wrong immediate format");
			if (parameter[2].length() > 6)
				Errors.addError("Third parameter size is too big");
			
			assignParts(parameter);
			
			if (rt > 31 || rs > 31) 
				Errors.addError("Invalid register number");			
		}
		//OTHER INSTRUCTIONS
	}
	
	public void assignParts(String parameter[]) {
		name = parameter[0].substring(0, parameter[0].indexOf("R")+1);
		
		String rtInit = parameter[0].substring(parameter[0].indexOf("R")+1, parameter[0].length());
		rt = Integer.parseInt(rtInit);
		
		String rsInit = parameter[1].substring(parameter[1].indexOf("R")+1, parameter[1].length());
		rs = Integer.parseInt(rsInit);
		
		if (parameter[2].contains("#")) { //STILL DOES NOT HANDLE HEX IMMEDIATE (0-8 ONLY)
			String imInit = parameter[2].substring(parameter[2].indexOf("#")+1, parameter[2].length());
			immediate = Integer.parseInt(imInit);  
		} else {
			String imInit = parameter[2].substring(parameter[2].indexOf("X")+1, parameter[2].length());
			immediate = Integer.parseInt(imInit);  
		}
	}
	
	public void buildOpcode() {
		StringBuilder sb = new StringBuilder();
		if (codeLine.contains("DADDIU")) {
			sb.append(DADDIU_OPCODE);
			sb.append(padZeros(convertBinary(rs), 5));
			sb.append(padZeros(convertBinary(rt), 5));

			String immediateString = padZeros(Integer.toString(immediate), 4);
			for (int i=0; i<immediateString.length(); i++) {
				int value = Integer.parseInt(immediateString.substring(i, i+1));
				sb.append(padZeros(convertBinary(value), 4));
			}
		}
		//OTHER INSTRUCTIONS
		
		finalopcode = sb.toString();
		finalhexopcode = convertHex(finalopcode);
		Opcodes.addOpcode("DADDIU        " + finalhexopcode);
	}
	
	public String padZeros(String binary, int n) { 
		if (binary.length() < n){
			for (int i = binary.length(); i<n; i++)
				binary = "0" + binary;
		}
		return binary;		
	}
	
	public String convertBinary(int n) {
		return Integer.toBinaryString(n);
	}
	
	public String convertHex(String bin) {
		return new BigInteger(bin, 2).toString(16);
	}
}
