package model;

public class RType extends Instruction {

	public static final String DADDU_OPCODE = "000000";   //DADDU OK
	public static final String DADDU_SA = "00000";
	public static final String DADDU_FUNC = "101101";
	
	public RType(String codeLine) {
		setCodeLine(codeLine);
		if(codeLine.contains(":"))
			code = codeLine.split(":")[1];
		else 
			code = codeLine;
		if (checkForErrors() == false)
			buildOpCode();
	}
	
	public boolean checkForErrors() {
	
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
			if ( !parameter[2].contains("R")) {
				Lists.addError("Third parameter is not a register");
				return true;
			}
			else {
				assignParts(parameter);
				if (rt > 31 || rs > 31 || rd > 31) {
					Lists.addError("Invalid register number");	
					return true;
				}
				return false; 
			}
		} 
	} 
	
	public void assignParts(String parameter[]) {
		name = parameter[0].substring(0, parameter[0].lastIndexOf("R"));
		
		String rdInit = parameter[0].substring(parameter[0].lastIndexOf("R")+1, parameter[0].length());
		rd = Integer.parseInt(rdInit);
		
		String rsInit = parameter[1].substring(parameter[1].indexOf("R")+1, parameter[1].length());
		rs = Integer.parseInt(rsInit);
		
		String rtInit = parameter[2].substring(parameter[2].indexOf("R")+1, parameter[2].length());
		rt = Integer.parseInt(rtInit);
	}

	public void buildOpCode() {
		StringBuilder sb = new StringBuilder();
		sb.append(DADDU_OPCODE);
		sb.append(util.padZeros(util.convertBinary(rs), 5));
		sb.append(util.padZeros(util.convertBinary(rt), 5));
		sb.append(util.padZeros(util.convertBinary(rd), 5));
		sb.append(DADDU_SA); 
		sb.append(DADDU_FUNC); 

		finalopcode = sb.toString();
		finalhexopcode = util.padZeros(util.convertHex(finalopcode).toUpperCase(), 8);
		Lists.addOpcode("DADDU        " + finalhexopcode);
	}
	
}
