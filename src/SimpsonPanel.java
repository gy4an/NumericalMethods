import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class SimpsonPanel extends JPanel {
    private JTextField functionField, aField, bField, nField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel resultLabel;

    public SimpsonPanel() {
        setLayout(new BorderLayout());

        // Inputs
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        functionField = new JTextField("sin(x)");
        aField = new JTextField("0");
        bField = new JTextField("3.14");
        nField = new JTextField("6"); // Must be multiple of 3

        inputPanel.add(new JLabel("Function f(x):"));
        inputPanel.add(functionField);
        inputPanel.add(new JLabel("Lower Limit a:"));
        inputPanel.add(aField);
        inputPanel.add(new JLabel("Upper Limit b:"));
        inputPanel.add(bField);
        inputPanel.add(new JLabel("Number of intervals n (multiple of 3):"));
        inputPanel.add(nField);

        JButton computeButton = new JButton("Compute");
        inputPanel.add(new JLabel());
        inputPanel.add(computeButton);

        add(inputPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"i", "x_i", "f(x_i)"}, 0);
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

            if (n <= 0 || n % 3 != 0 || a >= b) {
                JOptionPane.showMessageDialog(this, "n must be a positive multiple of 3 and a < b.");
                return;
            }

            Expression expression = new ExpressionBuilder(func)
                    .variables("x")
                    .build();

            double h = (b - a) / n;
            double sum = 0.0;

            for (int i = 0; i <= n; i++) {
                double x = a + i * h;
                double fx = expression.setVariable("x", x).evaluate();

                tableModel.addRow(new Object[]{i, String.format("%.5f", x), String.format("%.5f", fx)});

                if (i == 0 || i == n) {
                    sum += fx;
                } else if (i % 3 == 0) {
                    sum += 2 * fx;
                } else {
                    sum += 3 * fx;
                }
            }

            double result = (3 * h / 8) * sum;
            resultLabel.setText(String.format("✅ Approximate Integral: %.6f", result));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
