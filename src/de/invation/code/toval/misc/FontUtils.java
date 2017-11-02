package de.invation.code.toval.misc;

import java.awt.Font;

public class FontUtils {
	
	public static final Font setBold(Font font){
		return font.deriveFont(Font.BOLD);
	}
	
	public static final Font setPlain(Font font){
		return font.deriveFont(Font.PLAIN);
	}
	
	public static final Font setSize(Font font, float size){
		return font.deriveFont(size);
	}

}
