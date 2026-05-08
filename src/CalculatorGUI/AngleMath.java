package CalculatorGUI;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Supplier;


public class AngleMath {
    private static final double TAN_COS_EPS = 1e-12;


    private final MathContext MC;
    private final Supplier<AngleMode> angleModeSupplier;

    public AngleMath(MathContext mc, Supplier<AngleMode> angleModeSupplier) {
        this.MC = mc;
        this.angleModeSupplier = angleModeSupplier;
    }
    

    BigDecimal exp(BigDecimal x) {
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


    
    BigDecimal ln(BigDecimal value) {
    	double v = value.doubleValue();

        // 定義域チェック：ln(x) は x > 0 のときのみ定義
        if (v <= 0.0) {
            return null; // applyUnaryOperation 側で Error 表示
        }

        double res = Math.log(v); // 自然対数 ln(x)

        return new BigDecimal(BigDecimal.valueOf(res).toPlainString(), MC);

    }
    
    BigDecimal log10(BigDecimal value) {
    	double v = value.doubleValue();

        if (v <= 0.0) {
            return null;
        }

        return new BigDecimal(BigDecimal.valueOf(Math.log10(v)).toPlainString(), MC);

    }
    
    BigDecimal _10_x(BigDecimal v) {
    	double d = Math.pow(10, v.doubleValue());
        return BigDecimal.valueOf(d);

    }

    /* 三角関数用定義コーナー */
    
    //ラジアンの定義
    /*
    private BigDecimal toRadians(BigDecimal degrees) {
        return degrees.multiply(PI).divide(new BigDecimal("180"), 20, RoundingMode.HALF_UP);
    }
    */
    
    private double toRadiansIfNeeded(BigDecimal v) {
        switch (angleModeSupplier.get()) {
        case DEG:
            return v.multiply(CalculatorGUI.PI, MC)
            		.divide(new BigDecimal("180"), MC)
            		.doubleValue();
        case GRAD:
            return v.multiply(CalculatorGUI.PI, MC)
            		.divide(new BigDecimal("200"), MC)
            		.doubleValue();
        default:
            return v.doubleValue();
        }
    /*
        if (angleModeSupplier.get() == AngleMode.DEG) {
            return Math.toRadians(v.doubleValue());
        }
        return v.doubleValue();
     */
    }


    private BigDecimal fromRadiansIfNeeded(double radValue) {
    	BigDecimal v = BigDecimal.valueOf(radValue);
    	
    	switch (angleModeSupplier.get()) {
    	case DEG:
            return v.multiply(new BigDecimal("180"))
            		.divide(CalculatorGUI.PI, MC);
            
        case GRAD:
            return v.multiply(new BigDecimal("200"))
            		.divide(CalculatorGUI.PI, MC);
        default:
            return v;
    	}
    /*
        if (angleModeSupplier.get() == AngleMode.DEG) {
            double deg = Math.toDegrees(radValue);
            return BigDecimal.valueOf(deg);
        } else {
            return BigDecimal.valueOf(radValue);
        }
     */
    }

    
    /*ラジアンの正規化
    private static BigDecimal normalize(BigDecimal x) {
        BigDecimal twoPi = PI.multiply(BigDecimal.valueOf(2));
        BigDecimal result = x.remainder(twoPi);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            result = result.add(twoPi);
        }
        return result;
    }
    */

    
    //テイラー展開を利用したsin(x), cos(x)の定義
    BigDecimal sin(BigDecimal x) {
        //if (displayField.getText().isEmpty()) return BigDecimal.ONE;
        double rad = toRadiansIfNeeded(x);
        double res = Math.sin(rad);
        return new BigDecimal(res, MC); // BigDecimal に戻す
    }
    
    BigDecimal cos(BigDecimal x) {
        //if (displayField.getText().isEmpty()) return BigDecimal.ONE;
        double rad = toRadiansIfNeeded(x);
        double res = Math.cos(rad);
        return new BigDecimal(res, MC); // BigDecimal に戻す

    }
    
    //tan(x)は sin(x) / cos(x)でOK
    BigDecimal tan(BigDecimal x) {
        double xrad = toRadiansIfNeeded(x); // DEG → RAD 変換
        double xcos = Math.cos(xrad);

        if (Double.isNaN(xcos) || Math.abs(xcos) < TAN_COS_EPS) {
        	//throw new ArithmeticException("tan undefined");
            return null; // applyUnaryOperation 側で "Error" 表示にする
        }

        return new BigDecimal(BigDecimal.valueOf(Math.tan(xrad)).toPlainString(), MC);
    }
    
    //逆三角関数
    BigDecimal arcsin(BigDecimal x) {
    	double v = x.doubleValue();
    	
    	if (v < -1.0 || v > 1.0) {
            return null; // applyUnaryOperation 側で Error 表示
        }
    	
        double rad = Math.asin(v);
        return fromRadiansIfNeeded(rad); // DEG モードなら度に変換

    }
    
    BigDecimal arccos(BigDecimal x) {
    	double v = x.doubleValue();
    	
    	if (v < -1.0 || v > 1.0) {
            return null; // applyUnaryOperation 側で Error 表示
        }
        double rad = Math.acos(x.doubleValue());
        return fromRadiansIfNeeded(rad); // DEG モードなら度に変換

    }
    
    BigDecimal arctan(BigDecimal x) {
    	double v = x.doubleValue();
    	double rad = Math.atan(v);
    	return fromRadiansIfNeeded(rad);
    }
    
    /*三角関数ここまで*/
}
