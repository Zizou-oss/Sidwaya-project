
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
   
    private static final Font EMOJI_FONT = getEmojiFont();
   
    private static Font getEmojiFont() {
        String[] emojifonts = {"Segoe UI Emoji", "Apple Color Emoji", "Noto Color Emoji", "Symbola", "DejaVu Sans"};
        for (String fontName : emojifonts) {
            Font font = new Font(fontName, Font.PLAIN, 24);
            if (!font.getFamily().equals(Font.DIALOG)) {
                return font;
            }
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, 24);
    }
   
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
            dropTablesIfExist();
       
            // Création des tables
            createTables();
            logger.info("Base de données initialisée avec succès");
        } catch (Exception e) {
            logger.severe("Erreur lors de l'initialisation de la base : " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void dropTablesIfExist() throws SQLException {
        String[] dropStatements = {
            "DROP TABLE IF EXISTS stagiaires",
            "DROP TABLE IF EXISTS decoration",
            "DROP TABLE IF EXISTS formation"
        };
   
        for (String dropStatement : dropStatements) {
            connection.createStatement().execute(dropStatement);
        }
    }
    private void createTables() throws SQLException {
        String createStagiairesTable = "CREATE TABLE IF NOT EXISTS stagiaires (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nom TEXT NOT NULL, " +
                "prenom TEXT NOT NULL, " +
                "theme TEXT, " +
                "departement TEXT, " +
                "fonction TEXT, " +
                "date_debut TEXT, " +
                "duree TEXT, " +
                "statut TEXT)";
        String createDecorationTable = "CREATE TABLE IF NOT EXISTS decoration (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nom TEXT NOT NULL, " +
                "prenom TEXT NOT NULL, " +
                "motifs TEXT, " +
                "departement TEXT, " +
                "fonction TEXT, " +
                "date_decoration TEXT, " +
                "type_decoration TEXT, " +
                "statut TEXT)";
        String createFormationTable = "CREATE TABLE IF NOT EXISTS formation (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date_formation TEXT, " +
                "intitule TEXT, " +
                "nb_participants TEXT, " +
                "liste_participants TEXT, " +
                "statut TEXT)";
        connection.createStatement().execute(createStagiairesTable);
        connection.createStatement().execute(createDecorationTable);
        connection.createStatement().execute(createFormationTable);
   
        insertSampleData();
    }
    private void insertSampleData() throws SQLException {
        // Vérifier si la table stagiaires est vide
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM stagiaires")) {
       
            if (rs.next() && rs.getInt(1) == 0) {
                // Insérer des données d'exemple pour stagiaires
                String insertStagiaire = "INSERT INTO stagiaires (nom, prenom, theme, departement, fonction, date_debut, duree, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertStagiaire)) {
                    pstmt.setString(1, "OUEDRAOGO");
                    pstmt.setString(2, "Jean");
                    pstmt.setString(3, "Développement Web");
                    pstmt.setString(4, "IT");
                    pstmt.setString(5, "Développeur");
                    pstmt.setString(6, "2024-01-15");
                    pstmt.setString(7, "3 mois");
                    pstmt.setString(8, "En cours");
                    pstmt.executeUpdate();
                }
            }
        }
   
        // Vérifier si la table decoration est vide
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM decoration")) {
       
            if (rs.next() && rs.getInt(1) == 0) {
                // Insérer des données d'exemple pour decoration
                String insertDecoration = "INSERT INTO decoration (nom, prenom, motifs, departement, fonction, date_decoration, type_decoration, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertDecoration)) {
                    pstmt.setString(1, "KONE");
                    pstmt.setString(2, "Marie");
                    pstmt.setString(3, "Excellence au travail");
                    pstmt.setString(4, "RH");
                    pstmt.setString(5, "Responsable RH");
                    pstmt.setString(6, "2024-03-20");
                    pstmt.setString(7, "Ordre Etalon");
                    pstmt.setString(8, "Terminé");
                    pstmt.executeUpdate();
                }
            }
        }
   
        // Vérifier si la table formation est vide
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM formation")) {
       
            if (rs.next() && rs.getInt(1) == 0) {
                // Insérer des données d'exemple pour formation
                String insertFormation = "INSERT INTO formation (date_formation, intitule, nb_participants, liste_participants, statut) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertFormation)) {
                    pstmt.setString(1, "2024-02-10");
                    pstmt.setString(2, "Formation Java");
                    pstmt.setString(3, "15");
                    pstmt.setString(4, "Jean OUEDRAOGO, Marie KONE, Paul TRAORE");
                    pstmt.setString(5, "Terminé");
                    pstmt.executeUpdate();
                }
            }
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
       
        JLabel logoLabel = createLogo();
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
   
    private JLabel createLogo() {
        JLabel logo = new JLabel("📖") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillOval(10, 10, 40, 40);
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(10, 10, 40, 40);
               
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        logo.setFont(EMOJI_FONT);
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
        String icon = getModuleIcon(text);
        JButton button = new JButton(icon + " " + text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               
                if (getBackground() == PRIMARY_COLOR) {
                    GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                    g2.setPaint(gradient);
                } else {
                    g2.setColor(getBackground());
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
               
                if (getModel().isRollover()) {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
       
        button.setFont(new Font(EMOJI_FONT.getName() + ", " + BUTTON_FONT.getName(), Font.BOLD, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(CARD_COLOR);
        button.setPreferredSize(new Dimension(180, 50));
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
       
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
       
        JLabel filtersTitle = new JLabel("🔍 Recherche et Filtres");
        filtersTitle.setFont(TITLE_FONT);
        filtersTitle.setForeground(TEXT_COLOR);
        filtersTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
       
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        controlsPanel.setBackground(CARD_COLOR);
       
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(CARD_COLOR);
       
        JLabel searchLabel = new JLabel("🔍 Recherche et Filtres:");
        searchLabel.setFont(SUBTITLE_FONT);
        searchLabel.setForeground(TEXT_COLOR);
       
        searchField = createModernTextField("Nom, prénom, thème...");
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
       
        statusCombo = createModernComboBox(new String[]{"Tous", "En cours", "À venir", "Planifiés", "Terminés"});
       
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
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
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
       
        dataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = dataTable.rowAtPoint(e.getPoint());
                int col = dataTable.columnAtPoint(e.getPoint());
               
                if (row >= 0 && col == dataTable.getColumnCount() - 1) { // Actions column
                    Rectangle cellRect = dataTable.getCellRect(row, col, false);
                    int relativeX = e.getX() - cellRect.x;
                    handleTableAction(row, relativeX, cellRect.width);
                }
            }
        });
       
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
           
            if (column == table.getColumnCount() - 1 && value != null) {
                setText("<html><div style='display: flex; gap: 10px;'>" +
                       "<span style='background: #22c55e; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px;'>✏️</span>" +
                       "<span style='background: #ef4444; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px;'>🗑️</span>" +
                       "</div></html>");
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setForeground(TEXT_COLOR);
                setHorizontalAlignment(SwingConstants.LEFT);
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
       
        JDialog dialog = new JDialog(this, "Ajouter " + itemType, true);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
       
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
       
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
       
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
            new EmptyBorder(25, 25, 25, 25)
        ));
       
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
       
        switch(currentModule) {
            case "Stagiaires": addModernStagiaireFields(formPanel, gbc); break;
            case "Décoration": addModernDecorationFields(formPanel, gbc); break;
            case "Formation": addModernFormationFields(formPanel, gbc); break;
        }
       
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 10, 0));
       
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setFont(BUTTON_FONT);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(239, 68, 68)); // Red color
        cancelButton.setBorder(new RoundedBorder(8, new Color(239, 68, 68)));
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> dialog.dispose());
       
        JButton saveButton = new JButton("Enregistrer");
        saveButton.setFont(BUTTON_FONT);
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(34, 197, 94)); // Green color
        saveButton.setBorder(new RoundedBorder(8, new Color(34, 197, 94)));
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(140, 40));
        saveButton.addActionListener(e -> {
            if (saveNewItem(dialog)) {
                showSuccessMessage(itemType + " ajouté avec succès!");
                dialog.dispose();
                updateModuleContent(currentModule);
            }
        });
       
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
       
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
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
                        pstmt.setString(1, getFieldValue(dialog, 0)); // Nom
                        pstmt.setString(2, getFieldValue(dialog, 1)); // Prénom
                        pstmt.setString(3, getFieldValue(dialog, 2)); // Thème
                        pstmt.setString(4, getComboValue(dialog, 3)); // Département
                        pstmt.setString(5, getFieldValue(dialog, 4)); // Fonction
                        pstmt.setString(6, getFieldValue(dialog, 5)); // Date début
                        pstmt.setString(7, getFieldValue(dialog, 6)); // Durée
                        pstmt.setString(8, getComboValue(dialog, 7)); // Statut
                        break;
                    case "Décoration":
                        pstmt.setString(1, getFieldValue(dialog, 0)); // Nom
                        pstmt.setString(2, getFieldValue(dialog, 1)); // Prénom
                        pstmt.setString(3, getFieldValue(dialog, 2)); // Motifs
                        pstmt.setString(4, getComboValue(dialog, 3)); // Département
                        pstmt.setString(5, getFieldValue(dialog, 4)); // Fonction
                        pstmt.setString(6, getFieldValue(dialog, 5)); // Date de décoration
                        pstmt.setString(7, getComboValue(dialog, 6)); // Type de décoration
                        pstmt.setString(8, getComboValue(dialog, 7)); // Statut
                        break;
                    case "Formation":
                        pstmt.setString(1, getFieldValue(dialog, 0)); // Date de Formation
                        pstmt.setString(2, getFieldValue(dialog, 1)); // Intitulé
                        pstmt.setString(3, getFieldValue(dialog, 2)); // Nombre de Participants
                        pstmt.setString(4, getTextAreaValue(dialog, 3)); // Liste de Participants
                        pstmt.setString(5, getComboValue(dialog, 4)); // Statut
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
    private String getTextAreaValue(JDialog dialog, int index) {
        Component[] components = getAllComponents(dialog);
        int textAreaCount = 0;
        for (Component comp : components) {
            if (comp instanceof JTextArea) {
                if (textAreaCount == index) {
                    return ((JTextArea) comp).getText();
                }
                textAreaCount++;
            }
        }
        return "";
    }
    private Component[] getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container)
                compList.addAll(Arrays.asList(getAllComponents((Container) comp)));
        }
        return compList.toArray(new Component[compList.size()]);
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
                return "INSERT INTO stagiaires (nom, prenom, theme, departement, fonction, date_debut, duree, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            case "Décoration":
                return "INSERT INTO decoration (nom, prenom, motifs, departement, fonction, date_decoration, type_decoration, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            case "Formation":
                return "INSERT INTO formation (date_formation, intitule, nb_participants, liste_participants, statut) VALUES (?, ?, ?, ?, ?)";
            default: return "";
        }
    }
    private String getFieldValue(JDialog dialog, int index) {
        JPanel mainPanel = (JPanel) dialog.getContentPane().getComponent(0);
        JPanel formPanel = (JPanel) mainPanel.getComponent(1); // formPanel is directly in mainPanel, not in a JScrollPane
       
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
        JPanel mainPanel = (JPanel) dialog.getContentPane().getComponent(0);
        JPanel formPanel = (JPanel) mainPanel.getComponent(1); // formPanel is directly in mainPanel, not in a JScrollPane
       
        Component comboComponent = formPanel.getComponent(index * 2 + 1);
       
        if (comboComponent instanceof JComboBox) {
            return (String) ((JComboBox<?>) comboComponent).getSelectedItem();
        }
       
        return "";
    }
    private String getTextAreaValue(JDialog dialog, int index) {
        JPanel mainPanel = (JPanel) dialog.getContentPane().getComponent(0);
        JPanel formPanel = (JPanel) mainPanel.getComponent(1); // formPanel is directly in mainPanel, not in a JScrollPane
       
        Component component = formPanel.getComponent(index * 2 + 1);
       
        if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
            return textArea.getText();
        } else if (component instanceof JTextArea) {
            return ((JTextArea) component).getText();
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
        String[] labels = {"Nom:", "Prénom:", "Thème:", "Département:", "Fonction:", "Date début:", "Durée:", "Statut:"};
       
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.weightx = 0.3;
           
            JLabel label = new JLabel(labels[i]);
            label.setFont(SUBTITLE_FONT);
            label.setForeground(TEXT_COLOR);
            panel.add(label, gbc);
           
            gbc.gridx = 1;
            gbc.weightx = 0.7;
           
            if (i == 3) { // Département
                JComboBox<String> combo = createModernComboBox(new String[]{"RH", "IT", "Finance", "Marketing"});
                combo.setPreferredSize(new Dimension(300, 35));
                panel.add(combo, gbc);
            } else if (i == 5) { // Date début
                JTextField field = createModernTextField("YYYY-MM-DD");
                field.setPreferredSize(new Dimension(300, 35));
                panel.add(field, gbc);
            } else if (i == 7) { // Statut
                JComboBox<String> combo = createModernComboBox(new String[]{"En cours", "À venir", "Planifiés", "Terminés"});
                combo.setPreferredSize(new Dimension(300, 35));
                panel.add(combo, gbc);
            } else {
                JTextField field = createModernTextField("Saisissez " + labels[i].replaceAll("[^a-zA-Z\\s]", "").toLowerCase());
                field.setPreferredSize(new Dimension(300, 35));
                panel.add(field, gbc);
            }
        }
    }
    private void addModernDecorationFields(JPanel panel, GridBagConstraints gbc) {
        String[] labels = {"Nom:", "Prénom:", "Motifs:", "Département:", "Fonction:", "Date de décoration:", "Type de décoration:", "Statut:"};
       
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.weightx = 0.3;
           
            JLabel label = new JLabel(labels[i]);
            label.setFont(SUBTITLE_FONT);
            label.setForeground(TEXT_COLOR);
            panel.add(label, gbc);
           
            gbc.gridx = 1;
            gbc.weightx = 0.7;
           
            if (i == 3) { // Département
                JComboBox<String> combo = createModernComboBox(new String[]{"RH", "IT", "Finance", "Marketing"});
                combo.setPreferredSize(new Dimension(300, 35));
                panel.add(combo, gbc);
            } else if (i == 5) { // Date de décoration
                JTextField field = createModernTextField("YYYY-MM-DD");
                field.setPreferredSize(new Dimension(300, 35));
                panel.add(field, gbc);
            } else if (i == 6) { // Type de décoration
                JComboBox<String> combo = createModernComboBox(new String[]{"Ordre Etalon", "Mérites Burkinabé", "Ordre Ministériels", "Médailles Douanes"});
                combo.setPreferredSize(new Dimension(300, 35));
                panel.add(combo, gbc);
            } else if (i == 7) { // Statut
                JComboBox<String> combo = createModernComboBox(new String[]{"En cours", "À venir", "Planifiés", "Terminés"});
                combo.setPreferredSize(new Dimension(300, 35));
                panel.add(combo, gbc);
            } else {
                JTextField field = createModernTextField("Saisissez " + labels[i].replaceAll("[^a-zA-Z\\s]", "").toLowerCase());
                field.setPreferredSize(new Dimension(300, 35));
                panel.add(field, gbc);
            }
        }
    }
    private void addModernFormationFields(JPanel panel, GridBagConstraints gbc) {
        String[] labels = {"Date de Formation:", "Intitulé:", "Nombre de Participants:", "Liste de Participants:", "Statut:"};
       
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.weightx = 0.3;
           
            JLabel label = new JLabel(labels[i]);
            label.setFont(SUBTITLE_FONT);
            label.setForeground(TEXT_COLOR);
            panel.add(label, gbc);
           
            gbc.gridx = 1;
            gbc.weightx = 0.7;
           
            if (i == 0) { // Date de Formation
                JTextField field = createModernTextField("YYYY-MM-DD");
                field.setPreferredSize(new Dimension(300, 35));
                panel.add(field, gbc);
            } else if (i == 3) { // Liste de Participants - text area for multiple names
                JTextArea textArea = new JTextArea(3, 20);
                textArea.setFont(BODY_FONT);
                textArea.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(8, BORDER_COLOR),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                textArea.setBackground(Color.WHITE);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(300, 80));
                panel.add(scrollPane, gbc);
            } else if (i == 4) { // Statut
                JComboBox<String> combo = createModernComboBox(new String[]{"En cours", "À venir", "Planifiés", "Terminés"});
                combo.setPreferredSize(new Dimension(300, 35));
                panel.add(combo, gbc);
            } else {
                JTextField field = createModernTextField("Saisissez " + labels[i].replaceAll("[^a-zA-Z\\s]", "").toLowerCase());
                field.setPreferredSize(new Dimension(300, 35));
                panel.add(field, gbc);
            }
        }
    }
   
    private void showSuccessMessage(String message) {
        JDialog successDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        successDialog.setUndecorated(true);
        successDialog.setSize(300, 150);
        successDialog.setLocationRelativeTo(this);
       
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SUCCESS_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
       
        JLabel iconLabel = new JLabel("✅", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        iconLabel.setFont(new Font(EMOJI_FONT.getName(), Font.PLAIN, 30));
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
            case "Stagiaires": return "👨‍💼"; // Professional person
            case "Décoration": return "🏡"; // House decoration
            case "Formation": return "📚"; // Books
            case "Accueil": return "🏠"; // Home
            case "Statistiques": return "📊"; // Chart
            case "Paramètres": return "⚙️"; // Settings
            case "Aide": return "❓"; // Question mark
            default: return "📋"; // Clipboard
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
        String moduleIcon = getModuleIcon(currentModule);
        listTitleLabel.setText("<html><span style='font-family: " + EMOJI_FONT.getName() + "; font-size: 18px;'>" +
                              moduleIcon + "</span> Liste des " + currentModule + " (" + count + ")</html>");
        listTitleLabel.setFont(TITLE_FONT);
    }
    private void updateListTitle(int count) {
        updateListTitle(count, getModuleIcon(currentModule));
    }
    private void updateTableColumns(String module) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == getColumnCount() - 1; // Only actions column is editable
            }
        };
       
        String[] columns;
        switch(module) {
            case "Stagiaires":
                columns = new String[]{"Nom", "Prénom", "Thème", "Département", "Fonction", "Date début", "Durée", "Statut", "Actions"};
                break;
            case "Décoration":
                columns = new String[]{"Nom", "Prénom", "Motifs", "Département", "Fonction", "Date décoration", "Type décoration", "Statut", "Actions"};
                break;
            case "Formation":
                columns = new String[]{"Date formation", "Intitulé", "Nombre participants", "Liste participants", "Statut", "Actions"};
                break;
            default:
                columns = new String[]{"Col1", "Col2", "Col3", "Col4", "Col5", "Col6", "Col7", "Col8"};
        }
       
        model.setColumnIdentifiers(columns);
        dataTable.setModel(model);
        currentTableModel = model;
       
        dataTable.getColumnModel().getColumn(columns.length - 1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
               
                if (column == table.getColumnCount() - 1) { // Actions column
                    setText("<html><div style='display: flex; gap: 20px; justify-content: center;'>" +
                            "<span style='background: #22c55e; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px;'>✏️ Modifier</span>" +
                            "<span style='background: #ef4444; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px;'>🗑️ Supprimer</span>" +
                            "</div></html>");
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    setForeground(TEXT_COLOR);
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
               
                return component;
            }
        });
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
                    Object[] row;
                   
                    switch(module) {
                        case "Stagiaires":
                            row = new Object[9];
                            row[0] = rs.getString("nom");
                            row[1] = rs.getString("prenom");
                            row[2] = rs.getString("theme");
                            row[3] = rs.getString("departement");
                            row[4] = rs.getString("fonction");
                            row[5] = rs.getString("date_debut");
                            row[6] = rs.getString("duree");
                            row[7] = rs.getString("statut");
                            row[8] = "Actions";
                            break;
                        case "Décoration":
                            row = new Object[9];
                            row[0] = rs.getString("nom");
                            row[1] = rs.getString("prenom");
                            row[2] = rs.getString("motifs");
                            row[3] = rs.getString("departement");
                            row[4] = rs.getString("fonction");
                            row[5] = rs.getString("date_decoration");
                            row[6] = rs.getString("type_decoration");
                            row[7] = rs.getString("statut");
                            row[8] = "Actions";
                            break;
                        case "Formation":
                            row = new Object[6];
                            row[0] = rs.getString("date_formation");
                            row[1] = rs.getString("intitule");
                            row[2] = rs.getString("nb_participants");
                            row[3] = rs.getString("liste_participants");
                            row[4] = rs.getString("statut");
                            row[5] = "Actions";
                            break;
                        default:
                            continue;
                    }
                   
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
                        case "planifiés": planifie = count; break;
                        case "terminés": termine = count; break;
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
                    sql += "LOWER(nom) LIKE ? OR LOWER(prenom) LIKE ? OR LOWER(theme) LIKE ? OR LOWER(departement) LIKE ? OR LOWER(fonction) LIKE ?";
                    break;
                case "Décoration":
                    sql += "LOWER(nom) LIKE ? OR LOWER(prenom) LIKE ? OR LOWER(motifs) LIKE ? OR LOWER(departement) LIKE ? OR LOWER(fonction) LIKE ? OR LOWER(type_decoration) LIKE ?";
                    break;
                case "Formation":
                    sql += "LOWER(intitule) LIKE ? OR LOWER(date_formation) LIKE ? OR LOWER(liste_participants) LIKE ? OR LOWER(nb_participants) LIKE ?";
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
                        pstmt.setString(5, searchPattern);
                        break;
                    case "Décoration":
                        pstmt.setString(1, searchPattern);
                        pstmt.setString(2, searchPattern);
                        pstmt.setString(3, searchPattern);
                        pstmt.setString(4, searchPattern);
                        pstmt.setString(5, searchPattern);
                        pstmt.setString(6, searchPattern);
                        break;
                    case "Formation":
                        pstmt.setString(1, searchPattern);
                        pstmt.setString(2, searchPattern);
                        pstmt.setString(3, searchPattern);
                        pstmt.setString(4, searchPattern);
                        break;
                }
               
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row;
                       
                        switch(currentModule) {
                            case "Stagiaires":
                                row = new Object[9];
                                row[0] = rs.getString("nom");
                                row[1] = rs.getString("prenom");
                                row[2] = rs.getString("theme");
                                row[3] = rs.getString("departement");
                                row[4] = rs.getString("fonction");
                                row[5] = rs.getString("date_debut");
                                row[6] = rs.getString("duree");
                                row[7] = rs.getString("statut");
                                row[8] = "Actions";
                                break;
                            case "Décoration":
                                row = new Object[9];
                                row[0] = rs.getString("nom");
                                row[1] = rs.getString("prenom");
                                row[2] = rs.getString("motifs");
                                row[3] = rs.getString("departement");
                                row[4] = rs.getString("fonction");
                                row[5] = rs.getString("date_decoration");
                                row[6] = rs.getString("type_decoration");
                                row[7] = rs.getString("statut");
                                row[8] = "Actions";
                                break;
                            case "Formation":
                                row = new Object[6];
                                row[0] = rs.getString("date_formation");
                                row[1] = rs.getString("intitule");
                                row[2] = rs.getString("nb_participants");
                                row[3] = rs.getString("liste_participants");
                                row[4] = rs.getString("statut");
                                row[5] = "Actions";
                                break;
                            default:
                                continue;
                        }
                       
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
           
            if (!"Tous".equals(selectedDept)) {
                sql.append(" AND departement = ?");
            }
           
            if (!"Tous".equals(selectedStatus)) {
                sql.append(" AND statut = ?");
            }
           
            try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
                int paramIndex = 1;
               
                if (!"Tous".equals(selectedDept)) {
                    pstmt.setString(paramIndex++, selectedDept);
                }
               
                if (!"Tous".equals(selectedStatus)) {
                    pstmt.setString(paramIndex++, selectedStatus);
                }
               
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row;
                       
                        switch(currentModule) {
                            case "Stagiaires":
                                row = new Object[9];
                                row[0] = rs.getString("nom");
                                row[1] = rs.getString("prenom");
                                row[2] = rs.getString("theme");
                                row[3] = rs.getString("departement");
                                row[4] = rs.getString("fonction");
                                row[5] = rs.getString("date_debut");
                                row[6] = rs.getString("duree");
                                row[7] = rs.getString("statut");
                                row[8] = "Actions";
                                break;
                            case "Décoration":
                                row = new Object[9];
                                row[0] = rs.getString("nom");
                                row[1] = rs.getString("prenom");
                                row[2] = rs.getString("motifs");
                                row[3] = rs.getString("departement");
                                row[4] = rs.getString("fonction");
                                row[5] = rs.getString("date_decoration");
                                row[6] = rs.getString("type_decoration");
                                row[7] = rs.getString("statut");
                                row[8] = "Actions";
                                break;
                            case "Formation":
                                row = new Object[6];
                                row[0] = rs.getString("date_formation");
                                row[1] = rs.getString("intitule");
                                row[2] = rs.getString("nb_participants");
                                row[3] = rs.getString("liste_participants");
                                row[4] = rs.getString("statut");
                                row[5] = "Actions";
                                break;
                            default:
                                continue;
                        }
                       
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
    private void handleTableAction(int row, int clickX, int cellWidth) {
        if (row < 0 || row >= dataTable.getRowCount()) return;
       
        String itemName;
        switch (currentModule) {
            case "Stagiaires":
            case "Décoration":
                itemName = (String) dataTable.getValueAt(row, 0) + " " + (String) dataTable.getValueAt(row, 1);
                break;
            case "Formation":
                itemName = (String) dataTable.getValueAt(row, 1);
                break;
            default:
                itemName = "l'élément";
        }
       
        boolean isModify = clickX < cellWidth / 2;
       
        if (isModify) {
            handleModifyAction(row, itemName);
        } else {
            handleDeleteAction(row, itemName);
        }
    }
   
    private void handleModifyAction(int row, String itemName) {
        try {
            // Get current data from the row
            Object[] currentData = new Object[dataTable.getColumnCount() - 1]; // Exclude actions column
            for (int i = 0; i < currentData.length; i++) {
                currentData[i] = dataTable.getValueAt(row, i);
            }
           
            // Create modification dialog based on current module
            JDialog modifyDialog = createModifyDialog(currentData, itemName);
            modifyDialog.setVisible(true);
           
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la modification", e);
            JOptionPane.showMessageDialog(this,
                "❌ Erreur lors de la modification: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
   
    private JDialog createModifyDialog(Object[] currentData, String itemName) {
        JDialog dialog = new JDialog(this, "Modifier " + getSingularForm(currentModule), true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
       
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
       
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("✏️ Modifier " + itemName);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel);
       
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
       
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
       
        JTextField[] fields = createFormFields(formPanel, gbc, currentData);
       
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(BACKGROUND_COLOR);
       
        JButton saveButton = createStyledButton("💾 Sauvegarder", SUCCESS_COLOR);
        JButton cancelButton = createStyledButton("❌ Annuler", ERROR_COLOR);
       
        saveButton.addActionListener(e -> {
            if (updateRecord(fields, currentData)) {
                dialog.dispose();
                updateModuleContent(currentModule);
                showSuccessMessage("Modification effectuée avec succès!");
            }
        });
       
        cancelButton.addActionListener(e -> dialog.dispose());
       
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
       
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
       
        dialog.add(mainPanel);
        return dialog;
    }
   
    private JTextField[] createFormFields(JPanel formPanel, GridBagConstraints gbc, Object[] currentData) {
        List<JTextField> fieldsList = new ArrayList<>();
        int row = 0;
       
        switch(currentModule) {
            case "Stagiaires":
                fieldsList.add(addFormField(formPanel, gbc, "Nom:", row++, (String)currentData[0]));
                fieldsList.add(addFormField(formPanel, gbc, "Prénom:", row++, (String)currentData[1]));
                fieldsList.add(addFormField(formPanel, gbc, "Thème:", row++, (String)currentData[2]));
                fieldsList.add(addComboField(formPanel, gbc, "Département:", row++,
                    new String[]{"RH", "IT", "Finance", "Marketing"},
                    (String)currentData[3]));
                fieldsList.add(addFormField(formPanel, gbc, "Fonction:", row++, (String)currentData[4]));
                fieldsList.add(addFormField(formPanel, gbc, "Date début:", row++, (String)currentData[5]));
                fieldsList.add(addFormField(formPanel, gbc, "Durée:", row++, (String)currentData[6]));
                fieldsList.add(addComboField(formPanel, gbc, "Statut:", row++,
                    new String[]{"En cours", "À venir", "Planifiés", "Terminés"},
                    (String)currentData[7]));
                break;
               
            case "Décoration":
                fieldsList.add(addFormField(formPanel, gbc, "Nom:", row++, (String)currentData[0]));
                fieldsList.add(addFormField(formPanel, gbc, "Prénom:", row++, (String)currentData[1]));
                fieldsList.add(addFormField(formPanel, gbc, "Motifs:", row++, (String)currentData[2]));
                fieldsList.add(addComboField(formPanel, gbc, "Département:", row++,
                    new String[]{"RH", "IT", "Finance", "Marketing"},
                    (String)currentData[3]));
                fieldsList.add(addFormField(formPanel, gbc, "Fonction:", row++, (String)currentData[4]));
                fieldsList.add(addFormField(formPanel, gbc, "Date décoration:", row++, (String)currentData[5]));
                fieldsList.add(addComboField(formPanel, gbc, "Type décoration:", row++,
                    new String[]{"Ordre Etalon", "Mérites Burkinabé", "Ordre Ministériels", "Médailles Douanes"},
                    (String)currentData[6]));
                fieldsList.add(addComboField(formPanel, gbc, "Statut:", row++,
                    new String[]{"En cours", "À venir", "Planifiés", "Terminés"},
                    (String)currentData[7]));
                break;
               
            case "Formation":
                fieldsList.add(addFormField(formPanel, gbc, "Date formation:", row++, (String)currentData[0]));
                fieldsList.add(addFormField(formPanel, gbc, "Intitulé:", row++, (String)currentData[1]));
                fieldsList.add(addFormField(formPanel, gbc, "Nombre participants:", row++, (String)currentData[2]));
                fieldsList.add(addTextAreaField(formPanel, gbc, "Liste participants:", row++, (String)currentData[3]));
                fieldsList.add(addComboField(formPanel, gbc, "Statut:", row++,
                    new String[]{"En cours", "À venir", "Planifiés", "Terminés"},
                    (String)currentData[4]));
                break;
        }
       
        return fieldsList.toArray(new JTextField[0]);
    }
    private JTextField addComboField(JPanel panel, GridBagConstraints gbc, String label, int row, String[] options, String selectedValue) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(BODY_FONT);
        panel.add(jLabel, gbc);
       
        gbc.gridx = 1;
        JComboBox<String> combo = new JComboBox<>(options);
        combo.setSelectedItem(selectedValue);
        combo.setFont(BODY_FONT);
        combo.setPreferredSize(new Dimension(200, 30));
        panel.add(combo, gbc);
       
        // Return a JTextField that gets its value from the combo
        JTextField hiddenField = new JTextField();
        hiddenField.setText(selectedValue);
        combo.addActionListener(e -> hiddenField.setText((String)combo.getSelectedItem()));
       
        return hiddenField;
    }
    private JTextField addTextAreaField(JPanel panel, GridBagConstraints gbc, String label, int row, String value) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(BODY_FONT);
        panel.add(jLabel, gbc);
       
        gbc.gridx = 1;
        JTextArea textArea = new JTextArea(3, 20);
        textArea.setText(value);
        textArea.setFont(BODY_FONT);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(200, 60));
        panel.add(scrollPane, gbc);
       
        // Return a JTextField that gets its value from the text area
        JTextField hiddenField = new JTextField();
        hiddenField.setText(value);
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { hiddenField.setText(textArea.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { hiddenField.setText(textArea.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { hiddenField.setText(textArea.getText()); }
        });
       
        return hiddenField;
    }
    private JTextField addFormField(JPanel formPanel, GridBagConstraints gbc, String label, int row, String initialValue) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(BODY_FONT);
        formPanel.add(jLabel, gbc);
       
        gbc.gridx = 1;
        JTextField field = new JTextField(initialValue, 20);
        field.setFont(BODY_FONT);
        formPanel.add(field, gbc);
        return field;
    }
    private String getUpdateSQL(String module) {
        switch(module) {
            case "Stagiaires":
                return "UPDATE stagiaires SET nom=?, prenom=?, theme=?, departement=?, fonction=?, date_debut=?, duree=?, statut=? WHERE nom=? AND prenom=?";
            case "Décoration":
                return "UPDATE decoration SET nom=?, prenom=?, motifs=?, departement=?, fonction=?, date_decoration=?, type_decoration=?, statut=? WHERE nom=? AND prenom=?";
            case "Formation":
                return "UPDATE formation SET date_formation=?, intitule=?, nb_participants=?, liste_participants=?, statut=? WHERE date_formation=? AND intitule=?";
            default: return "";
        }
    }
    private boolean updateRecord(JTextField[] fields, Object[] originalData) {
        try {
            String sql = getUpdateSQL(currentModule);
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                switch(currentModule) {
                    case "Stagiaires":
                        pstmt.setString(1, fields[0].getText().trim());
                        pstmt.setString(2, fields[1].getText().trim());
                        pstmt.setString(3, fields[2].getText().trim());
                        pstmt.setString(4, fields[3].getText().trim());
                        pstmt.setString(5, fields[4].getText().trim());
                        pstmt.setString(6, fields[5].getText().trim());
                        pstmt.setString(7, fields[6].getText().trim());
                        pstmt.setString(8, fields[7].getText().trim());
                        pstmt.setString(9, (String) originalData[0]);
                        pstmt.setString(10, (String) originalData[1]);
                        break;
                    case "Décoration":
                        pstmt.setString(1, fields[0].getText().trim());
                        pstmt.setString(2, fields[1].getText().trim());
                        pstmt.setString(3, fields[2].getText().trim());
                        pstmt.setString(4, fields[3].getText().trim());
                        pstmt.setString(5, fields[4].getText().trim());
                        pstmt.setString(6, fields[5].getText().trim());
                        pstmt.setString(7, fields[6].getText().trim());
                        pstmt.setString(8, fields[7].getText().trim());
                        pstmt.setString(9, (String) originalData[0]);
                        pstmt.setString(10, (String) originalData[1]);
                        break;
                    case "Formation":
                        pstmt.setString(1, fields[0].getText().trim());
                        pstmt.setString(2, fields[1].getText().trim());
                        pstmt.setString(3, fields[2].getText().trim());
                        pstmt.setString(4, fields[3].getText().trim());
                        pstmt.setString(5, fields[4].getText().trim());
                        pstmt.setString(6, (String) originalData[0]);
                        pstmt.setString(7, (String) originalData[1]);
                        break;
                }
               
                int updatedRows = pstmt.executeUpdate();
                return updatedRows > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour", e);
            JOptionPane.showMessageDialog(this,
                "❌ Erreur lors de la mise à jour: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
   
    private void handleDeleteAction(int row, String itemName) {
        int result = JOptionPane.showConfirmDialog(
            this,
            "🗑️ Êtes-vous sûr de vouloir supprimer " + itemName + " ?\nCette action est irréversible.",
            "Supprimer " + getSingularForm(currentModule),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
       
        if (result == JOptionPane.YES_OPTION) {
            try {
                String tableName = getTableName(currentModule);
                String key1 = (String) dataTable.getValueAt(row, 0);
                String key2 = (String) dataTable.getValueAt(row, 1);
                String sql;
                switch (currentModule) {
                    case "Stagiaires":
                        sql = "DELETE FROM " + tableName + " WHERE nom = ? AND prenom = ?";
                        break;
                    case "Décoration":
                        sql = "DELETE FROM " + tableName + " WHERE nom = ? AND prenom = ?";
                        break;
                    case "Formation":
                        sql = "DELETE FROM " + tableName + " WHERE date_formation = ? AND intitule = ?";
                        break;
                    default:
                        return;
                }
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, key1);
                    pstmt.setString(2, key2);
                    int deletedRows = pstmt.executeUpdate();
                   
                    if (deletedRows > 0) {
                        JOptionPane.showMessageDialog(this,
                            "✅ " + itemName + " a été supprimé avec succès!",
                            "Suppression réussie",
                            JOptionPane.INFORMATION_MESSAGE);
                        updateModuleContent(currentModule); // Refresh the table
                    }
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erreur lors de la suppression", e);
                JOptionPane.showMessageDialog(this,
                    "❌ Erreur lors de la suppression: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
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
