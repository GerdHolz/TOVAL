package de.invation.code.toval.parser.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractLogReader<R extends AbstractLogReadingResult> {

	protected File inputFile;
	protected R logReadingResult;

	protected AbstractLogReader(File inputFile) throws Exception {
		this.inputFile = inputFile;
		this.logReadingResult = createLogReadingResult();
	}

	public void readFile() throws Exception {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(inputFile.getAbsolutePath()));
			String line = null;
			int lineNumber = 0;
			while ((line = in.readLine()) != null) {
				lineNumber++;
				try {
					readLine(lineNumber, line);
				} catch (LineException e) {
					logReadingResult.addLineException(e);
					if (e.isCritical()) {
						throw new Exception(e);
					}
				}
			}
		} catch (IOException e) {
			throw new Exception("Exception while reading file: " + e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new Exception("Exception while closing file: " + e.getMessage());
				}
			}
		}
	}

	protected void readLine(int lineNumber, String line) throws LineException {
		Matcher matcher = getAcceptedLinePattern().matcher(line);
		if (matcher.find()){
			processLine(lineNumber, line, matcher);
		} else {
			if(!isHeaderLine(line)){
				throw new LineException(lineNumber, line, new Exception("Cannot parse line, unexpected structure"), false);
			}
		}
	}
	
	protected abstract Pattern getAcceptedLinePattern();
	
	protected abstract void processLine(int lineNumber, String line, Matcher matcher) throws LineException;
	
	protected abstract R createLogReadingResult();
	
	protected abstract String[] getHeaderLines();
	
	private boolean isHeaderLine(String line) {
		for (String headerLine : getHeaderLines()) {
			if (headerLine.equals(line))
				return true;
		}
		return false;
	}
	
	public R getLogReadingResult(){
		return logReadingResult;
	}

}
