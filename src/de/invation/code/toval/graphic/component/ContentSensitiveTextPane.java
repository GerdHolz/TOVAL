package de.invation.code.toval.graphic.component;

import java.awt.Font;

import javax.swing.JTextPane;

import de.invation.code.toval.misc.HTMLUtils;

public class ContentSensitiveTextPane extends JTextPane {

	private static final long serialVersionUID = 8696492311749594152L;

	private static final String FONT_TAG_START = "<font";
	private static final int FONT_SIZE_HTML = 3;
	private static final String fontTagFormat = "<font face=\"%s\" size=\"" + FONT_SIZE_HTML + "\">";
	private static final String htmlFormat1 = "<html>%s%s";
	private static final String htmlFormat2 = "%s%s";
	
	

	@Override
	public void setText(String text) {
		if(HTMLUtils.isHTMLText(text)){
			setContentType("text/html");
			if(text.contains(FONT_TAG_START)){
				super.setText(text);
			} else {
				if(text.startsWith("<html>")){
					super.setText(String.format(htmlFormat1, getFontTag(), text.length() > 6 ? text.substring(6) : ""));
				} else {
					super.setText(String.format(htmlFormat2, getFontTag(), text));
				}
			}
		} else {
			setContentType("text/plain");
			super.setText(text);
			setFont(getPreferredFont());
		}
	}
	
	private String getFontTag(){
		Font fontToUse = getPreferredFont();
		return String.format(fontTagFormat, fontToUse.getFamily(), fontToUse.getSize());
	}
	
	private Font getPreferredFont(){
		return new Font(getFont().getName(), Font.PLAIN, getFont().getSize());
	}
	
//	public static void main(String[] args) {
//		String testHTML = "<html><body style=\"font-family:'Arial'; font-size:'x-small'\">Sehr geehrte(r) Modellgruppenverantwortliche(r),<br><br>Für das Review der Modellgruppe <b>Best Practices</b> sind noch nicht alle übersandten Reviewvorlagen vollständig bearbeitet worden.<br><br>Sie werden dringend gebeten dafür Sorge zu tragen, dass die übersandten Reviewvorlagen bearbeitet und an das Prozessmanagement zurück gesandt werden. Andernfalls kann der Reviewvorgang nicht beendet werden.<br><br>Details zum Reviewstatus:<table><tr><td>Start:</td><td><b>01.03.2017</b></td></tr><tr><td>Bearbeitungszeit (geplant):</td><td>28 Tage</td></tr><tr><td>Bearbeitungszeit (aktuell):</td><td>55 Tage</td></tr><tr><td>Überfällig:</td><td><b>27 Tage</b></td></tr><tr><td>Eskalationsstufe:</td><td><b>2</b></td></tr></table><br>Betroffene Reviewvorlagen:<ul><li>[Best Practices] Reviewvorlage - Stocker.xlsx</li></ul><br><br>Für Rückfragen stehen Ihnen die Mitarbeiter des Prozessmanagements gerne zur Verfügung.<br>Kontakt: Fr. Hipp (5106) oder Hr. Stocker (5278)<br><br>Freundliche Grüße<br><br>Prozessmanagement Office (GPMO)</body></html>";
//		ContentSensitiveTextPane textPane = new ContentSensitiveTextPane();
//		textPane.setText(testHTML);
//		JPanel panel = new JPanel(new BorderLayout());
//		panel.add(textPane);
//		new DisplayFrame(panel, true, true);
//	}
	

}
