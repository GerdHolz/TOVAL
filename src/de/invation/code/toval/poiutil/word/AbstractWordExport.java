package de.invation.code.toval.poiutil.word;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

import de.invation.code.toval.file.FileUtils;
import de.invation.code.toval.file.IFileExporter;
import de.invation.code.toval.validate.Validate;

public abstract class AbstractWordExport implements IFileExporter{
	
	private static final Logger log = LogManager.getLogger(AbstractWordExport.class);
	
//	private static final int TOTAL_PAGE_WIDTH = 9200;
	
	private static final long DEFAULT_PAGE_MARGIN_TOP 		= 1417L;	// 2.5cm
	private static final long DEFAULT_PAGE_MARGIN_BOTTOM 	= 1134L; 	// 2.0cm
	private static final long DEFAULT_PAGE_MARGIN_LEFT 		= 1417L;	// 2.5cm
	private static final long DEFAULT_PAGE_MARGIN_RIGHT 	= 1417L;	// 2.5cm
	
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
	
	private static final String DATE_FORMAT_STRING = "dd.MM.yyyy";
	protected static final String NULL_REPLACEMENT_STRING = null;
	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
	
	private static final String TO_STRING_FORMAT_DIFF = "%.0f";
	
	public static final ParagraphAlignment DEFAULT_ALIGNMENT_PARAGRAPH = ParagraphAlignment.LEFT;
	public static final ParagraphAlignment DEFAULT_ALIGNMENT_TITLE = ParagraphAlignment.LEFT;
	
	public static final Integer DEFAULT_ROW_HEIGHT = null;
	
	private static final int DEFAULT_TABLE_ROW_VERTICAL_PADDING = 300;
	private static final Color DEFAULT_TABLE_HEADER_COLOR = Color.LIGHT_GRAY;
//	private static final int DEFAULT_TABLE_HEADER_HEIGHT = 500;
	private static final XWPFVertAlign DEFAULT_TABLE_HEADER_VERTICAL_ALIGNMENT = XWPFVertAlign.CENTER;
	private static final int DEFAULT_TABLE_CELL_MARGIN = 80;
	private static final int DEFAULT_TABLE_CELL_MARGIN_TOP = DEFAULT_TABLE_CELL_MARGIN;
	private static final int DEFAULT_TABLE_CELL_MARGIN_LEFT = DEFAULT_TABLE_CELL_MARGIN;
	private static final int DEFAULT_TABLE_CELL_MARGIN_RIGHT = DEFAULT_TABLE_CELL_MARGIN;
	private static final int DEFAULT_TABLE_CELL_MARGIN_BOTTOM = DEFAULT_TABLE_CELL_MARGIN;
	
	public static final String LINE_BREAK = "\n";
	
	public static final OutputMode DEFAULT_OUTPUT_MODE = OutputMode.WORD;
	
	private static final XWPFVertAlign DEFAULT_ROW_VERTICAL_ALIGNMENT = XWPFVertAlign.CENTER;

	private static final PageOrientation DEFAULT_PAGE_ORIENTATION = PageOrientation.PORTRAIT;
	private static final PaperSize DEFAULT_PAPER_SIZE = PaperSize.DIN_ISO_A4;
	
	private XWPFDocument document;
	private File outputFile;
	private OutputMode outputMode = DEFAULT_OUTPUT_MODE;
	protected CTSectPr sectionPropertiesWholeDocument;
	
	private PageOrientation pageOrientation;
	private PaperSize paperSize;
	
	public AbstractWordExport(String outputFile) throws Exception{
		this(new File(outputFile));
	}
	
