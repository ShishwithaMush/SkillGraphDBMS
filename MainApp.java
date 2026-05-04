package com.dbms.ui;

import com.dbms.DBConnection;
import com.dbms.MainApp;
import com.dbms.model.User;
import com.dbms.service.AuthService;
import com.dbms.service.ValidationException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.Optional;

public class AuthView {
    private final MainApp app;
    private final AuthService auth = new AuthService();

    public AuthView(MainApp app) {
        this.app = app;
    }

    public JPanel login() {
        JPanel root = UIUtils.gradient();
        JPanel card = UIUtils.card();
        card.setPreferredSize(new Dimension(500, 610));

        JTextField email = UIUtils.field();
        JPasswordField password = UIUtils.pass();
        JCheckBox showPassword = option("Show password");
        JCheckBox remember = option("Remember Me");
        JLabel formHint = UIUtils.hint("Enter the same email and password you used during signup.");
        formHint.setAlignmentX(Component.CENTER_ALIGNMENT);

        showPassword.addActionListener(e -> password.setEchoChar(showPassword.isSelected() ? 0 : '*'));

        JButton forgot = UIUtils.link("Forgot Password?");
        forgot.addActionListener(e -> UIUtils.alert(root, "Forgot Password", "This is a UI placeholder. Ask your placement coordinator to reset your password.", JOptionPane.INFORMATION_MESSAGE));

        JButton login = UIUtils.primary("Login");
        login.setMaximumSize(new Dimension(440, 46));
        login.addActionListener(e -> {
            try {
                String passwordText = new String(password.getPassword());
                auth.validateLogin(email.getText(), passwordText);
                Optional<User> user = auth.login(email.getText(), passwordText);
                if (user.isPresent()) {
                    app.login(user.get());
                } else {
                    UIUtils.alert(root, "Login Failed", "No matching account was found. Check your email and password.", JOptionPane.WARNING_MESSAGE);
                }
            } catch (ValidationException ex) {
                UIUtils.alert(root, "Check Details", ex.getMessage(), JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                UIUtils.alert(root, "Database Error", friendlyDbMessage(ex), JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton checkDb = UIUtils.link("Check Database");
        checkDb.addActionListener(e -> checkDatabase(root));

        JButton signup = UIUtils.link("Create a new account");
        signup.addActionListener(e -> app.showSignup());

        card.add(UIUtils.title("SG", 28));
        card.add(Box.createVerticalStrut(10));
        card.add(UIUtils.title("Skill Graph", 36));
        card.add(center(UIUtils.text("Placement Preparation System")));
        card.add(Box.createVerticalStrut(18));
        card.add(formHint);
        card.add(Box.createVerticalStrut(16));
        card.add(UIUtils.inputBlock("Email", "Email address", email, "Example: student@example.com"));
        card.add(Box.createVerticalStrut(10));
        card.add(UIUtils.inputBlock("Lock", "Password", password, "Minimum 6 characters"));
        card.add(Box.createVerticalStrut(4));

        JPanel options = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        options.setOpaque(false);
        options.add(remember);
        options.add(showPassword);
        options.add(forgot);
        options.add(checkDb);
        card.add(options);
        card.add(Box.createVerticalStrut(16));
        card.add(login);
        card.add(Box.createVerticalStrut(10));
        card.add(signup);
        root.add(card);
        return root;
    }

    public JPanel signup() {
        JPanel root = UIUtils.gradient();
        JPanel card = UIUtils.card();
        card.setPreferredSize(new Dimension(590, 850));

        JTextField name = UIUtils.field();
        JTextField email = UIUtils.field();
        JPasswordField password = UIUtils.pass();
        JPasswordField confirmPassword = UIUtils.pass();
        JTextField college = UIUtils.field();
        JTextField branch = UIUtils.field();
        JTextField year = UIUtils.field();
        JLabel strength = UIUtils.hint("Password strength: enter at least 6 characters");
        strength.setAlignmentX(Component.LEFT_ALIGNMENT);

        password.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateStrength(password, strength); }
            public void removeUpdate(DocumentEvent e) { updateStrength(password, strength); }
            public void changedUpdate(DocumentEvent e) { updateStrength(password, strength); }
        });

        JCheckBox showPassword = option("Show passwords");
        showPassword.addActionListener(e -> {
            char echo = showPassword.isSelected() ? 0 : '*';
            password.setEchoChar(echo);
            confirmPassword.setEchoChar(echo);
        });

        JButton create = UIUtils.primary("Create Account");
        create.setMaximumSize(new Dimension(440, 46));
        create.addActionListener(e -> {
            try {
                String passwordText = new String(password.getPassword());
                String confirmText = new String(confirmPassword.getPassword());
                if (!passwordText.equals(confirmText)) {
                    throw new ValidationException("Password and confirm password must match.");
                }
                User user = auth.signup(name.getText(), email.getText(), passwordText, college.getText(), branch.getText(), year.getText());
                UIUtils.alert(root, "Welcome", "Account created successfully. You are now logged in.", JOptionPane.INFORMATION_MESSAGE);
                app.login(user);
            } catch (ValidationException ex) {
                UIUtils.alert(root, "Check Details", ex.getMessage(), JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                UIUtils.alert(root, "Database Error", friendlyDbMessage(ex), JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton checkDb = UIUtils.link("Check Database");
        checkDb.addActionListener(e -> checkDatabase(root));

        JButton login = UIUtils.link("Already have an account? Login");
        login.addActionListener(e -> app.showLogin());

        card.add(UIUtils.title("Create Account", 34));
        card.add(center(UIUtils.text("Fill these details exactly as shown below.")));
        card.add(Box.createVerticalStrut(14));
        card.add(UIUtils.inputBlock("User", "Full name", name, "Example: Ananya Sharma"));
        card.add(Box.createVerticalStrut(7));
        card.add(UIUtils.inputBlock("Email", "Email address", email, "Example: student@example.com. Duplicate emails are blocked."));
        card.add(Box.createVerticalStrut(7));
        card.add(UIUtils.inputBlock("Lock", "Password", password, "Use 6+ characters. Add numbers/symbols for a stronger password."));
        card.add(strength);
        card.add(Box.createVerticalStrut(7));
        card.add(UIUtils.inputBlock("Check", "Confirm password", confirmPassword, "Re-enter the same password."));
        card.add(Box.createVerticalStrut(7));
        card.add(UIUtils.inputBlock("College", "College", college, "Example: ABC Institute of Technology"));
        card.add(Box.createVerticalStrut(7));
        card.add(UIUtils.inputBlock("Branch", "Branch", branch, "Example: Computer Science"));
        card.add(Box.createVerticalStrut(7));
        card.add(UIUtils.inputBlock("Year", "Graduation year", year, "Example: 2027. Must be numeric."));
        card.add(Box.createVerticalStrut(4));
        card.add(showPassword);
        card.add(Box.createVerticalStrut(10));
        card.add(create);
        card.add(Box.createVerticalStrut(6));
        card.add(checkDb);
        card.add(login);
        root.add(wrapScrollable(card));
        return root;
    }

    private JScrollPane wrapScrollable(JPanel card) {
        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(650, 760));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private void checkDatabase(Component root) {
        try {
            if (DBConnection.testConnection()) {
                UIUtils.alert(root, "Database Connected", "Connection successful.\n" + DBConnection.checkRequiredTables() + "\n\n" + DBConnection.getConfigSummary(), JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            UIUtils.alert(root, "Database Not Connected", friendlyDbMessage(ex) + "\n\nCurrent config:\n" + DBConnection.getConfigSummary(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private JCheckBox option(String text) {
        JCheckBox box = new JCheckBox(text);
        box.setOpaque(false);
        box.setForeground(Color.WHITE);
        box.setFocusPainted(false);
        return box;
    }

    private JComponent center(JComponent component) {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        return component;
    }

    private void updateStrength(JPasswordField password, JLabel label) {
        String value = new String(password.getPassword());
        int score = 0;
        if (value.length() >= 6) score++;
        if (value.length() >= 10) score++;
        if (value.matches(".*[A-Z].*")) score++;
        if (value.matches(".*[0-9].*")) score++;
        if (value.matches(".*[^A-Za-z0-9].*")) score++;
        if (value.isEmpty()) {
            label.setForeground(UIUtils.MUTED);
            label.setText("Password strength: enter at least 6 characters");
        } else if (score <= 2) {
            label.setForeground(UIUtils.DANGER);
            label.setText("Password strength: weak. Add uppercase letters, numbers, or symbols.");
        } else if (score <= 4) {
            label.setForeground(UIUtils.MUTED);
            label.setText("Password strength: good.");
        } else {
            label.setForeground(UIUtils.SUCCESS);
            label.setText("Password strength: strong.");
        }
    }

    private String friendlyDbMessage(SQLException ex) {
        String details = ex.getMessage();
        if (details != null && details.contains("ORA-00942")) {
            return "Oracle is connected, but the required table does not exist for this SQL Plus user.\n\nFix:\n1. Open SQL Plus using the same username/password from db.properties.\n2. Run this script:\n   @C:\\Users\\dmadh\\SkillGraph\\oracle_schema.sql\n\nOr change db.properties to the Oracle user that already owns the tables.\n\nDetails: " + details;
        }
        return "Could not connect to Oracle / SQL Plus or save the record.\n\nCheck:\n1. Oracle database service is running.\n2. Your SQL Plus username owns the required tables.\n3. db.properties has the correct Oracle URL, username, and password.\n\nDetails: " + details;
    }
}
