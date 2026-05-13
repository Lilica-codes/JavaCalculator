package calculatorGUI.state;

import java.math.BigDecimal;
import java.util.function.Function;


public class CalculatorState {
    public BigDecimal num1 = BigDecimal.ZERO;
    public BigDecimal num2 = BigDecimal.ZERO;
    public BigDecimal result = BigDecimal.ZERO;
    public BigDecimal lastNum2 = BigDecimal.ZERO;
    public BigDecimal memory = BigDecimal.ZERO;
    
    public Function<BigDecimal, BigDecimal> lastUnaryOp = null;
    
    public char operator = '\0';
    
    public CalcState state = CalcState.INPUT_NUM1;
    public AngleMode angleMode = AngleMode.RAD;
	public PanelMode currentPanelMode = PanelMode.MODE00;
	
	//計算機入力管理用のフラグ
	public enum CalcState {
	    INPUT_NUM1,     // num1 を入力中
	    AFTER_OPERATOR, // 演算子を押した直後（次の入力は num2 開始）
	    INPUT_NUM2,     // num2 を入力中
	    AFTER_EQUAL,    // = の直後（結果表示中）
	    AFTER_UNARY,	 // 関数適用後の状態
	    OVERWRITE,      // 単項演算や C/CE 後の上書き開始
	    ERROR			 // エラー。全ての入力を停止。10
	}
}
