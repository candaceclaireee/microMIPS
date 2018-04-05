package model;

public class JType extends Instruction{

	public static final String BC_OPCODE = "110010";
	public boolean toJump = false;
	String codeLine;
	
	public JType(String codeLine) {
		setCodeLine(codeLine);
		if(codeLine.contains(":"))
			code = codeLine.split(";")[1];
		else 
			code = codeLine;
		
		buildOpcode();
		sendOpCode();
		
	}
	public void buildOpcode() {
		StringBuilder sb = new StringBuilder();
			sb.append(BC_OPCODE);
			sb.append(padZeros(getJumpAddress(), 22));
	

			finalopcode = sb.toString();
			finalhexopcode = padZeros(convertHex(finalopcode).toUpperCase(), 8);
			
		}  
	public void sendOpCode() {
		Lists.addOpcode("BC        " + finalhexopcode);
		}
	}
