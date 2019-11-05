package de.invation.code.toval.poiutil.excel;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.ChartLegend;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.LegendPosition;
import org.apache.poi.ss.usermodel.charts.LineChartData;
import org.apache.poi.ss.usermodel.charts.LineChartSeries;
import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDispBlanksAs;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarkerStyle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDispBlanksAs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;

import de.invation.code.toval.file.FileUtils;
import de.invation.code.toval.file.IFileExporter;
import de.invation.code.toval.misc.StringUtils;
import de.invation.code.toval.time.TimeScale;
import de.invation.code.toval.time.TimeValue;
import de.invation.code.toval.validate.Validate;

public abstract class AbstractExcelExport implements IFileExporter {
	
	private 	static final Logger log = LogManager.getLogger(AbstractExcelExport.class);
	
	public 		static final Color 					DEFAULT_COLOR_HEADING 					= new Color(200,200,200);
	public 		static final Color 					DEFAULT_COLOR_REGULAR 					= new Color(255,255,255);
	
	public 		static final Color 					DEFAULT_HYPERLINK_COLOR_HEADING 		= Color.blue;
	public 		static final Color 					DEFAULT_HYPERLINK_COLOR_REGULAR 		= Color.blue;
	
	public 		static final short 					DEFAULT_ROW_HEIGHT_HEADING 				= 400;
	public 		static final short 					DEFAULT_ROW_HEIGHT_REGULAR 				= 400;
	
	public 		static final short 					DEFAULT_FONT_HEIGHT_HEADING 			= 10;
	public 		static final short 					DEFAULT_FONT_HEIGHT_REGULAR 			= 10;
	
	private 	static final HorizontalAlignment 	DEFAULT_HORIZONTAL_ALIGNMENT_HEADING 	= HorizontalAlignment.LEFT;
	private 	static final HorizontalAlignment 	DEFAULT_HORIZONTAL_ALIGNMENT_REGULAR 	= HorizontalAlignment.LEFT;
	
	private 	static final VerticalAlignment 		DEFAULT_VERTICAL_ALIGNMENT_HEADING 		= VerticalAlignment.CENTER;
	private 	static final VerticalAlignment 		DEFAULT_VERTICAL_ALIGNMENT_REGULAR 		= VerticalAlignment.CENTER;
	
	public 		static final boolean 				DEFAULT_USE_DEFAULT_FORMATS 			= true;
	
	protected 	static final String 				NULL_REPLACEMENT_STRING 				= null;
//	protected 	static final String 				NULL_REPLACEMENT_STRING 				= "-null-";
	
	private 	static final String 				DEFAULT_DATE_FORMAT_PATTERN_POI 		= "dd.mm.yyyy";
	private 	static final String 				DEFAULT_DATE_FORMAT_PATTERN 			= "dd.MM.yyyy";
	private 	static final SimpleDateFormat 		DEFAULT_DATE_FORMAT 					= new SimpleDateFormat(DEFAULT_DATE_FORMAT_PATTERN);
	
	private 	static final int 					DEFAULT_FLOAT_PRECISION 				= 2;
	
	private 	static final String 				TO_STRING_FORMAT_TIME_VALUE 			= "%.0f";
	
//	private static final String FORMAT_NUMBER_FORMAT = "#,##0.0000";
	
	private XSSFWorkbook workbook;
	
	private 	File 								outputFile;
	
	private		short								cellFormatDate			= 0;
	
	private 	boolean 							useDefaultFormats 		= DEFAULT_USE_DEFAULT_FORMATS;
	
	private 	boolean 							sheetProtectionEnabled;
	private 	String 								sheetProtectionPassword;
	
	private 	Map<CellType,Color> 				cellColor 				= new HashMap<>();
	private 	Map<CellType,Color> 				hyperlinkColor 			= new HashMap<>();
	private 	Map<CellType,Short> 				rowHeight 				= new HashMap<>();
	private 	Map<CellType,Short> 				fontHeight 				= new HashMap<>();
	private 	Map<CellType,HorizontalAlignment> 	horizontalAlignment 	= new HashMap<>();
	private 	Map<CellType,VerticalAlignment> 	verticalAlignment 		= new HashMap<>();
	
	private 	Map<String,XSSFSheet> 				sheets 					= new HashMap<>();
	
	public AbstractExcelExport(File outputFile){
		this.outputFile = outputFile;
		initialize();
	}
	
	public AbstractExcelExport(String outputFile){
		this(new File(FileUtils.ensureAbsolutePath(outputFile)));
	}
	
	private void initialize(){
		workbook = new XSSFWorkbook();
		setCellFormatDate(DEFAULT_DATE_FORMAT_PATTERN_POI);
		cellColor.put(CellType.HEADING, DEFAULT_COLOR_HEADING);
		cellColor.put(CellType.REGULAR, DEFAULT_COLOR_REGULAR);
		hyperlinkColor.put(CellType.HEADING, DEFAULT_HYPERLINK_COLOR_HEADING);
		hyperlinkColor.put(CellType.REGULAR, DEFAULT_HYPERLINK_COLOR_REGULAR);
		rowHeight.put(CellType.HEADING, DEFAULT_ROW_HEIGHT_HEADING);
		rowHeight.put(CellType.REGULAR, DEFAULT_ROW_HEIGHT_REGULAR);
		fontHeight.put(CellType.HEADING, DEFAULT_FONT_HEIGHT_HEADING);
		fontHeight.put(CellType.REGULAR, DEFAULT_FONT_HEIGHT_REGULAR);
		horizontalAlignment.put(CellType.HEADING, DEFAULT_HORIZONTAL_ALIGNMENT_HEADING);
		horizontalAlignment.put(CellType.REGULAR, DEFAULT_HORIZONTAL_ALIGNMENT_REGULAR);
		verticalAlignment.put(CellType.HEADING, DEFAULT_VERTICAL_ALIGNMENT_HEADING);
		verticalAlignment.put(CellType.REGULAR, DEFAULT_VERTICAL_ALIGNMENT_REGULAR);
	}
	
	@Override
	public boolean contentsToExport() {
		return sheetsCreated();
	}
	
	/**
	 * Indicates whether sheets have been created so far.
	 * @return
	 */
	public boolean sheetsCreated(){
		return !sheets.isEmpty();
	}
	
	/**
	 * Indicates whether there has been created a sheet with the given name before.
	 * @param sheetName sheet name.
	 * @return {@code true} when there has been created a sheet with the same name before; {@code false} otherwise.
	 */
	protected boolean containsSheet(String sheetName){
		return sheets.containsKey(sheetName);
	}
	
	/**
	 * Returns the sheet with the given name.
	 * @param sheetName The name of the requested sheet.
	 * @return sheet with the requested name or {@code null} in case no such sheet exists.
	 */
	protected XSSFSheet getSheet(String sheetName){
		return sheets.get(sheetName);
	}
	
	/**
	 * Indicates whether default formats are used for cell values.<br>
	 * @return
	 */
	public boolean isUseDefaultFormats() {
		return useDefaultFormats;
	}

	public void setUseDefaultFormats(boolean useDefaultFormats) {
		this.useDefaultFormats = useDefaultFormats;
	}

	/**
	 * Returns the sheet protection password.
	 * @return Sheet protection password.
	 */
	private String getSheetProtectionPassword() {
		return sheetProtectionPassword;
	}

	/**
	 * Indicates whether sheet protection is enabled.
	 * @return {@code true} when sheet protection is enabled;
	 * {@code false} otherwise.
	 */
	protected boolean isSheetProtectionEnabled() {
		return sheetProtectionEnabled;
	}
	
