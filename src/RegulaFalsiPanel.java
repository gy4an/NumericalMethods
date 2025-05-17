import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public class RegulaFalsiPanel extends JPanel {
    private JTextField equationField, aField, bField, tolField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel resultLabel; // <-- new label for estimated root

    public RegulaFalsiPanel() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        equationField = new JTextField();
        aField = new JTextField();
        bField = new JTextField();
        tolField = new JTextField("0.0001");

        inputPanel.add(new JLabel("f(x) ="));
        inputPanel.add(equationField);
        inputPanel.add(new JLabel("X0:"));
        inputPanel.add(aField);
        inputPanel.add(new JLabel("X1:"));
        inputPanel.add(bField);
        inputPanel.add(new JLabel("Tolerance:"));
        inputPanel.add(tolField);

        JButton computeButton = new JButton("Compute");
        inputPanel.add(computeButton);

        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Iteration", "X0", "X1", "X2", "f(X2)", "Ea"}, 0);
        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        resultLabel = new JLabel("Estimated root: ");
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(resultLabel, BorderLayout.SOUTH);

        computeButton.addActionListener(e -> computeRegulaFalsi());
    }

    private void computeRegulaFalsi() {
        tableModel.setRowCount(0);
        resultLabel.setText("Estimated root: "); // clear previous result

        try {
            String exprStr = equationField.getText().trim();
            double a = Double.parseDouble(aField.getText());
            double b = Double.parseDouble(bField.getText());
            double tol = Double.parseDouble(tolField.getText());

            // Ask user for decimal precision
            String decInput = JOptionPane.showInputDialog(this, "How many decimal places to round to?", "4");
            int decimals = Integer.parseInt(decInput.trim());

            Expression expr = new ExpressionBuilder(exprStr)
                    .variables("x")
                    .functions(new Function("sqrt", 1) {
                        @Override
                        public double apply(double... args) {
                            return Math.sqrt(args[0]);
                        }
                    })
                    .build();

            int maxIter = 100;
            double c = a;
            double fc = 0;
            double prevC = 0;

            for (int i = 1; i <= maxIter; i++) {
                double fa = evaluate(expr, a);
                double fb = evaluate(expr, b);

                if (fb - fa == 0) {
                    JOptionPane.showMessageDialog(this, "Division by zero detected in iteration " + i);
                    return;
                }

                c = (a * fb - b * fa) / (fb - fa);
                fc = evaluate(expr, c);

                double ea;
                if (i == 1) {
                    ea = 0;
                } else {
                    ea = Math.abs((c) - (prevC));
                }

                String eaDisplay;
                if (i == 1) {
                    eaDisplay = "";
                } else {
                    eaDisplay = String.valueOf(round(ea, decimals));
                }

                tableModel.addRow(new Object[]{
                        i,
                        round(a, decimals),
                        round(b, decimals),
                        round(c, decimals),
                        round(fc, decimals),
                        eaDisplay
                });

                if (Math.abs(fc) <= tol) break;

                if (fa * fc < 0) {
                    b = c;
                } else {
                    a = c;
                }
                prevC = c;
            }

            resultLabel.setText("Estimated root: " + round(c, decimals)); // <- show result

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input or equation: " + ex.getMessage());
        }
    }

    private double evaluate(Expression expr, double x) {
        return expr.setVariable("x", x).evaluate();
    }

    private double round(double value, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }
}
