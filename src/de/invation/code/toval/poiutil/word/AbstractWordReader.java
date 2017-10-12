package de.invation.code.toval.poiutil.word;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import de.invation.code.toval.validate.Validate;

public abstract class AbstractWordReader {
	
	private static final Logger log = LogManager.getLogger(AbstractWordReader.class);
	
	public static final boolean DEFAULT_READ_DOCUMENT_HEADERS 				= true;
	public static final boolean DEFAULT_READ_DOCUMENT_FOOTERS 				= true;
	public static final boolean DEFAULT_PROCESS_DOCUMENT_CONTENT 			= true;
	public static final boolean DEFAULT_PROCESS_DOCUMENT_PARAGRAPHS			= true;
	public static final boolean DEFAULT_PROCESS_DOCUMENT_TABLES				= true;
	public static final boolean DEFAULT_STRING_BASED_PARAGRAPH_PROCESSING 	= false;
	public static final boolean DEFAULT_STRING_BASED_TABLE_PROCESSING 		= false;
	public static final boolean DEFAULT_STRING_BASED_TABLE_ROW_PROCESSING 	= false;
	public static final boolean DEFAULT_STRING_BASED_HEADER_PROCESSING 		= true;
	public static final boolean DEFAULT_STRING_BASED_FOOTER_PROCESSING 		= true;
	
	private boolean readDocumentHeader 				= DEFAULT_READ_DOCUMENT_HEADERS;
	private boolean readDocumentFooter 				= DEFAULT_READ_DOCUMENT_FOOTERS;
	private boolean processDocumentContent 			= DEFAULT_PROCESS_DOCUMENT_CONTENT;
	private boolean stringBasedParagraphProcessing 	= DEFAULT_STRING_BASED_PARAGRAPH_PROCESSING;
	private boolean stringBasedTableProcessing 		= DEFAULT_STRING_BASED_TABLE_PROCESSING;
	private boolean stringBasedTableRowProcessing 	= DEFAULT_STRING_BASED_TABLE_ROW_PROCESSING;
	private boolean stringBasedHeaderProcessing 	= DEFAULT_STRING_BASED_HEADER_PROCESSING;
	private boolean stringBasedFooterProcessing 	= DEFAULT_STRING_BASED_FOOTER_PROCESSING;
	
	private File inputFile;
	private XWPFDocument document;
	
	private int processedParagraphs = 0;
	private int processedTables = 0;
	private XWPFParagraph lastProcessedParagraph = null;
	private XWPFTable lastProcessedTable = null;
	
	private boolean processingStopped = false;
	
	private int numTablesFound = 0;
	private int numParagraphsFound = 0;
	
	private boolean processParagraphs = DEFAULT_PROCESS_DOCUMENT_PARAGRAPHS;
	private boolean processTables = DEFAULT_PROCESS_DOCUMENT_TABLES;
	
	public AbstractWordReader(String inputFile) throws Exception{
		this(new File(inputFile));
	}
	
	public AbstractWordReader(File inputFile) throws Exception{
		Validate.exists(inputFile);
		this.inputFile = inputFile;
	}

	public boolean isProcessParagraphs() {
		return processParagraphs;
	}

	public void setProcessParagraphs(boolean processParagraphs) {
		this.processParagraphs = processParagraphs;
	}
	
	public void enableParagraphProcessing(){
		setProcessParagraphs(true);
	}
	
	public void disableParagraphProcessing(){
		setProcessParagraphs(false);
	}

	public boolean isProcessTables() {
		return processTables;
	}

	public void setProcessTables(boolean processTables) {
		this.processTables = processTables;
	}
	
	public void enableTableProcessing(){
		setProcessTables(true);
	}
	
	public void disableTableProcessing(){
		setProcessTables(false);
	}

	public File getInputFile() {
		return inputFile;
	}

	public boolean isReadDocumentHeaders() {
		return readDocumentHeader;
	}

