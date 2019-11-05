package de.invation.code.toval.poiutil.excel;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public class FormattedCellValue {
	
	public int cellType;
	public Object cellValue;
	public XSSFCellStyle cellStyle;
	
	public FormattedCellValue(XSSFCell cell) {
		this.cellType = cell.getCellType();
		if(cellType == CellType.BOOLEAN.getCode()){
			cellValue = cell.getBooleanCellValue();
		} else if(cellType == CellType.ERROR.getCode()){
			cellValue = cell.getErrorCellValue();
		} else if(cellType == CellType.FORMULA.getCode()){
			cellValue = cell.getCellFormula();
		} else if(cellType == CellType.NUMERIC.getCode()){
			cellValue = cell.getNumericCellValue();
		} else if(cellType == CellType.STRING.getCode()){
			cellValue = cell.getStringCellValue();
		}
		cellStyle = cell.getCellStyle();
	}
	
	public FormattedCellValue(int cellType, Object cellValue, XSSFCellStyle cellStyle) {
		this(cellValue, cellStyle);
		this.cellType = cellType;
	}
	
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
