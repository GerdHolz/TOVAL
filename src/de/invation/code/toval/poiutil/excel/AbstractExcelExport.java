package de.invation.code.toval.poiutil.excel;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
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

import de.invation.code.toval.file.IFileExporter;
import de.invation.code.toval.misc.StringUtils;
import de.invation.code.toval.time.TimeScale;
import de.invation.code.toval.time.TimeValue;
import de.invation.code.toval.validate.Validate;

public abstract class AbstractExcelExport implements IFileExporter {
	
	private static final Logger log = LogManager.getLogger(AbstractExcelExport.class);
	
	private static final short DEFAULT_FONT_HEIGHT_REGULAR = 10;
	private static final short DEFAULT_ROW_HEIGHT_REGULAR = 400;
	private static final Color DEFAULT_COLOR_REGULAR = new Color(255,255,255);
	private static final HorizontalAlignment DEFAULT_HORIZONTAL_ALIGNMENT_REGULAR = HorizontalAlignment.LEFT;
	private static final VerticalAlignment DEFAULT_VERTICAL_ALIGNMENT_REGULAR = VerticalAlignment.CENTER;
	
	private static final short DEFAULT_FONT_HEIGHT_HEADING = 10;
	private static final short DEFAULT_ROW_HEIGHT_HEADING = 400;
	private static final Color DEFAULT_COLOR_HEADING = new Color(200,200,200);
	private static final HorizontalAlignment DEFAULT_HORIZONTAL_ALIGNMENT_HEADING = HorizontalAlignment.LEFT;
	private static final VerticalAlignment DEFAULT_VERTICAL_ALIGNMENT_HEADING = VerticalAlignment.CENTER;
	
	private static final String DATE_FORMAT_STRING_POI = "dd.mm.yyyy";
	private static final String DATE_FORMAT_STRING = "dd.MM.yyyy";
//	protected static final String NULL_REPLACEMENT_STRING = "-null-";
	protected static final String NULL_REPLACEMENT_STRING = null;
	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
	
	private static final String TO_STRING_FORMAT_DIFF = "%.0f";
	private static final int DEFAULT_FLOAT_PRECISION = 2;
//	private static final String FORMAT_NUMBER_FORMAT = "#,##0.0000";
	
	private XSSFWorkbook workbook;
	
	private File outputFile;
	
	protected short cellDateFormat = 0;
	
	private boolean sheetProtectionEnabled;
	private String lockingPassword;
	
	public AbstractExcelExport(File outputFile){
		this.outputFile = outputFile;
		workbook = new XSSFWorkbook();
		cellDateFormat = workbook.createDataFormat().getFormat(DATE_FORMAT_STRING_POI);
	}
	
	protected String getLockingPassword() {
		return lockingPassword;
	}

	protected boolean isSheetProtectionEnabled() {
		return sheetProtectionEnabled;
	}
	
	public void disableSheetProtection(){
		this.sheetProtectionEnabled = false;
		this.lockingPassword = null;
	}

