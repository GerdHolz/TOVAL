package de.invation.code.toval.poiutil.excel;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.invation.code.toval.validate.Validate;

public class AbstractExcelReader {
	
	private static final Logger log = LogManager.getLogger(AbstractExcelReader.class);
	
	public static final boolean DEFAULT_PROCESS_WORKBOOK_CONTENT 			= true;
	
	private boolean processWorkbookContent 			= DEFAULT_PROCESS_WORKBOOK_CONTENT;
	private File inputFile;
	private XSSFWorkbook workbook;
	
	private boolean processingStopped = false;
	
	private int currentRow;
	private int currentCol;
	
	public AbstractExcelReader(String inputFile) throws Exception{
		this(new File(inputFile));
	}
	
	public AbstractExcelReader(File inputFile) throws Exception{
		Validate.exists(inputFile);
		this.inputFile = inputFile;
	}
	
	public boolean isProcessWorkbookContent() {
		return processWorkbookContent;
	}

	public void setProcessWorkbookContent(boolean processWorkbookContent) {
		this.processWorkbookContent = processWorkbookContent;
	}
	
	protected int getCurrentRow() {
		return currentRow;
	}

	protected int getCurrentCol() {
		return currentCol;
	}

	public void readExcelFile() throws Exception {
		reset();
		
		log.info("Reading excel file...");
		FileInputStream fis = new FileInputStream(inputFile);
        workbook = new XSSFWorkbook(fis);
        fis.close();
		
		if(isProcessWorkbookContent() && !processingStopped)
			processWorkbookContent();

		postProcessing();
		
		log.info("File processing finished.");
		if(processingStopped)
			log.warn("File processing was explicitly stopped");
	}
	
	private void processWorkbookContent() {
		log.info("Processing workbook content...");
		for(int i=0; i<workbook.getNumberOfSheets(); i++){
			if(processingStopped)
				break;
			
			XSSFSheet nextSheet = workbook.getSheetAt(i);
			if(nextSheet == null){
				log.debug("Found new sheet but aborted processing because of NULL value");
				continue;
			} else {
				log.debug("Found new sheet: {}", nextSheet.getSheetName());
			}
			if(sheetFound(nextSheet))
				processSheet(nextSheet);
        }
		contentProcessingFinished();
	}
	
	/**
	 * Gets called when a new sheet was found while expecting workbook content.
	 * @param nextSheet Sheet found
	 * @return <code>true</code> when the sheet should be processed; <code>false</code> otherwise.
	 */
	protected boolean sheetFound(XSSFSheet nextSheet) {
		return true;
	}

	private void processSheet(XSSFSheet sheet){
//		System.out.println("sheet found: " + sheet.getSheetName());
		currentRow = 0;
		Iterator<Row> iterator = sheet.iterator();
		while (iterator.hasNext()) {
			currentRow++;
			XSSFRow nextRow = (XSSFRow) iterator.next();
			if(nextRow == null){
				log.debug("Found new row ({}) but aborted processing because of NULL value", currentRow);
				continue;
			} else {
				log.debug("Found new row ({})", currentRow);
			}
			if(rowFound(sheet, nextRow))
				processRow(sheet, nextRow);
		}
	}
	
	/**
	 * Gets called when a new row was found while expecting sheet content.
	 * @param nextSheet Sheet found
	 * @return <code>true</code> when the row should be processed; <code>false</code> otherwise.
	 */
	protected boolean rowFound(XSSFSheet sheet, XSSFRow nextRow) {
		return true;
	}
	
	private void processRow(XSSFSheet sheet, XSSFRow row){
		currentCol = 0;
		for(int cellNum=0; cellNum<row.getLastCellNum(); cellNum++){
			currentCol++;
			XSSFCell nextCell = row.getCell(cellNum);
			if(nextCell == null){
				log.debug("Found new cell ({}) but aborted processing because of NULL value", currentCol);
				continue;
			} else {
				log.debug("Found new cell ({})", currentCol);
			}
        	if(cellFound(sheet, row, nextCell))
        		processCell(sheet, row, nextCell);
		}
	}
	
	/**
	 * Gets called when a new cell was found while expecting row content.
	 * @param nextSheet Sheet found
	 * @return <code>true</code> when the cell should be processed; <code>false</code> otherwise.
	 */
	protected boolean cellFound(XSSFSheet sheet, XSSFRow row, XSSFCell nextCell) {
		return true;
	}
	
	private void processCell(XSSFSheet sheet, XSSFRow row, XSSFCell cell){
        cellValueFound(currentRow, currentCol, sheet, new FormattedCellValue(cell));
	}
	
	protected void cellValueFound(int row, int col, XSSFSheet sheet, FormattedCellValue formattedCellValue){}
	
	protected void stopFurtherProcessing(){
		processingStopped = true;
	}
	
	public boolean processingStoppedExplicitly(){
		return processingStopped;
	}
	
	protected void reset(){}
	
	protected void contentProcessingFinished() {}
	
	protected void postProcessing() {}
	
//	protected Object getFormulaValue(XSSFCell cell){
//		if(cell.getCellTypeEnum() != CellType.FORMULA)
//			return null;
//		if(cell.getCachedFormulaResultTypeEnum() == CellType.STRING)
//			return cell.getRichStringCellValue();
//		if(cell.getCachedFormulaResultTypeEnum() == CellType.NUMERIC)
//			return cell.getNumericCellValue();
//		return null;
//	}
	
	public enum sheetReadingMode {
		COLS, ROWS;
	}

}