	public AbstractWordExport(File outputFile) throws Exception{
		super();
		
		if(paperSize == null)
			paperSize = getDefaultPaperSize();
		if(pageOrientation == null)
			pageOrientation = getDefaultOrientation();
		
		this.outputFile = outputFile;
		this.document = new XWPFDocument();
		
		CTDocument1 document = this.document.getDocument();
		CTBody body = document.getBody();
		if (!body.isSetSectPr())
		     body.addNewSectPr();
		sectionPropertiesWholeDocument = body.getSectPr();
		if(!sectionPropertiesWholeDocument.isSetPgSz())
		    sectionPropertiesWholeDocument.addNewPgSz();
		if(!sectionPropertiesWholeDocument.isSetPgMar())
			sectionPropertiesWholeDocument.addNewPgMar();
		if(!sectionPropertiesWholeDocument.isSetPgBorders())
			sectionPropertiesWholeDocument.addNewPgBorders();
		
		setPaperSize(getDefaultPaperSize());
		setPageOrientation(getDefaultOrientation());
		
		CTPageMar pageMar = sectionPropertiesWholeDocument.getPgMar();
		pageMar.setLeft(BigInteger.valueOf(getPageMarginLeft()));
		pageMar.setTop(BigInteger.valueOf(getPageMarginTop()));
		pageMar.setRight(BigInteger.valueOf(getPageMarginRight()));
		pageMar.setBottom(BigInteger.valueOf(getPageMarginBottom()));
		
//		System.out.println(" page margin left: " + pageMar.getLeft());
//		System.out.println("page margin right: " + pageMar.getRight());
//		System.out.println("page width: " + getPageWidth());
	}
	
	/**
	 * Set a file located within a java package as template file (document styles).<br>
	 * @param packagePath Path of the package where the template file resides.<br>
	 * The separator character '/' has also to be used as leading and trailing character.
	 * @param templateFile Name of the template file.
	 * @throws Exception
	 */
	public void setTemplate(String packagePath, String templateFile) throws Exception{
		setTemplate(getTemplateFileInternal(packagePath, templateFile));
	}

	public void setTemplate(String templateFile) throws Exception{
		setTemplate(new File(templateFile));
	}
	
	public void setTemplate(File templateFile) throws Exception{
		Validate.exists(templateFile);
		
//		System.out.println(templateFile.getAbsolutePath());
		
		XWPFDocument template = createDocumentFromTemplate(templateFile);
		XWPFStyles newStyles = this.document.createStyles();
		newStyles.setStyles(template.getStyle());
	}
	
	public long getPageMarginTop(){
		return DEFAULT_PAGE_MARGIN_TOP;
	}
	
	public long getPageMarginBottom(){
		return DEFAULT_PAGE_MARGIN_BOTTOM;
	}
	
	public long getPageMarginLeft(){
		return DEFAULT_PAGE_MARGIN_LEFT;
	}
	
	public long getPageMarginRight(){
		return DEFAULT_PAGE_MARGIN_RIGHT;
	}
	
	/**
	 * Returns the page width without margins.
	 * @return
	 */
	protected long getPageWidth(){
		return getPaperSize().width_in_points*20 - Math.round((getPageMarginLeft()/20.0) - (getPageMarginRight()/20.0));
	}
	
	/**
	 * Returns the page height without margins.
	 * @return
	 */
	protected long getPageHeight(){
		return getPaperSize().width_in_points - getPageMarginTop() - getPageMarginBottom();
	}
	
	public PageOrientation getPageOrientation() {
		return pageOrientation;
	}

	public void setPageOrientation(PageOrientation pageOrientation) {
		this.pageOrientation = pageOrientation;
		document.getDocument().getBody().getSectPr().getPgSz().setOrient(pageOrientation.getPoiOrientation());
	}

	public PaperSize getPaperSize() {
		return paperSize;
	}

	public void setPaperSize(PaperSize paperSize) {
		this.paperSize = paperSize;
		document.getDocument().getBody().getSectPr().getPgSz().setW(BigInteger.valueOf(paperSize.width_in_points * 20));
		document.getDocument().getBody().getSectPr().getPgSz().setH(BigInteger.valueOf(paperSize.height_in_points * 20));
	}
	
	public void setPaperSize(PaperSize paperSize, PageOrientation pageOrientation) {
		setPaperSize(paperSize);
		setPageOrientation(pageOrientation);
	}
	
	protected PaperSize getDefaultPaperSize(){
		return DEFAULT_PAPER_SIZE;
	}
	
	protected PageOrientation getDefaultOrientation(){
		return DEFAULT_PAGE_ORIENTATION;
	}
	
	protected int getDefaultTableCellMarginTop(){
		return DEFAULT_TABLE_CELL_MARGIN_TOP;
	}
	protected int getDefaultTableCellMarginBottom(){
		return DEFAULT_TABLE_CELL_MARGIN_BOTTOM;
	}
	protected int getDefaultTableCellMarginLeft(){
		return DEFAULT_TABLE_CELL_MARGIN_LEFT;
	}
	protected int getDefaultTableCellMarginRight(){
		return DEFAULT_TABLE_CELL_MARGIN_RIGHT;
	}
	
