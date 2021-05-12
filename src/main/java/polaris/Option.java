package polaris;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 04.08.2020, 13:47
 */

public abstract class Option<T>{
    private boolean isSome;

    public Option(boolean isSome) {
        this.isSome = isSome;
    }

    public static <T1> Option<T1> flatten(Option<Option<T1>> option) {
        return option.match(some -> some, Option::none);
    }

    public static <T1> Option<T1> some(T1 object) {
        return Option.from(object);
    }

    public static <T1> Option<T1> none() {
        return new None<>();
    }

    public static <T1> Option<T1> from(T1 object) {
        return object != null
            ? new Some<>(object)
            : Option.none();
    }

    public static <T> Option<T> fromOptional(Optional<T> optional) {
        return optional.map(Option::some).orElseGet(Option::none);
    }

    public void matchVoid(Consumer<T> some, Runnable none) {
        if (this.isSome()) {
            some.accept(this.toSome().getValue());
        } else {
            none.run();
        }
    }

    public void match(Consumer<T> some) {
        if (this.isSome()) {
            some.accept(this.toSome().getValue());
        }
    }

    public <T1> T1 match(Function<T, T1> some, Supplier<T1> none) {
        if (this.isSome()) {
            return some.apply(this.toSome().getValue());
        }
        return none.get();
    }

    public <T1> Option<T1> bind(Function<T, Option<T1>> binder) {
        return this.match(binder, Option::none);
    }

    public <T1> Option<T1> map(Function<T, T1> mapper) {
        return this.bind(binder -> Option.from(mapper.apply(binder)));
    }

    public Result<T> toResult(Supplier<String> onNone) {
        return this.match(Result::success, () -> Result.failure(onNone.get()));
    }

    public T getValueOrThrow() throws GetValueOrThrowException {
        if (this.isSome()) {
            return this.toSome().getValue();
        }
        throw new GetValueOrThrowException(String.format("cannot get value from '%s'", this.getClass().getTypeName()));
    }

    public T getValueOrDefault(T defaultValue) {
        return this.match(some -> some, () -> defaultValue);
    }

    public boolean isSome() {
        return isSome;
    }

    public boolean isNone() {
        return !this.isSome();
    }

    private Some<T> toSome() {
        return (Some<T>) this;
    }

    private None<T> toNone() {
        return (None<T>) this;
    }
}
