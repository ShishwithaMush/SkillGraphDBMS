package com.dbms.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public final class UIUtils {
    public static final Color BLUE = new Color(30, 140, 255);
    public static final Color PINK = new Color(255, 91, 200);
    public static final Color PURPLE = new Color(91, 46, 230);
    public static final Color TEXT = new Color(245, 247, 255);
    public static final Color MUTED = new Color(226, 232, 240);
    public static final Color DANGER = new Color(255, 205, 220);
    public static final Color SUCCESS = new Color(190, 255, 220);

    private UIUtils() {}

    public static JPanel gradient() {
        return new GradientPanel();
    }

    public static JLabel title(String text, int size) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, size));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    public static JLabel text(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    public static JLabel hint(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    public static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    public static JTextField field() {
        JTextField field = new JTextField();
        styleInput(field);
        return field;
    }

    public static JTextField field(String tooltip) {
        JTextField field = field();
        field.setToolTipText(tooltip);
        return field;
    }

    public static JPasswordField pass() {
        JPasswordField field = new JPasswordField();
        styleInput(field);
        field.setEchoChar('*');
        return field;
    }

    public static JPasswordField pass(String tooltip) {
        JPasswordField field = pass();
        field.setToolTipText(tooltip);
        return field;
    }

    public static void styleInput(JTextField field) {
        field.setMaximumSize(new Dimension(440, 36));
        field.setPreferredSize(new Dimension(440, 36));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new EmptyBorder(8, 14, 8, 14));
        field.setBackground(new Color(255, 255, 255, 235));
        field.setForeground(new Color(17, 24, 39));
        field.setCaretColor(new Color(17, 24, 39));
    }

    public static JPanel inputBlock(String icon, String labelText, JComponent input, String hintText) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setMaximumSize(new Dimension(460, hintText == null || hintText.isEmpty() ? 58 : 76));

        JLabel label = label(icon + "  " + labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.add(label);
        block.add(Box.createVerticalStrut(5));
        block.add(input);
        if (hintText != null && !hintText.isEmpty()) {
            JLabel hint = hint(hintText);
            hint.setAlignmentX(Component.LEFT_ALIGNMENT);
            block.add(Box.createVerticalStrut(4));
            block.add(hint);
        }
        return block;
    }

    public static JButton primary(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setBackground(PINK);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton link(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void alert(Component parent, String title, String message, int type) {
        JOptionPane.showMessageDialog(parent, message, title, type);
    }

    public static JPanel panel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    public static JPanel card() {
        JPanel panel = new RoundPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));
        return panel;
    }

    public static class GradientPanel extends JPanel {
        public GradientPanel() {
            setLayout(new GridBagLayout());
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, PURPLE, getWidth(), getHeight(), PINK));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setPaint(new GradientPaint(getWidth(), 0, BLUE, 0, getHeight(), new Color(255, 91, 200, 120)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }

        public boolean isOpaque() {
            return false;
        }
    }

    public static class RoundPanel extends JPanel {
        public RoundPanel() {
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 58));
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 28, 28));
            g2.setColor(new Color(255, 255, 255, 105));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 28, 28));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