	public void setReadDocumentHeader(boolean readDocumentHeader) {
		this.readDocumentHeader = readDocumentHeader;
	}

	public boolean isReadDocumentFooters() {
		return readDocumentFooter;
	}

	public void setReadDocumentFooter(boolean readDocumentFooter) {
		this.readDocumentFooter = readDocumentFooter;
	}

	public boolean isProcessDocumentContent() {
		return processDocumentContent;
	}

	public void setProcessDocumentContent(boolean processDocumentContent) {
		this.processDocumentContent = processDocumentContent;
	}

	public boolean isStringBasedParagraphProcessing() {
		return stringBasedParagraphProcessing;
	}

	public void setStringBasedParagraphProcessing(boolean stringBasedParagraphProcessing) {
		this.stringBasedParagraphProcessing = stringBasedParagraphProcessing;
	}

	public boolean isStringBasedTableProcessing() {
		return stringBasedTableProcessing;
	}

	public void setStringBasedTableProcessing(boolean stringBasedTableProcessing) {
		this.stringBasedTableProcessing = stringBasedTableProcessing;
	}
	
	public boolean isStringBasedTableRowProcessing() {
		return stringBasedTableRowProcessing;
	}

	public void setStringBasedTableRowProcessing(boolean stringBasedTableProcessing) {
		this.stringBasedTableRowProcessing = stringBasedTableProcessing;
	}

	public boolean isStringBasedHeaderProcessing() {
		return stringBasedHeaderProcessing;
	}

	public void setStringBasedHeaderProcessing(boolean stringBasedHeaderProcessing) {
		this.stringBasedHeaderProcessing = stringBasedHeaderProcessing;
	}

	public boolean isStringBasedFooterProcessing() {
		return stringBasedFooterProcessing;
	}

	public void setStringBasedFooterProcessing(boolean stringBasedFooterProcessing) {
		this.stringBasedFooterProcessing = stringBasedFooterProcessing;
	}

	protected XWPFParagraph getLastProcessedParagraph() {
		return lastProcessedParagraph;
	}

	protected XWPFTable getLastProcessedTable() {
		return lastProcessedTable;
	}
	
	protected void stopFurtherProcessing(){
		processingStopped = true;
	}
	
	public boolean processingStoppedExplicitly(){
		return processingStopped;
	}

	public void readWordFile() throws Exception {
		processedParagraphs = 0;
		processedTables = 0;
		
		log.info("Reading word file...");
		FileInputStream fis = new FileInputStream(inputFile);
		document = new XWPFDocument(OPCPackage.open(fis));
		fis.close();
		
		if(isReadDocumentHeaders() && !processingStopped)
			readAndProcessHeaders();
		
		if(isReadDocumentFooters() && !processingStopped)
			readAndProcessFooter();
		
		if(isProcessDocumentContent() && !processingStopped)
			processDocumentContent();

		postProcessing();
		
		log.info("File processing finished.");
		if(processingStopped)
			log.warn("File processing was explicitly stopped");
	}

	private void readAndProcessHeaders(){
		log.info("Reading document headers...");
		for(HeaderFooterType type: HeaderFooterType.values()){
			if(processingStopped)
				return;
			
			log.info("Reading header ({})...", type);
			XWPFHeader documentHeader = type.getHeader(document);
			if(documentHeader == null){
				headerNotFound(type);
				continue;
			}
			if(isStringBasedHeaderProcessing()){
				if(documentHeader.getText() == null){
					headerNotFound(type);
					continue;
				}
				processHeaderString(documentHeader.getText(), type);
			} else {
				processHeader(documentHeader, type);
			}
		}
		headerProcessingFinished();
	}

