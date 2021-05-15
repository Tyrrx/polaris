package polaris;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 04.08.2020, 13:47
 */
public final class Success<T> extends Result<T> {

	private final T value;

	public Success(T value) {
		super(true);
		this.value = value;
	}

	@Override
	public void matchVoid(Consumer<T> success, Consumer<String> failure) {
		success.accept(value);
	}

	@Override
	public <T1> T1 match(Function<T, T1> success, Function<String, T1> failure) {
		return success.apply(value);
	}
}
