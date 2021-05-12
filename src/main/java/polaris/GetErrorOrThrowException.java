package polaris;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 04.08.2020, 13:47
 */

public final class GetErrorOrThrowException extends Exception {
	public GetErrorOrThrowException(String message) {
		super(message);
	}

	public GetErrorOrThrowException(
		String message,
		Throwable cause) {
		super(message,
			cause);
	}

	public GetErrorOrThrowException(Throwable cause) {
		super(cause);
	}

	protected GetErrorOrThrowException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace) {
		super(message,
			cause,
			enableSuppression,
			writableStackTrace);
	}
}
