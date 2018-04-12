package model;

public class JType extends Instruction{

	public static final String BC_OPCODE = "110010";
	public boolean toJump = false;

	public Utilities util;
	
	public JType(String codeLine) {
		util = new Utilities ();
		setCodeLine(codeLine);
		if(codeLine.contains(":"))
			code = codeLine.split(":")[1];
		else 
			code = codeLine;	
	}
	
	public boolean checkForErrors(){

		if (code.contains("BC")) {
	
			String parameter[] = code.split(" ");
			name = parameter[0].substring(0, parameter[0].length()).replaceAll("\\s+","");
			
			if (parameter.length != 2) {
				Lists.addError(name + ": Invalid parameters");
				return true;
			}
			else { 
				return false;
			}
		}	
		return false;	
	}
	
	public void buildOpcode() {

		String jumptoCode = "";
		int bcIndex = 0; 
		int bcOffset = 0;
		
		for (int k = 0; k< Lists.getInstructions().size(); k++ ) {
			Instruction i = Lists.getInstructions().get(k);
			
			if (i.getCode().contains("BC")) {
				jumptoCode = i.getCode().split(" ")[1];
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
		

		StringBuilder sb = new StringBuilder();
		sb.append(BC_OPCODE);
		sb.append(util.padZeros(util.convertBinary(countDistance), 26));
		finalopcode = sb.toString();
		finalhexopcode = util.padZeros(util.convertHex(finalopcode).toUpperCase(), 8);
		Lists.addOpcode("BC        " + finalhexopcode);	

	}  
}
