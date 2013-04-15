package common.exception;

public class SystemException extends Exception {

	private static final long serialVersionUID = -3424751258506286616L;

	public SystemException() {
		super();
	}

	public SystemException(String s) {
		super(s);
	}

	public SystemException(Throwable throwable) {
		super(throwable);
	}

	public SystemException(String s, Throwable throwable) {
		super(s, throwable);
	}

}
