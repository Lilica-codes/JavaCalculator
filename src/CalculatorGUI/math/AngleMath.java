package calculatorGUI.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Supplier;

import calculatorGUI.state.AngleMode;


public class AngleMath extends BasicMath {
    private static final double TAN_COS_EPS = 1e-12;


    //protected final MathContext MC;
    //protected final Supplier<AngleMode> angleModeSupplier;

    public AngleMath(MathContext mc, Supplier<AngleMode> angleModeSupplier) {
    	super(mc, angleModeSupplier);
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
            return v.multiply(MathConstants.PI, MC)
            		.divide(new BigDecimal("180"), MC)
            		.doubleValue();
        case GRAD:
            return v.multiply(MathConstants.PI, MC)
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
            		.divide(MathConstants.PI, MC);
            
        case GRAD:
            return v.multiply(new BigDecimal("200"))
            		.divide(MathConstants.PI, MC);
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
    public BigDecimal sin(BigDecimal x) {
        //if (displayField.getText().isEmpty()) return BigDecimal.ONE;
        double rad = toRadiansIfNeeded(x);
        double res = Math.sin(rad);
        return new BigDecimal(res, MC); // BigDecimal に戻す
    }
    
    public BigDecimal cos(BigDecimal x) {
        //if (displayField.getText().isEmpty()) return BigDecimal.ONE;
        double rad = toRadiansIfNeeded(x);
        double res = Math.cos(rad);
        return new BigDecimal(res, MC); // BigDecimal に戻す

    }
    
    //tan(x)は sin(x) / cos(x)でOK
    public BigDecimal tan(BigDecimal x) {
        double xrad = toRadiansIfNeeded(x); // DEG → RAD 変換
        double xcos = Math.cos(xrad);

        if (Double.isNaN(xcos) || Math.abs(xcos) < TAN_COS_EPS) {
        	//throw new ArithmeticException("tan undefined");
            return null; // applyUnaryOperation 側で "Error" 表示にする
        }

        return new BigDecimal(BigDecimal.valueOf(Math.tan(xrad)).toPlainString(), MC);
    }
    
    //逆三角関数
    public BigDecimal arcsin(BigDecimal x) {
    	double v = x.doubleValue();
    	
    	if (v < -1.0 || v > 1.0) {
            return null; // applyUnaryOperation 側で Error 表示
        }
    	
        double rad = Math.asin(v);
        return fromRadiansIfNeeded(rad); // DEG モードなら度に変換

    }
    
    public BigDecimal arccos(BigDecimal x) {
    	double v = x.doubleValue();
    	
    	if (v < -1.0 || v > 1.0) {
            return null; // applyUnaryOperation 側で Error 表示
        }
        double rad = Math.acos(x.doubleValue());
        return fromRadiansIfNeeded(rad); // DEG モードなら度に変換

    }
    
    public BigDecimal arctan(BigDecimal x) {
    	double v = x.doubleValue();
    	double rad = Math.atan(v);
    	return fromRadiansIfNeeded(rad);
    }
    
    /*三角関数ここまで*/
    
    protected BigDecimal sinRadFixed(BigDecimal rad) {
        // 角度モードを無視してラジアンで計算
    	double res = Math.sin(rad.doubleValue());
        return new BigDecimal(res, MC);
    }
    
    
}