	private XWPFDocument createDocumentFromTemplate(File templateFile) throws Exception{
		Validate.exists(templateFile);
		Validate.noDirectory(templateFile);
		return new XWPFDocument(new FileInputStream(templateFile));
	}
	
	@Override
	public void createFile() throws Exception {
		createWordFile();
	}
	
	public void createWordFile() throws Exception {
		if(!contentsToExport())
			throw new Exception("No content to export, file creation aborted.");
		
		log.info("Writing word file...");
		addContent(document);
        FileOutputStream out = new FileOutputStream(outputFile);
        document.write(out);
        out.close();
        document.close();
        log.info("File creation finished.");
	}
	
	protected abstract void addContent(XWPFDocument document) throws Exception;
	
	protected XWPFParagraph addParagraph(String text, ParagraphAlignment alignment) {
		return addParagraph(getStyleNameStandard(), text, alignment);
	}
	
	protected XWPFParagraph addParagraph(String text) {
		return addParagraph(getStyleNameStandard(), text);
	}
	
	protected XWPFParagraph addParagraph(String style, String text) {
		return addParagraph(style, text, DEFAULT_ALIGNMENT_PARAGRAPH);
	}
	
	protected XWPFParagraph addTitle(String text){
		return addTitle(text, DEFAULT_ALIGNMENT_TITLE);
	}
	
	protected XWPFParagraph addTitle(String text, ParagraphAlignment alignment){
		return addParagraph(getStyleNameTitle(), text, alignment);
	}
	
	protected abstract String getStyleNameTitle();
	
	protected abstract String getStyleNameStandard();
	
	protected XWPFParagraph addParagraph(String style, String text, ParagraphAlignment alignment) {
		XWPFParagraph paragraph = document.createParagraph();
		// paragraph.setPageBreak(true);
		paragraph.setAlignment(alignment);
		paragraph.setStyle(style);
		if(text == null)
			return paragraph;
		if(text.equals(LINE_BREAK)){
			XWPFRun run = paragraph.getRuns().isEmpty() ? paragraph.createRun() : getLastRun(paragraph);
			run.addCarriageReturn();
			return paragraph;
		}
		addText(paragraph, text);
		return paragraph;
	}
	
	protected List<XWPFRun> addText(XWPFParagraph paragraph, String text){
		List<XWPFRun> result = new ArrayList<>();
		if(text.equals(LINE_BREAK)){
			XWPFRun run = paragraph.createRun();
			run.addCarriageReturn();
			result.add(run);
			return result;
		}
		String[] splittedText = text.split(LINE_BREAK);
		for(int i=0;i<splittedText.length;i++){
			XWPFRun run = paragraph.createRun();
			result.add(run);
			run.setText(splittedText[i]);
			if(i<splittedText.length-1)
				run.addCarriageReturn();
		}
		return result;
	}
	
	protected void addVerticalSpacing(int spacing){
		document.getLastParagraph().setSpacingAfter(document.getLastParagraph().getSpacingAfter() + spacing);
	}
	
//	protected void addLineBreak(){
//		addLineBreak(STYLE_NAME_STANDARD);
//	}
//	
//	protected void addLineBreak(String style){
//		addParagraph(style, LINE_BREAK);
//	}
//	
	protected XWPFRun addLineBreak(XWPFParagraph paragraph){
		return addText(paragraph, LINE_BREAK).get(0);
	}
	
	protected XWPFRun getLastRun(XWPFParagraph paragraph){
		if(paragraph.getRuns().isEmpty())
			return null;
		return paragraph.getRuns().get(paragraph.getRuns().size()-1);
	}

	protected XWPFTable newTable(XWPFDocument document, String... firstRow){
		return newTable(document, 
//						getDefaultTableHeaderHeight(), 
						getDefaultTableHeaderVerticalAlignment(), 
						getDefaultTableHeaderColor(), 
						firstRow);
	}
	
	private Color getDefaultTableHeaderColor() {
		return DEFAULT_TABLE_HEADER_COLOR;
	}

//	private Integer getDefaultTableHeaderHeight() {
//		return DEFAULT_TABLE_HEADER_HEIGHT;
//	}

