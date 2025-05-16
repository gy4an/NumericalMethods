import javax.swing.*;
import java.awt.*;

public class SimpsonPanel extends JPanel {
    public SimpsonPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Simpson 1/8 Rule GUI - Work in Progress");
        add(label, BorderLayout.NORTH);
    }
}