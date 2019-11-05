package de.invation.code.toval.poiutil.word;

public enum PaperSize {
	
	DIN_ISO_A0	("DIN_ISO A0",  841, 1189),
	DIN_ISO_A1	("DIN_ISO A1",  594,  841),
	DIN_ISO_A2	("DIN_ISO A2",  420,  594),
	DIN_ISO_A3	("DIN_ISO A3",  297,  420),
	DIN_ISO_A4	("DIN_ISO A4",  210,  297),
	DIN_ISO_A5	("DIN_ISO A5",  148,  210),
	DIN_ISO_A6	("DIN_ISO A6",  105,  148),
	DIN_ISO_A7	("DIN_ISO A7",   74,  105),
	DIN_ISO_A8	("DIN_ISO A8",   52,   74),
	DIN_ISO_A9	("DIN_ISO A9",   37,   52),
	DIN_ISO_A10	("DIN_ISO A10",  26,   37),
	
	DIN_ISO_B0	("DIN_ISO B0", 1000, 1414),
	DIN_ISO_B1	("DIN_ISO B1",  707, 1000),
	DIN_ISO_B2	("DIN_ISO B2",  500,  707),
	DIN_ISO_B3	("DIN_ISO B3",  353,  500),
	DIN_ISO_B4	("DIN_ISO B4",  250,  353),
	DIN_ISO_B5	("DIN_ISO B5",  176,  250),
	DIN_ISO_B6	("DIN_ISO B6",  125,  176),
	DIN_ISO_B7	("DIN_ISO B7",   88,  125),
	DIN_ISO_B8	("DIN_ISO B8",   62,   88),
	DIN_ISO_B9	("DIN_ISO B9",   44,   62),
	DIN_ISO_B10	("DIN_ISO B10",  31,   44),
	
	DIN_ISO_C0	("DIN_ISO C0",  917, 1297),
	DIN_ISO_C1	("DIN_ISO C1",  648,  917),
	DIN_ISO_C2	("DIN_ISO C2",  458,  648),
	DIN_ISO_C3	("DIN_ISO C3",  324,  458),
	DIN_ISO_C4	("DIN_ISO C4",  229,  324),
	DIN_ISO_C5	("DIN_ISO C5",  162,  229),
	DIN_ISO_C6	("DIN_ISO C6",  114,  162),
	DIN_ISO_C7	("DIN_ISO C7",   81,  114),
	DIN_ISO_C8	("DIN_ISO C8",   57,   81),
	DIN_ISO_C9	("DIN_ISO C9",   40,   57),
	DIN_ISO_C10	("DIN_ISO C10",  28,   40),
	
	DIN_D0	("DIN D0",  771, 1090),
	DIN_D1	("DIN D1",  545,  771),
	DIN_D2	("DIN D2",  385,  545),
	DIN_D3	("DIN D3",  272,  385),
	DIN_D4	("DIN D4",  192,  272),
	DIN_D5	("DIN D5",  136,  192),
	DIN_D6	("DIN D6",   96,  136),
	DIN_D7	("DIN D7",   68,   96),
	DIN_D8	("DIN D8",   48,   68),
	
	ANSI_A	("ANSI A",   216,    279),
	ANSI_B	("ANSI B",   279,    432),
	ANSI_C	("ANSI C",   432,    559),
	ANSI_D	("ANSI D",   559,    864),
	ANSI_E	("ANSI E",   864,   1118),
	
	US_LETTER	("US Letter",  216, 279),
	US_LEGAL	("US Legal",   216, 356),
	US_TABLOID	("US Tabloid", 279, 432),
	US_LEDGER	("US Ledger",  432, 279);
	
	private static final double MILLIMETERS_PER_INCH 	= 25.4;
	private static final int 	POINTS_PER_INCH 		= 72;
	
	private static final String toStringFormat = 
			  "paper size '%s'\n"
			+ "millimeters: %s x %s\n"
			+ "     inches: %.1f x %.1f\n"
			+ "     points: %s x %s";
	
	public String name;
	public int width_in_millimeters;
	public int height_in_millimeters;
	public double width_in_inches;
	public double height_in_inches;
	public int width_in_points;
	public int height_in_points;
	
	private PaperSize(String name, int width_in_millimeters, int height_in_millimeters) {
		this.name = name;
		this.width_in_millimeters = width_in_millimeters;
		this.height_in_millimeters = height_in_millimeters;
		this.width_in_inches = toInches(width_in_millimeters);
		this.height_in_inches = toInches(height_in_millimeters);
		this.width_in_points = (int) Math.round(width_in_inches * POINTS_PER_INCH);
		this.height_in_points = (int) Math.round(height_in_inches * POINTS_PER_INCH);
	}
	
	private double toInches(int millimeters){
		return millimeters * (1 / MILLIMETERS_PER_INCH);
	}
	
	@Override
	public String toString(){
		return String.format(toStringFormat, name, width_in_millimeters, height_in_millimeters, width_in_inches, height_in_inches, width_in_points, height_in_points);
	}

}