	public void enableSheetProtection(String lockingPassword) {
		this.sheetProtectionEnabled = true;
		this.lockingPassword = lockingPassword;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void createFile() throws Exception {
		createExcelFile();
	}
	
	public void createExcelFile() throws Exception {
		if(!contentsToExport())
			throw new Exception("No content to export, file creation aborted.");
		
		log.info("Writing excel file...");
		createSheets(workbook);
        FileOutputStream out = new FileOutputStream(outputFile);
        workbook.write(out);
        out.close();
        workbook.close();
        log.info("File creation finished.");
	}
	
	protected int getDefaultFloatPrecision(){
		return DEFAULT_FLOAT_PRECISION;
	}
	
	public FormattedCellValue getCellValue(Object value) {
		if(value == null)
			return new FormattedCellValue(value, null);
		XSSFCellStyle cellStyle = null;
		if(String.class.isAssignableFrom(value.getClass()))
			return new FormattedCellValue(nullCheck(value), null);
		if(Integer.class.isAssignableFrom(value.getClass()) || Short.class.isAssignableFrom(value.getClass()) || Long.class.isAssignableFrom(value.getClass())){
			cellStyle = newCellStyle(CellType.REGULAR);
			cellStyle.setDataFormat(getNumberFormat(0));
			return new FormattedCellValue(nullCheck(value, ""), cellStyle);
		}
		if(Double.class.isAssignableFrom(value.getClass()) || Float.class.isAssignableFrom(value.getClass())){
			cellStyle = newCellStyle(CellType.REGULAR);
			cellStyle.setDataFormat(getNumberFormat(getDefaultFloatPrecision()));
			return new FormattedCellValue(nullCheck(value, ""), cellStyle);
		}	
		if(Date.class.isAssignableFrom(value.getClass())){
			cellStyle = newCellStyle(CellType.REGULAR);
			cellStyle.setDataFormat(cellDateFormat);
			return new FormattedCellValue(nullCheck(value), cellStyle);
		}
		return new FormattedCellValue(nullCheck(value), null);
	}
	
	// Cell style creation
	
	protected XSSFCellStyle newHLinkCellStyle(){
		XSSFCellStyle hlink_style = workbook.createCellStyle();
        Font hlink_font = getFont(CellType.REGULAR);
        hlink_font.setColor(IndexedColors.LIGHT_BLUE.getIndex());
        hlink_style.setFont(hlink_font);
        return hlink_style;
	}

	protected XSSFCellStyle newCellStyle(CellType type){
		return newCellStyle(type, getCellColor(type));
	}
	
	protected XSSFCellStyle newCellStyle(CellType type, Color color){
		return newCellStyle(getFont(type), color, getVerticalAlignment(type), getHorizontalAlignment(type));
	}
	
	private XSSFCellStyle newCellStyle(Font font, Color color, VerticalAlignment alignVertical, HorizontalAlignment alignHorizontal){
		XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();
		if(color != null){
			cellStyle.setFillForegroundColor(new XSSFColor(color));          
			cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		}
		cellStyle.setFont(font);
		cellStyle.setVerticalAlignment(alignVertical);
		cellStyle.setAlignment(alignHorizontal);
		
        return cellStyle;
	}
	
	// Cell style assignment
	
	protected void assignCellStyle(Row row, CellStyle cellStyle, int start, int end){
		for(int i=start; i<=end; i++){
			row.getCell(i).setCellStyle(cellStyle);
		}
	}
	
	// Data formats
	
	protected void setDataFormat(XSSFCellStyle cellStyle, String dataFormatString){
		if(dataFormatString != null && !dataFormatString.isEmpty()){
			cellStyle.setDataFormat(workbook.createDataFormat().getFormat(dataFormatString));
		}
	}
	
	protected short getNumberFormat(int precision){
		Validate.biggerEqual(precision, 0);
		if(precision == 0)
			return workbook.createDataFormat().getFormat("0");
		return workbook.createDataFormat().getFormat("0.".concat(StringUtils.createString('0', precision)));
	}
	
	// Fonts
	
	protected Font getFont(CellType type){
		return getFont(type, false, false);
	}
	
	protected Font getFont(CellType type, boolean bold, boolean italic){
		Font font = workbook.createFont();
	    font.setFontHeightInPoints(getFontHeightInPoints(type));
	    font.setBold(bold);
	    font.setItalic(italic);
	    return font;
	}
	
	protected Short getFontHeightInPoints(CellType type){
		switch(type){
		case HEADING:
			return DEFAULT_FONT_HEIGHT_HEADING;
		case REGULAR:
			return DEFAULT_FONT_HEIGHT_REGULAR;
		default:
			return null;
		}
	}
	
	// Alignments
	
	protected VerticalAlignment getVerticalAlignment(CellType type){
		switch(type){
		case HEADING:
			return DEFAULT_VERTICAL_ALIGNMENT_HEADING;
		case REGULAR:
			return DEFAULT_VERTICAL_ALIGNMENT_REGULAR;
		default:
			return null;
		}
	}
	
	protected HorizontalAlignment getHorizontalAlignment(CellType type){
		switch(type){
		case HEADING:
			return DEFAULT_HORIZONTAL_ALIGNMENT_HEADING;
		case REGULAR:
			return DEFAULT_HORIZONTAL_ALIGNMENT_REGULAR;
		default:
			return null;
		}
	}
	
	// Row height
	
	protected Short getRowHeight(CellType type){
		switch(type){
		case HEADING:
			return DEFAULT_ROW_HEIGHT_HEADING;
		case REGULAR:
			return DEFAULT_ROW_HEIGHT_REGULAR;
		default:
			return null;
		}
	}
	
	// Cell color
	
	protected Color getCellColor(CellType type){
		switch(type){
		case HEADING:
			return DEFAULT_COLOR_HEADING; 
		case REGULAR:
			return DEFAULT_COLOR_REGULAR;
		default:
			return null;
		}
	}
	
	protected Color signalColor(TimeValue diff, TimeValue timeToRedMark){
		timeToRedMark.setScale(TimeScale.DAYS, true);
		diff.setScale(TimeScale.DAYS, true);
		if(diff.getValue() <= 0)
			return new Color(0,255,0);
		if(diff.isBiggerEqualThan(timeToRedMark))
			return new Color(255,0,0);
		int green = (int) (255 - Math.round((255/timeToRedMark.getValue())*diff.getValue()));
		return new Color(255, green, 0);
	}
	
	// Sheet creation

	protected abstract void createSheets(XSSFWorkbook workbook);
	
	protected SheetCreationResult createSheet(String sheetName, String... colNames){
		XSSFSheet sheet = workbook.createSheet(sheetName);
		createHeadingRow(sheet, 0, convertValues(colNames));
		if(isSheetProtectionEnabled())
			enableSheetProtection(sheet);
		return new SheetCreationResult(sheet, colNames.length, 1);
	}
	
	private void enableSheetProtection(XSSFSheet sheet){
		sheet.protectSheet(getLockingPassword());
		CTSheetProtection sheetProtection = sheet.getCTWorksheet().getSheetProtection();
		sheetProtection.setSelectLockedCells(false);
		sheetProtection.setSelectUnlockedCells(false);
		sheetProtection.setFormatCells(true);
		sheetProtection.setFormatColumns(true);
		sheetProtection.setFormatRows(true);
		sheetProtection.setInsertColumns(true);
		sheetProtection.setInsertRows(true);
		sheetProtection.setInsertHyperlinks(true);
		sheetProtection.setDeleteColumns(true);
		sheetProtection.setDeleteRows(true);
		sheetProtection.setSort(false);
		sheetProtection.setAutoFilter(false);
		sheetProtection.setPivotTables(true);
		sheetProtection.setObjects(true);
		sheetProtection.setScenarios(true);
	}
	
	protected SheetCreationResult createSingleColSheet(String sheetName, String heading, List<String> values){
		XSSFSheet sheet = workbook.createSheet(sheetName);
		createHeadingRow(sheet, 0, convertValues(new String[]{heading}));
		int rowNumber = 0;
		for(String str: values){
			createNormalRow(sheet, ++rowNumber, convertValues(new String[]{str}));
		}
		if(isSheetProtectionEnabled())
			enableSheetProtection(sheet);
		sheet.setAutoFilter(new CellRangeAddress(0,rowNumber,0,0));
		return new SheetCreationResult(sheet, 1, rowNumber);
	}
	
	
	
	// Chart creation
	
	protected void createLineChart(XSSFSheet sheet, boolean gapsForNullValues, int firstCellCol, int firstCellRow, int secondCellCol, int secondCellRow, ChartDataSource<Number> xAxis, ValueSeries... valueSeries){
		Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, firstCellCol, firstCellRow, secondCellCol, secondCellRow);

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

        for(ValueSeries valueSer: valueSeries){
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
	
	protected ValueSeries createValueSeries(XSSFSheet sheet, CellRangeAddress cellRangeAddress, String name){
		return new ValueSeries(createChartDataSource(sheet, cellRangeAddress), name);
	}
	
	protected ChartDataSource<Number> createChartDataSource(XSSFSheet sheet, CellRangeAddress cellRangeAddress){
		return DataSources.fromNumericCellRange(sheet, cellRangeAddress);
	}
	
	// Row creation
	
	protected final Row createHeadingRow(XSSFSheet sheet, int rowNumber, FormattedCellValue... cellValues){
		return createRow(sheet, rowNumber, newCellStyle(CellType.HEADING), cellValues);
	}
	
	protected Row createNormalRow(Sheet sheet, int rowNumber, FormattedCellValue... cellValues){
		return createRow(sheet, rowNumber, newCellStyle(CellType.REGULAR), cellValues);
	}
	
	private Row createRow(Sheet sheet, int rowNumber, XSSFCellStyle fallbackCellStyle, FormattedCellValue... cellValues){
		Row row = sheet.createRow(rowNumber);
		for(int i=0; i<cellValues.length; i++){
			setCellValue(row.createCell(i), cellValues[i], fallbackCellStyle);
		}
		return row;
	}
	
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
	
	protected void setCellValue(XSSFSheet sheet, CellRangeAddressList cellRange, FormattedCellValue formattedCellValue, XSSFCellStyle fallbackCellStyle){
		for(CellRangeAddress range: cellRange.getCellRangeAddresses()){
			for(int columnIndex=range.getFirstColumn(); columnIndex<=range.getLastColumn(); columnIndex++){
				for(int rowIndex=range.getFirstRow(); rowIndex<=range.getLastRow(); rowIndex++){
					if(sheet.getRow(rowIndex).getCell(columnIndex) == null){
						XSSFCell newCell = sheet.getRow(rowIndex).createCell(columnIndex);
						XSSFCellStyle cellStyle = newCellStyle(CellType.REGULAR);
						if(isSheetProtectionEnabled())
							cellStyle.setLocked(protectCell(sheet, rowIndex, columnIndex));
						newCell.setCellStyle(cellStyle);
					}
					setCellValue(sheet.getRow(rowIndex).getCell(columnIndex), formattedCellValue, fallbackCellStyle);
				}
			}
		}
	}
	
	protected abstract boolean protectCell(XSSFSheet sheet, int row, int col);
	
	// Filter and autosize

	protected void filterCols(SheetCreationResult sheet){
		filterCols(sheet.getSheet(), sheet.getRowCount(), sheet.getColCount()-1);
	}
	
	protected void filterCols(XSSFSheet sheet, int rowCount, int colCount){
		sheet.setAutoFilter(new CellRangeAddress(0, rowCount, 0, colCount));
	}
	
	protected void filterCol(XSSFSheet sheet, int rowCount, int colNum){
		sheet.setAutoFilter(new CellRangeAddress(0, rowCount, colNum, colNum));
	}
	
	protected void autoSize(SheetCreationResult sheet){
		autoSize(sheet.getSheet(), 0, sheet.getColCount()-1);
	}
	
	protected void autoSize(XSSFSheet sheet, int firstCol, int lastCol){
		for(int i=firstCol; i<=lastCol; i++){
			autoSize(sheet, i);
		}
	}
	
	protected void autoSize(XSSFSheet sheet, int colNum){
		sheet.autoSizeColumn(colNum);
		sheet.setColumnWidth(colNum, sheet.getColumnWidth(colNum) + 600);
	}
	
	// String conversion
	
	protected String dateString(Date date){
		if(date == null)
			return NULL_REPLACEMENT_STRING;
		return DATE_FORMAT.format(date);
	}

	
	protected String diffValue(TimeValue timeValue){
		return String.format(TO_STRING_FORMAT_DIFF, timeValue.getValue());
	}
	
	// Validation
	
	protected Object nullCheck(Object value){
		return nullCheck(value, NULL_REPLACEMENT_STRING);
	}
	
	protected Object nullCheck(Object value, Object replacement){
		if(value == null)
			return replacement;
		return value;
	}
	
	// Helper methods
	
	protected void createValueList(XSSFSheet sheet, String[] values, CellRangeAddressList cellRange, String defaultValue){
		DataValidation dataValidation = null;
		DataValidationConstraint constraint = null;
		DataValidationHelper validationHelper = null;

		validationHelper = new XSSFDataValidationHelper(sheet);
		constraint = validationHelper.createExplicitListConstraint(values);
		dataValidation = validationHelper.createValidation(constraint, cellRange);
		dataValidation.setSuppressDropDownArrow(true);      
		sheet.addValidationData(dataValidation);
		
		setCellValue(sheet, cellRange, new FormattedCellValue(defaultValue, null), newCellStyle(CellType.REGULAR));
	}
	
	protected SheetCreationResult addValueList(SheetCreationResult sheet, String heading, String[] values, String defaultValue){
		int valueListColNum = sheet.getColCount();
//		System.out.println("(" + 1 + "," + sheet.getRowCount() + "," + valueListColNum + "," + valueListColNum + ")");
		CellRangeAddressList cellRange =  new CellRangeAddressList(1, sheet.getRowCount(), valueListColNum, valueListColNum);
		createValueList(sheet.getSheet(), values, cellRange, defaultValue);
		return addColumn(sheet, heading, new FormattedCellValue(defaultValue, null));
	}
	
	protected int getColNumber(String colName, String[] colNames){
		for(int i=0;i<colNames.length;i++){
			if(colName.equals(colNames[i]))
				return i;
		}
		return 0;
	}
	
	protected <T> FormattedCellValue[] convertValues(T[] values){
		return convertValues(values, null);
	}
	
	protected FormattedCellValue[] convertValues(Object[] values, XSSFCellStyle cellStyle){
		FormattedCellValue[] result = new FormattedCellValue[values.length];
		for(int i=0; i<result.length; i++)
			result[i] = new FormattedCellValue(values[i], cellStyle);
		return result;
	}
	
	protected Hyperlink getHyperlink(String address, String label){
        Hyperlink link = workbook.getCreationHelper().createHyperlink(Hyperlink.LINK_URL);
        link.setAddress(address);
        link.setLabel(label);
        return link;
	}
	
	protected SheetCreationResult addColumn(SheetCreationResult sheet, String heading, FormattedCellValue cellValue){
		int newColNum = sheet.getColCount();
//		System.out.println("(" + 1 + "," + sheet.getRowCount() + "," + newColNum + "," + newColNum + ")");
		CellRangeAddressList cellRange =  new CellRangeAddressList(1, sheet.getRowCount(), newColNum, newColNum);
		setCellValue(sheet.getSheet().getRow(0).createCell(newColNum), new FormattedCellValue(heading, null), newCellStyle(CellType.HEADING));
		setCellValue(sheet.getSheet(), cellRange, cellValue != null ? cellValue : new FormattedCellValue(null, newCellStyle(CellType.REGULAR)), newCellStyle(CellType.REGULAR));

		SheetCreationResult result = new SheetCreationResult(sheet.getSheet(), sheet.getColCount() + 1, sheet.getRowCount());
		filterCols(result);
		autoSize(result);
		return result;
	}
	
	protected enum CellType {
		REGULAR, HEADING;
	}
	
	public class ValueSeries {
		public ChartDataSource<Number> dataSource;
		public String name;
		
		public ValueSeries(ChartDataSource<Number> dataSource, String name) {
			super();
			this.dataSource = dataSource;
			this.name = name;
		}
	}
	
}
