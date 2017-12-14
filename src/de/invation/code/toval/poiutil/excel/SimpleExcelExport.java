package de.invation.code.toval.poiutil.excel;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.invation.code.toval.file.FileUtils;
import de.invation.code.toval.validate.Validate;

public class SimpleExcelExport extends AbstractExcelExport {
	
	public static final int DEFAULT_LINE_CHART_WIDTH = 10;
	public static final int DEFAULT_LINE_CHART_HEIGHT = 10;
	
	private Map<String,Integer> rowCount = new HashMap<>();
	private Map<String,Integer> colCount = new HashMap<>();
	
	private int lineChartWidth = DEFAULT_LINE_CHART_WIDTH;
	private int lineChartHeight = DEFAULT_LINE_CHART_HEIGHT;
	
	private String lastAddedSheet = null;
	
	public SimpleExcelExport(File outputFile) {
		super(outputFile);
	}

	public int getLineChartWidth() {
		return lineChartWidth;
	}

	public void setLineChartWidth(int lineChartWidth) {
		Validate.positive(lineChartWidth);
		this.lineChartWidth = lineChartWidth;
	}

	public int getLineChartHeight() {
		return lineChartHeight;
	}

	public void setLineChartHeight(int lineChartHeight) {
		Validate.positive(lineChartHeight);
		this.lineChartHeight = lineChartHeight;
	}

	@Override
	protected void addContent(XSSFWorkbook workbook) {}

	@Override
	protected boolean protectCell(XSSFSheet sheet, int row, int col) {
		return false;
	}

	public Row addHeadingRow(Object[] rowValues) throws Exception{
		return addHeadingRow(getLastAddedSheet(), rowValues);
	}
	
	public Row addHeadingRow(String sheetName, Object[] rowValues) throws Exception{
		if(!containsSheet(sheetName))
			addSheet(sheetName);
		Row result = addHeadingRow(getSheet(sheetName), 0, convertValues(rowValues));
		if(getRowCount(sheetName) == 0)
			incrementRowCount(sheetName);
		adjustColCount(sheetName, rowValues.length);
		return result;
	}
	
	public Row addNormalRow(Object[] rowValues) throws Exception{
		return addNormalRow(getLastAddedSheet(), rowValues);
	}
	
	public Row addNormalRow(String sheetName, Object[] rowValues) throws Exception{
		if(!containsSheet(sheetName))
			addSheet(sheetName);
		Row result = addNormalRow(getSheet(sheetName), getRowCount(sheetName), rowValues);
		incrementRowCount(sheetName);
		adjustColCount(sheetName, rowValues.length);
		return result;
	}
	
	@Override
	protected XSSFSheet addSheet(String sheetName) throws Exception {
		XSSFSheet sheet = super.addSheet(sheetName);
		rowCount.put(sheetName, 0);
		colCount.put(sheetName, 0);
		lastAddedSheet = sheetName;
		return sheet;
	}

	private int getRowCount(String sheetName) throws Exception{
		if(!containsSheet(sheetName))
			throw new Exception("No sheet with this name");
		return rowCount.get(sheetName);
	}
	
	private int getColCount(String sheetName) throws Exception{
		if(!containsSheet(sheetName))
			throw new Exception("No sheet with this name");
		return colCount.get(sheetName);
	}
	
	private void incrementRowCount(String sheetName){
		rowCount.put(sheetName, rowCount.get(sheetName) + 1);
	}
	
	private void adjustColCount(String sheetName, int colCount){
		if(colCount > this.colCount.get(sheetName))
			this.colCount.put(sheetName, colCount);
	}
	
	private String getLastAddedSheet() throws Exception{
		if(lastAddedSheet == null)
			throw new Exception("No sheets have been added so far");
		return lastAddedSheet;
	}
	
	public void addFilter() throws Exception{
		addFilter(getLastAddedSheet());
	}
	
	public void addFilter(String sheetName) throws Exception{
		addFilterForColumns(getSheet(sheetName), getRowCount(sheetName), getColCount(sheetName) - 1);
	}
	
	public void autoSizeColumns() throws Exception{
		autoSizeColumns(getLastAddedSheet());
	}
	
	public void autoSizeColumns(String sheetName) throws Exception{
		autoSizeColumns(getSheet(sheetName), 0, getColCount(sheetName) - 1);
	}
	
	public void addLineChart(Row xAxis, Row values, String nameForValueSeries, boolean gapsForNullValues) throws Exception{
		addLineChart(getLastAddedSheet(), xAxis, values, nameForValueSeries, gapsForNullValues);
	}
	
	public void addLineChart(Row xAxis, List<Row> values, List<String> namesForValueSeries, boolean gapsForNullValues) throws Exception{
		addLineChart(getLastAddedSheet(), xAxis, values, namesForValueSeries, gapsForNullValues);
	}
	
	public void addLineChart(String sheetName, Row xAxis, Row values, String nameForValueSeries, boolean gapsForNullValues) throws Exception{
		addLineChart(getSheet(sheetName), xAxis, values, nameForValueSeries, gapsForNullValues, getChartPosition(sheetName));
	}
	
	public void addLineChart(String sheetName, Row xAxis, List<Row> values, List<String> namesForValueSeries, boolean gapsForNullValues) throws Exception{
		addLineChart(getSheet(sheetName), gapsForNullValues, getChartPosition(sheetName), createCellRange(xAxis), createChartValueSeries(getSheet(sheetName), createCellRanges(values), namesForValueSeries));
	}

	private CellRangeAddress getChartPosition(String sheetName) throws Exception{
		return createCellRange(getRowCount(sheetName) + 1, getRowCount(sheetName) + getLineChartHeight() + 1, 0, getLineChartWidth());
	}

}
