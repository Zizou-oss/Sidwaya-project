
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Système de Gestion Modernisé pour Les Editions Sidwaya
 * Gestion des Stagiaires, Décoration & Formation
 * @author Zizou - Version Moderne
 */
public class main extends JFrame {
    private static final Logger logger = Logger.getLogger(main.class.getName());
    
    // Couleurs modernes
    private static final Color PRIMARY_COLOR = new Color(46, 125, 50);
    private static final Color SECONDARY_COLOR = new Color(67, 160, 71);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color WARNING_COLOR = new Color(255, 152, 0);
    private static final Color ERROR_COLOR = new Color(244, 67, 54);
    private static final Color INFO_COLOR = new Color(33, 150, 243);
    
    // Polices modernes
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    // Variables pour le module actuel
    private String currentModule = "Stagiaires";
    private DefaultTableModel currentTableModel;
    
    // Composants d'interface
    private JLabel titleLabel, subtitleLabel, listTitleLabel;
    private JButton[] navigationButtons;
    private JLabel[] statValues;
    private JTextField searchField;
    private JComboBox<String> departmentCombo, statusCombo;
    private JButton addButton;
    private JTable dataTable;
    
    // Base de données
    private Connection connection;
    
    public main() {
        initializeDatabase();
        initializeModernUI();
        setupEventHandlers();
        updateModuleContent("Stagiaires");
    }

    private void initializeDatabase() {
        try {
            // Charger le driver SQLite
            Class.forName("org.sqlite.JDBC");

            // Dossier "db" dans ton projet
            String dbDir = "db";
            File dir = new File(dbDir);
            if (!dir.exists()) {
                dir.mkdirs(); // crée le dossier s'il n'existe pas
            }

            // Chemin vers la base
            String dbPath = dbDir + File.separator + "sidwaya_management.db";

            // Connexion SQLite
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            // Afficher les infos dans la console
            System.out.println("Dossier projet : " + new File(".").getAbsolutePath());
            System.out.println("Fichier DB utilisé : " + new File(dbPath).getAbsolutePath());

            // Création des tables
            createTables();

            logger.info("Base de données initialisée avec succès");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'initialisation de la base : " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    private void createTables() throws SQLException {
        String createStagiairesTable = "CREATE TABLE IF NOT EXISTS stagiaires (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nom TEXT NOT NULL, " +
            "theme TEXT, " +
            "departement TEXT, " +
            "fonction TEXT, " +
            "date_debut TEXT, " +
            "duree TEXT, " +
            "statut TEXT)";
        
        String createDecorationTable = "CREATE TABLE IF NOT EXISTS decoration (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nom_projet TEXT NOT NULL, " +
            "client TEXT, " +
            "type TEXT, " +
            "budget TEXT, " +
            "date_debut TEXT, " +
            "date_fin TEXT, " +
            "statut TEXT)";
        
        String createFormationTable = "CREATE TABLE IF NOT EXISTS formation (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nom_formation TEXT NOT NULL, " +
            "formateur TEXT, " +
            "domaine TEXT, " +
            "participants INTEGER, " +
            "date_debut TEXT, " +
            "duree TEXT, " +
            "statut TEXT)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createStagiairesTable);
            stmt.execute(createDecorationTable);
            stmt.execute(createFormationTable);
        }
        
        // Insérer des données d'exemple si les tables sont vides
        insertSampleData();
    }
    
