package de.invation.code.toval.poiutil.excel;

import org.apache.poi.xssf.usermodel.XSSFSheet;

public class SheetCreationResult {
	
	private XSSFSheet sheet;
	private int colCount;
	private int rowCount;
	
	public SheetCreationResult(XSSFSheet sheet, int colCount, int rowCount) {
		super();
		this.sheet = sheet;
		this.colCount = colCount;
		this.rowCount = rowCount;
	}

	public XSSFSheet getSheet() {
		return sheet;
	}

	public int getColCount() {
		return colCount;
	}

	public int getRowCount() {
		return rowCount;
	}

}
