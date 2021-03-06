package model;

import java.math.BigInteger;

public class Utilities {
	// UTILSSS
	public boolean isHex(String str)
	{
	  return str.matches("-?[0-9a-fA-F]+");  //match a number with optional '-' and decimal.
	}
	
	public boolean isHexBetween (String x, String y, String z) {
		return (Integer.parseInt(x, 16) <= Integer.parseInt(y, 16) && Integer.parseInt(y, 16) <= Integer.parseInt(z, 16));
	}
	
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
	
	public String intToHex(int n) {
		return Integer.toHexString(n);
	}
	
	public String hexToBin(String h) {
		  return new BigInteger(h, 16).toString(2);
	}
	
	public String decToHex (String n){
		int num= Integer.parseInt(n);
		return Integer.toHexString(num);
	}
	
	public int binToInt(String bin) {
		return Integer.parseInt(bin, 2);
	}
	
	public int hexToDec(String n) {
		return Integer.parseInt(n, 16);
	}
}
