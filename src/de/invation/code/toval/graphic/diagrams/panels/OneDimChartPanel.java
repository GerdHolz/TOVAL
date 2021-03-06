package de.invation.code.toval.graphic.diagrams.panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import de.invation.code.toval.graphic.diagrams.models.OneDimChartModel;
import de.invation.code.toval.graphic.diagrams.models.ChartModel.ValueDimension;
import de.invation.code.toval.graphic.util.GraphicUtils;


@SuppressWarnings("serial")
public class OneDimChartPanel extends ScatterChartPanel {
	
	private static final int MINIMUM_MARKER_HEIGHT = 100;
	
	public OneDimChartPanel(OneDimChartModel<?> values) {
		this(values, true);
	}
	
	public OneDimChartPanel(OneDimChartModel<?> values, boolean zeroBased) {
		this(values, zeroBased, false, false);
	}
	
	public OneDimChartPanel(OneDimChartModel<?> values, boolean zeroBased, boolean onlyIntegerTicksX, boolean onlyIntegerTicksY) {
		super(values, zeroBased, onlyIntegerTicksX, onlyIntegerTicksY, true, false);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getRequiredPoints(ValueDimension.X) + spacing.left + spacing.right, spacing.bottom + spacing.top + MINIMUM_MARKER_HEIGHT);
	}
	
	@Override
	protected void paintValues(Graphics g, boolean paintLines) {
		Point valueLocation;
		for (int i = 0; i < getValueCount(); i++) {
			valueLocation = getPointFor(i);
			Point base = new Point(valueLocation.x, getXAxisEnd().y);
			Point marking = new Point(valueLocation.x, getPaintingRegion().getCenter().y);
			g.drawLine(base.x, base.y, marking.x, marking.y);
			GraphicUtils.fillCircle(g, marking, 5);
		}
	}

}
