import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SimpsonPanel extends JPanel {
    private JTextField functionField, aField, bField, nField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel resultLabel, hLabel;

    public SimpsonPanel() {
        setLayout(new BorderLayout());

        // Inputs
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        functionField = new JTextField("3x^2 + 2x + 1");
        aField = new JTextField("0");
        bField = new JTextField("1");
        nField = new JTextField("6"); // Must be multiple of 3

        hLabel = new JLabel("h: ");
        hLabel.setHorizontalAlignment(SwingConstants.LEFT);
        hLabel.setFont(new Font("Arial", Font.BOLD, 14));

        inputPanel.add(new JLabel("Function f(x):"));
        inputPanel.add(functionField);
        inputPanel.add(new JLabel("Lower Limit a:"));
        inputPanel.add(aField);
        inputPanel.add(new JLabel("Upper Limit b:"));
        inputPanel.add(bField);
        inputPanel.add(new JLabel("Number of intervals n (multiple of 3):"));
        inputPanel.add(nField);

        JButton computeButton = new JButton("Compute");
        inputPanel.add(hLabel);
        inputPanel.add(computeButton);

        add(inputPanel, BorderLayout.NORTH);

        // Table with updated columns
        tableModel = new DefaultTableModel(new Object[]{"Intervals", "x", "f(x)", "Multiplier", "f(xᵢ)"}, 0);
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // Result label
        resultLabel = new JLabel("Approximate Integral: ");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(resultLabel, BorderLayout.SOUTH);

        computeButton.addActionListener(e -> computeSimpson());
    }

    private void computeSimpson() {
        tableModel.setRowCount(0); // clear table
        resultLabel.setText("Approximate Integral: ");

        try {
            double a = Double.parseDouble(aField.getText());
            double b = Double.parseDouble(bField.getText());
            int n = Integer.parseInt(nField.getText());
            String func = functionField.getText().trim();

            func = func.replaceAll("(?<=[0-9])(?=x)", "*");  // 5x → 5*x
            func = func.replaceAll("(?<=\\))(?=x)", ")*x");   // )x → )*x
            func = func.replaceAll("(?<=\\d)(?=\\()", "*");   // 2(x) → 2*(x)
            func = func.replaceAll("(?<=x)(?=\\()", "*");     // x( → x*(


            if (n <= 0 || n % 3 != 0 || a >= b) {
                JOptionPane.showMessageDialog(this, "n must be a positive multiple of 3 and a < b.");
                return;
            }

            double h = (b - a) / n;
            double sum = 0.0;

            System.out.println("Processed function: " + func);

            for (int i = 0; i <= n; i++) {
                double x = a + i * h;

                Expression expression = new ExpressionBuilder(func)
                        .variable("x")
                        .build();

                double fx = expression.setVariable("x", x).evaluate();

                int multiplier;
                if (i == 0 || i == n) {
                    multiplier = 1;
                } else if (i % 3 == 0) {
                    multiplier = 2;
                } else {
                    multiplier = 3;
                }

                sum += multiplier * fx;

                tableModel.addRow(new Object[]{
                        "x" + i,
                        String.format("%.5f", x),
                        String.format("%.5f", fx),
                        multiplier,
                        String.format("%.5f", multiplier * fx)
                });


            }

            double result = ((3*h)/8) * sum;
            hLabel.setText(String.format("h: %.6f", h));
            resultLabel.setText(String.format("Estimated Integral: %.6f", result) +  " sq. units");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