    private void insertSampleData() throws SQLException {
        // Vérifier si la table stagiaires est vide
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM stagiaires")) {
            
            
        }
    }

    private void initializeModernUI() {
        setTitle("Système de Gestion - Les Editions Sidwaya");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(BACKGROUND_COLOR);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        createHeaderSection(mainPanel);
        createNavigationSection(mainPanel);
        createContentSection(mainPanel);
        
        add(mainScrollPane);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void createHeaderSection(JPanel mainPanel) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        JPanel logoTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        logoTitlePanel.setBackground(PRIMARY_COLOR);
        
        JLabel logoLabel = createModernLogoLabel();
        logoTitlePanel.add(logoLabel);
        
        JPanel titleInfo = new JPanel(new GridBagLayout());
        titleInfo.setBackground(PRIMARY_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        
        titleLabel = new JLabel("Les Editions Sidwaya");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0;
        titleInfo.add(titleLabel, gbc);
        
        JLabel systemLabel = new JLabel("Système de Gestion");
        systemLabel.setFont(SUBTITLE_FONT);
        systemLabel.setForeground(new Color(220, 255, 220));
        gbc.gridy = 1;
        titleInfo.add(systemLabel, gbc);
        
        logoTitlePanel.add(titleInfo);
        
        subtitleLabel = new JLabel("Gestion des Stagiaires, Décoration & Formation");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(new Color(200, 255, 200));
        
        headerPanel.add(logoTitlePanel, BorderLayout.WEST);
        headerPanel.add(subtitleLabel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }
    
    private JLabel createModernLogoLabel() {
        JLabel logo = new JLabel("📚") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(5, 5, 50, 50);
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(5, 5, 50, 50);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        logo.setForeground(Color.WHITE);
        logo.setPreferredSize(new Dimension(60, 60));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        return logo;
    }

    private void createNavigationSection(JPanel mainPanel) {
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        navigationPanel.setBackground(BACKGROUND_COLOR);
        navigationPanel.setBorder(new EmptyBorder(10, 40, 10, 40));
        
        String[] modules = {"Stagiaires", "Décoration", "Formation"};
        navigationButtons = new JButton[modules.length];
        
        for (int i = 0; i < modules.length; i++) {
            navigationButtons[i] = createModernNavButton(modules[i]);
            final int index = i;
            navigationButtons[i].addActionListener(e -> updateModuleContent(modules[index]));
            navigationPanel.add(navigationButtons[i]);
        }
        
        mainPanel.add(navigationPanel, BorderLayout.CENTER);
    }
    
    private JButton createModernNavButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getBackground() == PRIMARY_COLOR) {
                    GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                } else {
                    g2.setColor(CARD_COLOR);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(new Color(0, 0, 0, 10));
                    g2.fillRoundRect(2, 2, getWidth(), getHeight(), 12, 12);
                }
                
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setPreferredSize(new Dimension(200, 45));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getBackground() != PRIMARY_COLOR) {
                    button.setBackground(new Color(245, 245, 245));
                }
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.getBackground() != PRIMARY_COLOR) {
                    button.setBackground(CARD_COLOR);
                }
                button.repaint();
            }
        });
        
        return button;
    }

    private void createContentSection(JPanel mainPanel) {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 40, 40, 40));
        
        JPanel statisticsPanel = createStatisticsSection();
        JPanel filtersPanel = createFiltersSection();
        JPanel tablePanel = createTableSection();
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(statisticsPanel, BorderLayout.NORTH);
        topPanel.add(filtersPanel, BorderLayout.CENTER);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatisticsSection() {
        JPanel statisticsPanel = new JPanel(new GridLayout(1, 5, 20, 0));
        statisticsPanel.setBackground(BACKGROUND_COLOR);
        statisticsPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        String[] statTitles = {"Total", "En cours", "À venir", "Planifiés", "Terminés"};
        Color[] statColors = {PRIMARY_COLOR, INFO_COLOR, WARNING_COLOR, SUCCESS_COLOR, new Color(158, 158, 158)};
        
        statValues = new JLabel[5];
        
        for (int i = 0; i < 5; i++) {
            statisticsPanel.add(createModernStatCard(statTitles[i], "0", statColors[i]));
        }
        
        return statisticsPanel;
    }
    
    private JPanel createModernStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(4, 4, getWidth()-4, getHeight()-4, 15, 15);
                g2.setColor(CARD_COLOR);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 15, 15);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth()-4, 5, 15, 15);
                g2.dispose();
            }
        };
        
        card.setPreferredSize(new Dimension(200, 100));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(BODY_FONT);
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);
        
        int index = java.util.Arrays.asList("Total", "En cours", "À venir", "Planifiés", "Terminés").indexOf(title);
        if (index >= 0 && index < 5) {
            statValues[index] = valueLabel;
        }
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    private JPanel createFiltersSection() {
        JPanel filtersPanel = new JPanel(new BorderLayout());
        filtersPanel.setBackground(CARD_COLOR);
        filtersPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        filtersPanel.setOpaque(true);
        
        filtersPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12, BORDER_COLOR),
            new EmptyBorder(25, 30, 25, 30)
        ));
        
        JLabel filtersTitle = new JLabel("🔍 Recherches et Filtres");
        filtersTitle.setFont(TITLE_FONT);
        filtersTitle.setForeground(TEXT_COLOR);
        filtersTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        controlsPanel.setBackground(CARD_COLOR);
        
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(CARD_COLOR);
        
        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setFont(SUBTITLE_FONT);
        searchLabel.setForeground(TEXT_COLOR);
        
        searchField = createModernTextField("Tapez pour rechercher...");
        searchField.setPreferredSize(new Dimension(280, 40));
        
        searchPanel.add(searchLabel, BorderLayout.NORTH);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        JPanel deptPanel = new JPanel(new BorderLayout(10, 0));
        deptPanel.setBackground(CARD_COLOR);
        
        JLabel deptLabel = new JLabel("Département:");
        deptLabel.setFont(SUBTITLE_FONT);
        deptLabel.setForeground(TEXT_COLOR);
        
        departmentCombo = createModernComboBox(new String[]{"Tous", "RH", "IT", "Finance", "Marketing", "Production"});
        
        deptPanel.add(deptLabel, BorderLayout.NORTH);
        deptPanel.add(departmentCombo, BorderLayout.CENTER);
        
        JPanel statusPanel = new JPanel(new BorderLayout(10, 0));
        statusPanel.setBackground(CARD_COLOR);
        
        JLabel statusLabel = new JLabel("Statut:");
        statusLabel.setFont(SUBTITLE_FONT);
        statusLabel.setForeground(TEXT_COLOR);
        
        statusCombo = createModernComboBox(new String[]{"Tous", "En cours", "Terminé", "Suspendu", "À venir"});
        
        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(statusCombo, BorderLayout.CENTER);
        
        controlsPanel.add(searchPanel);
        controlsPanel.add(deptPanel);
        controlsPanel.add(statusPanel);
        
        filtersPanel.add(filtersTitle, BorderLayout.NORTH);
        filtersPanel.add(controlsPanel, BorderLayout.CENTER);
        
        return filtersPanel;
    }
    
    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                if (isFocusOwner()) {
                    g2.setColor(PRIMARY_COLOR);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(BORDER_COLOR);
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
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
        
        return field;
    }
    
    private JComboBox<String> createModernComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<String>(items) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        combo.setFont(BODY_FONT);
        combo.setForeground(TEXT_COLOR);
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(180, 40));
        combo.setBorder(new EmptyBorder(5, 15, 5, 15));
        combo.setOpaque(false);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return combo;
    }

    private JPanel createTableSection() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12, BORDER_COLOR),
            new EmptyBorder(25, 30, 25, 30)
        ));
        
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(CARD_COLOR);
        tableHeader.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        listTitleLabel = new JLabel("📋 Liste des Stagiaires (0)");
        listTitleLabel.setFont(TITLE_FONT);
        listTitleLabel.setForeground(TEXT_COLOR);
        
        addButton = createModernActionButton("+ Ajouter Stagiaire");
        
        tableHeader.add(listTitleLabel, BorderLayout.WEST);
        tableHeader.add(addButton, BorderLayout.EAST);
        
        createModernTable();
        JScrollPane tableScrollPane = new JScrollPane(dataTable);
        tableScrollPane.setBorder(null);
        tableScrollPane.setBackground(Color.WHITE);
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        tableScrollPane.setPreferredSize(new Dimension(0, 400));
        
        tablePanel.add(tableHeader, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JButton createModernActionButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void createModernTable() {
        dataTable = new JTable() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        
        dataTable.setFont(BODY_FONT);
        dataTable.setRowHeight(50);
        dataTable.setShowGrid(false);
        dataTable.setIntercellSpacing(new Dimension(0, 1));
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.setBackground(Color.WHITE);
        dataTable.setSelectionBackground(new Color(232, 245, 233));
        dataTable.setSelectionForeground(TEXT_COLOR);
        
        JTableHeader header = dataTable.getTableHeader();
        header.setFont(SUBTITLE_FONT);
        header.setForeground(TEXT_COLOR);
        header.setBackground(new Color(248, 249, 250));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(0, 45));
        
        dataTable.setDefaultRenderer(Object.class, new ModernCellRenderer());
    }
    
    class ModernCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            setFont(BODY_FONT);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            
            if (!isSelected) {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 252, 252));
            }
            
            if (column == 6 && value != null) {
                String status = value.toString().toLowerCase();
                switch(status) {
                    case "en cours": setForeground(INFO_COLOR); break;
                    case "à venir": setForeground(WARNING_COLOR); break;
                    case "planifié": setForeground(SUCCESS_COLOR); break;
                    case "terminé": setForeground(new Color(158, 158, 158)); break;
                    default: setForeground(TEXT_COLOR);
                }
                setText("● " + value.toString());
            } else if (column == 7) {
                setForeground(PRIMARY_COLOR);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else {
                setForeground(TEXT_COLOR);
            }
            
            return component;
        }
    }
    
    class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        private Color color;
        
        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(1, 1, 1, 1); }
        
        @Override
        public boolean isBorderOpaque() { return false; }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
    }

    private void setupEventHandlers() {
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        departmentCombo.addActionListener(e -> applyFilters());
        statusCombo.addActionListener(e -> applyFilters());
        
        addButton.addActionListener(e -> showModernAddDialog());
    }
    
    private void showModernAddDialog() {
        String itemType = getSingularForm(currentModule);
        
        JDialog dialog = new JDialog(this, "➕ Ajouter " + itemType, true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 30, 20, 30));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("Nouveau " + itemType);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel descLabel = new JLabel("Remplissez les informations ci-dessous");
        descLabel.setFont(BODY_FONT);
        descLabel.setForeground(new Color(120, 120, 120));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12, BORDER_COLOR),
            new EmptyBorder(30, 25, 30, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        switch(currentModule) {
            case "Stagiaires": addModernStagiaireFields(formPanel, gbc); break;
            case "Décoration": addModernDecorationFields(formPanel, gbc); break;
            case "Formation": addModernFormationFields(formPanel, gbc); break;
        }
        
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.setBackground(BACKGROUND_COLOR);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        JButton cancelButton = createSecondaryButton("Annuler");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = createPrimaryButton("💾 Enregistrer");
        saveButton.addActionListener(e -> {
            if (saveNewItem(dialog)) {
                showSuccessMessage(dialog, itemType + " ajouté avec succès!");
                dialog.dispose();
                updateModuleContent(currentModule);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private boolean saveNewItem(JDialog dialog) {
        try {
            String tableName = getTableName(currentModule);
            String sql = getInsertSQL(currentModule);
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                switch(currentModule) {
                    case "Stagiaires":
                        pstmt.setString(1, getFieldValue(dialog, 0));
                        pstmt.setString(2, getFieldValue(dialog, 1));
                        pstmt.setString(3, getComboValue(dialog, 2));
                        pstmt.setString(4, getFieldValue(dialog, 3));
                        pstmt.setString(5, getFieldValue(dialog, 4));
                        pstmt.setString(6, getFieldValue(dialog, 5));
                        pstmt.setString(7, "En cours");
                        break;
                    case "Décoration":
                        pstmt.setString(1, getFieldValue(dialog, 0));
                        pstmt.setString(2, getFieldValue(dialog, 1));
                        pstmt.setString(3, getComboValue(dialog, 2));
                        pstmt.setString(4, getFieldValue(dialog, 3));
                        pstmt.setString(5, getFieldValue(dialog, 4));
                        pstmt.setString(6, getFieldValue(dialog, 5));
                        pstmt.setString(7, "Planifié");
                        break;
                    case "Formation":
                        pstmt.setString(1, getFieldValue(dialog, 0));
                        pstmt.setString(2, getFieldValue(dialog, 1));
                        pstmt.setString(3, getComboValue(dialog, 2));
                        pstmt.setInt(4, Integer.parseInt(getFieldValue(dialog, 3)));
                        pstmt.setString(5, getFieldValue(dialog, 4));
                        pstmt.setString(6, getFieldValue(dialog, 5));
                        pstmt.setString(7, "Planifié");
                        break;
                }
                
                pstmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ajout", e);
            JOptionPane.showMessageDialog(dialog, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private String getTableName(String module) {
        switch(module) {
            case "Stagiaires": return "stagiaires";
            case "Décoration": return "decoration";
            case "Formation": return "formation";
            default: return "";
        }
    }
    
    private String getInsertSQL(String module) {
        switch(module) {
            case "Stagiaires": 
                return "INSERT INTO stagiaires (nom, theme, departement, fonction, date_debut, duree, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";
            case "Décoration": 
                return "INSERT INTO decoration (nom_projet, client, type, budget, date_debut, date_fin, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";
            case "Formation": 
                return "INSERT INTO formation (nom_formation, formateur, domaine, participants, date_debut, duree, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";
            default: return "";
        }
    }
    
    private String getFieldValue(JDialog dialog, int index) {
        // Accéder correctement aux composants du dialogue
        JPanel mainPanel = (JPanel) dialog.getContentPane().getComponent(0);
        JScrollPane formScrollPane = (JScrollPane) mainPanel.getComponent(1);
        JPanel formPanel = (JPanel) formScrollPane.getViewport().getView();
        
        // Le formPanel contient directement les champs dans une grille
        Component fieldComponent = formPanel.getComponent(index * 2 + 1);
        
        if (fieldComponent instanceof JTextField) {
            return ((JTextField) fieldComponent).getText();
        } else if (fieldComponent instanceof JComboBox) {
            return (String) ((JComboBox<?>) fieldComponent).getSelectedItem();
        }
        
        return "";
    }

    private String getComboValue(JDialog dialog, int index) {
        // Même logique d'accès que getFieldValue
        JPanel mainPanel = (JPanel) dialog.getContentPane().getComponent(0);
        JScrollPane formScrollPane = (JScrollPane) mainPanel.getComponent(1);
        JPanel formPanel = (JPanel) formScrollPane.getViewport().getView();
        
        Component comboComponent = formPanel.getComponent(index * 2 + 1);
        
        if (comboComponent instanceof JComboBox) {
            return (String) ((JComboBox<?>) comboComponent).getSelectedItem();
        }
        
        return "";
    }
    
    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 249, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                
                if (getModel().isRollover()) {
                    g2.setColor(new Color(0, 0, 0, 10));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void addModernStagiaireFields(JPanel panel, GridBagConstraints gbc) {
        String[] labels = {"👤 Nom & Prénom:", "📚 Thème:", "🏢 Département:", "💼 Fonction:", "📅 Date début:", "⏱ Durée (mois):"};
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.weightx = 0.3;
            
            JLabel label = new JLabel(labels[i]);
            label.setFont(SUBTITLE_FONT);
            label.setForeground(TEXT_COLOR);
            panel.add(label, gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            
            if (i == 2) {
                JComboBox<String> combo = createModernComboBox(new String[]{"IT", "Marketing", "Finance", "RH", "Production"});
                combo.setPreferredSize(new Dimension(300, 45));
                panel.add(combo, gbc);
            } else {
                JTextField field = createModernTextField("Saisissez " + labels[i].replaceAll("[^a-zA-Z\\s]", "").toLowerCase());
                field.setPreferredSize(new Dimension(300, 45));
                panel.add(field, gbc);
            }
        }
    }

    private void addModernDecorationFields(JPanel panel, GridBagConstraints gbc) {
        String[] labels = {"🎨 Nom du Projet:", "👥 Client:", "🎭 Type:", "💰 Budget (FCFA):", "📅 Date début:", "📅 Date fin:"};
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.weightx = 0.3;
            
            JLabel label = new JLabel(labels[i]);
            label.setFont(SUBTITLE_FONT);
            label.setForeground(TEXT_COLOR);
            panel.add(label, gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            
            if (i == 2) {
                JComboBox<String> combo = createModernComboBox(new String[]{"Mariage", "Corporate", "Anniversaire", "Conférence", "Autre"});
                combo.setPreferredSize(new Dimension(300, 45));
                panel.add(combo, gbc);
            } else {
                JTextField field = createModernTextField("Saisissez " + labels[i].replaceAll("[^a-zA-Z\\s]", "").toLowerCase());
                field.setPreferredSize(new Dimension(300, 45));
                panel.add(field, gbc);
            }
        }
    }

    private void addModernFormationFields(JPanel panel, GridBagConstraints gbc) {
        String[] labels = {"📖 Nom Formation:", "👨‍🏫 Formateur:", "🎯 Domaine:", "👥 Nb Participants:", "📅 Date début:", "⏱ Durée:"};
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.weightx = 0.3;
            
            JLabel label = new JLabel(labels[i]);
            label.setFont(SUBTITLE_FONT);
            label.setForeground(TEXT_COLOR);
            panel.add(label, gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            
            if (i == 2) {
                JComboBox<String> combo = createModernComboBox(new String[]{"Programmation", "Marketing", "Management", "Finance", "Design"});
                combo.setPreferredSize(new Dimension(300, 45));
                panel.add(combo, gbc);
            } else {
                JTextField field = createModernTextField("Saisissez " + labels[i].replaceAll("[^a-zA-Z\\s]", "").toLowerCase());
                field.setPreferredSize(new Dimension(300, 45));
                panel.add(field, gbc);
            }
        }
    }
    
    private void showSuccessMessage(JDialog parent, String message) {
        JDialog successDialog = new JDialog(parent, "Succès", true);
        successDialog.setSize(300, 150);
        successDialog.setLocationRelativeTo(parent);
        successDialog.setUndecorated(true);
        
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, SUCCESS_COLOR, 0, getHeight(), new Color(129, 199, 132));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel iconLabel = new JLabel("✅", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        iconLabel.setForeground(Color.WHITE);
        
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(SUBTITLE_FONT);
        messageLabel.setForeground(Color.WHITE);
        
        panel.add(iconLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        
        successDialog.add(panel);
        
        Timer timer = new Timer(2000, e -> successDialog.dispose());
        timer.setRepeats(false);
        timer.start();
        
        successDialog.setVisible(true);
    }

    private void updateModuleContent(String module) {
        currentModule = module;
        updateListTitle(0, getModuleIcon(module));
        addButton.setText("+ Ajouter " + getSingularForm(module));
        updateTableColumns(module);
        loadDataFromDatabase(module);
        updateButtonStyles(module);
        updateStatistics();
    }
    
    private String getModuleIcon(String module) {
        switch(module) {
            case "Stagiaires": return "🎓";
            case "Décoration": return "🎨";
            case "Formation": return "📚";
            default: return "📋";
        }
    }

    private String getSingularForm(String module) {
        switch(module) {
            case "Stagiaires": return "Stagiaire";
            case "Décoration": return "Projet Déco";
            case "Formation": return "Formation";
            default: return "Item";
        }
    }

    private void updateListTitle(int count, String icon) {
        listTitleLabel.setText(icon + " Liste des " + currentModule + " (" + count + ")");
    }
    
    private void updateListTitle(int count) {
        updateListTitle(count, getModuleIcon(currentModule));
    }

    private void updateTableColumns(String module) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        
        String[] columns;
        switch(module) {
            case "Stagiaires":
                columns = new String[]{"Nom & Prénom", "Thème", "Département", "Fonction", "Date début", "Durée", "Statut", "Actions"};
                break;
            case "Décoration":
                columns = new String[]{"Nom Projet", "Client", "Type Déco", "Budget", "Date Début", "Date Fin", "Statut", "Actions"};
                break;
            case "Formation":
                columns = new String[]{"Nom Formation", "Formateur", "Domaine", "Participants", "Date Début", "Durée", "Statut", "Actions"};
                break;
            default:
                columns = new String[]{"Col1", "Col2", "Col3", "Col4", "Col5", "Col6", "Col7", "Col8"};
        }
        
        model.setColumnIdentifiers(columns);
        dataTable.setModel(model);
        currentTableModel = model;
    }

    private void loadDataFromDatabase(String module) {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.setRowCount(0); // Clear existing data
        
        try {
            String tableName = getTableName(module);
            String sql = "SELECT * FROM " + tableName;
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    Object[] row = new Object[8];
                    
                    switch(module) {
                        case "Stagiaires":
                            row[0] = rs.getString("nom");
                            row[1] = rs.getString("theme");
                            row[2] = rs.getString("departement");
                            row[3] = rs.getString("fonction");
                            row[4] = rs.getString("date_debut");
                            row[5] = rs.getString("duree");
                            row[6] = rs.getString("statut");
                            break;
                        case "Décoration":
                            row[0] = rs.getString("nom_projet");
                            row[1] = rs.getString("client");
                            row[2] = rs.getString("type");
                            row[3] = rs.getString("budget");
                            row[4] = rs.getString("date_debut");
                            row[5] = rs.getString("date_fin");
                            row[6] = rs.getString("statut");
                            break;
                        case "Formation":
                            row[0] = rs.getString("nom_formation");
                            row[1] = rs.getString("formateur");
                            row[2] = rs.getString("domaine");
                            row[3] = rs.getInt("participants");
                            row[4] = rs.getString("date_debut");
                            row[5] = rs.getString("duree");
                            row[6] = rs.getString("statut");
                            break;
                    }
                    
                    row[7] = "✏ Modifier | 🗑 Supprimer";
                    model.addRow(row);
                }
            }
            
            updateListTitle(model.getRowCount());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement des données", e);
            JOptionPane.showMessageDialog(this, "Erreur de chargement: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateButtonStyles(String activeModule) {
        for (JButton button : navigationButtons) {
            button.setBackground(CARD_COLOR);
            button.setForeground(TEXT_COLOR);
            button.repaint();
        }
        
        for (int i = 0; i < navigationButtons.length; i++) {
            if (navigationButtons[i].getText().equals(activeModule)) {
                navigationButtons[i].setBackground(PRIMARY_COLOR);
                navigationButtons[i].setForeground(Color.WHITE);
                navigationButtons[i].repaint();
                break;
            }
        }
    }

    private void updateStatistics() {
        try {
            String tableName = getTableName(currentModule);
            String sql = "SELECT statut, COUNT(*) as count FROM " + tableName + " GROUP BY statut";
            
            int total = 0;
            int enCours = 0, aVenir = 0, planifie = 0, termine = 0;
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
                if (rs.next()) total = rs.getInt(1);
            }
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    String statut = rs.getString("statut").toLowerCase();
                    int count = rs.getInt("count");
                    
                    switch(statut) {
                        case "en cours": enCours = count; break;
                        case "à venir": aVenir = count; break;
                        case "planifié": planifie = count; break;
                        case "terminé": termine = count; break;
                    }
                }
            }
            
            statValues[0].setText(String.valueOf(total));
            statValues[1].setText(String.valueOf(enCours));
            statValues[2].setText(String.valueOf(aVenir));
            statValues[3].setText(String.valueOf(planifie));
            statValues[4].setText(String.valueOf(termine));
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour des statistiques", e);
        }
    }

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        if (searchText.isEmpty()) {
            loadDataFromDatabase(currentModule);
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.setRowCount(0);
        
        try {
            String tableName = getTableName(currentModule);
            String sql = "SELECT * FROM " + tableName + " WHERE ";
            
            // Construire la requête selon le module
            switch(currentModule) {
                case "Stagiaires":
                    sql += "LOWER(nom) LIKE ? OR LOWER(theme) LIKE ? OR LOWER(departement) LIKE ? OR LOWER(fonction) LIKE ?";
                    break;
                case "Décoration":
                    sql += "LOWER(nom_projet) LIKE ? OR LOWER(client) LIKE ? OR LOWER(type) LIKE ? OR LOWER(budget) LIKE ?";
                    break;
                case "Formation":
                    sql += "LOWER(nom_formation) LIKE ? OR LOWER(formateur) LIKE ? OR LOWER(domaine) LIKE ?";
                    break;
            }
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                String searchPattern = "%" + searchText + "%";
                
                switch(currentModule) {
                    case "Stagiaires":
                        pstmt.setString(1, searchPattern);
                        pstmt.setString(2, searchPattern);
                        pstmt.setString(3, searchPattern);
                        pstmt.setString(4, searchPattern);
                        break;
                    case "Décoration":
                        pstmt.setString(1, searchPattern);
                        pstmt.setString(2, searchPattern);
                        pstmt.setString(3, searchPattern);
                        pstmt.setString(4, searchPattern);
                        break;
                    case "Formation":
                        pstmt.setString(1, searchPattern);
                        pstmt.setString(2, searchPattern);
                        pstmt.setString(3, searchPattern);
                        break;
                }
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = new Object[8];
                        
                        switch(currentModule) {
                            case "Stagiaires":
                                row[0] = rs.getString("nom");
                                row[1] = rs.getString("theme");
                                row[2] = rs.getString("departement");
                                row[3] = rs.getString("fonction");
                                row[4] = rs.getString("date_debut");
                                row[5] = rs.getString("duree");
                                row[6] = rs.getString("statut");
                                break;
                            case "Décoration":
                                row[0] = rs.getString("nom_projet");
                                row[1] = rs.getString("client");
                                row[2] = rs.getString("type");
                                row[3] = rs.getString("budget");
                                row[4] = rs.getString("date_debut");
                                row[5] = rs.getString("date_fin");
                                row[6] = rs.getString("statut");
                                break;
                            case "Formation":
                                row[0] = rs.getString("nom_formation");
                                row[1] = rs.getString("formateur");
                                row[2] = rs.getString("domaine");
                                row[3] = rs.getInt("participants");
                                row[4] = rs.getString("date_debut");
                                row[5] = rs.getString("duree");
                                row[6] = rs.getString("statut");
                                break;
                        }
                        
                        row[7] = "✏ Modifier | 🗑 Supprimer";
                        model.addRow(row);
                    }
                }
            }
            
            updateListTitle(model.getRowCount());
            updateStatistics();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la recherche", e);
            JOptionPane.showMessageDialog(this, "Erreur de recherche: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        String selectedDept = (String) departmentCombo.getSelectedItem();
        String selectedStatus = (String) statusCombo.getSelectedItem();
        
        if ("Tous".equals(selectedDept) && "Tous".equals(selectedStatus)) {
            loadDataFromDatabase(currentModule);
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.setRowCount(0);
        
        try {
            String tableName = getTableName(currentModule);
            StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " WHERE 1=1");
            
            if (!"Tous".equals(selectedDept) && "Stagiaires".equals(currentModule)) {
                sql.append(" AND departement = ?");
            }
            
            if (!"Tous".equals(selectedStatus)) {
                sql.append(" AND statut = ?");
            }
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                
                if (!"Tous".equals(selectedDept) && "Stagiaires".equals(currentModule)) {
                    pstmt.setString(paramIndex++, selectedDept);
                }
                
                if (!"Tous".equals(selectedStatus)) {
                    pstmt.setString(paramIndex++, selectedStatus);
                }
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = new Object[8];
                        
                        switch(currentModule) {
                            case "Stagiaires":
                                row[0] = rs.getString("nom");
                                row[1] = rs.getString("theme");
                                row[2] = rs.getString("departement");
                                row[3] = rs.getString("fonction");
                                row[4] = rs.getString("date_debut");
                                row[5] = rs.getString("duree");
                                row[6] = rs.getString("statut");
                                break;
                            case "Décoration":
                                row[0] = rs.getString("nom_projet");
                                row[1] = rs.getString("client");
                                row[2] = rs.getString("type");
                                row[3] = rs.getString("budget");
                                row[4] = rs.getString("date_debut");
                                row[5] = rs.getString("date_fin");
                                row[6] = rs.getString("statut");
                                break;
                            case "Formation":
                                row[0] = rs.getString("nom_formation");
                                row[1] = rs.getString("formateur");
                                row[2] = rs.getString("domaine");
                                row[3] = rs.getInt("participants");
                                row[4] = rs.getString("date_debut");
                                row[5] = rs.getString("duree");
                                row[6] = rs.getString("statut");
                                break;
                        }
                        
                        row[7] = "✏ Modifier | 🗑 Supprimer";
                        model.addRow(row);
                    }
                }
            }
            
            updateListTitle(model.getRowCount());
            updateStatistics();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors du filtrage", e);
            JOptionPane.showMessageDialog(this, "Erreur de filtrage: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextField.arc", 8);
            UIManager.put("ComboBox.arc", 8);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Impossible de charger le look and feel système", ex);
            
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ex2) {
                logger.log(Level.SEVERE, "Impossible de charger Nimbus", ex2);
            }
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new main().setVisible(true);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erreur lors du lancement de l'application", e);
                JOptionPane.showMessageDialog(null, 
                    "Erreur lors du lancement de l'application: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}