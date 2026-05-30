import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginForm extends JFrame {

    private static final Color NAVY = new Color(26, 60, 110);
    private static final Color GOLD = new Color(212, 175, 55);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel attemptsLabel;
    private int failedAttempts = 0;
    private static final int MAX_ATTEMPTS = 3;

    public LoginForm() {
        setTitle("Hotel Management System - Login");
        setSize(440, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // ---- Header ----
        JPanel header = new JPanel();
        header.setBackground(NAVY);
        header.setPreferredSize(new Dimension(0, 90));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        JLabel titleLabel = new JLabel("GRAND EAGLE HOTEL");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Management System");
        subLabel.setForeground(GOLD);
        subLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(5));
        header.add(subLabel);

        // ---- Form ----
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 50, 15, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel userLabel = styledLabel("Username");
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(userLabel, gbc);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameField.setPreferredSize(new Dimension(0, 36));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        gbc.gridy = 1;
        form.add(usernameField, gbc);

        JLabel passLabel = styledLabel("Password");
        gbc.gridy = 2;
        form.add(passLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setPreferredSize(new Dimension(0, 36));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        gbc.gridy = 3;
        form.add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(NAVY);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(0, 40));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 4;
        gbc.insets = new Insets(18, 0, 5, 0);
        form.add(loginBtn, gbc);

        attemptsLabel = new JLabel(" ");
        attemptsLabel.setForeground(new Color(180, 30, 30));
        attemptsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        attemptsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(attemptsLabel, gbc);

        loginBtn.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);

        // ---- Footer ----
        JPanel footer = new JPanel();
        footer.setBackground(new Color(245, 245, 250));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        JLabel footerLabel = new JLabel("Hotel Management System © 2026");
        footerLabel.setForeground(new Color(150, 150, 160));
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.add(footerLabel);
        add(footer, BorderLayout.SOUTH);
    }

    private JLabel styledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(70, 70, 90));
        return label;
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            UserAccount user = HotelApp.authenticate(username, password);
            failedAttempts = 0;
            dispose();
            openDashboard(user);
        } catch (InvalidLoginException e) {
            failedAttempts++;
            int remaining = MAX_ATTEMPTS - failedAttempts;
            passwordField.setText("");
            if (remaining > 0) {
                attemptsLabel.setText(e.getMessage() + " (" + remaining + " attempt(s) left)");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Maximum login attempts reached. The system will now close.",
                        "Access Denied", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    private void openDashboard(UserAccount user) {
        SwingUtilities.invokeLater(() -> {
            switch (user.getRole()) {
                case "ADMIN":
                    new AdminDashboard(user).setVisible(true);
                    break;
                case "MANAGER":
                    new ManagerDashboard(user).setVisible(true);
                    break;
                case "RECEPTIONIST":
                    new ReceptionistDashboard(user).setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Unknown role. Contact system admin.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    new LoginForm().setVisible(true);
            }
        });
    }
}
