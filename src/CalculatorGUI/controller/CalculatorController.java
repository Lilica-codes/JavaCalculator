package calculatorGUI.controller;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;

import calculatorGUI.math.AngleMath;
import calculatorGUI.math.BasicMath;
import calculatorGUI.math.GammaMath;
import calculatorGUI.math.MathConstants;
import calculatorGUI.state.CalculatorState;
import calculatorGUI.state.CalculatorState.CalcState;
import calculatorGUI.ui.CalculatorButtons;
import calculatorGUI.ui.CalculatorGUI;
import calculatorGUI.util.FormatUtil;

public class CalculatorController {
    private final CalculatorState st;
    private final CalculatorGUI gui;
    
    private final BasicMath basicMath;
    private final AngleMath angleMath;
    private final GammaMath gammaMath;


	
	public CalculatorController(CalculatorState state, CalculatorGUI gui) {
		this.st = state;
		this.gui = gui;
	    // MathContext は State か Controller で持っている前提
	    MathContext MC = new MathContext(20);

	    // BasicMath（角度不要）
	    this.basicMath = new BasicMath(MC, () -> st.angleMode);

	    // AngleMath（State の angleMode を Supplier で渡す）
	    this.angleMath = new AngleMath(MC, () -> st.angleMode);

	    // GammaMath（必要なら MC だけ）
	    this.gammaMath = new GammaMath(MC, () -> st.angleMode);
	}

    public void handle(String command) {
    	
        
        System.out.println("cmd = [" + command + "]");
        
        
        if (command.equals("")) {
        	return;
        }
        
        // 数字の場合
        if (command.length() == 1 && "0123456789".contains(command)) {
        	digit_entry(command);
        	return;
        }
        
        // 演算子の場合
        if ("+-×÷".contains(command)) {
        	operator_entry(command);
        	return;
        }

        //単項計算, 単体ボタン一式まとめ
        
        else {
        	
        	switch(command) {
				case "AC": //オールクリアボタン
					allClear();
					break;
				case "CE": //クリアエントリーボタン
					clearEntry();
		            break;

        		case CalculatorButtons.LBL_BS:
        			backspace();
        			break;
        		case "=": //イコール処理
	        		equal();
	        		break;
        		case ".": //小数点
	        		puttingDot(); 
	        		break;
 
        		case "√": 
        			applyUnaryOperation(v -> sqrt(v));
        			break;

        		case "1/x": 
        			applyUnaryOperation(v -> BigDecimal.ONE.divide(v, 20, RoundingMode.HALF_UP));
    				break;
        		case "±": 
        			applyUnaryOperation(v -> v.negate());
    				break;
        		case "ln": 
        			applyUnaryOperation(v -> basicMath.ln(v));
    				break;
        		case CalculatorButtons.LBL_LOG10: 
        			applyUnaryOperation(v -> basicMath.log10(v));
    				break;
        		case "mod":
        			operator_entry("%");
        			break;

        		case "sin":
        			applyUnaryOperation(v -> angleMath.sin(v));
        			break;
        		case "cos":
        			applyUnaryOperation(v -> angleMath.cos(v));
        			break;
        		case "tan":
        			applyUnaryOperation(v -> angleMath.tan(v));
        			break;
        		case "asin":
        			applyUnaryOperation(v -> angleMath.arcsin(v));
        			break;
        		case "acos":
        			applyUnaryOperation(v -> angleMath.arccos(v));
        			break;
        		case "atan":
        			applyUnaryOperation(v -> angleMath.arctan(v));
        			break;

        		case CalculatorButtons.LBL_X2:
        			applyUnaryOperation(v -> v.multiply(v));
        			break;
        		case CalculatorButtons.LBL_X3:
        			applyUnaryOperation(v -> v.multiply(v).multiply(v));
        			break;
        		case CalculatorButtons.LBL_PI:
        			applyUnaryOperation(v -> MathConstants.PI);
        			break;
        		
        		case CalculatorButtons.LBL_XY:
        			operator_entry("^");
        			break;
        		case CalculatorButtons.LBL_POW_10:
        			applyUnaryOperation(v -> basicMath._10_x(v));
        			break;
        		case CalculatorButtons.LBL_E_X:
        			applyUnaryOperation(v -> basicMath.exp(v));
        			break;
        		case CalculatorButtons.LBL_YRT_X:
        			operator_entry("√");
        			break;
        		case CalculatorButtons.LBL_CEIL:
        			applyUnaryOperation(v -> basicMath.ceil(v));
        			break;
        		case CalculatorButtons.LBL_FLOOR:
        			applyUnaryOperation(v -> basicMath.floor(v));
        			break;
        		case "EXP":
        			finalizeExpIfNeeded();
        			puttingExp();
        		    break;
        		case "%":
        		    inputPercent();
        		    break;
        		case "|x|":
        			applyUnaryOperation(v -> v.abs());
        		    break;
        		case "x!":
        			applyUnaryOperation(v -> gammaMath.factorial(v));
        		    break;
        		case "RAD":
        		case "DEG":
        		case "GRAD":
        			switchAngleMode();
        		    break;
        		
        		case "MC":
        		case "MR":
        		case "M+":
        		case "M-":
        			memoryMode(command);
        			break;
        			
        		case CalculatorButtons.LBL_SECOND:
        			switchPanelMode();
        			break;
        			
        		default :
        			break;
        	}
        }
    }
    