	private XWPFVertAlign getDefaultTableHeaderVerticalAlignment() {
		return DEFAULT_TABLE_HEADER_VERTICAL_ALIGNMENT;
	}
	
	private Integer getDefaultTableRowHeight() {
		return DEFAULT_ROW_HEIGHT;
	}

	private XWPFVertAlign getDefaultTableRowVerticalAlignment() {
		return DEFAULT_ROW_VERTICAL_ALIGNMENT;
	}
	
	private int getDefaultTableRowVerticalPadding(){
		return DEFAULT_TABLE_ROW_VERTICAL_PADDING;
	}
	
	protected XWPFTable newTable(	XWPFDocument document, 
//									Integer rowHeight, 
									XWPFVertAlign alignment, 
									Color color, 
									String... firstRow){
		Validate.notNull(document);
		Validate.notNull(firstRow);
		Validate.notEmpty(firstRow);
		XWPFTable table = document.createTable();
		table.setCellMargins(getDefaultTableCellMarginTop(), getDefaultTableCellMarginLeft(), getDefaultTableCellMarginBottom(), getDefaultTableCellMarginRight());
		XWPFTableRow row = table.getRow(0);
//		if(rowHeight != null)
//			row.setHeight(rowHeight);
		XWPFRun run = row.getCell(0).getParagraphs().get(0).createRun();
		run.setBold(true);
		run.setText(firstRow[0]);
		
		if(firstRow.length < 2)
			return table;
		for(int i=1;i<firstRow.length;i++){
			XWPFRun run2 = table.getRow(0).createCell().getParagraphs().get(0).createRun();
			run2.setBold(true);
			run2.setText(firstRow[i]);
		}
		if(alignment != null)
			setVerticalAlignment(row, alignment);
		if(color != null)
			setColor(row, color);
		return table;
	}
	
	protected XWPFTableRow addRow(XWPFTable table, boolean addIfNecessary, String... values) throws Exception{
		return addRow(table, addIfNecessary, getDefaultTableRowHeight(), getDefaultTableRowVerticalAlignment(), values);
	}
	
	protected XWPFTableRow addRow(XWPFTable table, boolean addIfNecessary, Integer height, String... values) throws Exception{
		return addRow(table, addIfNecessary, height, getDefaultTableRowVerticalAlignment(), values);
	}

	protected XWPFTableRow addRow(XWPFTable table, boolean addIfNecessary, XWPFVertAlign alignment, String... values) throws Exception{
		return addRow(table, addIfNecessary, getDefaultTableRowHeight(), alignment, values);
	}
	
	protected XWPFTableRow addRow(XWPFTable table, boolean addIfNecessary, Integer height, XWPFVertAlign alignment, String... values) throws Exception{
		XWPFTableRow tableRow = table.createRow();
		if(height != null)
			tableRow.setHeight(height);
		int cellCount = tableRow.getTableCells().size();
		if(cellCount > -1 && values.length > cellCount){
			if(!addIfNecessary)
				throw new Exception("Unexpected number of values.");
			for(int j=0;j<values.length-cellCount; j++)
				tableRow.addNewTableCell();
		}
		
		for(int i=0; i<values.length; i++){
			if(values[i] == null || values[i].isEmpty())
				continue;
			XWPFParagraph para = tableRow.getCell(i).getParagraphs().get(0);
			String[] splittedText = values[i].split(LINE_BREAK);
			for(int j=0;j<splittedText.length;j++){
				XWPFRun run = para.createRun();
				run.setText(splittedText[j].trim());
				if(j<splittedText.length-1)
					run.addBreak();
			}
		}
		if(alignment != null)
			setVerticalAlignment(tableRow, alignment);
		return tableRow;
	}
	
	public void setFont(XWPFTable table, String fontFamily, int fontSize){
		for(XWPFTableRow row: table.getRows()){
			setFont(row, fontFamily, fontSize);
		}
	}
	
	public void setFont(XWPFTableRow tableRow, String fontFamily, int fontSize){
		for(XWPFTableCell cell : tableRow.getTableCells()){
			for(XWPFParagraph paragraph : cell.getParagraphs()){
				setFont(paragraph, fontFamily, fontSize);
			}
		}
	}
	
