import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;

public class CholeskyPanel extends JPanel {
    private JTable matrixATable, vectorBTable;
    private JTextArea outputArea;

    public CholeskyPanel() {
        setLayout(new BorderLayout(10, 10));

        // Top panel with Matrix A and Vector b side by side
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Matrix A Table (3x3)
        DefaultTableModel matrixAModel = new DefaultTableModel(3, 3);
        matrixATable = new JTable(matrixAModel);
        matrixATable.setGridColor(Color.GRAY);
        matrixATable.setShowGrid(true);
        matrixATable.setPreferredScrollableViewportSize(new Dimension(250, 70));
        topPanel.add(new JScrollPane(matrixATable));

        // Vector b Table (3x1)
        DefaultTableModel vectorBModel = new DefaultTableModel(3, 1);
        vectorBTable = new JTable(vectorBModel);
        vectorBTable.setGridColor(Color.GRAY);
        vectorBTable.setShowGrid(true);
        vectorBTable.setPreferredScrollableViewportSize(new Dimension(80, 70));
        topPanel.add(new JScrollPane(vectorBTable));

        add(topPanel, BorderLayout.NORTH);

        // Control panel with solve button aligned left
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(e -> solveCrout());
        controlPanel.add(solveButton);

        add(controlPanel, BorderLayout.CENTER);

        // Output area for steps
        outputArea = new JTextArea(15, 60);
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Computation Steps"));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void solveCrout() {
        try {
            int n = matrixATable.getRowCount();
            double[][] A = new double[n][n];
            double[] b = new double[n];

            // Ask for decimal places
            String input = JOptionPane.showInputDialog(this, "Enter number of decimal places (0-10):", "Decimal Places", JOptionPane.QUESTION_MESSAGE);
            int decimals = 4; // default
            if (input != null && !input.trim().isEmpty()) {
                decimals = Math.max(0, Math.min(10, Integer.parseInt(input.trim())));
            }
            String pattern = "#." + "#".repeat(decimals);
            DecimalFormat df = new DecimalFormat(pattern);

            // Parse matrix A
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    A[i][j] = Double.parseDouble(matrixATable.getValueAt(i, j).toString());
                }
            }

            // Parse vector b
            for (int i = 0; i < n; i++) {
                b[i] = Double.parseDouble(vectorBTable.getValueAt(i, 0).toString());
            }

            double[][] L = new double[n][n];
            double[][] U = new double[n][n];

            // Initialize U diagonals to 1 (Crout's Method)
            for (int i = 0; i < n; i++) {
                U[i][i] = 1;
            }

            outputArea.setText("--- Crout’s Method Decomposition Steps ---\n");

            for (int j = 0; j < n; j++) {
                for (int i = j; i < n; i++) {
                    double sum = 0;
                    for (int k = 0; k < j; k++) {
                        sum += L[i][k] * U[k][j];
                    }
                    L[i][j] = A[i][j] - sum;
                    outputArea.append("L" + (i+1)  + (j+1) + " = " + df.format(L[i][j]) + "\n");
                }

                for (int i = j + 1; i < n; i++) {
                    double sum = 0;
                    for (int k = 0; k < j; k++) {
                        sum += L[j][k] * U[k][i];
                    }
                    U[j][i] = (A[j][i] - sum) / L[j][j];
                    outputArea.append("U" + (j+1)  + (i+1) + " = " + df.format(U[j][i]) + "\n");
                }
            }

            // Print L and U
            outputArea.append("\nMatrix L:\n");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    outputArea.append((j <= i ? df.format(L[i][j]) : "0") + "\t");
                }
                outputArea.append("\n");
            }

            outputArea.append("\nMatrix U:\n");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    outputArea.append((i > j ? "0" : (i == j ? "1" : df.format(U[i][j]))) + "\t");
                }
                outputArea.append("\n");
            }

            // Forward substitution (L * y = b)
            double[] y = new double[n];
            outputArea.append("\nForward Substitution (R):\n");
            for (int i = 0; i < n; i++) {
                double sum = 0;
                for (int j = 0; j < i; j++) {
                    sum += L[i][j] * y[j];
                }
                y[i] = (b[i] - sum) / L[i][i];
                outputArea.append("R" + (i + 1) + " = " + df.format(y[i]) + "\n");
            }

            // Backward substitution (U * x = y)
            double[] x = new double[n];
            outputArea.append("\nBackward Substitution (x):\n");
            for (int i = n - 1; i >= 0; i--) {
                double sum = 0;
                for (int j = i + 1; j < n; j++) {
                    sum += U[i][j] * x[j];
                }
                x[i] = y[i] - sum; // U[i][i] = 1
                outputArea.append("x" + (i + 1) + " = " + df.format(x[i]) + "\n");
            }

        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

}
