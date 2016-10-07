package de.invation.code.toval.parser.log;

public class LineException extends Throwable {
	
	private int lineNumber;
	private Exception exception;
	private String lineText;
	private boolean critical;
	
	
	public LineException(int lineNumber, String lineText, Exception exception, boolean critical) {
		super();
		this.lineNumber = lineNumber;
		this.exception = exception;
		this.lineText = lineText;
		this.critical = critical;
	}
	
	public boolean isCritical(){
		return critical;
	}

	public String getExceptionMessage(){
		return exception.getMessage();
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getLineText() {
		return lineText;
	}

}