    // 計算処理メソッド化
    private BigDecimal calculate(BigDecimal a, BigDecimal b, char op) {
        switch (op) {
            case '+': return a.add(b);
            case '-': return a.subtract(b);
            case '*': return a.multiply(b);
            case '/':
                // 小数点以下 20 桁、四捨五入
                return a.divide(b, 20, RoundingMode.HALF_UP);
            case '^':
            	return basicMath.powBD(a, b);
            case 'r':
            	BigDecimal inv = BigDecimal.ONE.divide(b, MathConstants.MC); // 1/y
                BigDecimal ln_r = basicMath.ln(a);               // ln(x)
                BigDecimal mul_r = inv.multiply(ln_r, MathConstants.MC);         // (1/y) * ln(x)
                return basicMath.exp(mul_r);                     // exp(...)
            case '%':
                BigDecimal rmd = a.remainder(b, MathConstants.MC);
	                if (rmd.signum() < 0) {
	                	rmd = rmd.add(b);
	                }
                return rmd;
            default:
                throw new IllegalArgumentException("Invalid operator: " + op);
        }
    }

 // 単項計算処理メソッド化
    private void applyUnaryOperation(Function<BigDecimal, BigDecimal> op) {

        if (gui.getDisplayText().isEmpty()) return;

        try {
            BigDecimal value = new BigDecimal(gui.getDisplayText());
            BigDecimal result = op.apply(value);

            gui.updateDisplay(FormatUtil.formatNumber(result));

            if (st.state == CalcState.INPUT_NUM2 || 
            	st.state == CalcState.AFTER_OPERATOR || 
            	st.state == CalcState.AFTER_UNARY) {
            	
                st.num2 = result;
                st.lastNum2 = result;
                //setState(CalcState.AFTER_UNARY);
            } else {
                st.num1 = result;
                st.lastNum2 = result;
                //setState(CalcState.AFTER_UNARY);
            }
            
            // ★ ここが重要：関数適用モードにする
            st.operator = 'F';   // Function の意味（任意の文字でOK）
            st.lastUnaryOp = op;   // ★ どの関数を使ったか保存
            setState(CalcState.AFTER_UNARY);
            
            
        } catch (Exception ex) {
        	gui.updateDisplay("Error");
            setState(CalcState.ERROR);
        }
    }

