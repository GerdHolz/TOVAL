package de.invation.code.toval.poiutil.excel;

import java.io.File;
import java.util.Arrays;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.invation.code.toval.file.FileUtils;

public class TestExcelExport extends AbstractExcelExport {

	public TestExcelExport(File outputFile) {
		super(outputFile);
	}

	public TestExcelExport(String outputFile) {
		super(outputFile);
	}

	@Override
	public boolean contentsToExport() {
		return true;
	}

	@Override
	protected boolean protectCell(XSSFSheet sheet, int rowIndex, int colIndex) {
		return false;
	}

	@Override
	protected void addContent(XSSFWorkbook workbook) throws Exception {
		XSSFSheet sheet = addSheet("Testsheet");
		addLineChart(
				sheet, 
				true, 
				createCellRange(0, 10, 0, 10), 
				createChartDataSource(new String[]{"Mo","Di","Mi","Do","Fr","Sa","So"}), 
				Arrays.asList(	createChartValueSeries(new Integer[]{10,10,20,30,10,10,10}, "Test 1"),
								createChartValueSeries(new Integer[]{40,40,40,10,70,80,40}, "Test 2")));
	}
	
	public static void main(String[] args) throws Exception {
		TestExcelExport export = new TestExcelExport("test.xlsx");
		export.createFile();
		FileUtils.openFile(export.getOutputFile());
	}

}
