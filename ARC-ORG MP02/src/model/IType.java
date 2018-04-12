package model;

public class IType extends Instruction{
	
	public static final String DADDIU_OPCODE = "011001"; 
	public static final String XORI_OPCODE = "001110";  
	public static final String LD_OPCODE = "110111";  
	public static final String SD_OPCODE = "111111"; 
	public static final String BLTC_OPCODE = "010111";
	
	private String jumptoCode;
	public Utilities util;
			
	public IType(String codeLine) {
		util = new Utilities ();
		setCodeLine(codeLine);
		if(codeLine.contains(":"))
			code = codeLine.split(":")[1];
		else 
			code = codeLine;
	}
	
	public boolean checkForErrors() {
		if (code.contains("DADDIU") || code.contains("XORI")) {
			String parameter[] = code.replaceAll("\\s+","").split(",");			
			
			name = parameter[0].substring(0, parameter[0].lastIndexOf("R")).replaceAll("\\s+","");

			if (parameter.length != 3) {
				Lists.addError(name + ": Invalid parameters");
				return true;
			}
			else { 
				if (!parameter[0].contains("R")) {
					Lists.addError(name + ": First parameter is not a register");
					return true;
				}
				if (!parameter[1].contains("R")) {
					Lists.addError(name + ": Second parameter is not a register");
					return true;
				}
				if (!parameter[2].matches("[#0-9a-fA-FxX]+")) {
					Lists.addError(name + ": Third parameter contains non hexadecimal values");
					return true;
				}
				if ( !parameter[2].contains("0X") && !parameter[2].contains("#")) {
					Lists.addError(name + ": Third parameter has a wrong immediate format");
					return true;
				}
				if (parameter[2].length() > 6) {
					Lists.addError(name + ": Third parameter size is too big");
					return true;
				}
				else {
					assignParts(parameter);
					if (rt > 31 || rs > 31) {
						Lists.addError(name + ": Invalid register number");	
						return true;
					}
					if (rt == 0) { 
						Lists.addError(name + ": Invalid rt. Cannot be zero");
						return true;
					}
				}
			} 
		} 
		else if (code.contains("LD") || code.contains("SD")) {
			String[]parameter = code.split(", |\\(");
			name = parameter[0].substring(0, parameter[0].lastIndexOf("R")).replaceAll("\\s+","");
			
			
			if (parameter.length != 3) {
				Lists.addError(name + ": Invalid parameters");
				return true;
			}
			else { 
				if (!parameter[0].contains("R")) {
					Lists.addError(name + ": First parameter is not a register");
					return true;
				}
				if (!parameter[2].contains("R")) {
					Lists.addError(name + ": Third parameter is not a register");
					return true;
				}
				else {
					assignParts(parameter);
					if (rt > 31 || base > 31) {
						Lists.addError(name + ": Invalid register number");	
						return true;
					}
				}
			}
		}	
		else if (code.contains("BLTC")) {
			String parameter[] = code.replaceAll("\\s+","").split(",");
			name = parameter[0].substring(0, parameter[0].lastIndexOf("R")).replaceAll("\\s+","");
			

			if (parameter.length != 3) {
				Lists.addError("Invalid parameters");
				return true;
			} else {
				if (!parameter[0].contains("R")) {
					Lists.addError(name + ": First parameter is not a register");
					return true;
				}
				if (!parameter[1].contains("R")) {
					Lists.addError(name + ": Second parameter is not a register");
					return true;
				} else {
					assignParts(parameter);
					if (rt > 31 || rs > 31) {
						Lists.addError(name + ": Invalid register number");	
						return true;
					} else if (rt == 0) {
						Lists.addError(name + ": Invalid rt. Cannot be zero");	
						return true;
					} else if (rs == rt) {
						Lists.addError(name + ": Error. rs cannot be equal to rt");
					}
				}
			}
		}
		return false;
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

			String rtInit = parameter[0].substring(parameter[0].lastIndexOf("R")+1, parameter[0].length());
			rt = Integer.parseInt(rtInit);
			
			String baseInit = parameter[2].substring(parameter[2].indexOf("R")+1, parameter[2].indexOf(")"));
			base = Integer.parseInt(baseInit);
			
			if (util.isHex(parameter[1])){
				offset= parameter[1];
				
				System.out.println(parameter[1]);
			}
			else {
				for (int m=0; m<Lists.getMemoryData().size(); m++){
//					System.out.println(Lists.getMemoryData().get(m).getLabel().toUpperCase()+" vs"+parameter[1].toUpperCase());
					if (Lists.getMemoryData().get(m).getLabel().toUpperCase().equalsIgnoreCase(parameter[1].toUpperCase())){
						offset =  Lists.getMemoryData().get(m).getAddress();
						System.out.println("offset: " +offset);
						break;
					}
				}
			}	
		}
		else if (code.contains("BLTC")) {

			String rsInit = parameter[0].substring(parameter[0].indexOf("R")+1, parameter[0].length());
			rs = Integer.parseInt(rsInit);
			
			String rtInit = parameter[1].substring(parameter[1].lastIndexOf("R")+1, parameter[1].length());
			rt = Integer.parseInt(rtInit);
			
			jumptoCode = parameter[2];
		}
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

			String offsetString = util.padZeros(util.hexToBin(util.padZeros(offset, 4)), 16);
			System.out.println(offsetString);
			sb.append(offsetString);
//			for (int i=0; i<offsetString.length(); i++) {
//				int value = util.convertInt(offsetString.substring(i, i+1));
//				sb.append(util.padZeros(util.convertBinary(value), 4));
//			}
			
			finalopcode = sb.toString();
			finalhexopcode = util.padZeros(util.convertHex(finalopcode).toUpperCase(), 8);
			Lists.addOpcode("LD        " + finalhexopcode);
		}
		else if (code.contains("BLTC")) {
			
			int bcIndex = 0; 
			int bcOffset = 0;
			
			for (int k = 0; k< Lists.getInstructions().size(); k++ ) {
				Instruction i = Lists.getInstructions().get(k);
				
				if (i.getCode().contains("BLTC")) {
					bcIndex = k;
				}
				if (i.getCodeLine().contains(":")) {
					if (i.getCodeLine().split(":")[0].equalsIgnoreCase(jumptoCode)) {
						bcOffset = k;
						break;
					}
				}
			}
			int countDistance = bcOffset - bcIndex -1;
			
			sb.append(BLTC_OPCODE); 
			sb.append(util.padZeros(util.convertBinary(rs), 5));
			sb.append(util.padZeros(util.convertBinary(rt), 5));
			sb.append(util.padZeros(util.convertBinary(countDistance), 16));

			finalopcode = sb.toString();
			finalhexopcode = util.padZeros(util.convertHex(finalopcode).toUpperCase(), 8);
			Lists.addOpcode("BLTC        " + finalhexopcode);
		}
	}
}