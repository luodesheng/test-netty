package lds.gw.exception;

public class Parse8583Exception extends AppException {

	private static final long serialVersionUID = 3619977740890727984L;

	public Parse8583Exception() {

	}

	public Parse8583Exception(String message) {
		super("503", message);
	}
	
	public Parse8583Exception(String code, String message) {
		super(code, message);
	}

	public Parse8583Exception(String message, Throwable e) {
		super("503", message, e);
	}

}