	private void readAndProcessFooter(){
		log.info("Reading document footers...");
		for(HeaderFooterType type: HeaderFooterType.values()){
			log.info("Reading footer ({})...", type);
			XWPFFooter documentFooter = type.getFooter(document);
			if(documentFooter == null){
				footerNotFound(type);
				continue;
			}
			if(isStringBasedFooterProcessing()){
				if(documentFooter.getText() == null){
					footerNotFound(type);
					continue;
				}
				processFooterString(documentFooter.getText(), type);
			} else {
				processFooter(documentFooter, type);
			}
			
			if(processingStopped)
				return;
		}
		footerProcessingFinished();
	}

	private void processDocumentContent() {
		log.info("Processing document content...");
		for(IBodyElement bodyElement: document.getBodyElements()){
			if(processingStopped)
				break;
			
			switch(bodyElement.getElementType()){
			case CONTENTCONTROL:
				break;
			case PARAGRAPH:
				numParagraphsFound++;
				paragraphFound((XWPFParagraph) bodyElement);
				if(!isProcessParagraphs())
					break;
				log.info("Processing paragraph {}...", ++processedParagraphs);
				if(isStringBasedParagraphProcessing()){
					processParagraphStringBased((XWPFParagraph) bodyElement);
				} else {
					processParagraph((XWPFParagraph) bodyElement, false);
				}
				paragraphProcessed((XWPFParagraph) bodyElement);
				lastProcessedParagraph = (XWPFParagraph) bodyElement;
				break;
			case TABLE:
				numTablesFound++;
				tableFound((XWPFTable) bodyElement);
				if(!isProcessTables())
					break;
				log.info("Processing table {}...", ++processedTables);
				processTable((XWPFTable) bodyElement, isStringBasedTableProcessing());
				tableProcessed((XWPFTable) bodyElement);
				lastProcessedTable = (XWPFTable) bodyElement;
				break;
			default:
				break;
			
			}
		}
		contentProcessingFinished();
	}

	protected void processParagraphStringBased(XWPFParagraph paragraph){
		processParagraphString(paragraph.getText());
	}
	
	public int getNumTablesFound() {
		return numTablesFound;
	}

	public int getNumParagraphsFound() {
		return numParagraphsFound;
	}

	protected void paragraphFound(XWPFParagraph paragraph) {}
	
	protected void paragraphProcessed(XWPFParagraph paragraph) {}
	
	protected void paragraphRunFound(XWPFParagraph paragraph, XWPFRun paragraphRun, boolean tableCellParagraph) {}
	
	protected void paragraphRunProcessed(XWPFParagraph paragraph, XWPFRun paragraphRun, boolean tableCellParagraph) {}
	
	protected void tableFound(XWPFTable table) {}
	
	protected void tableProcessed(XWPFTable table) {}
	
	protected void headerProcessingFinished() {}
	
	protected void footerProcessingFinished() {}
	
	protected void contentProcessingFinished() {}
	
	protected void postProcessing() {}
	
	protected void headerNotFound(HeaderFooterType type){}
	
	protected void footerNotFound(HeaderFooterType type){}
	
	protected void processHeader(XWPFHeader header, HeaderFooterType type){}
	
	protected void processHeaderString(String header, HeaderFooterType type){}
	
	protected void processFooter(XWPFFooter footer, HeaderFooterType type){}
	
	protected void processFooterString(String footer, HeaderFooterType type){}
	
	protected void processParagraph(XWPFParagraph paragraph, boolean tableCellParagraph){
		for(XWPFRun paragraphRun: paragraph.getRuns()){
			if(processingStopped)
				return;
			
			paragraphRunFound(paragraph, paragraphRun, tableCellParagraph);
			processParagraphRun(paragraph, paragraphRun, tableCellParagraph);
			paragraphRunProcessed(paragraph, paragraphRun, tableCellParagraph);
		}
	}
	
	protected void processParagraphRun(XWPFParagraph paragraph, XWPFRun paragraphRun, boolean tableCellParagraph) {
		VerticalAlign subscript = paragraphRun.getSubscript();
        String smalltext = paragraphRun.getText(0);
        switch (subscript) {
            case BASELINE:
                System.out.println("smalltext, plain = " + smalltext);
                break;
            case SUBSCRIPT:
                System.out.println("smalltext, subscript = " + smalltext);
                break;
            case SUPERSCRIPT:
                System.out.println("smalltext, superscript = " + smalltext);
                break;
        }
	}

