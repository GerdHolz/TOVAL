package de.invation.code.toval.element;

public class ElementException extends RuntimeException {

	private static final long serialVersionUID = -6092330725587202089L;
	
	private ErrorCode errorCode = null;
	private final String msg_ElementDoesNotStoreID = "Element does not store ID information";
	private final String msg_ElementDoesNotStoreName = "Element does not store name information";
	private final String msg_ElementDoesNotStoreType = "Element does not store type information";
	
	private boolean usePredefinedMessages = true;
	
	public ElementException(ErrorCode errorCode){
		super();
		this.errorCode = errorCode;
	}
	
	public ElementException(ErrorCode errorCode, String message){
		super(message);
		usePredefinedMessages = false;
	}
	
	public ElementException(String message){
		super(message);
		usePredefinedMessages = false;
	}
        
        public ElementException(ErrorCode errorCode, Throwable cause){
		super(cause);
		this.errorCode = errorCode;
	}
	
	public ElementException(ErrorCode errorCode, String message, Throwable cause){
		super(message, cause);
		usePredefinedMessages = false;
	}
	
	public ElementException(String message, Throwable cause){
		super(message, cause);
		usePredefinedMessages = false;
	}
	
	@Override
	public String getMessage(){
		if(!usePredefinedMessages){
			return super.getMessage();
		}
		switch(errorCode){
			case ELEMENT_DOES_NOT_STORE_ID: return msg_ElementDoesNotStoreID;
			case ELEMENT_DOES_NOT_STORE_NAME: return msg_ElementDoesNotStoreName;
			case ELEMENT_DOES_NOT_STORE_TYPE: return msg_ElementDoesNotStoreType;
		}
		return null;
	}
	
	public ErrorCode getErrorCode(){
		return errorCode;
	}
	
	public enum ErrorCode { 
		ELEMENT_DOES_NOT_STORE_ID,
		ELEMENT_DOES_NOT_STORE_NAME,
		ELEMENT_DOES_NOT_STORE_TYPE	;
	}

}
