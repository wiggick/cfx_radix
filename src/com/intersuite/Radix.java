/**
 * 
 */
package com.intersuite;
import com.allaire.cfx.* ;
import java.math.*;
import java.lang.reflect.*;
import java.util.HashSet;



/**
 * Combination ColdFusion cfx custom tag or Java POJO that can be used to convert numbers from one Radix base to another, supporting
 * standard Radix bases as well as support for arbitrary base conversions.  BigInteger support for very large number
 * conversions such as a 64bit hash.
 * 
Copyright (c) 2016, Christopher Wigginton
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of Intersuite.com nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Christopher Wigginton
 * @version 1.0
 *
 */
public class Radix implements CustomTag 
{

	
	//BASES below 32 use a substring of BASE32. if source for this range contains alphas, they will be upper cased.
	public final static String BASE32="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
	public final static String BASEALPHA="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; 
	public final static String BASE62="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	public final static String BASE75="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_.,!=-*(){}[]"; 
	
	
	/**
	 * ColdFusion Custom Tag that takes a numeric input and converts from Radix Source Base to Radix Target Base.
	 * @author Christopher Wigginton <c_wigginton@yahoo.com>
	 * @version 1.0
	 * @param VALUE String containing number to be formatted 
	 * @param SOURCEBASE String containing BASE2 to BASE32, BASE62,BASE75,BASEALPHA or an arbitrary string of at least two unique characters that does not start with BASE		
	 * @param TARGETBASE String containing BASE2 to BASE32, BASE62,BASE75,BASEALPHA or an arbitrary string of at least two unique characters that does not start with BASE
	 * @param VARIABLE (Optional) String containing caller variable to set result.
	 */
	 public void processRequest( Request request, Response response ) throws Exception
	   {
		 
		 boolean setCallerVariable = false;
		 String strVariable = "";
		 
		// validate that required attributes were passed
	      if (  !request.attributeExists("VALUE") ||
	    		!request.attributeExists("SOURCEBASE") ||
	            !request.attributeExists("TARGETBASE"))
	      {
	         throw new Exception( 
	            "Missing attribute (VALUE,SOURCEBASE, and TARGETBASE are " +
	            "required attributes for this tag)" ) ;
	      }
		 
	
		  String input = request.getAttribute( "VALUE" ) ;
		  String sourceBase = request.getAttribute( "SOURCEBASE" ) ;
		  String targetBase = request.getAttribute( "TARGETBASE" ) ;
		  
		  if(request.attributeExists("VARIABLE") ){
			  strVariable = request.getAttribute("VARIABLE") ;  
			  setCallerVariable = true;
		  }
		  
		  String result = convert(input,sourceBase,targetBase);
		  
		  if (setCallerVariable){
			  response.setVariable(strVariable, result);
		  }else{
			  response.write( result) ;
		  }
	  }
	
	 // For Testing
	 public static void main( String args[] ) throws Exception{
		 
		 String result;
		 result = convert("A8E2A5E989E54EBE354F93D6A7194815","BASE16","BASEALPHA");
		 System.out.println("A8E2A5E989E54EBE354F93D6A7194815 = " + result);
		 
		 result = convert("ChristopherWigginton","BASEALPHA","BASE75");
		 System.out.println("ChristopherWigginton = " + result);
		 
		 result = convert("ECygzo{xfXPP8tOL*g","BASE75","BASEALPHA");
		 System.out.println("ECygzo{xfXPP8tOL*g = " + result);
		 
		result = convert("FFFF","BASE16","BASE10");
		System.out.println("FFFF = " + result);
		 
	 }
	
