import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;


public class RegulaFalsiPanel extends JPanel {
    private JTextField equationField, aField, bField, tolField;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public RegulaFalsiPanel() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        equationField = new JTextField();
        aField = new JTextField();
        bField = new JTextField();
        tolField = new JTextField("0.0001");

        inputPanel.add(new JLabel("f(x) ="));
        inputPanel.add(equationField);
        inputPanel.add(new JLabel("x0:"));
        inputPanel.add(aField);
        inputPanel.add(new JLabel("x1:"));
        inputPanel.add(bField);
        inputPanel.add(new JLabel("Tolerance:"));
        inputPanel.add(tolField);

        JButton computeButton = new JButton("Compute");
        inputPanel.add(computeButton);

        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Iteration", "x0", "x1", "x2", "f(x2)"}, 0);
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        computeButton.addActionListener(e -> computeRegulaFalsi());
    }

    private void computeRegulaFalsi() {
        tableModel.setRowCount(0);
        try {
            String expr = equationField.getText().trim();
            double a = Double.parseDouble(aField.getText());
            double b = Double.parseDouble(bField.getText());
            double tol = Double.parseDouble(tolField.getText());

            int maxIter = 100;
            double c = a;

            for (int i = 1; i <= maxIter; i++) {
                double fa = evaluate(expr, a);
                double fb = evaluate(expr, b);

                c = (a * fb - b * fa) / (fb - fa);
                double fc = evaluate(expr, c);

                tableModel.addRow(new Object[]{i, a, b, c, fc});

                if (Math.abs(fc) < tol) break;

                if (fa * fc < 0)
                    b = c;
                else
                    a = c;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input or equation: " + ex.getMessage());
        }
    }

    private double evaluate(String expr, double x) {
        return SimpleMathEvaluator.evaluate(expr, x);
    }
}
