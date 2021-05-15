import org.junit.jupiter.api.Test;
import polaris.Failure;
import polaris.Result;
import polaris.Success;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class ResultTests {

    @Test
    public void test_result_success_creation() {
        Result<Integer> res = Result.success(42);
        assertThat(res)
            .isInstanceOf(Success.class);
    }

    @Test
    public void test_result_failure_creation() {
        Result<Integer> res = Result.failure("e");
        assertThat(res)
            .isInstanceOf(Failure.class);
    }

    @Test
    public void test_result_ofOptional_success_creation() {
        Optional<Integer> optional = Optional.of(42);
        Result<Integer> res = Result.ofOptional(optional, "e");
        assertThat(res)
            .isInstanceOf(Success.class);
    }

    @Test
    public void test_result_ofOptional_failure_creation() {
        Optional<Integer> optional = Optional.empty();
        Result<Integer> res = Result.ofOptional(optional, "e");
        assertThat(res)
            .isInstanceOf(Failure.class);
    }

    @Test
    public void test_result_match_success() {
        Result<Integer> res = Result.success(42);
        assertThat(res)
            .isInstanceOf(Success.class)
            .satisfies(r -> r.match(s -> true, e -> false));
    }

    @Test
    public void test_result_match_failure() {
        Result<Integer> res = Result.failure("e");
        assertThat(res)
            .isInstanceOf(Failure.class)
            .satisfies(r ->
                r.match(
                    s -> true,
                    e -> false));
    }
    @Test
    public void test_result_matchVoid_success() {
        Result<Integer> res = Result.success(42);
        res.matchVoid(s -> {
            assertThat(s).isInstanceOf(Integer.class);
        }, e -> {
            assertThat(e).isEmpty();
        });
    }

    @Test
    public void test_result_matchVoid_failure() {
        Result<Integer> res = Result.failure("e");
        res.matchVoid(s -> {
            assertThat(true).isFalse();
        }, e -> {
            assertThat(e).isEqualTo("e");
        });
    }

    @Test
    public void test_result_bind_success_on_success() {
        Result<Integer> res = Result.success(42);
        assertThat(res.bind(num -> Result.success(num*num)))
            .isInstanceOf(Success.class);

    }

    @Test
    public void test_result_bind_success_on_failure() {
        Result<Integer> res = Result.failure("e");
        res = res.bind(num -> {
            assertThat(true).isFalse();
            return Result.success(42);
        });
        assertThat(res).isInstanceOf(Failure.class);
    }

    @Test
    public void test_result_bind_failure_on_success() {
        Result<Integer> res = Result.success(42);
        assertThat(res.bind(num -> Result.failure("e")))
            .isInstanceOf(Failure.class);

    }

    @Test
    public void test_result_bind_failure_on_failure() {
        Result<Integer> res = Result.failure("e");
        res = res.bind(num -> {
            assertThat(true).isFalse();
            return Result.failure("e1");
        });
        assertThat(res).isInstanceOf(Failure.class);
    }

    @Test
    public void test_result_map_on_success() {
        Result<Integer> res = Result.success(42);
        res = res.map(num -> {
            assertThat(true).isTrue();
            return num;
        });
        assertThat(res).isInstanceOf(Success.class);
    }

    @Test
    public void test_result_map_on_failure() {
        Result<Integer> res = Result.failure("e");
        res = res.map(num -> {
            assertThat(true).isFalse();
            return num;
        });
        assertThat(res).isInstanceOf(Failure.class);
    }

    @Test
    public void test_success_toOptional() {
        Result<Integer> res = Result.success(42);
        assertThat(res.toOptional()).isPresent().satisfies(integer -> integer.get().equals(42));
    }

    @Test
    public void test_failure_toOptional() {
        Result<Integer> res = Result.failure("e");
        assertThat(res.toOptional()).isEmpty();
    }

    @Test
    public void test_getValueOrDefault_value() {
        Result<Integer> res = Result.success(42);
        assertThat(res.getValueOrDefault(0))
            .satisfies(e -> e.equals(42));
    }

    @Test
    public void test_getValueOrDefault_default() {
        Result<Integer> res = Result.failure("e");
        assertThat(res.getValueOrDefault(42))
            .satisfies(e -> e.equals(42));
    }

    @Test
    public void test_getErrorOrDefault_value() {
        Result<Integer> res = Result.failure("e");
        assertThat(res.getErrorOrDefault()).isEqualTo("e");
    }

    @Test
    public void test_getErrorOrDefault_default() {
        Result<Integer> res = Result.success(42);
        assertThat(res.getErrorOrDefault()).isEqualTo("");
    }

}
