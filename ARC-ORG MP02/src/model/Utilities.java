package model;

import java.math.BigInteger;

public class Utilities {
	// UTILSSS
	public String padZeros(String binary, int n) { 
		if (binary.length() < n){
			for (int i = binary.length(); i<n; i++)
				binary = "0" + binary;
		}
		return binary;		
	}
	
	public String removeExtrasHex (String hex) {
		String newHex = "";
		
		if (hex.contains("#")){
			newHex = hex.substring(hex.indexOf("#")+1, hex.length());
		}
		else {
			newHex = hex.substring(hex.indexOf("x")+1, hex.length());
		}

		return newHex;
	}
	
	public String convertBinary(int n) {
		return Integer.toBinaryString(n);
	}
	
	public String convertHex(String bin) {
		return new BigInteger(bin, 2).toString(16);
	}
	
	public int convertInt(String n) {
		return Integer.decode("0x"+n);
	}
	
	public String hexToBin(String h) {
		  return new BigInteger(h, 16).toString(2);
	}
	
	public String decToHex (String n){
		int num= Integer.parseInt(n);
		return Integer.toHexString(num);
	}
	public int hexToDec(String n) {
		return Integer.parseInt(n, 16);
	}
}
