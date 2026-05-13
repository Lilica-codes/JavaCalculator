package calculatorGUI.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Supplier;

import calculatorGUI.state.AngleMode;

public class BasicMath {
    // private static final double TAN_COS_EPS = 1e-12;


    protected final MathContext MC;
    protected final Supplier<AngleMode> angleModeSupplier;

    public BasicMath(MathContext mc, Supplier<AngleMode> angleModeSupplier) {
        this.MC = mc;
        this.angleModeSupplier = angleModeSupplier;
    }
    
    public BigDecimal exp(BigDecimal x) {
    	double d = Math.exp(x.doubleValue());
        return BigDecimal.valueOf(d);
    }

	/*
     // BigDecimal におけるe^xの定義
        BigDecimal exp(BigDecimal x) {
        BigDecimal sum = BigDecimal.ONE;
        BigDecimal term = BigDecimal.ONE;

        for (int i = 1; i < 50; i++) {
            term = term.multiply(x).divide(BigDecimal.valueOf(i), 30, RoundingMode.HALF_UP);
            sum = sum.add(term);
        }

        return sum;
    }

    
    // BigDecimal におけるlnの定義
    private BigDecimal ln(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArithmeticException("Error");
        }

        BigDecimal x = new BigDecimal(Math.log(value.doubleValue())); // 初期値
        //BigDecimal ONE = BigDecimal.ONE;

        for (int i = 0; i < 20; i++) {
            BigDecimal eToX = exp(x);
            x = x.subtract(eToX.subtract(value).divide(eToX, 30, RoundingMode.HALF_UP));
        }

        return x;
    }
    */


    
    public BigDecimal ln(BigDecimal value) {
    	double v = value.doubleValue();

        // 定義域チェック：ln(x) は x > 0 のときのみ定義
        if (v <= 0.0) {
            return null; // applyUnaryOperation 側で Error 表示
        }

        double res = Math.log(v); // 自然対数 ln(x)

        return new BigDecimal(BigDecimal.valueOf(res).toPlainString(), MC);

    }
    
    public BigDecimal log10(BigDecimal value) {
    	double v = value.doubleValue();

        if (v <= 0.0) {
            return null;
        }

        return new BigDecimal(BigDecimal.valueOf(Math.log10(v)).toPlainString(), MC);

    }
    
    public BigDecimal _10_x(BigDecimal v) {
        return BigDecimal.valueOf(Math.pow(10, v.doubleValue()));

    }

    public BigDecimal ceil(BigDecimal v) {
        return BigDecimal.valueOf(Math.ceil(v.doubleValue()));

    }
    
    public BigDecimal floor(BigDecimal v) {
        return BigDecimal.valueOf(Math.floor(v.doubleValue()));

    }
    
    public BigDecimal powBD(BigDecimal a, BigDecimal b) {
        // a^b = exp(b * ln(a))
        return exp( ln(a).multiply(b, MC) );
    }
}