	/**
	 * Disables sheet protection.
	 */
	public void disableSheetProtection(){
		this.sheetProtectionEnabled = false;
		this.sheetProtectionPassword = null;
	}

	/**
	 * Enables sheet protection with the given password.
	 * @param sheetProtectionPassword Password for sheet protection.
	 */
	public void enableSheetProtection(String sheetProtectionPassword) {
		this.sheetProtectionEnabled = true;
		this.sheetProtectionPassword = sheetProtectionPassword;
	}

	@Override
	public File getOutputFile() {
		return outputFile;
	}

	@Override
	public void createFile() throws Exception {
		if(!contentsToExport())
			throw new Exception("No content to export, file creation aborted.");
		
		log.info("Writing excel file...");
		addContent(workbook);
        FileOutputStream out = new FileOutputStream(outputFile);
        workbook.write(out);
        out.close();
        workbook.close();
        log.info("File creation finished.");
	}
	
	/**
	 * Returns the default precision used for float values.<br>
	 * Precision is interpreted in terms of the number of digits after "the comma".
	 * @return {@link #DEFAULT_FLOAT_PRECISION}
	 */
	protected int getDefaultFloatPrecision(){
		return DEFAULT_FLOAT_PRECISION;
	}
	
	// Cell style creation
	
	/**
	 * Creates a cell style for hyperlinks.<br>
	 * The given cell type is used to determine font, vertical and horizontal alignment.
	 * The font color is determined using {@link #getHyperlinkCellColor(CellType)}.
	 * @see #createCellStyle(CellType)
	 * @see #getHyperlinkCellColor(CellType)
	 * @return
	 */
	protected XSSFCellStyle createHLinkCellStyle(CellType cellType){
		XSSFCellStyle hlink_style = createCellStyle(cellType);
        Font hlink_font = hlink_style.getFont();
        hlink_font.setColor(new XSSFColor(getHyperlinkCellColor(cellType)).getIndex());
        return hlink_style;
	}

	/**
	 * Creates a new cell style with the given characteristics.<br>
	 * The given cell type is used to determine font, cell color, vertical and horizontal alignment.
	 * @see #getFont(CellType)
	 * @see #getCellColor(CellType)
	 * @see #getVerticalAlignment(CellType)
	 * @see #getHorizontalAlignment(CellType)
	 * @see #createCellStyle(CellType, Color)
	 * @param cellType Cell type for which the style is created.
	 * @return
	 */
	protected XSSFCellStyle createCellStyle(CellType cellType){
		return createCellStyle(cellType, getCellColor(cellType));
	}
	
	/**
	 * Creates a new cell style with the given characteristics.<br>
	 * The given cell type is used to determine font, vertical and horizontal alignment.
	 * @see #getFont(CellType)
	 * @see #getVerticalAlignment(CellType)
	 * @see #getHorizontalAlignment(CellType)
	 * @see #createCellStyle(Font, Color, VerticalAlignment, HorizontalAlignment)
	 * @param cellType Cell type for which the style is created.
	 * @param cellColor Cell color.
	 * @return
	 */
	protected XSSFCellStyle createCellStyle(CellType cellType, Color cellColor){
		return createCellStyle(getFont(cellType), cellColor, getVerticalAlignment(cellType), getHorizontalAlignment(cellType));
	}
	