 // 数字入力
    private void digit_entry(String command) {
    	switch(st.state) {
	    	case ERROR:
	    		return;
	    	case OVERWRITE: // ★ overwrite のときは必ず上書き
	    		gui.updateDisplay(command);
	    		setState(st.operator == '\0' ? CalcState.INPUT_NUM1 : CalcState.INPUT_NUM2);
	    		return;
			case AFTER_EQUAL:
				gui.updateDisplay(command);
	            setState(CalcState.INPUT_NUM1);
				return;
			case AFTER_OPERATOR:
				gui.updateDisplay(command);
	            setState(CalcState.INPUT_NUM2);
				return;
			case INPUT_NUM1:
			case INPUT_NUM2:
				String s = gui.getDisplayText();
		        // ★ "0" のときだけ上書き（"0." は除外）
		        if (s.equals("0")) {
		        	gui.updateDisplay(command);
		        } else {
		        	gui.updateDisplay(s + command);
		        }
		        return;
			case AFTER_UNARY:
				gui.updateDisplay(command);
			    setState(CalcState.INPUT_NUM2);
			    return;
			default:
				break;
    	}
    }
    
 // 演算子入力
    private void operator_entry(String command) {
    	
    	if (command.equals("×")) 		command = "*";
    	else if (command.equals("÷"))  command = "/";
    	else if (command.equals("√"))  command = "r";
    	else if (command.equals("mod"))  command = "%";

        // ★ 演算子連続押し（num2 未入力）
        if (st.state == CalcState.AFTER_OPERATOR) {
            st.operator = command.charAt(0);  // 置き換え
            return;
        }

        // ★ num2 が入力されている → 計算する
        if (st.state == CalcState.INPUT_NUM2) {
            // num2 が入力済みなら計算
        	st.num2 = new BigDecimal(gui.getDisplayText());
        	st.num1 = calculate(st.num1, st.num2, st.operator);
        	gui.updateDisplay(FormatUtil.formatNumber(st.num1));
        } else if (st.state == CalcState.INPUT_NUM1 || st.state == CalcState.AFTER_EQUAL 
        		|| st.state == CalcState.OVERWRITE) {
            // num1 を確定
            st.num1 = new BigDecimal(gui.getDisplayText());
        }


        // ★ 新しい演算子をセット
        st.operator = command.charAt(0);

        // ★ num2 入力開始
        setState(CalcState.AFTER_OPERATOR);
    }
    
 // BigDecimal におけるsqrtの定義
    private BigDecimal sqrt(BigDecimal value) {

        BigDecimal x = new BigDecimal(Math.sqrt(value.doubleValue())); // 初期値
        BigDecimal TWO = new BigDecimal("2");

        for (int i = 0; i < 20; i++) {
            x = x.add(value.divide(x, 20, RoundingMode.HALF_UP))
                 .divide(TWO, 20, RoundingMode.HALF_UP);
        }

        return x;
    }
    
  // パーセント挙動設定
    private void inputPercent() {
        if (st.state == CalcState.ERROR) return;

        try {
            BigDecimal x = new BigDecimal(gui.getDisplayText());

            // 演算子がない → 単独の数値として扱う（÷100）
            if (st.operator == '\0' || st.num1 == null) {
                BigDecimal result = x.divide(new BigDecimal("100"), MathConstants.MC);
                gui.updateDisplay(FormatUtil.formatNumber(result));
                st.state = CalcState.OVERWRITE;
                return;
            }

            BigDecimal base = st.num1;

            BigDecimal result;

            // + と - は「num1 に対する割合」
            if (st.operator == '+' || st.operator == '-') {
                result = base.multiply(x.divide(new BigDecimal("100"), MathConstants.MC), MathConstants.MC);
            }
            // × と ÷ は単純に ÷100
            else {
                result = x.divide(new BigDecimal("100"), MathConstants.MC);
            }

            gui.updateDisplay(FormatUtil.formatNumber(result));
            st.num2 = result;
            st.lastNum2 = result;
            st.state = CalcState.OVERWRITE;

        } catch (Exception ex) {
            gui.updateDisplay("Error");
            st.state = CalcState.ERROR;
        }
    }
    
