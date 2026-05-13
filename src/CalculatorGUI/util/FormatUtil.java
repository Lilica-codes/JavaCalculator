package calculatorGUI.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FormatUtil {
    // 計算の答えが.0の時はそれを消すメソッド
    public static String formatNumber(BigDecimal num) {
    	
    	num = num.stripTrailingZeros();

        if (num.scale() <= 0)  return num.toPlainString();
        
        num = num.setScale(10, RoundingMode.HALF_UP)
                .stripTrailingZeros();

        return num.toPlainString();
    }
}
