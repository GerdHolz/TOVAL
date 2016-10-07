package de.invation.code.toval.graphic.component;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

public abstract class TablePanel<T extends JTable> extends JPanel {
	
	private static final long serialVersionUID = -1252295861769767277L;
	
	protected T tableReview;
	protected String description;

	public TablePanel(String description) {
		super(new BorderLayout());
		this.description = description;
	}
	
	protected void setUpGUI(){
		JLabel label = new JLabel(description, JLabel.LEADING);
		Font font = label.getFont();
		Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
		label.setFont(boldFont);
		add(label, BorderLayout.PAGE_START);
		JScrollPane scrollPane = new JScrollPane(getTable());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, BorderLayout.CENTER);
		JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.LEADING));
		for(JButton button: getButtons())
			panelButtons.add(button);
		add(panelButtons, BorderLayout.PAGE_END);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
	
	public T getTable(){
		if(tableReview == null){
			tableReview = createTable();
		}
		return tableReview;
	}
	
	protected abstract T createTable();
	
	protected abstract List<JButton> getButtons();

}
