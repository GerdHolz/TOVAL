package de.invation.code.toval.poiutil.excel;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public class FormattedCellValue {
	
	public Object cellValue;
	public XSSFCellStyle cellStyle;
	
	public FormattedCellValue(Object cellValue, XSSFCellStyle cellStyle) {
		super();
		this.cellValue = cellValue;
		this.cellStyle = cellStyle;
	}
	
	@Override
	public String toString(){
		return cellValue + " " + cellStyle;
	}

}
