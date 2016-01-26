/**
 * 
 */
package com.intersuite.radix;

import java.math.*;
import java.lang.reflect.*;
import java.util.HashSet;

/**
 * @author cwigginton
 *
 */
public class Radix {

	/**
	 * @param args
	 */
	public final static String BASE2="01"; 
	public final static String BASE8="01234567"; 
	public final static String BASE10="0123456789"; 
	public final static String BASE16="0123456789ABCDEF"; 
	public final static String BASE32="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
	public final static String BASE62="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
	public final static String BASE75="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_.,!=-*(){}[]"; 
	
	public final static String BASE20="0123456789ABCDEFGHJK";//Vigesimal, not sure on this one, as K is usually written as 10
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String converted;
		
		//converted =  Convert("0A","BASE16","BASE2");
		converted =  convert("EBF5FF85AC7DA2937DCF692AA47326F48BA8ADFD","BASE16","BASE75");
		System.out.println(converted);
		converted =  convert("h-E)k-dVi{]3y57fR]fu-ku9q-","BASE75","BASE16");
		
		converted =  convert("112","BASE8","BASE2");
		System.out.println(converted);
		
		converted =  convert("74","BASE10","BASE8");
				System.out.println(converted);
	}
	
	public static String convert(String input,String sourceBase,String targetBase){
		StringBuffer result = new StringBuffer();
		Radix radix = new Radix();
		Class cls = radix.getClass();
		Field sourceBaseField;
		Field targetBaseField;
		
		String sourceRadix;
		String targetRadix;
		String sourceString;
				
		
		//BASE16 and BASE32, to support upper and lowercase, we convert source to all upper
		if(sourceBase.equals("BASE16") || sourceBase.equals("BASE32")){
			sourceString = input.toUpperCase();
		}else{
			sourceString = input;
		}
			
		//Check if existing source or target matches name of static bases, otherwise
		//we'll use the passed in strings as the radix.
		try{
			sourceBaseField = cls.getDeclaredField(sourceBase);
			sourceBaseField.setAccessible(true);
			sourceRadix = sourceBaseField.get(sourceBaseField).toString();
		}
		catch (NoSuchFieldException nsfe){
			sourceRadix = sourceBase;
		}
		catch (IllegalAccessException iae){
			return "Illegal Access";
			
		}
		
		try{		
			targetBaseField = cls.getDeclaredField(targetBase);
			targetBaseField.setAccessible(true);
			targetRadix = targetBaseField.get(targetBaseField).toString();
		}
		catch (NoSuchFieldException nsfe){
			targetRadix = targetBase;
		}
		catch (IllegalAccessException iae){
			return "Illegal Access";		
		}
		
		if(! uniqueChar(targetRadix) ){
			return "target not unique";
		}
		
		if(! uniqueChar(sourceRadix) ){
			return "source not unique";
		}
		
		if (! sourceMatchesBase(sourceString,sourceRadix)){
			return "Source does not match base";
		}
		
		BigInteger baseLength = new BigInteger(Integer.toString(sourceRadix.length()));
		BigInteger targetLength = new BigInteger(Integer.toString(targetRadix.length()));
		
		//System.out.println("baseLength:" + baseLength.toString());
		
		StringBuilder revSourceString = new StringBuilder(sourceString).reverse();
		
		BigInteger baseTotal = new BigInteger("0");
		
		//Loop the source and build the bigInt
		for(int i= revSourceString.length() - 1;i > -1;i--){
	
			//System.out.println("column:" + i);
			BigInteger powerRadix = baseLength.pow(i);
			//System.out.println(baseLength.toString() + "^" + i);
			
			char chRadix = revSourceString.charAt(i);
			//System.out.println("hex character:" + chRadix);
			int idxRadix = sourceRadix.indexOf(chRadix);
			BigInteger valRadix = new BigInteger(Integer.toString(idxRadix));		
			//System.out.println("hex to dec val:" + idxRadix);
			
			BigInteger colResult = valRadix.multiply(powerRadix);
			
			//System.out.println(valRadix.toString()+"x(" + baseLength.toString() + "^" + i + ") +" +  valRadix.toString() +" =" + colResult.toString());
			
			baseTotal = baseTotal.add(colResult);
			
		}
		
		//System.out.println("Total:" +  baseTotal.toString());
				
		
		BigInteger[] rTarget = baseTotal.divideAndRemainder(targetLength);
		BigInteger quotient = rTarget[0];
		BigInteger remainder = rTarget[1];
	
		//System.out.println("quotient:" + quotient.toString());
		//System.out.println("remainder:" + remainder.toString());
		
		result.append(targetRadix.charAt(remainder.intValue()));
		
		//System.out.println("targetBase:" + targetBase.length());
		
		do{
				 
			 rTarget = quotient.divideAndRemainder(targetLength);
			 quotient = rTarget[0];
			 remainder = rTarget[1];
			 
			 //System.out.println("quotient:" + quotient.toString());
			//System.out.println("remainder:" + remainder.toString());
			result.append(targetRadix.charAt(remainder.intValue()));
			
		}while(! quotient.equals(BigInteger.ZERO));
		
		
		//System.out.println(result.reverse().toString());
		
		return result.reverse().toString();
		
	}
	
	   private static boolean sourceMatchesBase(String source,String base){
		   boolean result = true;
		   char[] arySource = source.toCharArray();
		    for (int i = 0; i < arySource.length; i++) {
		        result = (base.indexOf(arySource[i]) >= 0);
		        //System.out.printf("source:%s base: %s against %s at index %s  result: %s %n",source,base,arySource[i],i,result);
		        if(!result){
		        	return result;
		        }
		    }
		   return result;
		   
	   }
	

		private static boolean uniqueChar(String str) {
            HashSet<Character> unique = new HashSet<Character>();
            for(int i=0; i<str.length();i++){
                unique.add(str.charAt(i));
            }
            if(unique.size()!=str.length()){
                return false;
            }       
            return true;
        }
		
		

}
