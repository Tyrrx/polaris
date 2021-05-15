import org.junit.jupiter.api.Test;
import polaris.No;

import static org.assertj.core.api.Assertions.assertThat;

public class UnitTests {
    
    @Test
    public void test_single_instance_of_unit() {
        assertThat(No.thing() == No.thing()).isTrue();
    }
}
