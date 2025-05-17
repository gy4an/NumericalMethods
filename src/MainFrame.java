import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Numerical Methods");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.add("Regula-Falsi", new RegulaFalsiPanel());
        tabbedPane.add("Cholesky’s Method", new CholeskyPanel());
        tabbedPane.add("Gauss-Jacobi", new GaussJacobiPanel());
        tabbedPane.add("Trapezoidal Rule", new TrapezoidalPanel());
        tabbedPane.add("Simpson 3/8 Rule", new SimpsonPanel());

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }
}