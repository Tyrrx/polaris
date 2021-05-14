package polaris;

import java.util.function.Consumer;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 04.08.2020, 13:47
 */

public final class Failure<T> extends Result<T> {
	private String message;

	public Failure(String message) {
		super(false);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public <T1> Failure<T1> convert() {
		return new Failure<>(this.getMessage());
	}

	@Override
	public void matchVoid(Consumer<T> success, Consumer<String> failure) {
		failure.accept(this.getMessage());
	}
}