	 /**
	  	* Takes an input number and converts it from one Radix Base to another.
		 * @param input String containing number to be formatted 
		 * @param sourceBase String containing BASE2 to BASE32, BASE62,BASE75,BASEALPHA or an arbitrary string of at least two unique characters that does not start with BASE		
		 * @param targetBase String containing BASE2 to BASE32, BASE62,BASE75,BASEALPHA or an arbitrary string of at least two unique characters that does not start with BASE
		 * @return The input number converted to the target base value.
		 */
	public static String convert(String input,String sourceBase,String targetBase) throws Exception {
		StringBuffer result = new StringBuffer();
		Radix radix = new Radix();
		Class<?> cls = radix.getClass();
		Field sourceBaseField;
		Field targetBaseField;
		
		String sourceRadix;
		String targetRadix;
		String sourceString;
		Boolean evenCharLength = false;
				
		
		//Support automatic  bases between 2 an 32, which is the maximum before we get into supporting lower case characters
		if(sourceBase.length() >= 5 && sourceBase.substring(0,4).equalsIgnoreCase("BASE")){
			
			int baseVal = 0;
			
			if (!sourceBase.equalsIgnoreCase("BASEALPHA"))
			{
				String baselen = sourceBase.substring(4,sourceBase.length());
				baseVal = Integer.parseInt(baselen);
			}
			
			
			if(baseVal > 0 && baseVal <= 32){
				
				sourceBaseField = cls.getDeclaredField("BASE32");
				sourceBaseField.setAccessible(true);
				sourceRadix = sourceBaseField.get(sourceBaseField).toString().substring(0,baseVal);
				sourceString = input.toUpperCase();
				
			}else{
				
				try{
					sourceBaseField = cls.getDeclaredField(sourceBase);
					sourceBaseField.setAccessible(true);
					sourceRadix = sourceBaseField.get(sourceBaseField).toString();
					sourceString = input;
				}
				catch (NoSuchFieldException nsfe){
					 throw new Exception( 
					            "Invalid Source Base.  Must be Between BASE2 - BASE32,BASE62,BASE65, or BASEALPHA.  Alternately, a String that does not contain BASE as the first characters." ) ;
				}
				catch (IllegalAccessException iae){
					throw new Exception( 
				            "An internal problem with the Radix class has occured accessing a property while evaluating the source base" ) ;

				}
			}
				
		}else{
			sourceRadix = sourceBase;
			sourceString = input;
		}
		
		
		if(targetBase.length() >= 5 && targetBase.substring(0,4).equalsIgnoreCase("BASE")){
			int baseVal = 0;
					
			if (!targetBase.equalsIgnoreCase("BASEALPHA"))
			{
				
				String baselen = targetBase.substring(4,targetBase.length());
				baseVal = Integer.parseInt(baselen);
			}
			
		
			
			if(baseVal > 0 && baseVal <= 32){
					targetBaseField = cls.getDeclaredField("BASE32");
					targetBaseField.setAccessible(true);
					targetRadix = targetBaseField.get(targetBaseField).toString().substring(0,baseVal);
					evenCharLength = true;
				}else{
					
					try{
						targetBaseField = cls.getDeclaredField(targetBase);
						targetBaseField.setAccessible(true);
						targetRadix = targetBaseField.get(targetBaseField).toString();
					}
					catch (NoSuchFieldException nsfe){
						 throw new Exception( 
						            "Invalid Target Base.  Must be Between BASE2 - BASE32,BASE62, BASE65,BASEALPHA.  Alternately, a String that does not contain BASE as the first characters." ) ;

					}
					catch (IllegalAccessException iae){
						throw new Exception( 
					            "An internal problem with the Radix class has occured accessing a property while evaluating the source base" ) ;
					}
				}
				
			
			
		}
		else{
			targetRadix = targetBase;
			
		}
		
		if (sourceRadix.length() < 2 || targetRadix.length() < 2){
			
			throw new Exception( 
		            "SOURCEBASE or TARGETBASE too short for derived base.  Must be longer than two characters and each character unique" ) ;
		}
			
		if(! uniqueChar(targetRadix) ){
			throw new Exception( 
		            "TARGETBASE does not contain unique characters" ) ;
		}
		
		if(! uniqueChar(sourceRadix) ){
			throw new Exception( 
		            "SOURCEBASE does not contain unique characters" ) ;
		}
		
		if (! sourceMatchesBase(sourceString,sourceRadix)){
			throw new Exception( 
		            "Characters in VALUE cannot be found in SOURCEBASE:" + sourceRadix.toString() ) ;
		}
		
		BigInteger baseLength = new BigInteger(Integer.toString(sourceRadix.length()));
		BigInteger targetLength = new BigInteger(Integer.toString(targetRadix.length()));
		
		StringBuilder revSourceString = new StringBuilder(sourceString).reverse();
		
		BigInteger baseTotal = new BigInteger("0");
		
		//Loop the source and build the bigInt
		for(int i= revSourceString.length() - 1;i > -1;i--){
	
			BigInteger powerRadix = baseLength.pow(i);
			
			char chRadix = revSourceString.charAt(i);
			
			int idxRadix = sourceRadix.indexOf(chRadix);
			BigInteger valRadix = new BigInteger(Integer.toString(idxRadix));		
			
			BigInteger colResult = valRadix.multiply(powerRadix);
			
			baseTotal = baseTotal.add(colResult);
			
		}
					
		BigInteger[] rTarget = baseTotal.divideAndRemainder(targetLength);
		BigInteger quotient = rTarget[0];
		BigInteger remainder = rTarget[1];
	
		result.append(targetRadix.charAt(remainder.intValue()));
					
		do{
			 rTarget = quotient.divideAndRemainder(targetLength);
			 quotient = rTarget[0];
			 remainder = rTarget[1];		 						
			 result.append(targetRadix.charAt(remainder.intValue()));				
		}while(! quotient.equals(BigInteger.ZERO));
				
		String stResult = result.reverse().toString();
		
		
		if (!targetBase.equalsIgnoreCase("BASE10") && evenCharLength && stResult.length() % 2 > 0){
			
			return "0" + stResult;
			
		}else{
			return stResult;
		}
	}
	
	 /**
  	* Checks each character of input against the base string.
  	* @param source String that contains the source number to convert.
  	* @param base String that contains the Radix Base	 
  	* @return boolean value if all characters of source are in base.
	 */
	   private static boolean sourceMatchesBase(String source,String base){
		   boolean result = true;
		   char[] arySource = source.toCharArray();
		    for (int i = 0; i < arySource.length; i++) {
		        result = (base.indexOf(arySource[i]) >= 0);
		       
		        if(!result){
		        	return result;
		        }
		    }
		   return result;
		   
	   }
	
	   /**
	  	* Checks that each character of base is unique.
	  	* @param str String that contains the Radix Base.
	  	* @return boolean value if all characters of characters in the base are unique.
		 */
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
