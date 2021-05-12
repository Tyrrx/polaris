package polaris;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 04.08.2020, 13:47
 */

public final class GetValueOrThrowException extends Exception {
    public GetValueOrThrowException(String message) {
        super(message);
    }

    public GetValueOrThrowException(
        String message,
        Throwable cause) {
        super(message,
            cause);
    }

    public GetValueOrThrowException(Throwable cause) {
        super(cause);
    }

    protected GetValueOrThrowException(
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
