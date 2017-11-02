package de.invation.code.toval.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapUtils {

	/**
	 * String for value separation.<br>
	 * Used for generating String representations of collections.
	 */
	public static final char VALUE_MAPPING = '=';

	/**
	 * String for value separation.<br>
	 * Used for generating String representations of collections.
	 */
	public static final char VALUE_SEPARATION = ' ';
	/**
	 * Default precision used for String representations of map elements having
	 * type <code>Float</code> or <code>Double</code>.
	 */
	public static final int DEFAULT_PRECISION = 2;
	/**
	 * String representation for empty maps.
	 */
	public static final String EMPTY_MAP = "[]";

	public static String toString(Map<?, ?> map) {
		return toString(map, DEFAULT_PRECISION, DEFAULT_PRECISION);
	}

	public static String toString(Map<?, ?> map, int precision) {
		return toString(map, precision, precision);
	}

	public static String toString(Map<?, ?> map, int keyPrecision, int valuePrecision) {
		if (map == null)
			throw new NullPointerException();
		if (map.isEmpty())
			return EMPTY_MAP;
		List<Object> keys = new ArrayList<>();
		List<Object> values = new ArrayList<>();
		for(Map.Entry<?, ?> entry: map.entrySet()){
			keys.add(entry.getKey());
			values.add(entry.getValue());
		}
		List<Object> v = new ArrayList<>();
		v.addAll(keys);
		v.addAll(values);
		return String.format(getFormat(map, keyPrecision, valuePrecision), v.toArray());
	}

	/**
	 * Returns a format-String that can be used to generate a String
	 * representation of a map using the String.format method.
	 * 
	 * @param map
	 *            Map for which a String representation is desired
	 * @param keyPrecision
	 *            Desired precision for <code>Float</code> and
	 *            <code>Double</code> elements
	 * @param valuePrecision
	 *            Desired precision for <code>Float</code> and
	 *            <code>Double</code> elements
	 * @return Format-String for <code>map</code>
	 * @see String#format(String, Object...)
	 */
	private static String getFormat(Map<?, ?> map, int keyPrecision, int valuePrecision) {
		StringBuilder builder = new StringBuilder();
		builder.append('{');
		int mappings = map.keySet().size();
		int c = 0;
		for (Object key : map.keySet()) {
			c++;
			builder.append(FormatUtils.getIndexedFormat(c, FormatUtils.getFormat(key, keyPrecision).substring(1)));
			builder.append(VALUE_MAPPING);
			builder.append(
					FormatUtils.getIndexedFormat(mappings + c, FormatUtils.getFormat(map.get(key), valuePrecision).substring(1)));
			if (c < mappings)
				builder.append(VALUE_SEPARATION);
		}
		builder.append('}');
		return builder.toString();
	}

}
