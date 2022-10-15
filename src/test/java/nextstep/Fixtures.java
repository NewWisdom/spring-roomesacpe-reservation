package nextstep;

import java.math.BigDecimal;
import nextstep.schedule.domain.ThemePrice;

public class Fixtures {

    public static ThemePrice themePrice(long value) {
        return new ThemePrice(BigDecimal.valueOf(value));
    }
}