    //バックスペース
	private void backspace() {
	    if (st.state == CalcState.ERROR || st.state == CalcState.AFTER_EQUAL) return;

	    String s = gui.getDisplayText();

	    // 1文字しかない or "0" の場合は 0 に戻す
	    if (s.length() <= 1 || s.equals("0")) {
	        gui.updateDisplay("0");
	        st.state = CalcState.OVERWRITE;
	        return;
	    }

	    // 最後の1文字を削除
	    s = s.substring(0, s.length() - 1);

	    // 末尾が "-" だけになるケース（- を消したら 0）
	    if (s.equals("-")) {
	        gui.updateDisplay("0");
	        st.state = CalcState.OVERWRITE;
	        return;
	    }

	    gui.updateDisplay(s);
	}
	
	//イコールボタン
	private void equal() {

		// ★ 単項関数の再適用
		if (st.operator == 'F') {
		    BigDecimal value = new BigDecimal(gui.getDisplayText());
		    BigDecimal result = st.lastUnaryOp.apply(value);

		    gui.updateDisplay(FormatUtil.formatNumber(result));

		    st.num1 = result;
		    st.lastNum2 = result;

		    setState(CalcState.AFTER_EQUAL);
		    return;
		}

	    try {

	        if (st.state == CalcState.INPUT_NUM2) {
	        	st.num2 = new BigDecimal(gui.getDisplayText());
	        	st.lastNum2 = st.num2;

	        } else if (st.state == CalcState.AFTER_UNARY) {

	            if (st.operator != '\0') {
	            	st.num2 = new BigDecimal(gui.getDisplayText());
	            	st.lastNum2 = st.num2;
	            } else {
	            	st.num1 = new BigDecimal(gui.getDisplayText());
	            	st.lastNum2 = st.num1;
	                gui.updateDisplay(FormatUtil.formatNumber(st.num1));
	                setState(CalcState.AFTER_EQUAL);
	                return;
	            }

	        } else if (st.state == CalcState.AFTER_EQUAL) {
	        	st.num2 = st.lastNum2;

	        } else {
	            return;
	        }

	        st.result = calculate(st.num1, st.num2, st.operator);

	        gui.updateDisplay(FormatUtil.formatNumber(st.result));

	        st.num1 = st.result;
	        setState(CalcState.AFTER_EQUAL);

	    } catch (Exception ex) {
	        gui.updateDisplay("Error");
	        setState(CalcState.ERROR);
	    }
	}
	
	//小数点
	private void puttingDot() {

	    // ★ 最優先：演算子直後の小数点は「0.」
	    if (st.state == CalcState.AFTER_OPERATOR || st.state == CalcState.OVERWRITE) {
	        gui.updateDisplay("0.");
	        setState(st.operator == '\0' ? CalcState.INPUT_NUM1 : CalcState.INPUT_NUM2);
	        return;
	    }

	    String s = gui.getDisplayText();
	    int idx = s.indexOf("E");

	    // ★ E の後に小数点は入れない
	    if (idx != -1) {
	        String afterE = s.substring(idx + 1);
	        if (afterE.contains(".")) return;
	        return; // 指数部は整数
	    }

	    // ★ E が無い場合：全体に小数点は1つだけ
	    if (s.contains(".")) return;

	    // ---- 通常の小数点処理 ----

	    switch (st.state) {
	        case ERROR:
	            return;

	        case AFTER_EQUAL:
	            setState(CalcState.INPUT_NUM1);
	            gui.updateDisplay("0.");
	            return;

	        default:
	            if (s.isEmpty() || s.equals("0")) {
	                gui.updateDisplay("0.");
	            } else {
	                gui.updateDisplay(s + ".");
	            }
	            return;
	    }
	}
	