	/**
	 * Creates a new cell style with the given characteristics.
	 * @param font Font.
	 * @param cellColor Cell color.
	 * @param alignVertical Vertical alignment.
	 * @param alignHorizontal Horizontal alignment.
	 * @return
	 */
	private XSSFCellStyle createCellStyle(Font font, Color cellColor, VerticalAlignment alignVertical, HorizontalAlignment alignHorizontal){
		XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();
		if(cellColor != null){
			cellStyle.setFillForegroundColor(new XSSFColor(cellColor));       
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		cellStyle.setFont(font);
		cellStyle.setVerticalAlignment(alignVertical);
		cellStyle.setAlignment(alignHorizontal);
		
        return cellStyle;
	}
	
	// Cell style assignment
	
	/**
	 * Applies toe given cell style to all cells within the given row (defined by start and end index).
	 * @param row Target row for cell style.
	 * @param cellStyle Cell style to apply.
	 * @param start Start index for target cells within the given row.
	 * @param end End index for target cells within the given row.
	 */
	protected void setCellStyle(Row row, CellStyle cellStyle, int start, int end){
		for(int i=start; i<=end; i++){
			row.getCell(i).setCellStyle(cellStyle);
		}
	}
	
	// Data formats
	
	/**
	 * Sets the data format of the given cell style according to the given format string.
	 * @param cellStyle Target cell style.
	 * @param dataFormatString Data format string.
	 */
	protected void setDataFormat(XSSFCellStyle cellStyle, String dataFormatString){
		if(dataFormatString != null && !dataFormatString.isEmpty()){
			cellStyle.setDataFormat(workbook.createDataFormat().getFormat(dataFormatString));
		}
	}
	
	/**
	 * Returns a number format according to the given precision.<br>
	 * Precision is interpreted in terms of the number of digits after "the comma".
	 * @param precision Desired precision.
	 * @return
	 */
	protected short getCellFormatNumber(int precision){
		Validate.biggerEqual(precision, 0);
		if(precision == 0)
			return workbook.createDataFormat().getFormat("0");
		return workbook.createDataFormat().getFormat("0.".concat(StringUtils.createString('0', precision)));
	}
	
	// Fonts
	
	/**
	 * Returns a font with the default height for the given cell type.<br>
	 * The font face is neither bold nor italic.
	 * @see #getFont(CellType, boolean, boolean)
	 * @param type Cell Type for which the font is requested.
	 * @return
	 */
	protected Font getFont(CellType type){
		return getFont(type, false, false);
	}
	
	/**
	 * Returns a font with the given characteristics.<br>
	 * The cell type is used to get the font height.
	 * @see #getFontHeightInPoints(CellType)
	 * @see #getFont(short, boolean, boolean)
	 * @param cellType Cell type for which the font is requested.
	 * @param bold Indicates whether the font face should be bold.
	 * @param italic Indicates whether the font face should be italic.
	 * @return
	 */
	protected Font getFont(CellType cellType, boolean bold, boolean italic){
		return getFont(getFontHeightInPoints(cellType), bold, italic);
	}
	
	/**
	 * Returns a font with the given characteristics.
	 * @param fontHeight Font height in points.
	 * @param bold Indicates whether the font face should be bold.
	 * @param italic Indicates whether the font face should be italic.
	 * @return
	 */
	protected Font getFont(short fontHeight, boolean bold, boolean italic){
		Font font = workbook.createFont();
	    font.setFontHeightInPoints(fontHeight);
	    font.setBold(bold);
	    font.setItalic(italic);
	    return font;
	}
	
	/**
	 * Returns the font height (in points) for cells of the given type.<br>
	 * Default for regular cells: {@link #DEFAULT_FONT_HEIGHT_REGULAR}<br>
	 * Default for heading cells: {@link #DEFAULT_FONT_HEIGHT_HEADING}
	 * @param cellType Cell type for which the font height is requested.
	 * @return
	 */
	protected Short getFontHeightInPoints(CellType cellType){
		return fontHeight.get(cellType);
	}
	
	/**
	 * Sets the font height (in points) to be used for cells of the given type.
	 * @param cellType Cell type for which the font height is set.
	 * @param fontHeight Font height (in points) to be used.
	 */
	protected void setFontHeightInPoints(CellType cellType, short fontHeight){
		Validate.notNull(cellType);
		Validate.notNull(fontHeight);
		this.fontHeight.put(cellType, fontHeight);
	}
	
	// Alignments
	
	/**
	 * Returns the vertical alignment used for cells of the given type.<br>
	 * Default for regular cells: {@link #DEFAULT_VERTICAL_ALIGNMENT_REGULAR}<br>
	 * Default for heading cells: {@link #DEFAULT_VERTICAL_ALIGNMENT_HEADING}
	 * @param cellType Cell type for which the alignment is requested.
	 * @return
	 */
	protected VerticalAlignment getVerticalAlignment(CellType cellType){
		return verticalAlignment.get(cellType);
	}
	
	/**
	 * Sets the vertical alignment to be used for cells of the given type.
	 * @param cellType Cell type for which the alignment is set.
	 * @param alignment alignment to be used.
	 */
	protected void setVerticalAlignment(CellType cellType, VerticalAlignment alignment){
		Validate.notNull(cellType);
		Validate.notNull(alignment);
		verticalAlignment.put(cellType, alignment);
	}
	
	/**
	 * Returns the horizontal alignment used for cells of the given type.<br>
	 * Default for regular cells: {@link #DEFAULT_HORIZONTAL_ALIGNMENT_REGULAR}<br>
	 * Default for heading cells: {@link #DEFAULT_HORIZONTAL_ALIGNMENT_HEADING}
	 * @param cellType Cell type for which the alignment is requested.
	 * @return
	 */
	protected HorizontalAlignment getHorizontalAlignment(CellType cellType){
		return horizontalAlignment.get(cellType);
	}
	
	/**
	 * Sets the horizontal alignment to be used for cells of the given type.
	 * @param cellType Cell type for which the alignment is set.
	 * @param alignment alignment to be used.
	 */
	protected void setHorizontalAlignment(CellType cellType, HorizontalAlignment alignment){
		Validate.notNull(cellType);
		Validate.notNull(alignment);
		horizontalAlignment.put(cellType, alignment);
	}
	
	// Row height
	
	/**
	 * Returns the row height used for cells of the given type.<br>
	 * Default for regular cells: {@link #DEFAULT_ROW_HEIGHT_REGULAR}<br>
	 * Default for heading cells: {@link #DEFAULT_ROW_HEIGHT_HEADING}
	 * @param cellType Cell type for which the row height is requested.
	 * @return
	 */
	protected Short getRowHeight(CellType cellType){
		return rowHeight.get(cellType);
	}
	
	/**
	 * Sets the row height to be used for cells of the given type.
	 * @param cellType Cell type for which the row height is set.
	 * @param rowHeight row height to be used.
	 */
	protected void setRowHeight(CellType cellType, short rowHeight){
		Validate.notNull(cellType);
		Validate.notNull(rowHeight);
		this.rowHeight.put(cellType, rowHeight);
	}
	
	// Cell color
	
	/**
	 * Returns the color used for cells of the given type.<br>
	 * This method is called when new cell styles are created using {@link #createCellStyle(CellType)}.<br>
	 * Default for regular cells: {@link #DEFAULT_COLOR_REGULAR}<br>
	 * Default for heading cells: {@link #DEFAULT_COLOR_HEADING}
	 * @see #setCellColor(CellType, Color)
	 * @param type Cell type for which the color is requested.
	 * @return
	 */
	protected Color getCellColor(CellType cellType) {
		return cellColor.get(cellType);
	}

	/**
	 * Sets the color to be used for cells of the given type.
	 * @param cellType Cell type for which the color is set.
	 * @param cellColor Color to be used.
	 */
	protected void setCellColor(CellType cellType, Color cellColor) {
		Validate.notNull(cellType);
		Validate.notNull(cellColor);
		this.cellColor.put(cellType, cellColor);
	}
	
	/**
	 * Returns the color used for cells of the given type containing hyperlinks.<br>
	 * This method is called when new cell styles are created using {@link #createCellStyle(CellType)}.<br>
	 * Default for regular cells: {@link #DEFAULT_HYPERLINK_COLOR_REGULAR}<br>
	 * Default for heading cells: {@link #DEFAULT_HYPERLINK_COLOR_HEADING}
	 * @see #setHyperlinkCellColor(CellType, Color)
	 * @param type Cell type for which the color is requested.
	 * @return
	 */
	public Color getHyperlinkCellColor(CellType cellType) {
		return hyperlinkColor.get(cellType);
	}

	/**
	 * Sets the color to be used for cells of the given type containing hyperlinks.
	 * @param cellType Cell type for which the color is set.
	 * @param cellColor Color to be used.
	 */
	public void setHyperlinkCellColor(CellType cellType, Color cellColor) {
		Validate.notNull(cellType);
		Validate.notNull(cellColor);
		this.hyperlinkColor.put(cellType, cellColor);
	}

	/**
	 * Returns a signal color (yellow to red) depending on a time value ({@code time}) and a threshold for the "red mark" {@code timeToRedMark}.<br>
	 * The nearer the given timevalue to the threshold the nearer the color value to red (starting with yellow).
	 * @param time Time value.
	 * @param timeToRedMark Threshold from which the returned color will be red.
	 * @return
	 */
	protected Color signalColor(TimeValue time, TimeValue timeToRedMark){
		timeToRedMark.setScale(TimeScale.DAYS, true);
		time.setScale(TimeScale.DAYS, true);
		if(time.getValue() <= 0)
			return new Color(0,255,0);
		if(time.isBiggerEqualThan(timeToRedMark))
			return new Color(255,0,0);
		int green = (int) (255 - Math.round((255/timeToRedMark.getValue())*time.getValue()));
		return new Color(255, green, 0);
	}
	
	// Sheet creation

	/**
	 * This method is called when file creation is triggered using {@link #createExcelFile()}.
	 * subclasses have to implement this method to place custom code for sheet creation.
	 * @param workbook Workbook where sheets are placed in.
	 */
	protected abstract void addContent(XSSFWorkbook workbook) throws Exception;
	
	/**
	 * Adds a new sheet with the given name.
	 * @param sheetName Name for the new sheet.
	 * @return
	 * @throws Exception In case there is already a sheet with the same name.
	 */
	protected XSSFSheet addSheet(String sheetName) throws Exception{
		if(containsSheet(sheetName))
			throw new Exception("There is already a sheet with same name");
		XSSFSheet newSheet = workbook.createSheet(sheetName);
		sheets.put(sheetName, newSheet);
		return newSheet;
	}
	
	/**
	 * Adds a new sheet with the given name and inserts a heading row with the given values.<br>
	 * String values for the heading row are converted to formatted cell values and inserted 
	 * using {@link #addHeadingRow(XSSFSheet, int, FormattedCellValue...)}. By this, the default cell style for heading rows is applied.
	 * @param sheetName Name for the new sheet.
	 * @param colNames Column names for the heading row to be inserted in the new sheet.
	 * @return
	 * @throws Exception In case there is already a sheet with the same name.
	 */
	protected SheetCreationResult addSheet(String sheetName, String... colNames) throws Exception{
		XSSFSheet sheet = addSheet(sheetName);
		addHeadingRow(sheet, 0, convertValues(colNames));
		if(isSheetProtectionEnabled())
			enableSheetProtection(sheet);
		return new SheetCreationResult(sheet, colNames.length, 1);
	}
	
	/**
	 * Enables sheet protection for the given sheet.<br>
	 * With enabled sheet protection, cells can be protected from unwanted manipulation. 
	 * This method activates standard Excel functions for password based cell protection. 
	 * The password for protection is gathered by calling {@link #getSheetProtectionPassword()}; 
	 * protection settings are gathered by calling {@link #getSheetProtectionSettings(XSSFSheet)}.<br>
	 * <br>
	 * This method is called when sheets are added and sheet protection has been activated before by 
	 * {@link #enableSheetProtection(String)} and thereby providing the sheet protection password.
	 * @param sheet Sheet for which protection is enabled.
	 */
	private void enableSheetProtection(XSSFSheet sheet){
		sheet.protectSheet(getSheetProtectionPassword());
		CTSheetProtection sheetProtection = sheet.getCTWorksheet().getSheetProtection();
		SheetProtectionSettings.apply(getSheetProtectionSettings(sheet), sheetProtection);
	}
	
	/**
	 * Returns the settings to be used for protecting the given sheet.<br>
	 * By default, this method returns the default protection settings in {@link SheetProtectionSettings#defaultSettings()}.
	 * @param sheet Sheet for which protection settings are requested.
	 * @return
	 */
	protected SheetProtectionSettings getSheetProtectionSettings(XSSFSheet sheet){
		return SheetProtectionSettings.defaultSettings();
	}
	
	/**
	 * Adds a new sheet with the given name and inserts all given values into the first column using the given header.
	 * @param sheetName Name for the new sheet.
	 * @param heading Heading for the column within the new sheet.
	 * @param values Values to be inserted into the first sheet column.
	 * @return
	 * @throws Exception In case there is already a sheet with the same name.
	 */
	protected SheetCreationResult addSheetWithColumn(String sheetName, String heading, List<String> values) throws Exception{
		XSSFSheet sheet = addSheet(sheetName);
		addHeadingRow(sheet, 0, convertValues(new String[]{heading}));
		int rowNumber = 0;
		for(String str: values){
			addNormalRow(sheet, ++rowNumber, convertValues(new String[]{str}));
		}
		if(isSheetProtectionEnabled())
			enableSheetProtection(sheet);
		sheet.setAutoFilter(new CellRangeAddress(0,rowNumber,0,0));
		return new SheetCreationResult(sheet, 1, rowNumber);
	}
	
	// Chart creation
	
	/**
	 * Adds a line chart to the given sheet.<br>
	 * @param sheet Sheet where chart is to be added.
	 * @param xAxis Cell range which contains X axis information.
	 * @param values Row containing chart values.
	 * @param nameForValueSeries Name for value series in row.
	 * @param gapsForNullValues When {@code true}, the line chart will show gaps for {@code null} values within values series; otherwise {@code null} values will be treated as zero.
	 * @param chartPosition Chart position.
	 * @throws Exception
	 */
	protected void addLineChart(XSSFSheet sheet, Row xAxis, Row values, String nameForValueSeries, boolean gapsForNullValues, CellRangeAddress chartPosition) throws Exception{
		CellRangeAddress cellRangeXAxis = createCellRange(xAxis);
		addLineChart(sheet, gapsForNullValues, chartPosition, cellRangeXAxis, createCellRange(values), nameForValueSeries);
	}
	
	/**
	 * Adds a line chart to the given sheet.<br>
	 * @param sheet Sheet where chart is to be added.
	 * @param xAxis Cell range which contains X axis information.
	 * @param gapsForNullValues When {@code true}, the line chart will show gaps for {@code null} values within values series; otherwise {@code null} values will be treated as zero.
	 * @param values Rows containing chart values.
	 * @param names Names for value series in rows.
	 * @param chartPosition Chart position.
	 * @throws Exception
	 */
	protected void addLineChart(XSSFSheet sheet, Row xAxis, boolean gapsForNullValues, List<Row> values, List<String> names, CellRangeAddress chartPosition) throws Exception{
		addLineChart(sheet, gapsForNullValues, chartPosition, createCellRange(xAxis), createChartValueSeries(sheet, createCellRanges(values), names));
	}
	
	/**
	 * Adds a line chart to the given sheet.<br>
	 * @param sheet Sheet where chart is to be added.
	 * @param gapsForNullValues When {@code true}, the line chart will show gaps for {@code null} values within values series; otherwise {@code null} values will be treated as zero.
	 * @param chartPosition Chart position.
	 * @param xAxis Cell range which contains X axis information.
	 * @param valueSeries Cell range for value series to insert.
	 * @param nameForValueSeries Name to be used for inserted value series.
	 */
	protected void addLineChart(XSSFSheet sheet, boolean gapsForNullValues, CellRangeAddress chartPosition, CellRangeAddress xAxis, CellRangeAddress cellRangeOfValueSeries, String nameForValueSeries){
		addLineChart(sheet, gapsForNullValues, chartPosition, xAxis, new ChartValueSeries[]{createChartValueSeries(sheet, cellRangeOfValueSeries, nameForValueSeries)});
	}
	
	/**
	 * Adds a line chart to the given sheet.<br>
	 * @param sheet Sheet where chart is to be added.
	 * @param gapsForNullValues When {@code true}, the line chart will show gaps for {@code null} values within values series; otherwise {@code null} values will be treated as zero.
	 * @param chartPosition Chart position.
	 * @param xAxis Cell range which contains X axis information.
	 * @param valueSeries (Multiple) value series to insert.
	 */
	protected void addLineChart(XSSFSheet sheet, boolean gapsForNullValues, CellRangeAddress chartPosition, CellRangeAddress xAxis, ChartValueSeries... valueSeries){
		addLineChart(sheet, gapsForNullValues, chartPosition, createChartDataSource(sheet, xAxis), Arrays.asList(valueSeries));
	}
	
	/**
	 * Adds a line chart to the given sheet.<br>
	 * @param sheet Sheet where chart is to be added.
	 * @param gapsForNullValues When {@code true}, the line chart will show gaps for {@code null} values within values series; otherwise {@code null} values will be treated as zero.
	 * @param chartPosition Chart position.
	 * @param xAxis Cell range which contains X axis information.
	 * @param valueSeries (Multiple) value series to insert.
	 */
	protected void addLineChart(XSSFSheet sheet, boolean gapsForNullValues, CellRangeAddress chartPosition, CellRangeAddress xAxis, Collection<ChartValueSeries> valueSeries){
		addLineChart(sheet, gapsForNullValues, chartPosition, createChartDataSource(sheet, xAxis), valueSeries);
	}
	
	/**
	 * Adds a line chart to the given sheet.<br>
	 * @param sheet Sheet where chart is to be added.
	 * @param gapsForNullValues When {@code true}, the line chart will show gaps for {@code null} values within values series; otherwise {@code null} values will be treated as zero.
	 * @param chartPosition Chart position.
	 * @param xAxis Cell range in form of chart data source which contains X axis information.
	 * @param valueSeries (Multiple) value series to insert.
	 */
	protected <T> void addLineChart(XSSFSheet sheet, boolean gapsForNullValues, CellRangeAddress chartPosition, ChartDataSource<T> xAxis, Collection<ChartValueSeries> valueSeries){
		Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, chartPosition.getFirstColumn(), chartPosition.getFirstRow(), chartPosition.getLastColumn(), chartPosition.getLastRow());

        XSSFChart chart = (XSSFChart) drawing.createChart(anchor);
        
        if(gapsForNullValues){
        	CTDispBlanksAs disp = CTDispBlanksAs.Factory.newInstance();
            disp.setVal(STDispBlanksAs.GAP);
            chart.getCTChart().setDispBlanksAs(disp);
        }
        
        ChartLegend legend = chart.getOrCreateLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        LineChartData data = chart.getChartDataFactory().createLineChartData();

        // Use a category axis for the bottom axis.
        ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        for(ChartValueSeries valueSer: valueSeries){
 
        	LineChartSeries chartSeries = data.addSeries(xAxis, valueSer.dataSource);
        	chartSeries.setTitle(valueSer.name);
        }
        chart.plot(data, bottomAxis, leftAxis);
        
        CTPlotArea plotArea = chart.getCTChart().getPlotArea();
        CTMarker ctMarker = CTMarker.Factory.newInstance();
        ctMarker.setSymbol(CTMarkerStyle.Factory.newInstance());
        for (CTLineSer ser : plotArea.getLineChartArray()[0].getSerArray()) {
            ser.setMarker(ctMarker);
        }
	}
	
