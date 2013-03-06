package graphic.diagrams;

import graphic.diagrams.models.DotChartModel;
import graphic.diagrams.models.OneDimChartModel;
import graphic.diagrams.models.ScatterChartModel;
import graphic.diagrams.panels.BarChartPanel;
import graphic.diagrams.panels.DotChartPanel;
import graphic.diagrams.panels.OneDimChartPanel;
import graphic.diagrams.panels.ScatterChartPanel;

import java.util.List;
import java.util.Map;

public class ChartFactory {
	
	public static <S extends Number & Comparable<? super S>,T extends Number & Comparable<? super T>> ScatterChartPanel createScatterChartPanel(List<S> xValues, List<T> yValues) {
		return new ScatterChartPanel(new ScatterChartModel<S,T>(xValues, yValues, true));
	}
	public static <S extends Number & Comparable<? super S>,T extends Number & Comparable<? super T>> ScatterChartPanel createScatterChartPanel(List<S> xValues, List<T> yValues, boolean zeroBased) {
		return new ScatterChartPanel(new ScatterChartModel<S,T>(xValues, yValues, true), zeroBased);
	}
	public static <S extends Number & Comparable<? super S>,T extends Number & Comparable<? super T>> ScatterChartPanel createScatterChartPanel(List<S> xValues, List<T> yValues, boolean zeroBased, boolean onlyIntegerTicks) {
		return new ScatterChartPanel(new ScatterChartModel<S,T>(xValues, yValues, true), zeroBased, onlyIntegerTicks);
	}
	
	public static <S extends Number & Comparable<? super S>> DotChartPanel createDotChartPanel(Map<S, Integer> statistic) {
		return new DotChartPanel(new DotChartModel<S>(statistic));
	}
	public static <S extends Number & Comparable<? super S>> DotChartPanel createDotChartPanel(Map<S, Integer> statistic, boolean zeroBased) {
		return new DotChartPanel(new DotChartModel<S>(statistic), zeroBased);
	}
	public static <S extends Number & Comparable<? super S>> DotChartPanel createDotChartPanel(Map<S, Integer> statistic, boolean zeroBased, boolean onlyIntegerTicks) {
		return new DotChartPanel(new DotChartModel<S>(statistic), zeroBased, onlyIntegerTicks);
	}
	public static <S extends Number & Comparable<? super S>> DotChartPanel createDotChartPanel(List<S> values, List<Integer> counts) {
		return new DotChartPanel(new DotChartModel<S>(values, counts));
	}
	public static <S extends Number & Comparable<? super S>> DotChartPanel createDotChartPanel(List<S> values, List<Integer> counts, boolean zeroBased) {
		return new DotChartPanel(new DotChartModel<S>(values, counts), zeroBased);
	}
	public static <S extends Number & Comparable<? super S>> DotChartPanel createDotChartPanel(List<S> values, List<Integer> counts, boolean zeroBased, boolean onlyIntegerTicks) {
		return new DotChartPanel(new DotChartModel<S>(values, counts), zeroBased, onlyIntegerTicks);
	}
	
	public static <S extends Number & Comparable<? super S>,T extends Number & Comparable<? super T>> BarChartPanel createBarChartPanel(List<S> xValues, List<T> yValues) {
		return createBarChartPanel(xValues, yValues, true);
	}
	public static <S extends Number & Comparable<? super S>,T extends Number & Comparable<? super T>> BarChartPanel createBarChartPanel(List<S> xValues, List<T> yValues, boolean zeroBased) {
		return new BarChartPanel(new ScatterChartModel<S,T>(xValues, yValues, true), zeroBased);
	}
	public static <S extends Number & Comparable<? super S>,T extends Number & Comparable<? super T>> BarChartPanel createBarChartPanel(List<S> xValues, List<T> yValues, boolean zeroBased, boolean onlyIntegerTicks) {
		return new BarChartPanel(new ScatterChartModel<S,T>(xValues, yValues, true), zeroBased, onlyIntegerTicks);
	}
	
	public static <S extends Number & Comparable<? super S>> OneDimChartPanel createOneDimChartPanel(List<S> values) {
		return createOneDimChartPanel(values, true);
	}
	public static <S extends Number & Comparable<? super S>> OneDimChartPanel createOneDimChartPanel(List<S> values, boolean zeroBased) {
		return new OneDimChartPanel(new OneDimChartModel<S>(values), zeroBased);
	}
	public static <S extends Number & Comparable<? super S>> OneDimChartPanel createOneDimChartPanel(List<S> values, boolean zeroBased, boolean onlyIntegerTicks) {
		return new OneDimChartPanel(new OneDimChartModel<S>(values), zeroBased, onlyIntegerTicks);
	}
	
	

}
