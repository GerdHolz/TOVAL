package de.invation.code.toval.misc;

import java.util.Collection;

import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;

public class HTMLUtils {
	
	private static final String STRING_NEW_LINE_WINDOWS = "\r\n";
	private static final String STRING_NEW_LINE_LINUX = "\n";
	private static final String STRING_NEW_LINE_OSX = "\r";
	private static final String STRING_TAG_NEW_LINE = "<br>";
	private static final String LIST_FORMAT_ORDERED = "<ol>%s</ol>";
	private static final String LIST_FORMAT_UNORDERED = "<ul>%s</ul>";
	private static final String LIST_ENTRY_FORMAT = "<li>%s</li>";
	private static final String NULL_REPLACEMENT = "null";
	private static final String STRING_TAG_HTML_START = "<html>";
	private static final String STRING_TAG_HTML_END = "</html>";
	private static final char CHAR_TAG_START = '<';
	private static final char CHAR_TAG_END = '>';
	private static final char CHAR_SPACE = ' ';
	private static final String STRING_SLASH = "/"; 
	private static final String STRING_EMPTY = "";
	private static final String FORMAT_TAG_START = CHAR_TAG_START + "%s";
	
	
	public static final <T> String createList(Collection<T> elements, boolean orderedList){
		StringBuilder docNameBuilder = new StringBuilder();
		if(elements != null){
			for(T element: elements){
				docNameBuilder.append(String.format(LIST_ENTRY_FORMAT, element != null ? element.toString() : NULL_REPLACEMENT));
			}
		}
		return String.format(orderedList ? LIST_FORMAT_ORDERED : LIST_FORMAT_UNORDERED, docNameBuilder.toString());
	}
	
	public static final String convertLineBreaksToHTML(String string){
		return string.replaceAll(STRING_NEW_LINE_WINDOWS, STRING_TAG_NEW_LINE).replaceAll(STRING_NEW_LINE_LINUX, STRING_TAG_NEW_LINE).replaceAll(STRING_NEW_LINE_OSX, STRING_TAG_NEW_LINE);
	}
	
	public static final String convertBRTagsToLineBreaks(String string){
		return convertBRTagsToLineBreaks(string, System.getProperty("line.separator"));
	}
	
	public static final String convertBRTagsToLineBreaks(String string, String lineBreak){
		return string.replaceAll(STRING_TAG_NEW_LINE, lineBreak);
	}
	
	public static final String wrapIntoHTML(String string){
		return surroundWithHTMLTags(convertLineBreaksToHTML(string));
	}
	
	public static final String wrapIntoHTML(String string, String newLine){
		return surroundWithHTMLTags(string.replaceAll(newLine, STRING_TAG_NEW_LINE));
	}
	
	public static final String surroundWithHTMLTags(String string){
		StringBuilder builder = new StringBuilder();
		builder.append(STRING_TAG_HTML_START);
		builder.append(string);
		builder.append(STRING_TAG_HTML_END);
		return builder.toString();
	}
	
//	private static final String unwrapFromHTML(String string, String newLine){
//		StringBuilder builder = new StringBuilder();
//		int tagStart;
//		int tagEnd;
//		String tagName = null;
//		int actualIndex = 0;
//		while(actualIndex < string.length()){
//			tagStart = -1;
//			tagEnd = -1;
//			while(string.charAt(actualIndex) != TAG_START){
//				actualIndex++;
//			}
//			if(string.charAt(actualIndex) == TAG_START){
//				tagStart = actualIndex;
//			}
//			if(tagStart < 0 || actualIndex == string.length() - 1)
//				break;
//			while(string.charAt(actualIndex++) != SPACE){}
//			if(string.charAt(actualIndex) == SPACE){
//				tagName = string.substring(tagStart + 1, actualIndex);
//				tagName = tagName.replaceAll(STRING_SLASH, STRING_EMPTY);
//			}
//			
//		}
//		return builder.toString();
//	}
	
	public static boolean isHTMLText(String text){
		for(Tag htmlTag: HTML.getAllTags()){
			if(text.contains(String.format(FORMAT_TAG_START, htmlTag.toString()))){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isValidTagName(String text){
		for(Tag htmlTag: HTML.getAllTags()){
			if(htmlTag.toString().equals(text.toLowerCase())){
				return true;
			}
		}
		return false;
	}

	public static String removeTags(String str, boolean replaceBRByNewLines) {
		return removeTags(str, replaceBRByNewLines, System.getProperty("line.separator"));
	}
	
	public static String removeTags(String str, boolean replaceBRByNewLines, String newLine) {
		if(replaceBRByNewLines){
			str = str.replaceAll(STRING_TAG_NEW_LINE, newLine);
		}
		int startPosition = str.indexOf(CHAR_TAG_START);
		int endPosition;
		int spacePosition;
		String tagName = null;
		StringBuilder result = new StringBuilder();
		while (startPosition != -1) {
			endPosition = str.indexOf(CHAR_TAG_END, startPosition);
			spacePosition = str.indexOf(CHAR_SPACE, startPosition);
			tagName = str.substring(startPosition + 1, spacePosition != -1 && spacePosition < endPosition ? spacePosition : (endPosition != -1 ? endPosition : str.length()));
			tagName = tagName.replaceAll(STRING_SLASH, STRING_EMPTY);
			result.append(str.substring(0, startPosition));
			str = (endPosition != -1 && endPosition + 1 < str.length() ? str.substring(endPosition + 1) : STRING_EMPTY);
			if(!isValidTagName(tagName)) {
				result.append(str.substring(0, endPosition != -1 && endPosition + 1 < str.length() ? endPosition + 1 : str.length()));
			}
			startPosition = str.indexOf(CHAR_TAG_START, 0);
		}
		return result.toString();
	}

}
