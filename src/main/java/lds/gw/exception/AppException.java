package lds.gw.exception;

public class AppException extends RuntimeException {

	private static final long serialVersionUID = -6152467768527529423L;
	
	public String errorCode = "500";
	public String errorMessage = "系统异常";

	public AppException() {
	}

	public AppException(String message) {
		super(message);
	}
	
	public AppException(String code, String message) {
		super(message);
		this.errorCode = code;
		this.errorMessage = message;
	}

	public AppException(String message, Throwable e) {
		super(message, e);
	}
	
	public AppException(String code, String message, Throwable e) {
		super(message, e);
		this.errorCode = code;
		this.errorMessage = message;
	}
}