	protected void processParagraphString(String paragraph){}
	
	protected void processTable(XWPFTable table, boolean stringBased){
		log.debug("Processing table...");
		log.debug("Number of rows: {}", table.getNumberOfRows());
		if(stringBased){
			processTableString(toString(table));
		} else {
			int rowCount = 0;
			for(XWPFTableRow tableRow: table.getRows()){
				if(processingStopped)
					return;
				
				log.trace("Processing table row {}...", ++rowCount);
				processTableRow(table, tableRow, isStringBasedTableRowProcessing());
			}
		}
	}
	
	protected void processTableRow(XWPFTable table, XWPFTableRow tableRow, boolean stringBased) {
		log.trace("Processing table row...");
		log.trace("Number of cells: {}", tableRow.getTableCells().size());
		if(stringBased){
			processTableRowString(toString(tableRow));
		} else {
			int cellCount = 0;
			for(XWPFTableCell tableCell: tableRow.getTableCells()){
				if(processingStopped)
					return;
				
				log.trace("Processing table cell {}...", ++cellCount);
				processTableCell(table, tableRow, tableCell);
			}
		}
	}
	
	protected void processTableCell(XWPFTable table, XWPFTableRow tableRow, XWPFTableCell tableCell) {
		log.trace("Processing table cell...");
		log.trace("Number of paragraphs: {}", tableCell.getParagraphs().size());
		int cellCount = 0;
		for(XWPFParagraph paragraph: tableCell.getParagraphs()){
			if(processingStopped)
				return;
			
			log.trace("Processing table cell paragraph {}...", ++cellCount);
			processParagraph(paragraph, true);
		}
	}

	private String[][] toString(XWPFTable table){
		String[][] tableStrings = new String[table.getNumberOfRows()][];
		for(int i=0; i<table.getNumberOfRows(); i++){
			tableStrings[i] = toString(table.getRow(i));
		}
		return tableStrings;
	}
	
	private String[] toString(XWPFTableRow tableRow){
		String[] tableRowStrings = new String[tableRow.getTableCells().size()];
		for(int i=0; i<tableRow.getTableCells().size(); i++){
			tableRowStrings[i] = toString(tableRow.getCell(i));
		}
		return tableRowStrings;
	}
	
	private String toString(XWPFTableCell tableCell){
		return tableCell.getTextRecursively();
	}

	protected void processTableRowString(String[] tableRow){}
	
	protected void processTableString(String[][] table){}

	public enum HeaderFooterType {
		FIRST_PAGE,
		DEFAULT,
		EVEN_PAGE,
		ODD_PAGE;
		
		public XWPFFooter getFooter(XWPFDocument document){
			switch(this){
			case DEFAULT:
				return new XWPFHeaderFooterPolicy(document).getDefaultFooter();
			case EVEN_PAGE:
				return new XWPFHeaderFooterPolicy(document).getEvenPageFooter();
			case FIRST_PAGE:
				return new XWPFHeaderFooterPolicy(document).getFirstPageFooter();
			case ODD_PAGE:
				return new XWPFHeaderFooterPolicy(document).getOddPageFooter();
			default:
				return null;
			}
		}
		
		public XWPFHeader getHeader(XWPFDocument document){
			switch(this){
			case DEFAULT:
				return new XWPFHeaderFooterPolicy(document).getDefaultHeader();
			case EVEN_PAGE:
				return new XWPFHeaderFooterPolicy(document).getEvenPageHeader();
			case FIRST_PAGE:
				return new XWPFHeaderFooterPolicy(document).getFirstPageHeader();
			case ODD_PAGE:
				return new XWPFHeaderFooterPolicy(document).getOddPageHeader();
			default:
				return null;
			}
		}
	}
}
