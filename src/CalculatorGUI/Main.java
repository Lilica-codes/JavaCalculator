package calculatorGUI;

import javax.swing.SwingUtilities;

import calculatorGUI.controller.CalculatorController;
import calculatorGUI.state.CalculatorState;
import calculatorGUI.ui.CalculatorGUI;

public class Main {
    public static void main(String[] args) {

        System.out.println("STATE -> INPUT_NUM1");

        SwingUtilities.invokeLater(() -> {

            CalculatorState state = new CalculatorState();
            CalculatorGUI gui = new CalculatorGUI();
            CalculatorController controller = new CalculatorController(state, gui);

            gui.setController(controller);  // ← GUI に Controller を渡す
        });
    }
}
