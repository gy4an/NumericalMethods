import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

public class SimpsonPanel extends JPanel {
    private JTextField functionField, aField, bField, nField;
    private JTextArea resultArea;

    public SimpsonPanel() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        functionField = new JTextField("Math.sin(x)");
        aField = new JTextField("0");
        bField = new JTextField("3.14");
        nField = new JTextField("6"); // Multiple of 3

        inputPanel.add(new JLabel("Function f(x):"));
        inputPanel.add(functionField);
        inputPanel.add(new JLabel("Lower Limit a:"));
        inputPanel.add(aField);
        inputPanel.add(new JLabel("Upper Limit b:"));
        inputPanel.add(bField);
        inputPanel.add(new JLabel("Number of intervals n (multiple of 3):"));
        inputPanel.add(nField);

        JButton computeButton = new JButton("Compute");
        computeButton.addActionListener(e -> computeSimpson());
        inputPanel.add(computeButton);

        resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void computeSimpson() {
        try {
            double a = Double.parseDouble(aField.getText());
            double b = Double.parseDouble(bField.getText());
            int n = Integer.parseInt(nField.getText());
            String func = functionField.getText();

            if (n % 3 != 0) {
                resultArea.setText("n must be a multiple of 3 for Simpson's 1/8 Rule.");
                return;
            }

            double h = (b - a) / n;
            double sum = 0.0;

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

            resultArea.setText("x\tf(x)\n");
            for (int i = 0; i <= n; i++) {
                double x = a + i * h;
                engine.put("x", x);
                double fx = ((Number) engine.eval(func)).doubleValue();
                resultArea.append(String.format("%.5f\t%.5f\n", x, fx));

                if (i == 0 || i == n) {
                    sum += fx;
                } else if (i % 3 == 0) {
                    sum += 2 * fx;
                } else {
                    sum += 3 * fx;
                }
            }

            double result = (3 * h / 8) * sum;
            resultArea.append("\nApproximate Integral: " + result);
        } catch (Exception ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }
}
