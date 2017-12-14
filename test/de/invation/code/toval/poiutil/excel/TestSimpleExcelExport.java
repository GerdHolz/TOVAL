package de.invation.code.toval.poiutil.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;

import de.invation.code.toval.file.FileUtils;
import de.invation.code.toval.misc.RandomUtils;

public class TestSimpleExcelExport {
	
	private static final int NUM_VALUES_PER_ROW = 10;
	private static final double MIN_VALUE = 0.0;
	private static final double MAX_VALUE = 30.0;
	private static final int NUM_ROWS = 3;
	
	private static final String SHEET_NAME = "Testsheet";
	private static final String HEADING_FORMAT = "Spalte %s";
	private static final String VALUE_SERIES_FORMAT = "Verlauf %s";
	
	private Number[] createValueSeries(){
		Number[] result = new Number[NUM_VALUES_PER_ROW]; 
		for(int i=0; i<NUM_VALUES_PER_ROW; i++)
			result[i] = RandomUtils.randomDouble(MIN_VALUE, MAX_VALUE);
		return result;
	}
	
	private String[] getHeadingRowNames(){
		String[] result = new String[NUM_VALUES_PER_ROW]; 
		for(int i=0; i<NUM_VALUES_PER_ROW; i++)
			result[i] = String.format(HEADING_FORMAT, i+1);
		return result;
	}

	@Test
	public void test() throws Exception {
		SimpleExcelExport export = new SimpleExcelExport(new File("test.xlsx"));
		export.setLineChartWidth(20);
		export.addSheet(SHEET_NAME);
		Row headingRow = export.addHeadingRow(getHeadingRowNames());
		List<Row> rows = new ArrayList<>();
		List<String> names = new ArrayList<>();
		for(int i=0; i<NUM_ROWS; i++){
			rows.add(export.addNormalRow(createValueSeries()));
			names.add(String.format(VALUE_SERIES_FORMAT, i+1));
		}
		export.addFilter();
		export.autoSizeColumns();
		export.addLineChart(headingRow, rows, names, false);
		export.createFile();
		FileUtils.openFile(export.getOutputFile());
	}

}