	/**
	 * Creates a value series for line chart creation within a sheet out of a cell range.
	 * @param sheet Sheet of value series.
	 * @param cellRangeAddress Cell range of value series.
	 * @param name Name for the value series.
	 * @return Value series for cell range to be used in line charts.
	 */
	protected ChartValueSeries createChartValueSeries(XSSFSheet sheet, CellRangeAddress cellRangeAddress, String name){
		return new ChartValueSeries(createChartDataSource(sheet, cellRangeAddress), name);
	}
	
	/**
	 * Creates a value series for line chart creation within a sheet out of a cell range.
	 * @param sheet Sheet of value series.
	 * @param cellRangeAddresses Cell ranges of value series.
	 * @param names Names for the value series.
	 * @return Value series for all cell ranges to be used in line charts.
	 */
	protected List<ChartValueSeries> createChartValueSeries(XSSFSheet sheet, List<CellRangeAddress> cellRangeAddresses, List<String> names){
		Validate.notEmpty(cellRangeAddresses);
		Validate.notEmpty(names);
		Validate.isTrue(cellRangeAddresses.size() == names.size());
		List<ChartValueSeries> result = new ArrayList<>();
		for(int i=0; i<cellRangeAddresses.size(); i++)
			result.add(createChartValueSeries(sheet, cellRangeAddresses.get(i), names.get(i)));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Number> ChartValueSeries createChartValueSeries(Collection<T> elements, String name){
		return createChartValueSeries(elements.toArray((T[]) Array.newInstance(Number.class, elements.size())), name);
	}
	
	protected <T extends Number> ChartValueSeries createChartValueSeries(T[] elements, String name){
		return new ChartValueSeries(createChartDataSource(elements), name);
	}
	
	/**
	 * Creates a chart data source for line chart creation out of a cell range in a given sheet.
	 * @param sheet Sheet of chart data source.
	 * @param cellRangeAddress Cell range of data source.
	 * @return Data source to be used as x-axis in line chars.
	 */
	protected ChartDataSource<Number> createChartDataSource(XSSFSheet sheet, CellRangeAddress cellRangeAddress){
		return DataSources.fromNumericCellRange(sheet, cellRangeAddress);
	}
	
	/**
	 * Creates a chart data source for line chart creation out of an element array.
	 * @param elements Element array to be used as data source.
	 * @return
	 */
	protected <T> ChartDataSource<T> createChartDataSource(T[] elements){
		return DataSources.fromArray(elements);
	}
	
	/**
	 * Creates a cell range according to the given row and column indexes.
	 * @param firstRowIndex Index of first row.
	 * @param lastRowIndex Index of last row.
	 * @param firstColIndex Index of first column.
	 * @param lastColIndex Index of last column.
	 * @return
	 */
	protected CellRangeAddress createCellRange(int firstRowIndex, int lastRowIndex, int firstColIndex, int lastColIndex){
		return new CellRangeAddress(firstRowIndex, lastRowIndex, firstColIndex, lastColIndex);
	}
	
	/**
	 * Creates a cell range out of a row.
	 * @param row Row for cell range creation
	 * @return Cell range for the given row.
	 */
	protected CellRangeAddress createCellRange(Row row){
		return createCellRange(row.getRowNum(), row.getRowNum(), 0, row.getLastCellNum() - 1);
	}
	
	/**
	 * Creates cell ranges out of rows.
	 * @param rows Rows for cell range creation
	 * @return Cell ranges for all given rows.
	 */
	protected List<CellRangeAddress> createCellRanges(Collection<Row> rows){
		List<CellRangeAddress> result = new ArrayList<>();
		for(Row row: rows)
			result.add(createCellRange(row));
		return result;
	}
	
	/**
	 * Creates a value series for line chart creation within a sheet out of a cell range.
	 * @param sheet Sheet of value series.
	 * @param cellRangeAddress Cell range of value series.
	 * @param name Name for the value series.
	 * @return Value series for cell range to be used in line charts.
	 */
	protected ChartValueSeries createChartValueSeriesFromRow(XSSFSheet sheet, Row row, String name){
		return createChartValueSeries(sheet, createCellRange(row), name);
	}
	
	/**
	 * Creates a value series for line chart creation within a sheet out of a cell range.
	 * @param sheet Sheet of value series.
	 * @param cellRangeAddresses Cell ranges of value series.
	 * @param names Names for the value series.
	 * @return Value series for all cell ranges to be used in line charts.
	 */
	protected List<ChartValueSeries> createChartValueSeriesFromRows(XSSFSheet sheet, List<Row> rows, List<String> names){
		return createChartValueSeries(sheet, createCellRanges(rows), names);
	}
	
	// Row creation
	
	/**
	 * Adds a new "heading" row (with values) to an existing sheet.<br>
	 * Normal rows are distinguished from heading rows (see {@link CellType}), basically because of different cell styles for inserted values.
	 * The position at which the new row is inserted is defined by the parameter {@code rowIndex}.
	 * The new row will contain all cell values in the order they are given using the cell styled within the formatted cell value objects.
	 * In case one of these values does not contain a cell style, the default cell style for regular table rows 
	 * (defined by {@link #createCellStyle(CellType)} in combination with {@link CellType#REGULAR}) will be used.
	 * This method calls {@link #addRow(Sheet, int, XSSFCellStyle, FormattedCellValue...)} accordingly.
	 * @param sheet Sheet where new row is to be added.
	 * @param rowIndex Index for new row.
	 * @param cellValues Cell values to be used for the new row cells.
	 * @return
	 */
	protected final Row addHeadingRow(XSSFSheet sheet, int rowIndex, FormattedCellValue... cellValues){
		return addRow(sheet, rowIndex, createCellStyle(CellType.HEADING), cellValues);
	}
	
	/**
	 * Adds a new "normal" row (with values) to an existing sheet.<br>
	 * Normal rows are distinguished from heading rows (see {@link CellType}), basically because of different cell styles for inserted values.
	 * The position at which the new row is inserted is defined by the parameter {@code rowIndex}.
	 * The new row will contain all cell values in the order they are given.
	 * This method converts the given Object values into formatted cell values and then calls {@link #addRow(Sheet, int, XSSFCellStyle, FormattedCellValue...)} accordingly.
	 * For conversion, a new cell style is created on base of regular cell type ({@link CellType#REGULAR}).
	 * @param sheet Sheet where new row is to be added.
	 * @param rowIndex Index for new row.
	 * @param cellValues Cell values to be used for the new row cells.
	 * @return
	 */
	protected Row addNormalRow(Sheet sheet, int rowIndex, Object... cellValues){
		return addNormalRow(sheet, rowIndex, convertValues(cellValues, createCellStyle(CellType.REGULAR)));
	}
	
	/**
	 * Adds a new "normal" row (with values) to an existing sheet.<br>
	 * Normal rows are distinguished from heading rows (see {@link CellType}), basically because of different cell styles for inserted values.
	 * The position at which the new row is inserted is defined by the parameter {@code rowIndex}.
	 * The new row will contain all cell values in the order they are given using the cell styles within the formatted cell value objects.
	 * In case one of these values does not contain a cell style, the default cell style for regular table rows 
	 * (defined by {@link #createCellStyle(CellType)} in combination with {@link CellType#REGULAR}) will be used.
	 * This method calls {@link #addRow(Sheet, int, XSSFCellStyle, FormattedCellValue...)} accordingly.
	 * @param sheet Sheet where new row is to be added.
	 * @param rowIndex Index for new row.
	 * @param cellValues Cell values to be used for the new row cells.
	 * @return
	 */
	protected Row addNormalRow(Sheet sheet, int rowIndex, FormattedCellValue... cellValues){
		return addRow(sheet, rowIndex, createCellStyle(CellType.REGULAR), cellValues);
	}
	
	/**
	 * Adds a new row (with values) to an existing sheet.<br>
	 * The position at which the new row is inserted is defined by the parameter {@code rowIndex}.
	 * The new row will contain all cell values in the order they are given using the cell styled within the formatted cell value objects.
	 * In case one of these values does not contain a cell style, the fallback cell style {@code fallbackCellStyle} is used.
	 * @param sheet Sheet where new row is to be added.
	 * @param rowIndex Index for new row.
	 * @param fallbackCellStyle Cell style to be used for values which do not contain a cell style.
	 * @param cellValues Cell values to be used for the new row cells.
	 * @return
	 */
	private Row addRow(Sheet sheet, int rowIndex, XSSFCellStyle fallbackCellStyle, FormattedCellValue... cellValues){
		Row row = sheet.createRow(rowIndex);
		for(int i=0; i<cellValues.length; i++){
			setCellValue(row.createCell(i), cellValues[i], fallbackCellStyle);
		}
		return row;
	}
	
	/**
	 * Inserts the given formatted cell value into the given cell.<br>
	 * When sheet protection is enabled, the cell is locked.<br>
	 * The cell value is set according to the concrete type of the cell value (of type Object) in {@link formattedCellValue}.
	 * For this, the method distinguishs between hyperlink, string, number, boolean, date, calendar and rich text string. 
	 * The style of the cell is set according to the style which is contained in {@link formattedCellValue}. 
	 * In case t{@link formattedCellValue} does not contain a cell style, {@link fallbackCellStyle} will be used.
	 * @param cell Target cell.
	 * @param formattedCellValue Formatted cell value to insert.
	 * @param fallbackCellStyle Cell style to be used when formatted cell value does not contain a cell style.
	 */
	protected void setCellValue(Cell cell, FormattedCellValue formattedCellValue, XSSFCellStyle fallbackCellStyle){
		CellReference cellReference = new CellReference(cell);
		Object cellValue = formattedCellValue.cellValue;
		XSSFCellStyle cellStyle = formattedCellValue.cellStyle != null ? formattedCellValue.cellStyle : fallbackCellStyle;
		if(isSheetProtectionEnabled())
			cellStyle.setLocked(protectCell((XSSFSheet) cell.getSheet(), cell.getRowIndex(), cell.getColumnIndex()));
		
		if(cellValue != null){
			if(Hyperlink.class.isAssignableFrom(cellValue.getClass())){
				cell.setCellValue(((Hyperlink) cellValue).getLabel());
				cell.setHyperlink((Hyperlink) cellValue);
			} else if(String.class.isAssignableFrom(cellValue.getClass())){
				cell.setCellValue((String) cellValue);
				cellReference.formatAsString();
			} else if(Number.class.isAssignableFrom(cellValue.getClass())){
				cell.setCellValue(((Number) cellValue).doubleValue());
			} else if(Boolean.class.isAssignableFrom(cellValue.getClass())){
				cell.setCellValue((Boolean) cellValue);
			} else if(Date.class.isAssignableFrom(cellValue.getClass())){
				cell.setCellValue((Date) cellValue);
			} else if(Calendar.class.isAssignableFrom(cellValue.getClass())){
				cell.setCellValue((Calendar) cellValue);
			} else if(RichTextString.class.isAssignableFrom(cellValue.getClass())){
				cell.setCellValue((RichTextString) cellValue);
				cellReference.formatAsString();
			}
		}
		
		cell.setCellStyle(cellStyle);
	}
	
	/**
	 * Inserts the given formatted cell value into all cells within the given range.
	 * @see #setCellValue(Cell, FormattedCellValue, XSSFCellStyle)
	 * @param sheet Sheet which contains the cells.
	 * @param cellRange Range which defines the target cells.
	 * @param formattedCellValue Formatted cell value to insert.
	 * @param fallbackCellStyle Cell style to be used when formatted cell value does not contain a cell style.
	 */
	protected void setCellValue(XSSFSheet sheet, CellRangeAddressList cellRange, FormattedCellValue formattedCellValue, XSSFCellStyle fallbackCellStyle){
		for(CellRangeAddress range: cellRange.getCellRangeAddresses()){
			for(int columnIndex=range.getFirstColumn(); columnIndex<=range.getLastColumn(); columnIndex++){
				for(int rowIndex=range.getFirstRow(); rowIndex<=range.getLastRow(); rowIndex++){
					if(sheet.getRow(rowIndex).getCell(columnIndex) == null){
						XSSFCell newCell = sheet.getRow(rowIndex).createCell(columnIndex);
						XSSFCellStyle cellStyle = createCellStyle(CellType.REGULAR);
						if(isSheetProtectionEnabled())
							cellStyle.setLocked(protectCell(sheet, rowIndex, columnIndex));
						newCell.setCellStyle(cellStyle);
					}
					setCellValue(sheet.getRow(rowIndex).getCell(columnIndex), formattedCellValue, fallbackCellStyle);
				}
			}
		}
	}
	
	/**
	 * Defines whether the cell defined by row and column index should be protected.<br>
	 * When sheet protection is enabled (via {@link #enableSheetProtection(XSSFSheet)} or {@link #enableSheetProtection(String)}),
	 * this method will be called for every cell within every sheet to which a value is assigned (see {@link #setCellValue(Cell, FormattedCellValue, XSSFCellStyle)}).
	 * By returning either <code>true</code> or <code>false</code>, cells for protection can be defined.
	 * @param sheet Sheet which contains the cell.
	 * @param rowIndex Row index of cell in question.
	 * @param colIndex Column index of cell in question.
	 * @return
	 */
	protected abstract boolean protectCell(XSSFSheet sheet, int rowIndex, int colIndex);
	
	// Filter and autosize

	/**
	 * Adds a filter to all columns within the given sheet.<br>
	 * <br>
	 * This method ensures that within the sheet, all columns will have the Excel filter menu where values can be selected/deselected, sorted, etc.
	 * @param sheet Sheet for whose columns a filter is to be added.
	 */
	protected void addFilterForColumns(SheetCreationResult sheet){
		addFilterForColumns(sheet.getSheet(), sheet.getRowCount(), sheet.getColCount()-1);
	}
	
	/**
	 * Adds a filter for all columns up to the column with the given index. 
	 * The filter comprises all cells up to the given row index.<br>
	 * <br>
	 * This method ensures that within the sheet, all defined columns will have the Excel filter menu where values can be selected/deselected, sorted, etc.
	 * @param sheet Sheet which contains the columns.
	 * @param rowIndex Index of the last filter row (inclusive).
	 * @param colIndex Index of the last filter column (inclusive).
	 */
	protected void addFilterForColumns(XSSFSheet sheet, int rowIndex, int colIndex){
		sheet.setAutoFilter(new CellRangeAddress(0, rowIndex, 0, colIndex));
	}
	
	/**
	 * Adds a filter to the given column (defined by column index) comprising all cells up to the given row index.<br>
	 * <br>
	 * This method ensures that within the sheet, the defined column will have the Excel filter menu where values can be selected/deselected, sorted, etc.
	 * @param sheet Sheet which contains the column.
	 * @param rowIndex Index of the last filter row (inclusive).
	 * @param colIndex Column index.
	 */
	protected void addFilterForColumn(XSSFSheet sheet, int rowIndex, int colIndex){
		sheet.setAutoFilter(new CellRangeAddress(0, rowIndex, colIndex, colIndex));
	}
	
	/**
	 * Adjusts the width of all columns within the given sheet, each according to the value with maximum width.
	 * @param sheet Sheet whose columns are to be adjusted.
	 */
	protected void autoSizeColumns(SheetCreationResult sheet){
		autoSizeColumns(sheet.getSheet(), 0, sheet.getColCount()-1);
	}
	
	/**
	 * Adjusts the width of the given columns (defined by column indexes), each according to the value with maximum width.
	 * @param sheet Sheet which contains the columns.
	 * @param firstColIndex Index of first column whose width is adjusted.
	 * @param lastColIndex Index of last column whose width is adjusted.
	 */
	protected void autoSizeColumns(XSSFSheet sheet, int firstColIndex, int lastColIndex){
		Validate.biggerEqual(lastColIndex, firstColIndex);
		for(int i=firstColIndex; i<=lastColIndex; i++){
			autoSizeColumn(sheet, i);
		}
	}
	
	/**
	 * Adjusts the width of the given column (defined by column index) according to the value with maximum width.
	 * @param sheet Sheet which contains the column.
	 * @param colIndex Index of column whose width is adjusted.
	 */
	protected void autoSizeColumn(XSSFSheet sheet, int colIndex){
		sheet.autoSizeColumn(colIndex);
		sheet.setColumnWidth(colIndex, sheet.getColumnWidth(colIndex) + 600);
	}
	
	// String conversion
	
//	/**
//	 * Converts the given date to a string.<br>
//	 * In case of <code>null</code>, this method returns the null replacement string defined by {@link #getNullReplacementString()}; 
//	 * otherwise the given date is formatted using the format which is returned by {@link #getCellFormatDate()}.
//	 * @param date Date to convert.
//	 * @return
//	 */
//	protected String toString(Date date){
//		if(date == null)
//			return NULL_REPLACEMENT_STRING;
//		return getCellFormatDate().format(date);
//	}

	/**
	 * Returns the date format which is used to convert date values to strings.<br>
	 * Note: This date format is only used when default format usage is enabled.<br>
	 * Default: {@value #DEFAULT_DATE_FORMAT}
	 * @see #setUseDefaultFormats(boolean) 
	 * @return
	 */
	protected short getCellFormatDate(){
		return cellFormatDate;
	}
	
	/**
	 * Sets the format used for cells containing dates.<br>
	 * Note: This date format is only used when default format usage is enabled.
	 * @see #setUseDefaultFormats(boolean)
	 * @param dateFormat Date format to use.
	 */
	protected boolean setCellFormatDate(String dateFormatPattern){
		this.cellFormatDate = workbook.createDataFormat().getFormat(dateFormatPattern);
		return getCellFormatDate() > -1;
	}
	
	/**
	 * Returns a string representation for a given time value.<br>
	 * For string conversion, the format {@link #TO_STRING_FORMAT_TIME_VALUE} is used.
	 * @param timeValue
	 * @return
	 */
	protected String toString(TimeValue timeValue){
		return String.format(TO_STRING_FORMAT_TIME_VALUE, timeValue.getValue());
	}
	
	// Validation
	
	/**
	 * Checks whether the given value is <code>null</code> and returns the null replacement string defined by {@link #getNullReplacementString()} if so.
	 * @link #NULL_REPLACEMENT_STRING
	 * @param value Value to check.
	 * @return
	 */
	protected Object nullCheck(Object value){
		return nullCheck(value, getNullReplacementString());
	}
	
	/**
	 * Returns the string which is used for <code>null</code> values.
	 * @return {@link #NULL_REPLACEMENT_STRING}
	 */
	protected String getNullReplacementString(){
		return NULL_REPLACEMENT_STRING;
	}
	
	/**
	 * Checks whether the given value is <code>null</code> and returns the given replacement value if so.
	 * @param value Value to check.
	 * @param replacement Replacement value in case {@link value} is <code>null</code>.
	 * @return
	 */
	protected Object nullCheck(Object value, Object replacement){
		if(value == null)
			return replacement;
		return value;
	}
	
	// Helper methods
	
	/**
	 * Creates a new value list and inserts it into the given cell range.<br>
	 * Value lists are used to define valid cell values which can be chosen via drop down menus.
	 * This method creates a new value list with the given valid values and then inserts this value 
	 * list into all cells of the given range using the default value as initial selected and visible value.
	 * @param sheet Sheet in which the value list is used.
	 * @param values Valid values.
	 * @param cellRange Range in which the value list is inserted.
	 * @param defaultValue Default value for the list.
	 */
	protected void createValueList(XSSFSheet sheet, String[] values, CellRangeAddressList cellRange, String defaultValue){
		// Create new data validation and assign the value constraint to all cells within the given cell range
		DataValidation dataValidation = null;
		DataValidationConstraint constraint = null;
		DataValidationHelper validationHelper = null;
		validationHelper = new XSSFDataValidationHelper(sheet);
		constraint = validationHelper.createExplicitListConstraint(values);
		dataValidation = validationHelper.createValidation(constraint, cellRange);
		dataValidation.setSuppressDropDownArrow(true);      
		sheet.addValidationData(dataValidation);
		
		// Assign the default value to all cells within the given cell range
		setCellValue(sheet, cellRange, new FormattedCellValue(defaultValue, null), createCellStyle(CellType.REGULAR));
	}
	
	/**
	 * Creates a new value list and uses this list in all cells of a newly added column.
	 * Value lists are used to define valid cell values which can be chosen via drop down menus.
	 * This method creates a new value list with the given valid values and then inserts this value 
	 * list into all cells of the newly added column with the given heading using the default value as initial selected and visible value.
	 * @param sheet Sheet where new column is to be added.
	 * @param heading Heading for the newly introduced column.
	 * @param values Valid values for the value list.
	 * @param defaultValue Default value of the value list.
	 * @return
	 */
	protected SheetCreationResult addColumnWithValues(SheetCreationResult sheet, String heading, String[] values, String defaultValue){
		int valueListColNum = sheet.getColCount();
		// Define cell range for inserting value list
		// Cells within the new column are defined by the actual sheet row count
		// The fisrt row is skipped and left for inserting the header
		CellRangeAddressList cellRange =  new CellRangeAddressList(1, sheet.getRowCount(), valueListColNum, valueListColNum);
		// Create value list and assign it to all cells of the defined the cell range
		createValueList(sheet.getSheet(), values, cellRange, defaultValue);
		// Add the header for the new column and return
		return addColumnWithValue(sheet, heading, new FormattedCellValue(defaultValue, null));
	}
	
	/**
	 * Helper method for getting the column number of a given column defined by its name. 
	 * @param colName Name if the column for which the index is needed.
	 * @param colNames List of all column names.
	 * @return
	 */
	protected int getColNumber(String colName, String[] colNames){
		for(int i=0;i<colNames.length;i++){
			if(colName.equals(colNames[i]))
				return i;
		}
		return 0;
	}
	
	/**
	 * Converts Object values to formatted cell values without using a cell style.<br>
	 * The values of type {@link FormattedCellValue} do not contain a cell style in this case (it is <code>null</code>).<br><br>
	 * @param values The object values to convert
	 * @param cellStyle The cell style for converted values
	 * @return Array containing converted values in the same order.
	 */
	protected <T> FormattedCellValue[] convertValues(T[] values){
		return convertValues(values, null, false);
	}
	
	/**
	 * Converts Object values to formatted cell values using the given cell style.<br>
	 * In case the given cell style is not {@code null} and default formats should be used, it is adjusted according to these standard formats (see {@link #convertValue(Object, XSSFCellStyle, boolean)}).
	 * Default format usage is defined by {@link #isUseDefaultFormats()}. 
	 * @param values The object values to convert.
	 * @param cellStyle The cell style for converted values
	 * @return Array containing converted values in the same order.
	 */
	protected FormattedCellValue[] convertValues(Object[] values, XSSFCellStyle cellStyle){
		return convertValues(values, cellStyle, isUseDefaultFormats());
	}
	
	/**
	 * Converts Object values to formatted cell values using the given cell style.<br>
	 * In case the given cell style is not {@code null} and default formats should be used, it is adjusted according to these standard formats (see {@link #convertValue(Object, XSSFCellStyle, boolean)}).
	 * @param values The object values to convert.
	 * @param cellStyle The cell style for converted values
	 * @param useDefaultFormats Indicates whether default formats should be used.
	 * @return Array containing converted values in the same order.
	 */
	protected FormattedCellValue[] convertValues(Object[] values, XSSFCellStyle cellStyle, boolean useDefaultFormats){
		FormattedCellValue[] result = new FormattedCellValue[values.length];
		for(int i=0; i<result.length; i++){
			result[i] = convertValue(values[i], cellStyle, useDefaultFormats);
//			result[i] = new FormattedCellValue(values[i], cellStyle);
		}
		return result;
	}
	
	/**
	 * Converts an Object value to a formatted cell value without assigning a cell style (will be {@code null}).
	 * @see #convertValue(Object, XSSFCellStyle, boolean)
	 * @param value Value to convert.
	 * @return
	 */
	protected FormattedCellValue convertValue(Object value) {
		return convertValue(value, null, false);
	}
	
	/**
	 * Converts an Object value to a formatted cell value using the given cell style.<br>
	 * In case the given cell style is not {@code null} and default formats should be used, it is adjusted according to these standard formats (see {@link #convertValue(Object, XSSFCellStyle, boolean)}).
	 * Default format usage is defined by {@link #isUseDefaultFormats()}. 
	 * @see #isUseDefaultFormats()
	 * @see #convertValue(Object, XSSFCellStyle, boolean)
	 * @param value Value to convert.
	 * @param cellStyle Cell style to use/adjust.
	 * @return
	 */
	protected FormattedCellValue convertValue(Object value, XSSFCellStyle cellStyle) {
		return convertValue(value, cellStyle, isUseDefaultFormats());
	}
	
	/**
	 * Converts an Object value to a formatted cell value using the given cell style.<br>
	 * In case the given cell style is not {@code null} and default formats should be used, it is adjusted according to these standard formats.
	 * <br>
	 * <br>
	 * Standard formats include:<br>
	 * <ul>
	 * <li>Number format for integer values (Java types: Integer, Long, Short)<br>
	 *     {@link #getCellFormatNumber(int)}</li>
	 * <li>Number format for other number values (Java types: Double, Float)<br>
	 *     Precision according to {@link #getDefaultFloatPrecision()}</li>
	 * <li>Date format for date values (Java types: Date)</li>
	 * </ul>
	 * <br>
	 * @param value Value to convert.
	 * @param cellStyle Cell style to use/adjust.
	 * @param useDefaultFormats Indicates whether default formats should be used.
	 * @return
	 */
	protected FormattedCellValue convertValue(Object value, XSSFCellStyle cellStyle, boolean useDefaultFormats) {
		XSSFCellStyle cellStyleInternal = cellStyle != null ? (XSSFCellStyle) cellStyle.clone() : null;
		if(value == null)
			return new FormattedCellValue(value, cellStyleInternal);
		
		if(String.class.isAssignableFrom(value.getClass()))
			return new FormattedCellValue(nullCheck(value), cellStyleInternal);
		if(Integer.class.isAssignableFrom(value.getClass()) || Short.class.isAssignableFrom(value.getClass()) || Long.class.isAssignableFrom(value.getClass())){
			if(cellStyleInternal != null && useDefaultFormats)
				cellStyleInternal.setDataFormat(getCellFormatNumber(0));
			return new FormattedCellValue(nullCheck(value, ""), cellStyleInternal);
		}
		if(Double.class.isAssignableFrom(value.getClass()) || Float.class.isAssignableFrom(value.getClass())){
			if(cellStyleInternal != null && useDefaultFormats)
				cellStyleInternal.setDataFormat(getCellFormatNumber(getDefaultFloatPrecision()));
			return new FormattedCellValue(nullCheck(value, ""), cellStyleInternal);
		}	
		if(Date.class.isAssignableFrom(value.getClass())){
			if(cellStyleInternal != null && useDefaultFormats)
				cellStyleInternal.setDataFormat(getCellFormatDate());
			return new FormattedCellValue(nullCheck(value), cellStyleInternal);
		}
		return new FormattedCellValue(nullCheck(value), cellStyleInternal);
	}
	
	/**
	 * Creates a hyperlink using a target address and a label which is displayed for the hyperlink.
	 * @see HyperlinkType
	 * @param address Hyperlink target address
	 * @param label Label to be displayed for the hyperlink
	 * @return
	 */
	protected Hyperlink getHyperlink(HyperlinkType type, String address, String label){
        Hyperlink link = workbook.getCreationHelper().createHyperlink(type);
        link.setAddress(address);
        link.setLabel(label);
        return link;
	}
	
	/**
	 * Adds a new column (heading + values) to an existing sheet.<br>
	 * The given cell value is used for every cell within the new column except the heading row.
	 * @param sheet Sheet where new column is to be added.
	 * @param heading Heading of the newly introduced column.
	 * @param cellValue Cell value to be used for all column cells.
	 * @return
	 */
	protected SheetCreationResult addColumnWithValue(SheetCreationResult sheet, String heading, FormattedCellValue cellValue){
		int newColNum = sheet.getColCount();
//		System.out.println("(" + 1 + "," + sheet.getRowCount() + "," + newColNum + "," + newColNum + ")");
		// Define cell range for inserting column values
		// Cells within the new column are defined by the actual sheet row count
		// The first row is skipped and left for inserting the header
		CellRangeAddressList cellRange =  new CellRangeAddressList(1, sheet.getRowCount(), newColNum, newColNum);
		// Set heading (insert into first row)
		setCellValue(sheet.getSheet().getRow(0).createCell(newColNum), new FormattedCellValue(heading, null), createCellStyle(CellType.HEADING));
		// Assign the given cell value to all cells of the defined the cell range
		setCellValue(sheet.getSheet(), cellRange, cellValue != null ? cellValue : new FormattedCellValue(null, createCellStyle(CellType.REGULAR)), createCellStyle(CellType.REGULAR));

		SheetCreationResult result = new SheetCreationResult(sheet.getSheet(), sheet.getColCount() + 1, sheet.getRowCount());
		addFilterForColumns(result);
		autoSizeColumns(result);
		return result;
	}

	/**
	 * Enumeration for distinguishing the two different cell types "heading" and "regular".<br>
	 * While type {@link #REGULAR} is exclusively used for cells in sheet headers (first row in a sheet),
	 * type {@link #HEADING} is used for all other cells within sheets.
	 */
	protected enum CellType {
		REGULAR, HEADING;
	}
	
	public class ChartValueSeries {
		public ChartDataSource<Number> dataSource;
		public String name;
		
		public ChartValueSeries(ChartDataSource<Number> dataSource, String name) {
			super();
			this.dataSource = dataSource;
			this.name = name;
		}
	}
	
}
