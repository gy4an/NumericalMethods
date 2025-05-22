import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TrapezoidalPanel extends JPanel {
    private JTextField functionField, aField, bField, nField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel resultLabel;

    public TrapezoidalPanel() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        functionField = new JTextField("sin(x)"); // use exp4j syntax, no Math.sin()
        aField = new JTextField("0");
        bField = new JTextField("3.14");
        nField = new JTextField("10");

        inputPanel.add(new JLabel("Function f(x):"));
        inputPanel.add(functionField);
        inputPanel.add(new JLabel("Lower Limit a:"));
        inputPanel.add(aField);
        inputPanel.add(new JLabel("Upper Limit b:"));
        inputPanel.add(bField);
        inputPanel.add(new JLabel("Number of intervals n:"));
        inputPanel.add(nField);

        JButton computeButton = new JButton("Compute");
        computeButton.addActionListener(e -> computeTrapezoidal());
        inputPanel.add(computeButton);

        add(inputPanel, BorderLayout.NORTH);

        // Table model and JTable setup
        tableModel = new DefaultTableModel(new Object[]{"Intervals", "x", "f(x)", "Multiplier", "f(xᵢ)"}, 0);
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // Result label for final integral value
        resultLabel = new JLabel("Approximate Integral: ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(resultLabel, BorderLayout.SOUTH);
    }

    private void computeTrapezoidal() {
        tableModel.setRowCount(0); // clear table
        resultLabel.setText("Approximate Integral: ");

        try {
            double a = Double.parseDouble(aField.getText());
            double b = Double.parseDouble(bField.getText());
            int n = Integer.parseInt(nField.getText());
            String func = functionField.getText().trim();

            func = func.replace("ln(", "log(");

            if (n <= 0 || a >= b) {
                JOptionPane.showMessageDialog(this, "Please enter valid inputs: n > 0 and a < b.");
                return;
            }

            double h = (b - a) / n;
            double sum = 0.0;

            Expression expression = new ExpressionBuilder(func)
                    .variable("x")
                    .build();

            for (int i = 0; i <= n; i++) {
                double x = a + i * h;
                double fx = expression.setVariable("x", x).evaluate();

                int multiplier = (i == 0 || i == n) ? 1 : 2;
                sum += multiplier * fx;

                tableModel.addRow(new Object[]{
                        i,
                        String.format("%.5f", x),
                        String.format("%.5f", fx),
                        multiplier,
                        String.format("%.5f", multiplier * fx)
                });
            }

            double result = (h / 2) * sum;
            resultLabel.setText(String.format("Approximate Integral: %.6f", result));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
