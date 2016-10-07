package de.invation.code.toval.graphic.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

/**
 * This customized ListCellRenderer color the rows of a JList with two colors in an alternating way.<br>
 * The first color is the background color of the JList.<br>
 * The second color is the <i>alternating color</i> ({@link #alternatingColor}).<br>
 * For selected rows, the renderer uses the <i>selected color</i> ({@link #selectedColor}).<br>
 * <br>
 * The border for list entries is set to 10px left,<br>
 * but can be adjusted by constructor parameters.
 * 
 * @author Thomas Stocker
 */
public class AlternatingRowColorListCellRenderer<O> extends JLabel implements ListCellRenderer<O> {

	private static final long serialVersionUID = 3860155129823751738L;
	
	public static final Color DEFAULT_SELECTED_COLOR = new Color(10, 100, 200);
	public static final Color DEFAULT_ALTERNATING_COLOR = new Color(230, 230, 230);
	public static final Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(0, 10, 0, 0);
	
	private Color selectedColor;
	private Color alternatingColor;

	/**
	 * Creates a new list cell renderer with default values.
	 * @see #DEFAULT_ALTERNATING_COLOR
	 * @see #DEFAULT_SELECTED_COLOR
	 * @see #DEFAULT_BORDER
	 */
	public AlternatingRowColorListCellRenderer(){
		this(DEFAULT_SELECTED_COLOR, DEFAULT_ALTERNATING_COLOR, DEFAULT_BORDER);
	}
	
	/**
	 * Creates a new list cell renderer with the given parameters.
	 * @param selectedColor The color used for selected rows.
	 * @param alternatingColor The second color used for alternating row coloring.<br>
	 * The first color is the background color of the list.
	 * @param border The border to be used for list entries.
	 */
	public AlternatingRowColorListCellRenderer(Color selectedColor, Color alternatingColor, Border border){
		setOpaque(true);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
		this.selectedColor = selectedColor;
		this.alternatingColor = alternatingColor;
		setBorder(border);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends O> list, O value, int index, boolean isSelected, boolean cellHasFocus) {

		if(value != null){
			setText(getText(value));
			setToolTipText(getTooltip(value));
		} else {
			setText("");
			setToolTipText("");
		}

		if (isSelected) {
			setBackground(selectedColor);
			setForeground(new Color(0, 0, 0));
		} else {
			if ((index % 2) == 0) {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			} else {
				setBackground(alternatingColor);
				setForeground(list.getForeground());
			}
		}
		return this;
	}
	
	protected String getText(O value){
		return value.toString();
	}

	protected String getTooltip(O value){
		return value.toString();
	}

}