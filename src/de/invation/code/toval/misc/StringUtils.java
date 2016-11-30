package de.invation.code.toval.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;


public class StringUtils {
	
	public static StringTokenizer splitArrayString(String array, String delimiter){
		array = array.replace("[", "");
		array = array.replace("]", "");
		return new StringTokenizer(array, delimiter);
	}
	
	/**
	 * Takes a string and splits it according to the quote character '.<br>
	 * Use this method for splitting a string into several parts, where each part corresponds to a quoted substring in the input.<br>
	 * A string "['stringA' 'stringB']" will result in the list (stringA,stringB) with ' being the character used for qouting.
	 * @param array Input string, containing quoted character sets.
	 * @param quoteChar The character used to quote strings in the input string.
	 * @return  list, containing all quotes strings in the input string.
	 */
	public static List<String> splitArrayStringQuoted(String array, char quoteChar){
		array = array.replace("[", "");
		array = array.replace("]", "");
		List<String> result = new ArrayList<>();
		Integer actStart = null;
		for(int i=0; i<array.length(); i++){
			if(array.charAt(i) == quoteChar){
				if(actStart == null){
					if(i<array.length()-1){
						actStart = i+1;
					}
				} else {
					result.add(array.substring(actStart, i));
					actStart = null;
				}
			}
		}
		return result;
	}
	
	/**
	 * Takes a string and splits it according to the quote character.<br>
	 * Use this method for splitting a string into several parts, where each part corresponds to a quoted substring in the input.<br>
	 * A string "['stringA' 'stringB']" will result in the list (stringA,stringB) with ' being the character used for qouting.
	 * @param array Input string, containing quoted character sets.
	 * @return  list, containing all quotes strings in the input string.
	 */
	public static List<String> splitArrayStringQuoted(String array){
		return splitArrayStringQuoted(array, '\'');
	}
	
	public static int countOccurrences(String string, char character){
		int count = 0;
		for(char c: string.toCharArray()){
			if(c == character)
				count++;
		}
		return count;
	}
	
	public static int countLines(String string) {
	    if(string == null || string.isEmpty())
	    	return 0;
	    
	    int lines = 1;
	    int pos = 0;
	    while ((pos = string.indexOf("\n", pos) + 1) != 0) {
	        lines++;
	    }
	    return lines;
	}
	
	public static String removeLeading(String string, char character){
		int index = 0;
		for(int i=0; i<string.length(); i++){
			if(string.charAt(i) != character){
				return string.substring(index);
			}
			index++;
		}
		if(index > 0)
			return "";
		return string;
	}
	
	public static String removeEnding(String string, char character){
		int index = string.length()-1;
		for(int i=string.length()-1; i>=0; i--){
			if(string.charAt(i) != character){
				return string.substring(0, index+1);
			}
			index--;
		}
		if(index < string.length()-1)
			return "";
		return string;
	}	
	
	public static String removeSurrounding(String string, char character){
		String result = removeLeading(string, character);
		return removeEnding(result, character);
	}
	
	public static String createString(char character, int occurrences){
		char[] arr = new char[occurrences];
		for(int i=0; i<occurrences; i++)
			arr[i] = character;
		return new String(arr);
	}
	
	public static String createString(String baseString, int occurrences){
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<occurrences; i++)
			builder.append(baseString);
		return builder.toString();
	}
	
	public static String convertToHTML(String string) throws ParameterException{
		Validate.notNull(string);
		String htmlString = "<html>"+string.replace("\n", "<br>")+"</html>";
		htmlString = htmlString.replace(" ", "&nbsp;");
		return htmlString;
	}

}
