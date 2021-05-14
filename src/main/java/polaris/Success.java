package polaris;

import java.util.function.Consumer;

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

	public T getValue() {
		return value;
	}

	@Override
	public void matchVoid(Consumer<T> success, Consumer<String> failure) {
		success.accept(this.getValue());
	}
}
