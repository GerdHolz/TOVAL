package de.invation.code.toval.poiutil.word;

import java.io.File;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;

import de.invation.code.toval.file.FileUtils;

public class TestWordExport extends AbstractWordExport {
	
	public static final String DEFAULT_EXPORT_TEMPLATE = "ExportTemplate.dotx";
	public static final String TEMPLATE_PATH = "/de/markant/adonis/export/word/";
	
	public static final String STYLE_NAME_TITLE = "Titel";
	public static final String STYLE_NAME_SUBTITLE = "Untertitel";
	public static final String STYLE_NAME_HEADING_1 = "berschrift1";
	public static final String STYLE_NAME_HEADING_2 = "berschrift2";
	public static final String STYLE_NAME_STANDARD = "Standard";
	public static final String STYLE_NAME_BOLD = "Fett";

	public TestWordExport(File outputFile) throws Exception {
		super(outputFile);
	}

	public TestWordExport(String outputFile) throws Exception {
		super(outputFile);
	}

	@Override
	public boolean contentsToExport() {
		return true;
	}

	@Override
	protected void addContent(XWPFDocument document) throws Exception {
		// TODO Auto-generated method stub
		this.addTitle("Testtitle");
	}

	@Override
	protected String getStyleNameTitle() {
		return STYLE_NAME_TITLE;
	}

	@Override
	protected String getStyleNameStandard() {
		return STYLE_NAME_STANDARD;
	}
	
	public static void main(String[] args) throws Exception {
		TestWordExport export = new TestWordExport("test.docx");
//		export.setTemplate("/de/invation/code/toval/poiutil/word/", "templateStandard.docx");
		export.createFile();
		FileUtils.openFile(export.getOutputFile());
	}
	
//	AbstractWordExport export = new AbstractWordExport("C:\\Testdokument.docx") {
//		
//		@Override
//		protected void addContent(XWPFDocument document) throws Exception {
//			XWPFStyles styles = document.getStyles();
//			System.out.println(styles);
//			addTitle("Modellgruppenübersicht");
//			addParagraph(STYLE_NAME_SUBTITLE, "Subtitle");
//			addParagraph("Dies ist ein Testtext mit einem\nZeilenumbruch");
//			addParagraph(STYLE_NAME_HEADING_1, "Heading 1");
//			
//			XWPFTable table = newTable(document, "eins", "zwei", "drei", "vier");
//			addRow(table, false, XWPFVertAlign.BOTTOM, "1", "2", "3", "4");
//			setColWidths(table, new int[]{200,1000,200,200}, 2);
//		}
//
//		@Override
//		public boolean contentsToExport() {
//			return true;
//		}
//	};
//	export.createWordFile();

}
