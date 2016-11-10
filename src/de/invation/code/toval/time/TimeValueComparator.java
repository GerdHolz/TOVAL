package de.invation.code.toval.time;

import java.util.Comparator;

public class TimeValueComparator implements Comparator<TimeValue> {

	@Override
	public int compare(TimeValue o1, TimeValue o2) {
		return o1.compareTo(o2);
	}

}
