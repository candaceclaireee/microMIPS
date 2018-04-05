package model;

public class JType extends Instruction{

	public static final String BC_OPCODE = "110010";
	public boolean toJump = false;
	
	public JType(String codeLine) {
		setCodeLine(codeLine);
		if(codeLine.contains(":"))
			code = codeLine.split(":")[1];
		else 
			code = codeLine;
		if (checkForErrors() == false)
			buildOpcode();		
	}
	
	public boolean checkForErrors(){
		return false; //CHANGE
	}
	
	public void buildOpcode() {

		String parameter[] = code.split(" ");
		name = parameter[0].substring(0, parameter[0].length()).replaceAll("\\s+","");
				
		//System.out.println(getJumpAddress());
		
		StringBuilder sb = new StringBuilder();
		sb.append(BC_OPCODE);
		sb.append(padZeros(getJumpAddress(), 26));
		finalopcode = sb.toString();
		finalhexopcode = padZeros(convertHex(finalopcode).toUpperCase(), 8);
		Lists.addOpcode("BC        " + finalhexopcode);	
	}  
}
