package calculatorGUI.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import calculatorGUI.controller.CalculatorController;
import calculatorGUI.state.AngleMode;
import calculatorGUI.state.PanelMode;

public class CalculatorGUI extends JFrame{
	
	//Initiation
    private JTextField displayField;
    private JButton radButton;

    private static final int CalculatorCols = 5; //電卓ボタン, 1行当たりの数

	private ArrayList<JButton> funcButtons = new ArrayList<>();
	private CalculatorController controller;
	
    // 関数ボタンの配列ラベル
    final String[] funcButtonLabels = {
            "sin", "cos", "tan",
            "mod", CalculatorButtons.LBL_X2, CalculatorButtons.LBL_XY,
            CalculatorButtons.LBL_E_X, "x!", "EXP",
            "ln", "|x|", "√",
            "1/x", CalculatorButtons.LBL_PI, CalculatorButtons.LBL_SECOND,
    };
    
    final String[] altfuncButtonLabels = {
            "asin", "acos", "atan",
            "mod", CalculatorButtons.LBL_X3, CalculatorButtons.LBL_XY,
            CalculatorButtons.LBL_POW_10, CalculatorButtons.LBL_CEIL, "EXP",
            CalculatorButtons.LBL_LOG10, CalculatorButtons.LBL_FLOOR, CalculatorButtons.LBL_YRT_X,
            "1/x", CalculatorButtons.LBL_PI, CalculatorButtons.LBL_SECOND,
    };
    
 // 数字ボタンの配列ラベル
    String[] numButtonLabels = {
    		CalculatorButtons.LBL_BS, "CE", "AC", "%", "÷",
            "M+", "7", "8", "9", "×", 
            "M-", "4", "5", "6", "-", 
            "MR", "1", "2", "3", "+", 
            "MC", "±", "0", ".", "=", 
    };
    
    public void setController(CalculatorController controller) {
        this.controller = controller;
    }
    
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
        
        // モード切り替え用 ボタンパネル
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        modePanel.setPreferredSize(new Dimension(0, 28)); // ← 高さだけ固定
        modePanel.setBackground(new Color(20, 20, 20));
        
        // 角度モード切り替え用 ボタンパネル
        radButton = makeButton("RAD", 40, 80, 40);
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
        
        btn.addActionListener(e -> controller.handle(btn.getText()));
        
        return btn;
    }
    
    public void updateDisplay(String text) {
    	displayField.setText(text);
    }
    
    public String getDisplayText() {
        return displayField.getText();
    }
    
    public void updatePanelMode(PanelMode pmode) {

    	
    	for (int i = 0; i < funcButtons.size(); i++) {
        	
        	switch (pmode) {
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
        System.out.println("PanelMode -> "+ pmode);
    }

    public void updateAngleMode(AngleMode angleMode) {
    	switch (angleMode) {
        case RAD:
            angleMode = AngleMode.DEG;
            radButton.setText("DEG");
            radButton.setBackground(new Color(80, 40, 40)); //DEGの色
            break;
        case DEG:
            angleMode = AngleMode.GRAD;
            radButton.setText("GRAD");
            radButton.setBackground(new Color(80, 80, 40)); //GRADの色
            break;
        case GRAD:
            angleMode = AngleMode.RAD;
            radButton.setText("RAD");
            radButton.setBackground(new Color(40, 80, 40)); //DEGの色
            break;
		}
        // デバッグ用ログ
        System.out.println("AngleMode -> "+ angleMode);
    }
    

}