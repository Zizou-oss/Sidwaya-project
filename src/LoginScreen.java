import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Écran de connexion modernisé pour Les Editions Sidwaya
 * @author Zizou
 */
public class LoginScreen extends JFrame {
    private static final Logger logger = Logger.getLogger(LoginScreen.class.getName());

    // Green color palette
    private static final Color PRIMARY_COLOR = new Color(46, 125, 50); // Green primary
    private static final Color SECONDARY_COLOR = new Color(67, 160, 71); // Lighter green
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250); // Light background
    private static final Color TEXT_COLOR = new Color(18, 18, 18); // Dark text
    private static final Color BORDER_COLOR = new Color(189, 189, 189); // Subtle border
    private static final Color ERROR_COLOR = new Color(239, 83, 80); // Red for errors

    // Modern fonts
    private static final Font HEADER_FONT = new Font("Roboto", Font.BOLD, 26);
    private static final Font BODY_FONT = new Font("Roboto", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Roboto", Font.BOLD, 14);

    private Connection connection;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;

    public LoginScreen() {
        setTitle("Connexion - Les Editions Sidwaya");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            initializeDatabase();
            initializeUI();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'initialisation", e);
            showErrorMessage("Erreur d'initialisation: " + e.getMessage());
            System.exit(1);
        }
    }

    private void initializeDatabase() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbDir = "db";
            File dir = new File(dbDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String dbPath = dbDir + File.separator + "sidwaya_management.db";
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createUsersTable();
            logger.info("Base de données initialisée avec succès");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Driver SQLite non trouvé", e);
            throw new SQLException("Driver SQLite non trouvé", e);
        }
    }

    private void createUsersTable() throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO users (username, password) VALUES ('admin', 'admin')");
            }
        }
    }

    private void initializeUI() {
        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 242, 245), 0, getHeight(), new Color(225, 235, 245));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Card panel for login form
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200, 50)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        cardPanel.setPreferredSize(new Dimension(360, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo
        JLabel logoLabel = new JLabel("📚");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        logoLabel.setForeground(PRIMARY_COLOR);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(logoLabel, gbc);

        // Title
        JLabel titleLabel = new JLabel("Les Editions Sidwaya");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridy = 1;
        cardPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Connexion au système");
        subtitleLabel.setFont(BODY_FONT);
        subtitleLabel.setForeground(new Color(120, 120, 120));
        gbc.gridy = 2;
        cardPanel.add(subtitleLabel, gbc);

        // Username field
        JLabel usernameLabel = new JLabel("Nom d'utilisateur:");
        usernameLabel.setFont(BODY_FONT);
        usernameLabel.setForeground(TEXT_COLOR);
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        cardPanel.add(usernameLabel, gbc);

        usernameField = createModernTextField("Nom d'utilisateur");
        usernameField.setPreferredSize(new Dimension(300, 40));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        cardPanel.add(usernameField, gbc);

        // Password field
        JLabel passwordLabel = new JLabel("Mot de passe:");
        passwordLabel.setFont(BODY_FONT);
        passwordLabel.setForeground(TEXT_COLOR);
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        cardPanel.add(passwordLabel, gbc);

        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        passwordPanel.setOpaque(false);
        passwordField = createModernPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        showPasswordCheckBox = new JCheckBox("Afficher");
        showPasswordCheckBox.setFont(BODY_FONT);
        showPasswordCheckBox.setForeground(TEXT_COLOR);
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.addActionListener(e -> {
            passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '•');
        });
        passwordPanel.add(showPasswordCheckBox, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        cardPanel.add(passwordPanel, gbc);

        // Login button
        JButton loginButton = createPrimaryButton("Se connecter");
        loginButton.addActionListener(e -> {
            if (authenticate()) {
                dispose();
                new main(connection).setVisible(true);
            } else {
                showErrorMessage("Nom d'utilisateur ou mot de passe incorrect");
            }
        });
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(loginButton, gbc);

        // Add card panel to main panel
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(cardPanel, gbc);

        add(mainPanel);
        setVisible(true);
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField(30) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(isFocusOwner() ? PRIMARY_COLOR : BORDER_COLOR);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(BODY_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(Color.WHITE);
        field.setBorder(new EmptyBorder(10, 15, 10, 15));
        field.setOpaque(false);
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { field.repaint(); }
            @Override
            public void focusLost(FocusEvent e) { field.repaint(); }
        });
        return field;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField(30) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(isFocusOwner() ? PRIMARY_COLOR : BORDER_COLOR);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(BODY_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(Color.WHITE);
        field.setBorder(new EmptyBorder(10, 15, 10, 15));
        field.setOpaque(false);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { field.repaint(); }
            @Override
            public void focusLost(FocusEvent e) { field.repaint(); }
        });
        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 30)); // Green-tinted hover effect
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(160, 40));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private boolean authenticate() {
        try {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, usernameField.getText());
                pstmt.setString(2, new String(passwordField.getPassword()));
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'authentification", e);
            showErrorMessage("Erreur d'authentification: " + e.getMessage());
            return false;
        }
    }

    private void showErrorMessage(String message) {
        JDialog errorDialog = new JDialog(this, "Erreur", true);
        errorDialog.setSize(320, 180);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 235, 238));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(ERROR_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel iconLabel = new JLabel("❌", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(ERROR_COLOR);

        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        messageLabel.setFont(BODY_FONT);
        messageLabel.setForeground(TEXT_COLOR);

        JButton closeButton = createSecondaryButton("Fermer");
        closeButton.addActionListener(e -> errorDialog.dispose());

        panel.add(iconLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        errorDialog.add(panel);
        errorDialog.setVisible(true);
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(46, 125, 50, 20)); // Green-tinted hover effect
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setPreferredSize(new Dimension(100, 35));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'initialisation du Look and Feel", e);
        }
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}