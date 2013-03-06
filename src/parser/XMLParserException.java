package parser;

public class XMLParserException extends ParserException {
	
	private static final long serialVersionUID = 7379282132696502134L;
	private final String msg_Tagstructure = "Corrupted tagstructure";
	
	private ErrorCode errorCode = null;

	public XMLParserException(ErrorCode errorCode) {
		super();
		this.errorCode = errorCode;
	}

	@Override
	public String getMessage(){
		switch(errorCode){
			case TAGSTRUCTURE: return msg_Tagstructure;
		}
		return null;
	}
	
	public ErrorCode getErrorCode(){
		return errorCode;
	}
	
	public enum ErrorCode { 
		TAGSTRUCTURE;
	}

}