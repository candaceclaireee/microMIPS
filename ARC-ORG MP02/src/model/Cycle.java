package model;

public class Cycle {
	private String IR;
	private String NPC;
	private String A;
	private String B;
	private String IMM;
	private String ALUOUPUT;
	private boolean COND;
	private String PC;
	private String LMD;
	private String RANGE;
	private String RN;
	
	public boolean isCOND() {
		return COND;
	}
	public void setCOND(boolean cOND) {
		COND = cOND;
	}
	public boolean getCOND() {
		return COND;
	}
	public String getIR() {
		return IR;
	}
	public void setIR(String iR) {
		IR = iR;
	}
	public String getNPC() {
		return NPC;
	}
	public void setNPC(String nPC) {
		NPC = nPC;
	}
	public String getA() {
		return A;
	}
	public void setA(String a) {
		A = a;
	}
	public String getB() {
		return B;
	}
	public void setB(String b) {
		B = b;
	}
	public String getIMM() {
		return IMM;
	}
	public void setIMM(String iMM) {
		IMM = iMM;
	}
	public String getALUOUPUT() {
		return ALUOUPUT;
	}
	public void setALUOUPUT(String aLUOUPUT) {
		ALUOUPUT = aLUOUPUT;
	}
	public String getPC() {
		return PC;
	}
	public void setPC(String pC) {
		PC = pC;
	}
	public String getLMD() {
		return LMD;
	}
	public void setLMD(String lMD) {
		LMD = lMD;
	}
	public String getRANGE() {
		return RANGE;
	}
	public void setRANGE(String rANGE) {
		RANGE = rANGE;
	}
	public String getRN() {
		return RN;
	}
	public void setRN(String rN) {
		RN = rN;
	}
	

}