	//メモリー機能
	private void memoryMode(String command) {
		switch(command) {
			case "MC":
			    st.memory = BigDecimal.ZERO;
			    break;
	
			case "MR":
				gui.updateDisplay(FormatUtil.formatNumber(st.memory));
				st.state = CalcState.OVERWRITE;
			    break;
	
			case "M+":
				st.memory = st.memory.add(new BigDecimal(gui.getDisplayText()), MathConstants.MC);
			    break;
	
			case "M-":
				st.memory = st.memory.subtract(new BigDecimal(gui.getDisplayText()), MathConstants.MC);
			    break;
		}
		
		System.out.print("MEMORY = ");
		System.out.println(st.memory);
	}
	
	private void allClear() {
		gui.updateDisplay("0");
        st.num1 = st.num2 = st.result = st.lastNum2 = BigDecimal.ZERO;
        st.operator = '\0';
        setState(CalcState.INPUT_NUM1);
	}

	private void clearEntry() {
        
        switch (st.state) {

	        case INPUT_NUM2:
	        case AFTER_UNARY:
	            // ★ num2 の入力値だけ消す
	        	gui.updateDisplay("0");
	            setState(CalcState.INPUT_NUM2);
	            return;
	
	        case AFTER_OPERATOR:
	            // ★ 演算子直後の CE は「0」に戻すだけ（num1/operator は保持）
	        	gui.updateDisplay("0");
	            setState(CalcState.INPUT_NUM2);
	            return;
	
	        case INPUT_NUM1:
	        case AFTER_EQUAL:
	            // ★ num1 の入力値だけ消す
	        	gui.updateDisplay("0");
	            setState(CalcState.INPUT_NUM1);
	            return;
	
	        case ERROR:
	            // ★ エラー状態は CE で復帰
	        	gui.updateDisplay("0");
	            setState(CalcState.INPUT_NUM1);
	            return;
	         
	        default:
	        	gui.updateDisplay("0");
	            setState(CalcState.INPUT_NUM1);
	            return;
        }
	}
	
	private void puttingExp() {
	    String s = gui.getDisplayText();

	    // すでに E がある場合は無視（E を2回押させない）
	    if (s.contains("E")) return;

	    // OVERWRITE のときは新規入力扱い
	    if (st.state == CalcState.OVERWRITE || st.state == CalcState.AFTER_EQUAL) {
	        gui.updateDisplay("1E");
	    } else {
	        gui.updateDisplay(s + "E");
	    }

	    // state は変えない（INPUT_NUM1 or INPUT_NUM2 を維持）
	    return;
	}
	
	private void finalizeExpIfNeeded(){
	    String s = gui.getDisplayText();

	    int idx = s.indexOf("E");
	    if (idx == -1) return; // E がない

	    // E の後に数字が無い場合
	    if (idx == s.length() - 1) {
	        gui.updateDisplay(s + "0"); // E0 に補完
	    }
	    // E- の後も補完
	    else if (s.endsWith("E-") || s.endsWith("E+")) {
	        gui.updateDisplay(s + "0");
	    }
	}

    
    //計算機の状態セット
    private void setState(CalcState s) {
        st.state = s;
        // デバッグ用ログ
        System.out.println("STATE -> " + s);
    }
    
    //計算機の状態セット
    private void switchAngleMode() {
        st.angleMode = st.angleMode.next();   // 状態を更新
        gui.updateAngleMode(st.angleMode);    // GUI に反映させる
    }
    
    private void switchPanelMode() {
        st.currentPanelMode = st.currentPanelMode.next();   // ← ここで MODE00 → MODE01 → MODE00 と循環
        gui.updatePanelMode(st.currentPanelMode);    // ← GUI に反映させる
        System.out.println("STATE -> " + st.currentPanelMode);
    }
    

}
