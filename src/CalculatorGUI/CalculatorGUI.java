package calculatorGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


public class CalculatorGUI extends JFrame implements ActionListener {
	
	//Initiation
    private JTextField displayField;
    private BigDecimal num1 = BigDecimal.ZERO;
    private BigDecimal num2 = BigDecimal.ZERO;
    private BigDecimal result = BigDecimal.ZERO;
    private BigDecimal lastNum2 = BigDecimal.ZERO;
    private BigDecimal memory = BigDecimal.ZERO;
    private Function<BigDecimal, BigDecimal> lastUnaryOp = null;
    private char operator;
    private static final int CalculatorCols = 5; //電卓ボタン, 1行当たりの数
	public static final BigDecimal PI = new BigDecimal("3.1415926535897932384626");
	private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);
	private PanelMode currentPanelMode = PanelMode.MODE00;
	
	private ArrayList<JButton> funcButtons = new ArrayList<>();
	
	//一部ボタンフォーマット整える用
	private static final String BS = "⌫";
	private static final String PI_LABEL = "𝝅";
	private static final String pow10 = "<html>10<sup>x</sup></html>";
	private static final String e_x = "<html>e<sup>x</sup></html>";
	private static final String log10 = "<html>log<sub>10</sub></html>";
	private static final String x_pow_2 = "<html>x<sup>2</sup></html>";
	private static final String x_pow_3 = "<html>x<sup>3</sup></html>";
	private static final String x_pow_y = "<html>x<sup>y</sup></html>";
	private static final String x_rt_y = "<html><sup>y</sup>&radic;x</html>";
	private static final String _2nd = "<html>2<sup>nd</sup></html>";
	
    // ボタンの配列ラベル
    final String[] funcButtonLabels = {
            "sin", "cos", "tan",
            "mod", x_pow_2, x_pow_y,
            e_x, "x!", "EXP",
            "ln", "|x|", "√",
            "1/x", PI_LABEL, _2nd,
    };
    
    final String[] altfuncButtonLabels = {
            "asin", "acos", "atan",
            "mod", x_pow_3, x_pow_y,
            pow10, "x!", "EXP",
            log10, "|x|", x_rt_y,
            "1/x", PI_LABEL, _2nd,
    };
	
	//計算機入力管理用のフラグ
	enum CalcState {
	    INPUT_NUM1,     // num1 を入力中
	    AFTER_OPERATOR, // 演算子を押した直後（次の入力は num2 開始）
	    INPUT_NUM2,     // num2 を入力中
	    AFTER_EQUAL,    // = の直後（結果表示中）
	    AFTER_UNARY,	 // 関数適用後の状態
	    OVERWRITE,      // 単項演算や C/CE 後の上書き開始
	    ERROR			 // エラー。全ての入力を停止。10
	}
	private CalcState state = CalcState.INPUT_NUM1;
	private AngleMode angleMode = AngleMode.RAD;
	private BasicMath basicMath;
    private AngleMath angleMath;
    private GammaMath gammaMath;
    
    public CalculatorGUI() {
        // ウィンドウの設定
        setTitle("JavaCalculator");
        setSize(480, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        
        
        
        // 表示フィールド
        displayField = new JTextField("0");
        displayField.setFont(new Font("Consolas", Font.BOLD, 28));
        displayField.setHorizontalAlignment(SwingConstants.RIGHT);
        displayField.setEditable(false);
        displayField.setBackground(new Color(30, 30, 30));
        displayField.setForeground(Color.WHITE);
        displayField.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        // add(displayField, BorderLayout.NORTH);
        
        //モード切り替え用 ボタンパネル
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        modePanel.setPreferredSize(new Dimension(0, 28)); // ← 高さだけ固定
        modePanel.setBackground(new Color(20, 20, 20));
        
        JButton radButton = makeButton("RAD", 40, 60, 40);
        radButton.setPreferredSize(new Dimension(60, 20));
        radButton.setFont(new Font("Meiryo", Font.PLAIN, 15));

        modePanel.add(radButton);

        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(new Color(20, 20, 20));

        topPanel.add(displayField, BorderLayout.NORTH);
        topPanel.add(modePanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        
        
        
        //ボタン用パネル
        JPanel forButtonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        forButtonPanel.setBackground(new Color(20, 20, 20));


        
        String[] numButtonLabels = {
        		BS, "CE", "AC", "%", "÷",
                "M+", "7", "8", "9", "×", 
                "M-", "4", "5", "6", "-", 
                "MR", "1", "2", "3", "+", 
                "MC", "±", "0", ".", "=", 
        };
        
        basicMath = new BasicMath(MC, () -> angleMode);
    	angleMath = new AngleMath(MC, () -> angleMode);
    	gammaMath = new GammaMath(MC, () -> angleMode);
        System.out.println("angleMode = " + angleMode); 
        
        // 関数ボタンパネルを追加
        JPanel funcPanel = new JPanel(new GridLayout(CalculatorCols, 3, 5, 5));
        funcPanel.setBackground(new Color(20, 20, 20));
        
        for (String label : funcButtonLabels) {
        	JButton thisBtn = makeButton(label, 60, 60, 60);
            funcPanel.add(thisBtn);
            funcButtons.add(thisBtn);
        }
        
        // 数字系ボタンパネルを追加
        JPanel numPanel = new JPanel(new GridLayout(CalculatorCols, 4, 5, 5));
        numPanel.setBackground(new Color(20, 20, 20));
        
        for (String label : numButtonLabels) {
            numPanel.add(makeButton(label, 40, 40, 40));
        }
        
        forButtonPanel.add(funcPanel);
        forButtonPanel.add(numPanel);

        add(forButtonPanel, BorderLayout.CENTER);
        
        // ウィンドウを表示
        setVisible(true);
    }
    
    
    // ボタンフォーマット
    private JButton makeButton(String text, int r, int g, int b) {
        JButton btn = new JButton(text);
        
        btn.setFont(new Font("Noto Sans Math", Font.BOLD, 18));
        
        btn.setBackground(new Color(r, g, b));
        
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        btn.addActionListener(this);
        return btn;
    }
    
    /*
    // 切り替え式ボタンフォーマット
    private JButton makeButton(String label, String altLabel, int r, int g, int b) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Meiryo", Font.BOLD, 18));
        btn.setBackground(new Color(r, g, b));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        final int LONG_PRESS_TIME = 500; // 500ms で長押し判定
        final boolean[] longPressed = { false };
        Timer timer = new Timer(LONG_PRESS_TIME, e -> {
            longPressed[0] = true;
            //btn.setBackground(new Color(255, 200, 0)); // 長押し色
            // 長押し時の動作（asin / acos / atan）
            btn.setText(altLabel);
        });
        timer.setRepeats(false);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                longPressed[0] = false;
                timer.restart();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                timer.stop();
                
                btn.setBackground(new Color(r, g, b));
                btn.setText(label);
                
                if (!longPressed[0]) {
                    // 短押し（sin / cos / tan）
                    handleTrigFunction(label);
                } else {
                	handleTrigFunction(altLabel);
                }
            }
        });

        return btn;
    }
    */
    
    /*
    //切り替えボタン用
    private void handleTrigFunction(String func) {
        switch (func) {
            case "sin":
                applyUnaryOperation(v -> angleMath.sin(v));
                break;
            case "asin":
                applyUnaryOperation(v -> angleMath.arcsin(v));
                break;
                
                
            case "cos":
                applyUnaryOperation(v -> angleMath.cos(v));
                break;
            case "acos":
                applyUnaryOperation(v -> angleMath.arccos(v));
                break;
                
            case "tan":
                applyUnaryOperation(v -> angleMath.tan(v));
                break;
            case "atan":
                applyUnaryOperation(v -> angleMath.arctan(v));
                break;
                
            case x_pow_2:
                applyUnaryOperation(v -> v.multiply(v));
                break;
            case x_pow_3:
                applyUnaryOperation(v -> v.multiply(v).multiply(v));
                break;
    		case "√": 
    			applyUnaryOperation(v -> sqrt(v));
    			break;
    		case x_rt_y:
    			operator_entry("√");
    			break;
        }
    }
    */

    
    // 計算の答えが.0の時はそれを消すメソッド
    private String formatNumber(BigDecimal num) {
    	
    	num = num.stripTrailingZeros();

        if (num.scale() <= 0)  return num.toPlainString();
        
        num = num.setScale(10, RoundingMode.HALF_UP)
                .stripTrailingZeros();

        return num.toPlainString();
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
            	BigDecimal inv = BigDecimal.ONE.divide(b, MC); // 1/y
                BigDecimal ln_r = basicMath.ln(a);               // ln(x)
                BigDecimal mul_r = inv.multiply(ln_r, MC);         // (1/y) * ln(x)
                return basicMath.exp(mul_r);                     // exp(...)
            case '%':
                BigDecimal rmd = a.remainder(b, MC);
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

        if (displayField.getText().isEmpty()) return;

        try {
            BigDecimal value = new BigDecimal(displayField.getText());
            BigDecimal result = op.apply(value);

            displayField.setText(formatNumber(result));

            if (state == CalcState.INPUT_NUM2 || 
            	state == CalcState.AFTER_OPERATOR || 
            	state == CalcState.AFTER_UNARY) {
            	
                num2 = result;
                lastNum2 = result;
                //setState(CalcState.AFTER_UNARY);
            } else {
                num1 = result;
                lastNum2 = result;
                //setState(CalcState.AFTER_UNARY);
            }
            
            // ★ ここが重要：関数適用モードにする
            operator = 'F';   // Function の意味（任意の文字でOK）
            lastUnaryOp = op;   // ★ どの関数を使ったか保存
            setState(CalcState.AFTER_UNARY);
            
            
        } catch (Exception ex) {
            displayField.setText("Error");
            setState(CalcState.ERROR);
        }
    }

 // 数字入力
    private void digit_entry(String command) {
    	switch(state) {
	    	case ERROR:
	    		return;
	    	case OVERWRITE: // ★ overwrite のときは必ず上書き
	    		displayField.setText(command);
	    		setState(operator == '\0' ? CalcState.INPUT_NUM1 : CalcState.INPUT_NUM2);
	    		return;
			case AFTER_EQUAL:
	            displayField.setText(command);
	            setState(CalcState.INPUT_NUM1);
				return;
			case AFTER_OPERATOR:
	            displayField.setText(command);
	            setState(CalcState.INPUT_NUM2);
				return;
			case INPUT_NUM1:
			case INPUT_NUM2:
				String s = displayField.getText();
		        // ★ "0" のときだけ上書き（"0." は除外）
		        if (s.equals("0")) {
		            displayField.setText(command);
		        } else {
		            displayField.setText(s + command);
		        }
		        return;
			case AFTER_UNARY:
				displayField.setText(command);
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
        if (state == CalcState.AFTER_OPERATOR) {
            operator = command.charAt(0);  // 置き換え
            return;
        }

        // ★ num2 が入力されている → 計算する
        if (state == CalcState.INPUT_NUM2) {
            // num2 が入力済みなら計算
            num2 = new BigDecimal(displayField.getText());
            num1 = calculate(num1, num2, operator);
            displayField.setText(formatNumber(num1));
        } else if (state == CalcState.INPUT_NUM1 || state == CalcState.AFTER_EQUAL 
        		|| state == CalcState.OVERWRITE) {
            // num1 を確定
            num1 = new BigDecimal(displayField.getText());
        }


        // ★ 新しい演算子をセット
        operator = command.charAt(0);

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
        if (state == CalcState.ERROR) return;

        try {
            BigDecimal x = new BigDecimal(displayField.getText());

            // 演算子がない → 単独の数値として扱う（÷100）
            if (operator == '\0' || num1 == null) {
                BigDecimal result = x.divide(new BigDecimal("100"), MC);
                displayField.setText(formatNumber(result));
                state = CalcState.OVERWRITE;
                return;
            }

            BigDecimal base = num1;

            BigDecimal result;

            // + と - は「num1 に対する割合」
            if (operator == '+' || operator == '-') {
                result = base.multiply(x.divide(new BigDecimal("100"), MC), MC);
            }
            // × と ÷ は単純に ÷100
            else {
                result = x.divide(new BigDecimal("100"), MC);
            }

            displayField.setText(formatNumber(result));
            num2 = result;
            lastNum2 = result;
            state = CalcState.OVERWRITE;

        } catch (Exception ex) {
            displayField.setText("Error");
            state = CalcState.ERROR;
        }
    }
    
    //バックスペース
	private void backspace() {
	    if (state == CalcState.ERROR || state == CalcState.AFTER_EQUAL) return;

	    String s = displayField.getText();

	    // 1文字しかない or "0" の場合は 0 に戻す
	    if (s.length() <= 1 || s.equals("0")) {
	        displayField.setText("0");
	        state = CalcState.OVERWRITE;
	        return;
	    }

	    // 最後の1文字を削除
	    s = s.substring(0, s.length() - 1);

	    // 末尾が "-" だけになるケース（- を消したら 0）
	    if (s.equals("-")) {
	        displayField.setText("0");
	        state = CalcState.OVERWRITE;
	        return;
	    }

	    displayField.setText(s);
	}
	
	//イコールボタン
	private void equal() {

		// ★ 単項関数の再適用
		if (operator == 'F') {
		    BigDecimal value = new BigDecimal(displayField.getText());
		    BigDecimal result = lastUnaryOp.apply(value);

		    displayField.setText(formatNumber(result));

		    num1 = result;
		    lastNum2 = result;

		    setState(CalcState.AFTER_EQUAL);
		    return;
		}

	    try {

	        if (state == CalcState.INPUT_NUM2) {
	            num2 = new BigDecimal(displayField.getText());
	            lastNum2 = num2;

	        } else if (state == CalcState.AFTER_UNARY) {

	            if (operator != '\0') {
	                num2 = new BigDecimal(displayField.getText());
	                lastNum2 = num2;
	            } else {
	                num1 = new BigDecimal(displayField.getText());
	                lastNum2 = num1;
	                displayField.setText(formatNumber(num1));
	                setState(CalcState.AFTER_EQUAL);
	                return;
	            }

	        } else if (state == CalcState.AFTER_EQUAL) {
	            num2 = lastNum2;

	        } else {
	            return;
	        }

	        result = calculate(num1, num2, operator);

	        displayField.setText(formatNumber(result));

	        num1 = result;
	        setState(CalcState.AFTER_EQUAL);

	    } catch (Exception ex) {
	        displayField.setText("Error");
	        setState(CalcState.ERROR);
	    }
	}
	
	//小数点
	private void puttingDot() {

	    // ★ 最優先：演算子直後の小数点は「0.」
	    if (state == CalcState.AFTER_OPERATOR || state == CalcState.OVERWRITE) {
	        displayField.setText("0.");
	        setState(operator == '\0' ? CalcState.INPUT_NUM1 : CalcState.INPUT_NUM2);
	        return;
	    }

	    String s = displayField.getText();
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

	    switch (state) {
	        case ERROR:
	            return;

	        case AFTER_EQUAL:
	            setState(CalcState.INPUT_NUM1);
	            displayField.setText("0.");
	            return;

	        default:
	            if (s.isEmpty() || s.equals("0")) {
	                displayField.setText("0.");
	            } else {
	                displayField.setText(s + ".");
	            }
	            return;
	    }
	}
	
	//メモリー機能
	private void memoryMode(String command) {
		switch(command) {
			case "MC":
			    memory = BigDecimal.ZERO;
			    break;
	
			case "MR":
			    displayField.setText(formatNumber(memory));
			    state = CalcState.OVERWRITE;
			    break;
	
			case "M+":
			    memory = memory.add(new BigDecimal(displayField.getText()), MC);
			    break;
	
			case "M-":
			    memory = memory.subtract(new BigDecimal(displayField.getText()), MC);
			    break;
		}
		
		System.out.print("MEMORY = ");
		System.out.println(memory);
	}
	
	private void allClear() {
		displayField.setText("0");
        num1 = num2 = result = lastNum2 = BigDecimal.ZERO;
        operator = '\0';
        setState(CalcState.INPUT_NUM1);
	}

	private void clearEntry() {
        
        switch (state) {

	        case INPUT_NUM2:
	        case AFTER_UNARY:
	            // ★ num2 の入力値だけ消す
	            displayField.setText("0");
	            setState(CalcState.INPUT_NUM2);
	            return;
	
	        case AFTER_OPERATOR:
	            // ★ 演算子直後の CE は「0」に戻すだけ（num1/operator は保持）
	            displayField.setText("0");
	            setState(CalcState.INPUT_NUM2);
	            return;
	
	        case INPUT_NUM1:
	        case AFTER_EQUAL:
	            // ★ num1 の入力値だけ消す
	            displayField.setText("0");
	            setState(CalcState.INPUT_NUM1);
	            return;
	
	        case ERROR:
	            // ★ エラー状態は CE で復帰
	            displayField.setText("0");
	            setState(CalcState.INPUT_NUM1);
	            return;
	         
	        default:
	            displayField.setText("0");
	            setState(CalcState.INPUT_NUM1);
	            return;
        }
	}
	
	private void puttingExp() {
	    String s = displayField.getText();

	    // すでに E がある場合は無視（E を2回押させない）
	    if (s.contains("E")) return;

	    // OVERWRITE のときは新規入力扱い
	    if (state == CalcState.OVERWRITE || state == CalcState.AFTER_EQUAL) {
	        displayField.setText("1E");
	    } else {
	        displayField.setText(s + "E");
	    }

	    // state は変えない（INPUT_NUM1 or INPUT_NUM2 を維持）
	    return;
	}
	
	private void finalizeExpIfNeeded(){
	    String s = displayField.getText();

	    int idx = s.indexOf("E");
	    if (idx == -1) return; // E がない

	    // E の後に数字が無い場合
	    if (idx == s.length() - 1) {
	        displayField.setText(s + "0"); // E0 に補完
	    }
	    // E- の後も補完
	    else if (s.endsWith("E-") || s.endsWith("E+")) {
	        displayField.setText(s + "0");
	    }
	}

    
    //計算機の状態セット
    private void setState(CalcState s) {
        this.state = s;
        // デバッグ用ログ
        System.out.println("STATE -> " + s);
    }
    
    //計算機の状態セット
    private void switchAngleMode(JButton btn) {
		
		switch (angleMode) {
	        case RAD:
	            angleMode = AngleMode.DEG;
	            btn.setText("DEG");
	            btn.setBackground(new Color(80, 40, 40)); //DEGの色
	            break;
	        case DEG:
	            angleMode = AngleMode.GRAD;
	            btn.setText("GRAD");
	            btn.setBackground(new Color(80, 80, 40)); //GRADの色
	            break;
	        case GRAD:
	            angleMode = AngleMode.RAD;
	            btn.setText("RAD");
	            btn.setBackground(new Color(40, 80, 40)); //DEGの色
	            break;
			}
	        // デバッグ用ログ
	        System.out.println("AngleMode -> "+ btn.getText());
    }
    
    private void switchPanelMode(JButton btn) {
		
		switch (currentPanelMode) {
        case MODE00:
        	currentPanelMode = PanelMode.MODE01;
            break;
        case MODE01:
        	currentPanelMode = PanelMode.MODE00;
            break;
		}
        // デバッグ用ログ
		updateShiftLabels();
        System.out.println("PanelMode -> "+ currentPanelMode);
    }
    
    private void updateShiftLabels() {
        for (int i = 0; i < funcButtons.size(); i++) {
        	
        	switch (currentPanelMode) {
        		case MODE00:
        			funcButtons.get(i).setText(funcButtonLabels[i]);
        			funcButtons.get(i).setBackground(new Color(60, 60, 60));
        			break;
        		case MODE01:
        			funcButtons.get(i).setText(altfuncButtonLabels[i]);
        			funcButtons.get(i).setBackground(new Color(50, 50, 50));
        			break;
        	}
        }
    }

    
 // ------------------------- ここからボタン処理 ------------------------- // 

    @Override
    public void actionPerformed(ActionEvent e) {
    	
        String command = e.getActionCommand();
        System.out.println("cmd = [" + command + "]");
        
        if (command.equals("")) {
        	return;
        }
        
        // 数字の場合
        if (command.length() == 1 && "0123456789".contains(command)) 
        	digit_entry(command);
        
        // 演算子の場合
        else if ("+-×÷".contains(command)) {
        	if (state == CalcState.ERROR) return;
        	operator_entry(command);
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
        	}
	        
        	if (state == CalcState.ERROR) return;
        	
        	switch(command) {
        		case BS:
        			backspace();
        			break;
        		case "=": //イコール処理
	        		equal();
	        		break;
        		case ".": //小数点
	        		puttingDot(); 
	        		break;
        		case PI_LABEL: 
        			applyUnaryOperation(v -> PI);
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
        		case log10: 
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

        		case x_pow_2:
        			applyUnaryOperation(v -> v.multiply(v));
        			break;
        		case x_pow_3:
        			applyUnaryOperation(v -> v.multiply(v).multiply(v));
        			break;
        		
        		case x_pow_y:
        			operator_entry("^");
        			break;
        		case pow10:
        			applyUnaryOperation(v -> basicMath._10_x(v));
        			break;
        		case e_x:
        			applyUnaryOperation(v -> basicMath.exp(v));
        			break;
        		case x_rt_y:
        			operator_entry("√");
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
        			switchAngleMode((JButton)e.getSource());
        		    break;
        		
        		case "MC":
        		case "MR":
        		case "M+":
        		case "M-":
        			memoryMode(command);
        			
        		case _2nd:
        			switchPanelMode((JButton)e.getSource());
        			
        		default :
        			break;
        	}
        }
    }
    

	public static void main(String[] args) {
		System.out.println("STATE -> INPUT_NUM1");

        // イベントディスパッチスレッドでGUIを作成
        SwingUtilities.invokeLater(() -> {
            new CalculatorGUI();
        });
        
    }
}