package polaris;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David Retzlaff
 * GitHub: https://github.com/Tyrrx}
 * Date: 04.08.2020, 13:47
 */

public abstract class Result<T> {

    private static final String defaultErrorSeparator = ", ";

    private final boolean isSuccess;

    public Result(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public static <TResult> Result<TResult> success(TResult value) {
        return new Success<>(value);
    }

    public static <TResult> Result<TResult> failure(String message) {
        return new Failure<>(message);
    }

    public static <TResult> Result<TResult> ofOptional(Optional<TResult> optional, String onNotPresentMessage) {
        return optional.isPresent()
            ? success(optional.get())
            : failure(onNotPresentMessage);
    }

    /**
     * Aggregates results from a stream to one result with a list of all values.
     * Enumerates all elements in stream
     *
     * @param stream         result stream
     * @param errorSeparator separator between error messages
     * @param <T>            is the type of the aggregation
     * @return 
     */

    public static <T> Result<List<T>> aggregate(Stream<Result<T>> stream, String errorSeparator) {
        StringBuffer stringBuffer = new StringBuffer();
        List<T> results = Result.choose(
            stream,
            result -> {
                stringBuffer
                    .append(errorSeparator)
                    .append(result.toFailure().getMessage());
            })
            .map(result -> result.getValueOrDefault(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (stringBuffer.length() > 0) {
            return Result.failure(stringBuffer.toString().replaceFirst(errorSeparator, ""));
        }
        return Result.success(results);
    }

    public static <T> Result<List<T>> aggregate(Stream<Result<T>> results) {
        return Result.aggregate(results, Result.defaultErrorSeparator);
    }

    public static <T> Result<List<T>> aggregate(Collection<Result<T>> results) {
        return Result.aggregate(results.stream(), Result.defaultErrorSeparator);
    }

    public static <T> Result<List<T>> aggregate(Collection<Result<T>> results, String errorSeparator) {
        return Result.aggregate(results.stream(), errorSeparator);
    }

    public static <T> Stream<Result<T>> choose(Stream<Result<T>> stream, Consumer<Result<T>> errorHandler) {
        return stream
            .filter(result ->
                result.match(
                    success -> true,
                    failure -> {
                        errorHandler.accept(result);
                        return false;
                    }));
    }

    public static <T> Stream<Result<T>> chooseMessages(Stream<Result<T>> stream, Consumer<String> errorHandler) {
        return choose(stream, errorResult -> errorHandler.accept(errorResult.toFailure().getMessage()));
    }

    public static <T> CompletableFuture<Void> matchVoidAsync(CompletableFuture<Result<T>> future, Consumer<T> success, Consumer<String> failure) {
        return future.thenAccept(result -> result.matchVoid(success, failure));
    }

    public static <T, T1> CompletableFuture<T1> matchAsync(CompletableFuture<Result<T>> future, Function<T, T1> success, Function<String, T1> failure) {
        return future.thenApply(result -> result.match(success, failure));
    }

    public static <T, T1> CompletableFuture<Result<T1>> bindAsync(CompletableFuture<Result<T>> future, Function<T, Result<T1>> binder) {
        return future.thenApply(result -> result.bind(binder));
    }

    public static <T, T1> CompletableFuture<Result<T1>> mapAsync(CompletableFuture<Result<T>> future, Function<T, T1> mapper) {
        return future.thenApply(result -> result.map(mapper));
    }

    public static <T> CompletableFuture<Result<List<T>>> aggregateAsync(Stream<CompletableFuture<Result<T>>> completableFutureStream, String errorSeparator) {
        return CompletableFuture.supplyAsync(() -> Result.aggregate(completableFutureStream.map(CompletableFuture::join), errorSeparator));
    }

    public static <T> CompletableFuture<Result<List<T>>> aggregateAsync(Stream<CompletableFuture<Result<T>>> futureStream) {
        return Result.aggregateAsync(futureStream, Result.defaultErrorSeparator);
    }

    public static <T> CompletableFuture<Result<List<T>>> aggregateAsync(Collection<CompletableFuture<Result<T>>> completableFutures, String errorSeparator) {
        return Result.aggregateAsync(completableFutures.stream(), errorSeparator);
    }

    public static <T> CompletableFuture<Result<List<T>>> aggregateAsync(Collection<CompletableFuture<Result<T>>> completableFutures) {
        return Result.aggregateAsync(completableFutures, Result.defaultErrorSeparator);
    }

    public static <T> CompletableFuture<Stream<Result<T>>> chooseAsync(Stream<CompletableFuture<Result<T>>> completableFutureStream, Consumer<Result<T>> errorHandler) {
        return CompletableFuture.supplyAsync(() -> Result.choose(completableFutureStream.map(CompletableFuture::join), errorHandler));
    }

    /**
     * Matches a result's state (success, failure) without a return value.
     *
     * @param success Consumer<T> executed on success
     * @param failure Consumer<T> executed on failure
     */
    public void matchVoid(Consumer<T> success, Consumer<String> failure) {
        if (this.isSuccess()) {
            success.accept(this.toSuccess().getValue());
        } else {
            failure.accept(this.toFailure().getMessage());
        }
    }

    /**
     * Matches a result's state (success, failure) and returns a value of type T1.
     *
     * @param success Function<T, T1> executed on success
     * @param failure Function<T, T1> executed on failure
     * @param <T1>    return value type
     * @return T1
     */
    public <T1> T1 match(Function<T, T1> success, Function<String, T1> failure) {
        if (this.isSuccess()) {
            return success.apply(this.toSuccess().getValue());
        }
        return failure.apply(this.toFailure().getMessage());
    }

    /**
     * Binds a result with a possible failure to an existing result if the existing result was successful.
     *
     * @param binder is a method that returns the result to bind
     * @param <T1>   is the type of the bind transformation
     * @return a result that contains the value and state of the binding
     */
    public <T1> Result<T1> bind(Function<T, Result<T1>> binder) {
        if (this.isSuccess()) {
            return binder.apply(toSuccess().getValue());
        }
        return this.toFailure().convert();
    }

    /**
     * Maps an existing value to a new Polaris.Result<T1> with value type T1.
     *
     * @param mapper Function<T, T1> transforms value on success
     * @param <T1>   New value type
     * @return Polaris.Result<T1>
     */
    public <T1> Result<T1> map(Function<T, T1> mapper) {
        return this.bind((value) -> Result.success(mapper.apply(value)));
    }

    public Optional<T> toOptional() {
        return this.match(Optional::of, failure -> Optional.empty());
    }

    public T getValueOrDefault(T defaultValue) {
        if (this.isSuccess()) {
            return this.toSuccess().getValue();
        }
        return defaultValue;
    }

    public T getValueOrThrow() throws GetValueOrThrowException {
        if (this.isSuccess()) {
            return this.toSuccess().getValue();
        }
        throw new GetValueOrThrowException(this.toFailure().getMessage());
    }

    public String getErrorOrDefault() {
        if (this.isFailure()) {
            return this.toFailure().getMessage();
        }
        return "";
    }

    public String getErrorOrThrow() throws GetErrorOrThrowException {
        if (this.isFailure()) {
            return this.toFailure().getMessage();
        }
        throw new GetErrorOrThrowException("Tried to get error message from a success.");
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }

    public boolean isFailure() {
        return !this.isSuccess;
    }

    private Success<T> toSuccess() {
        return (Success<T>) this;
    }

    private Failure<T> toFailure() {
        return (Failure<T>) this;
    }
}
