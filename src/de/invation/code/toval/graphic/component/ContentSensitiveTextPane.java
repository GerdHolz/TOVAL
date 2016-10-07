package de.invation.code.toval.graphic.component;

import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;

public class ContentSensitiveTextPane extends JTextPane {

	private static final long serialVersionUID = 8696492311749594152L;

	private static final String FONT_TAG_START = "<font";
	private static final int FONT_SIZE_HTML = 3;
	private static final String fontTagFormat = "<font face=\"%s\" size=\"" + FONT_SIZE_HTML + "\">";
	private static final String htmlFormat1 = "<html>%s%s";
	private static final String htmlFormat2 = "%s%s";
	

	public void setContent(String text) {
		if(isTextHTML(text)){
			setContentType("text/html");
			if(text.contains(FONT_TAG_START)){
				setText(text);
			} else {
				if(text.startsWith("<html>")){
					setText(String.format(htmlFormat1, getFontTag(), text.length() > 6 ? text.substring(6) : ""));
				} else {
					setText(String.format(htmlFormat2, getFontTag(), text));
				}
			}
		} else {
			setContentType("text/plain");
			setText(text);
			setFont(getPreferredFont());
		}
	}
	
	private String getFontTag(){
		Font fontToUse = getPreferredFont();
		return String.format(fontTagFormat, fontToUse.getFamily(), fontToUse.getSize());
	}
	
	private Font getPreferredFont(){
		return new Font(getFont().getName(), Font.PLAIN, getFont().getSize());
	}
	
	private boolean isTextHTML(String text){
		for(Tag htmlTag: HTML.getAllTags()){
			if(text.contains(htmlTag.toString())){
				return true;
			}
		}
		return false;
	}

}
