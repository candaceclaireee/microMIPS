package model;

public class IType extends Instruction{
	
	public static final String DADDIU_OPCODE = "011001";  // DADDIU OK
	public static final String XORI_OPCODE = "001110";  // XORI OK
	public static final String LD_OPCODE = "110111"; 
	public static final String SD_OPCODE = "111111";
	public static final String BLTC_OPCODE = "010111";
	
	String codeLine;
		
	public IType(String code) {
		codeLine = code;
		if (checkForErrors() == false)
			buildOpcode();
	}
	
	public boolean checkForErrors() {
		if (codeLine.contains("DADDIU") || codeLine.contains("XORI")) {
			String parameter[] = codeLine.replaceAll("\\s+","").split(",");

			if (parameter.length != 3) {
				Errors.addError("Invalid parameters");
				return true;
			}
			else { 
				if (!parameter[0].contains("R")) {
					Errors.addError("First parameter is not a register");
					return true;
				}
				if (!parameter[1].contains("R")) {
					Errors.addError("Second parameter is not a register");
					return true;
				}
				if ( !parameter[2].contains("0X") && !parameter[2].contains("#")) {
					Errors.addError("Third parameter has a wrong immediate format");
					return true;
				}
				if (parameter[2].length() > 6) {
					Errors.addError("Third parameter size is too big");
					return true;
				}
				else {
					assignParts(parameter);
					if (rt > 31 || rs > 31) {
						Errors.addError("Invalid register number");	
						return true;
					}
					return false; 
				}
			} 
		} 
		return false;	
		//OTHER INSTRUCTIONS
	}
	
	public void assignParts(String parameter[]) {
		name = parameter[0].substring(0, parameter[0].indexOf("R")+1);
		
		String rtInit = parameter[0].substring(parameter[0].lastIndexOf("R")+1, parameter[0].length());
		rt = Integer.parseInt(rtInit);
		
		String rsInit = parameter[1].substring(parameter[1].indexOf("R")+1, parameter[1].length());
		rs = Integer.parseInt(rsInit);
		
		if (parameter[2].contains("#")) { 
			immediate = parameter[2].substring(parameter[2].indexOf("#")+1, parameter[2].length());
		} else {
			immediate = parameter[2].substring(parameter[2].indexOf("X")+1, parameter[2].length());
		}
	}
	
	public void buildOpcode() {
		StringBuilder sb = new StringBuilder();
		if (codeLine.contains("DADDIU")) {
			sb.append(DADDIU_OPCODE);
			sb.append(padZeros(convertBinary(rs), 5));
			sb.append(padZeros(convertBinary(rt), 5));

			String immediateString = padZeros(immediate, 4); 
			for (int i=0; i<immediateString.length(); i++) {
				int value = convertInt(immediateString.substring(i, i+1));
				sb.append(padZeros(convertBinary(value), 4));
			}

			finalopcode = sb.toString();
			finalhexopcode = convertHex(finalopcode).toUpperCase();
			Opcodes.addOpcode("DADDIU        " + finalhexopcode);
			
		}  else if (codeLine.contains("XORI")) {
			sb.append(XORI_OPCODE);
			sb.append(padZeros(convertBinary(rs), 5));
			sb.append(padZeros(convertBinary(rt), 5));

			String immediateString = padZeros(immediate, 4);
			for (int i=0; i<immediateString.length(); i++) {
				int value = convertInt(immediateString.substring(i, i+1));
				sb.append(padZeros(convertBinary(value), 4));
			}
			
			finalopcode = sb.toString();
			finalhexopcode = convertHex(finalopcode).toUpperCase();
			Opcodes.addOpcode("XORI        " + finalhexopcode);
		}
		//OTHER INSTRUCTIONS
		
	}
	
}
