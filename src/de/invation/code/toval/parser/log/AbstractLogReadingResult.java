package de.invation.code.toval.parser.log;

import java.util.ArrayList;
import java.util.List;

public class AbstractLogReadingResult {
	
	private List<LineException> lineExceptions = new ArrayList<>();
	
	public void addLineException(LineException lineException){
		this.lineExceptions.add(lineException);
	}

	public List<LineException> getLineExceptions() {
		return lineExceptions;
	}

}
