package model;

public class IType extends Instruction{
	
	public static final String DADDIU_OPCODE = "011001";  // DADDIU OK
	public static final String XORI_OPCODE = "001110";  // XORI OK
	public static final String LD_OPCODE = "110111";  // LD OK
	public static final String SD_OPCODE = "111111"; // SD OK
	public static final String BLTC_OPCODE = "010111";
			
	public IType(String codeLine) {
		setCodeLine(codeLine);
		if(codeLine.contains(":"))
			code = codeLine.split(":")[1];
		else 
			code = codeLine;
		if (checkForErrors() == false)
			buildOpcode();
	}
	
	public boolean checkForErrors() {
		if (code.contains("DADDIU") || code.contains("XORI")) {
			String parameter[] = code.replaceAll("\\s+","").split(",");

			if (parameter.length != 3) {
				Lists.addError("Invalid parameters");
				return true;
			}
			else { 
				if (!parameter[0].contains("R")) {
					Lists.addError("First parameter is not a register");
					return true;
				}
				if (!parameter[1].contains("R")) {
					Lists.addError("Second parameter is not a register");
					return true;
				}
				if ( !parameter[2].contains("0X") && !parameter[2].contains("#")) {
					Lists.addError("Third parameter has a wrong immediate format");
					return true;
				}
				if (parameter[2].length() > 6) {
					Lists.addError("Third parameter size is too big");
					return true;
				}
				else {
					assignParts(parameter);
					if (rt > 31 || rs > 31) {
						Lists.addError("Invalid register number");	
						return true;
					}
					return false; 
				}
			} 
		} 
		else if (code.contains("LD") || code.contains("SD")) {
			String[]parameter = code.split(", |\\(");
			
			if (parameter.length != 3) {
				Lists.addError("Invalid parameters");
				return true;
			}
			else { 
				if (!parameter[0].contains("R")) {
					Lists.addError("First parameter is not a register");
					return true;
				}
				if (!parameter[2].contains("R")) {
					Lists.addError("Third parameter is not a register");
					return true;
				}
				if (!(parameter[1].length()==4)) {
					Lists.addError("Second parameter size is invalid");
					return true;
				}
				else {
					assignParts(parameter);
					if (rt > 31 || rs > 31) {
						Lists.addError("Invalid register number");	
						return true;
					}
					return false; 
				}
			}
		}
		return false;	
		//BLTC!!!
	}
	
	public void assignParts(String parameter[]) {
		if (code.contains("DADDIU") || code.contains("XORI")) {
			name = parameter[0].substring(0, parameter[0].lastIndexOf("R")).replaceAll("\\s+","");
			
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
		else if (code.contains("LD") || code.contains("SD")) {
			name = parameter[0].substring(0, parameter[0].lastIndexOf("R")).replaceAll("\\s+","");
			
			String rtInit = parameter[0].substring(parameter[0].lastIndexOf("R")+1, parameter[0].length());
			rt = Integer.parseInt(rtInit);
			
			String baseInit = parameter[2].substring(parameter[2].indexOf("R")+1, parameter[2].indexOf(")"));
			base = Integer.parseInt(baseInit);
			
			offset =  parameter[1];
		}
		//BLTC!!!
	}
	
	public void buildOpcode() {
		StringBuilder sb = new StringBuilder();
		if (code.contains("DADDIU")) {
			sb.append(DADDIU_OPCODE);
			sb.append(util.padZeros(util.convertBinary(rs), 5));
			sb.append(util.padZeros(util.convertBinary(rt), 5));

			String immediateString = util.padZeros(immediate, 4); 
			for (int i=0; i<immediateString.length(); i++) {
				int value = util.convertInt(immediateString.substring(i, i+1));
				sb.append(util.padZeros(util.convertBinary(value), 4));
			}

			finalopcode = sb.toString();
			finalhexopcode = util.padZeros(util.convertHex(finalopcode).toUpperCase(), 8);
			Lists.addOpcode("DADDIU        " + finalhexopcode);
		}  
		else if (code.contains("XORI")) {
			sb.append(XORI_OPCODE);
			sb.append(util.padZeros(util.convertBinary(rs), 5));
			sb.append(util.padZeros(util.convertBinary(rt), 5));

			String immediateString = util.padZeros(immediate, 4);
			for (int i=0; i<immediateString.length(); i++) {
				int value = util.convertInt(immediateString.substring(i, i+1));
				sb.append(util.padZeros(util.convertBinary(value), 4));
			}
			
			finalopcode = sb.toString();
			finalhexopcode = util.padZeros(util.convertHex(finalopcode).toUpperCase(), 8);
			Lists.addOpcode("XORI        " + finalhexopcode);
		}
		else if (code.contains("SD")) {
			sb.append(SD_OPCODE);
			sb.append(util.padZeros(util.convertBinary(base), 5));
			sb.append(util.padZeros(util.convertBinary(rt), 5));

			String offsetString = util.padZeros(offset, 4);
			for (int i=0; i<offsetString.length(); i++) {
				int value = util.convertInt(offsetString.substring(i, i+1));
				sb.append(util.padZeros(util.convertBinary(value), 4));
			}
			
			finalopcode = sb.toString();
			finalhexopcode = util.padZeros(util.convertHex(finalopcode).toUpperCase(), 8);
			Lists.addOpcode("SD        " + finalhexopcode);
		}
		else if (code.contains("LD")) {
			sb.append(LD_OPCODE);
			sb.append(util.padZeros(util.convertBinary(base), 5));
			sb.append(util.padZeros(util.convertBinary(rt), 5));

			String offsetString = util.padZeros(offset, 4);
			for (int i=0; i<offsetString.length(); i++) {
				int value = util.convertInt(offsetString.substring(i, i+1));
				sb.append(util.padZeros(util.convertBinary(value), 4));
			}
			
			finalopcode = sb.toString();
			finalhexopcode = util.padZeros(util.convertHex(finalopcode).toUpperCase(), 8);
			Lists.addOpcode("LD        " + finalhexopcode);
		}
	 // ELSE IF BLTC!!!!
	}
	
}
