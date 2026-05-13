package calculatorGUI.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MathConstants {
	public static final BigDecimal PI = new BigDecimal("3.1415926535897932384626");
	public static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);
}
