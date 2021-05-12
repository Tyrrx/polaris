package polaris;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 04.08.2020, 13:47
 */
public final class Some<T> extends Option<T> {

    private T value;

    public Some(T value) {
        super(true);
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
