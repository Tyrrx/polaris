package polaris;

import java.util.function.Consumer;
import java.util.function.Function;

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

	public <T1> Failure<T1> convert() {
		return new Failure<>(message);
	}

	@Override
	public void matchVoid(Consumer<T> success, Consumer<String> failure) {
		failure.accept(message);
	}

	@Override
	public <T1> T1 match(Function<T, T1> success, Function<String, T1> failure) {
		return failure.apply(message);
	}
}
