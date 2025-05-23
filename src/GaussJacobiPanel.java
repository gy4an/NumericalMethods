import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;

public class GaussJacobiPanel extends JPanel {
    private JTextField expr1, expr2, expr3, toleranceField;
    private JTextArea outputArea;
    private JButton computeBtn;

    public GaussJacobiPanel() {
        setLayout(new BorderLayout());

        // Top panel with two columns
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Left column - labels
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(new JLabel("x1 ="), gbc);
        gbc.gridy++;
        topPanel.add(new JLabel("x2 ="), gbc);
        gbc.gridy++;
        topPanel.add(new JLabel("x3 ="), gbc);
        gbc.gridy++;
        topPanel.add(new JLabel("Ea ≤"), gbc);

        // Right column - inputs and button
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        expr1 = new JTextField("(11 + 2*x2 - x3) / 6");
        expr1.setPreferredSize(new Dimension(250, 25));
        topPanel.add(expr1, gbc);

        gbc.gridy++;
        expr2 = new JTextField("(5 + 2*x1 - 2*x3) / 7");
        expr2.setPreferredSize(new Dimension(250, 25));
        topPanel.add(expr2, gbc);

        gbc.gridy++;
        expr3 = new JTextField("(1 + x1 + 2*x2) / 5");
        expr3.setPreferredSize(new Dimension(250, 25));
        topPanel.add(expr3, gbc);

        gbc.gridy++;
        toleranceField = new JTextField("0.0001");
        toleranceField.setPreferredSize(new Dimension(100, 25));
        topPanel.add(toleranceField, gbc);

        gbc.gridy++;
        computeBtn = new JButton("Compute");
        computeBtn.setPreferredSize(new Dimension(100, 30));
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        topPanel.add(computeBtn, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Output area
        outputArea = new JTextArea(20, 70);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Button action
        computeBtn.addActionListener(e -> computeGaussJacobi());
    }

    private void computeGaussJacobi() {
        try {
            // Prompt for decimal places
            String decimalInput = JOptionPane.showInputDialog(this, "Enter number of decimal places (e.g. 4):", "Decimal Places", JOptionPane.QUESTION_MESSAGE);
            if (decimalInput == null) return;
            int decimalPlaces = Integer.parseInt(decimalInput.trim());
            if (decimalPlaces < 0 || decimalPlaces > 10)
                throw new IllegalArgumentException("Enter a number between 0 and 10.");
            String formatStr = "%." + decimalPlaces + "f";

            double tolerance = Double.parseDouble(toleranceField.getText());
            double x1 = 0, x2 = 0, x3 = 0;

            Expression exprX1 = new ExpressionBuilder(expr1.getText()).variables("x1", "x2", "x3").build();
            Expression exprX2 = new ExpressionBuilder(expr2.getText()).variables("x1", "x2", "x3").build();
            Expression exprX3 = new ExpressionBuilder(expr3.getText()).variables("x1", "x2", "x3").build();

            StringBuilder result = new StringBuilder();
            result.append(String.format("%-6s%-12s%-12s%-12s%-12s%-12s%-12s%-14s%-14s%-14s%n",
                    "Iter", "x1", "x2", "x3", "x1'", "x2'", "x3'", "Ea_x1 (%)", "Ea_x2 (%)", "Ea_x3 (%)"));

            int i = 1;
            while (true) {
                double oldX1 = x1;
                double oldX2 = x2;
                double oldX3 = x3;

                double newX1 = exprX1.setVariable("x1", oldX1).setVariable("x2", oldX2).setVariable("x3", oldX3).evaluate();
                double newX2 = exprX2.setVariable("x1", oldX1).setVariable("x2", oldX2).setVariable("x3", oldX3).evaluate();
                double newX3 = exprX3.setVariable("x1", oldX1).setVariable("x2", oldX2).setVariable("x3", oldX3).evaluate();

                double ea_x1 = (newX1 != 0) ? Math.abs((newX1 - oldX1) / newX1) * 100 : 0;
                double ea_x2 = (newX2 != 0) ? Math.abs((newX2 - oldX2) / newX2) * 100 : 0;
                double ea_x3 = (newX3 != 0) ? Math.abs((newX3 - oldX3) / newX3) * 100 : 0;

                result.append(String.format("%-6d%-12s%-12s%-12s%-12s%-12s%-12s%-14s%-14s%-14s%n",
                        i,
                        String.format(formatStr, oldX1),
                        String.format(formatStr, oldX2),
                        String.format(formatStr, oldX3),
                        String.format(formatStr, newX1),
                        String.format(formatStr, newX2),
                        String.format(formatStr, newX3),
                        String.format(formatStr, ea_x1),
                        String.format(formatStr, ea_x2),
                        String.format(formatStr, ea_x3)));

                x1 = newX1;
                x2 = newX2;
                x3 = newX3;

                if (ea_x1 <= tolerance && ea_x2 <= tolerance && ea_x3 <= tolerance) {
                    break;
                }

                if (i++ > 1000) {
                    result.append("\nStopped after 1000 iterations (no convergence).\n");
                    break;
                }
            }

            result.append("\nFinal Answer:\n");
            result.append("𝒙₁ = ").append(String.format(formatStr, x1)).append("\n");
            result.append("𝒙₂ = ").append(String.format(formatStr, x2)).append("\n");
            result.append("𝒙₃ = ").append(String.format(formatStr, x3)).append("\n");

            outputArea.setText(result.toString());

        } catch (NumberFormatException e) {
            outputArea.setText("Invalid input for decimal places or tolerance.");
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }
}