	public void setFont(XWPFTableRow tableRow, int cell, String fontFamily, int fontSize){
		for(XWPFParagraph paragraph : tableRow.getCell(cell).getParagraphs()){
			setFont(paragraph, fontFamily, fontSize);
		}
	}
	
	public void setFont(XWPFParagraph paragraph, String fontFamily, int fontSize){
		for (XWPFRun run : paragraph.getRuns()) {
			run.setFontFamily(fontFamily);
			run.setFontSize(fontSize);
		}
	}
	
	protected void setVerticalAlignment(XWPFTableRow tableRow, XWPFVertAlign alignment){
		for(XWPFTableCell cell : tableRow.getTableCells()){
			cell.setVerticalAlignment(alignment);
		}
	}
	
	public void setColor(XWPFTableRow tableRow, int col, Color color){
		setColor(tableRow.getCell(col), color);
	}
	
	public void setColor(XWPFTableRow tableRow, Color color){
		for(XWPFTableCell cell : tableRow.getTableCells()){
			setColor(cell, color);
		}
	}
	
	public void setColor(XWPFTableCell tableCell, Color color){
		tableCell.setColor(getColorString(color));
	}
	
	protected String getColorString(Color color){
		return String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
	}

	public void setColWidths(XWPFTable table, long[] minWidths, TableColWidthDistributionPolicy distributionPolicy) {
		switch(distributionPolicy){
		case DISTRIBUTE_OVER_ALL_COLS:
			setColWidths(table, minWidths, -1);
			break;
		case EXTRA_SPACE_GOES_TO_FIRST_COL:
			setColWidths(table, minWidths, 0);
			break;
		case EXTRA_SPACE_GOES_TO_LAST_COL:
			setColWidths(table, minWidths, minWidths.length - 1);
			break;
		}
	}
	
	public void setColWidths(XWPFTable table, long[] minWidths, int colIndex) {
		int totalMinwidth = 0;
		for(long minWidth: minWidths)
			totalMinwidth += minWidth;
		long extraSpace = getPageWidth() - totalMinwidth;
		if(extraSpace > 0){
			switch(colIndex){
			case -1:
				int extraSpacePerCol = (int) Math.round(extraSpace / (minWidths.length + 0.0));
				for(int i=0; i<minWidths.length; i++)
					minWidths[i] = minWidths[i] + extraSpacePerCol;
				break;
			default:
				minWidths[colIndex] = minWidths[colIndex] + extraSpace;
				break;
			}
		}
		for (int i = 0; i < table.getNumberOfRows(); i++) {
			XWPFTableRow row = table.getRow(i);
			int numCells = row.getTableCells().size();
			for (int j = 0; j < numCells; j++) {
				XWPFTableCell cell = row.getCell(j);
				cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(minWidths[j]));
			}
		}
	}
	
	public OutputMode getOutputMode() {
		return outputMode;
	}

	public void setOutputMode(OutputMode outputMode) {
		this.outputMode = outputMode;
	}
	
	@Override
	public String getFileName() {
		return FileUtils.getFile(getOutputFile());
	}

	@Override
	public File getOutputFile() {
		return outputFile;
	}

	public enum OutputMode {
		WORD, PDF;
	}
	
	public enum TableColWidthDistributionPolicy {
		DISTRIBUTE_OVER_ALL_COLS,
		EXTRA_SPACE_GOES_TO_FIRST_COL,
		EXTRA_SPACE_GOES_TO_LAST_COL;
	}
	
	public static File getTemplateFileInternal(String path, String fileName) throws Exception{
		File templateFile = null;
		try {
			URL url = AbstractWordExport.class.getResource(path.concat(fileName));
			if(url == null)
				throw new Exception("URL for internal template file is null.");
			templateFile = new File(url.getFile());
		} catch(Exception e){
			throw new Exception("Unable to set internal template file", e);
		}
		return templateFile;
	}
	
	public enum PageOrientation {
		PORTRAIT(STPageOrientation.PORTRAIT), 
		LANDSCAPE(STPageOrientation.LANDSCAPE);
		
		private STPageOrientation.Enum poiOrientation;

		private PageOrientation(STPageOrientation.Enum poiOrientation) {
			this.poiOrientation = poiOrientation;
		}

		public STPageOrientation.Enum getPoiOrientation() {
			return poiOrientation;
		}
	}

}
