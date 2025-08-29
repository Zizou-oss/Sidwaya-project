import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

/**
 * Interface principale pour Les Editions Sidwaya
 * Gestion des Stagiaires, Décoration & Formation
 * @author Zizou
 */
public class main extends JFrame {
    private static final Logger logger = Logger.getLogger(main.class.getName());

    // Couleurs modernes
    private static final Color PRIMARY_COLOR = new Color(46, 125, 50); // Vert principal
    private static final Color SECONDARY_COLOR = new Color(67, 160, 71); // Vert clair
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Fond clair
    private static final Color CARD_COLOR = Color.WHITE; // Fond des cartes
    private static final Color TEXT_COLOR = new Color(33, 33, 33); // Texte principal
    private static final Color BORDER_COLOR = new Color(200, 200, 200); // Bordures
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80); // Vert succès
    private static final Color WARNING_COLOR = new Color(255, 152, 0); // Orange avertissement
    private static final Color INFO_COLOR = new Color(33, 150, 243); // Bleu info

    // Polices modernes
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 11);

    // Variables pour le module actuel
    private String currentModule = "Stagiaires";
    private DefaultTableModel currentTableModel;
    private JLabel titleLabel, subtitleLabel, listTitleLabel;
    private JButton[] navigationButtons;
    private JLabel[] statValues;
    private JTextField searchField;
    private JComboBox<String> departmentCombo, statusCombo;
    private JDateChooser fromDateChooser, toDateChooser;
    private JButton addButton;
    private JTable dataTable;
    private Connection connection;

    public main(Connection connection) {
        this.connection = connection;
        setTitle("Système de Gestion - Les Editions Sidwaya");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(900, 600)); // Ensure enough width for horizontal layout

        try {
            initializeDatabase();
            initializeModernUI();
            setupEventHandlers();
            updateModuleContent("Stagiaires");
            adjustForScreenSize();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'initialisation", e);
            JOptionPane.showMessageDialog(this, "Erreur d'initialisation: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void adjustForScreenSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1100, (int) (screenSize.width * 0.80));
        int height = Math.min(650, (int) (screenSize.height * 0.85));
        setSize(width, height);
        setLocationRelativeTo(null);
    }

    private void initializeDatabase() throws SQLException {
        String createStagiairesTable = "CREATE TABLE IF NOT EXISTS stagiaires (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nom TEXT NOT NULL, " +
                "theme TEXT, " +
                "departement TEXT, " +
                "fonction TEXT, " +
                "date_debut TEXT, " +
                "duree INTEGER, " +
                "date_fin TEXT, " +
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
        insertSampleData();
    }

    private void insertSampleData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM stagiaires");
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO stagiaires (nom, theme, departement, fonction, date_debut, duree, date_fin, statut) VALUES " +
                        "('Jean Dupont', 'Développement Web', 'IT', 'Développeur', '2025-01-01', 6, '2025-07-01', 'En cours')," +
                        "('Marie Kone', 'Marketing Digital', 'Marketing', 'Assistant', '2025-02-01', 3, '2025-05-01', 'En cours')");
            }

            rs = stmt.executeQuery("SELECT COUNT(*) FROM decoration");
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO decoration (nom_projet, client, type, budget, date_debut, date_fin, statut) VALUES " +
                        "('Gala Annuel', 'Entreprise XYZ', 'Corporate', '5000000', '2025-03-01', '2025-03-15', 'Planifié')," +
                        "('Mariage Traore', 'Famille Traore', 'Mariage', '3000000', '2025-04-01', '2025-04-10', 'Planifié')");
            }

            rs = stmt.executeQuery("SELECT COUNT(*) FROM formation");
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO formation (nom_formation, formateur, domaine, participants, date_debut, duree, statut) VALUES " +
                        "('Java Avancé', 'Prof. Diallo', 'Programmation', 15, '2025-05-01', '2 mois', 'Planifié')," +
                        "('Stratégie Marketing', 'Dr. Sow', 'Marketing', 20, '2025-06-01', '1 mois', 'Planifié')");
            }
        }
    }

    private void initializeModernUI() {
        getContentPane().removeAll();
        setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(12);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        createHeaderSection(mainPanel);
        createNavigationSection(mainPanel);
        createContentSection(mainPanel);

        add(mainScrollPane);
    }

    private void createHeaderSection(JPanel mainPanel) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel logoTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
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
        gbc.gridx = 0;
        gbc.gridy = 0;
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
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        navigationPanel.setBackground(BACKGROUND_COLOR);
        navigationPanel.setBorder(new EmptyBorder(5, 20, 5, 20));

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
        button.setPreferredSize(new Dimension(150, 35));
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
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
        contentPanel.setBorder(new EmptyBorder(15, 30, 30, 30));

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
        JPanel statisticsPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        statisticsPanel.setBackground(BACKGROUND_COLOR);
        statisticsPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        String[] statTitles = {"Total", "En cours", "À venir", "Planifiés", "Terminés"};
        Color[] statColors = {PRIMARY_COLOR, INFO_COLOR, WARNING_COLOR, SUCCESS_COLOR, new Color(158, 158, 158)};
        statValues = new JLabel[5];

        for (int i = 0; i < 5; i++) {
            statValues[i] = new JLabel("0");
            statisticsPanel.add(createModernStatCard(statTitles[i], statValues[i], statColors[i]));
        }

        return statisticsPanel;
    }

    private JPanel createModernStatCard(String title, JLabel valueLabel, Color accentColor) {
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
        card.setPreferredSize(new Dimension(160, 80));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(BODY_FONT);
        titleLabel.setForeground(new Color(100, 100, 100));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(accentColor);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFiltersSection() {
        JPanel filtersPanel = new JPanel(new BorderLayout());
        filtersPanel.setBackground(CARD_COLOR);
        filtersPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, BORDER_COLOR),
                new EmptyBorder(15, 20, 15, 20)));

        JLabel filtersTitle = new JLabel("🔍 Recherches et Filtres");
        filtersTitle.setFont(TITLE_FONT);
        filtersTitle.setForeground(TEXT_COLOR);
        filtersTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Use FlowLayout for horizontal arrangement
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        controlsPanel.setBackground(CARD_COLOR);

        // Search
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(CARD_COLOR);
        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setFont(SUBTITLE_FONT);
        searchLabel.setForeground(TEXT_COLOR);
        searchField = createModernTextField("Tapez pour rechercher...");
        searchField.setPreferredSize(new Dimension(180, 30)); // Adjusted width
        searchPanel.add(searchLabel, BorderLayout.NORTH);
        searchPanel.add(searchField, BorderLayout.CENTER);
        controlsPanel.add(searchPanel);

        // Department
        JPanel deptPanel = new JPanel(new BorderLayout(5, 0));
        deptPanel.setBackground(CARD_COLOR);
        JLabel deptLabel = new JLabel("Département:");
        deptLabel.setFont(SUBTITLE_FONT);
        deptLabel.setForeground(TEXT_COLOR);
        departmentCombo = createModernComboBox(new String[]{"Tous", "RH", "IT", "Finance", "Marketing", "Production"});
        departmentCombo.setPreferredSize(new Dimension(150, 30)); // Adjusted width
        deptPanel.add(deptLabel, BorderLayout.NORTH);
        deptPanel.add(departmentCombo, BorderLayout.CENTER);
        controlsPanel.add(deptPanel);

        // Status
        JPanel statusPanel = new JPanel(new BorderLayout(5, 0));
        statusPanel.setBackground(CARD_COLOR);
        JLabel statusLabel = new JLabel("Statut:");
        statusLabel.setFont(SUBTITLE_FONT);
        statusLabel.setForeground(TEXT_COLOR);
        statusCombo = createModernComboBox(new String[]{"Tous", "En cours", "Terminé", "Suspendu", "À venir"});
        statusCombo.setPreferredSize(new Dimension(150, 30)); // Adjusted width
        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(statusCombo, BorderLayout.CENTER);
        controlsPanel.add(statusPanel);

        // Date début De
        JPanel fromDatePanel = new JPanel(new BorderLayout(5, 0));
        fromDatePanel.setBackground(CARD_COLOR);
        JLabel fromDateLabel = new JLabel("Date Début De:");
        fromDateLabel.setFont(SUBTITLE_FONT);
        fromDateLabel.setForeground(TEXT_COLOR);
        fromDateChooser = new JDateChooser();
        fromDateChooser.setFont(BODY_FONT);
        fromDateChooser.setPreferredSize(new Dimension(150, 30)); // Adjusted width
        fromDatePanel.add(fromDateLabel, BorderLayout.NORTH);
        fromDatePanel.add(fromDateChooser, BorderLayout.CENTER);
        controlsPanel.add(fromDatePanel);

        // Date début À
        JPanel toDatePanel = new JPanel(new BorderLayout(5, 0));
        toDatePanel.setBackground(CARD_COLOR);
        JLabel toDateLabel = new JLabel("Date Début À:");
        toDateLabel.setFont(SUBTITLE_FONT);
        toDateLabel.setForeground(TEXT_COLOR);
        toDateChooser = new JDateChooser();
        toDateChooser.setFont(BODY_FONT);
        toDateChooser.setPreferredSize(new Dimension(150, 30)); // Adjusted width
        toDatePanel.add(toDateLabel, BorderLayout.NORTH);
        toDatePanel.add(toDateChooser, BorderLayout.CENTER);
        controlsPanel.add(toDatePanel);

        filtersPanel.add(filtersTitle, BorderLayout.NORTH);
        filtersPanel.add(controlsPanel, BorderLayout.CENTER);
        return filtersPanel;
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField(30) {
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
        field.setBorder(new EmptyBorder(8, 12, 8, 12));
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
        combo.setBorder(new EmptyBorder(5, 12, 5, 12));
        combo.setOpaque(false);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return combo;
    }

    private JPanel createTableSection() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, BORDER_COLOR),
                new EmptyBorder(20, 25, 20, 25)));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(CARD_COLOR);
        tableHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

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
        tableScrollPane.setPreferredSize(new Dimension(0, 350));
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

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
        button.setPreferredSize(new Dimension(150, 30));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void createModernTable() {
        currentTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };
        dataTable = new JTable(currentTableModel) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        dataTable.setFont(BODY_FONT);
        dataTable.setRowHeight(35);
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
        header.setPreferredSize(new Dimension(0, 40));

        dataTable.setDefaultRenderer(Object.class, new ModernCellRenderer());
        dataTable.setDefaultEditor(Object.class, new ButtonEditor());
    }

    class ModernCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(BODY_FONT);
            setBorder(new EmptyBorder(8, 12, 8, 12));
            if (!isSelected) {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 252, 252));
            }
            if (column == 7 && value != null) {
                String status = value.toString().toLowerCase();
                switch (status) {
                    case "en cours": setForeground(INFO_COLOR); break;
                    case "à venir": setForeground(WARNING_COLOR); break;
                    case "planifié": setForeground(SUCCESS_COLOR); break;
                    case "terminé": setForeground(new Color(158, 158, 158)); break;
                    default: setForeground(TEXT_COLOR);
                }
                setText("● " + value.toString());
            } else if (column == 8) {
                setForeground(PRIMARY_COLOR);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else {
                setForeground(TEXT_COLOR);
            }
            return component;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private int row;

        public ButtonEditor() {
            super(new JTextField());
            button = new JButton();
            button.setOpaque(false);
            button.setFont(BODY_FONT);
            button.setForeground(PRIMARY_COLOR);
            button.setBorder(new EmptyBorder(8, 12, 8, 12));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.addActionListener(e -> {
                if (label.contains("Modifier")) {
                    showModernEditDialog(row);
                } else if (label.contains("Supprimer")) {
                    deleteItem(row);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            label = value.toString();
            button.setText(label);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
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
        fromDateChooser.getDateEditor().addPropertyChangeListener("date", e -> applyFilters());
        toDateChooser.getDateEditor().addPropertyChangeListener("date", e -> applyFilters());
        addButton.addActionListener(e -> showModernAddDialog());
    }

    private void showModernAddDialog() {
        String itemType = getSingularForm(currentModule);
        JDialog dialog = new JDialog(this, "➕ Ajouter " + itemType, true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(25, 25, 15, 25));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

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
                new EmptyBorder(25, 20, 25, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        final JTextField dateFinField;
        final JDateChooser dateDebutChooser;
        final JTextField dureeField;
        switch (currentModule) {
            case "Stagiaires":
                String[] stagiaireLabels = {"👤 Nom & Prénom:", "📚 Thème:", "🏢 Département:", "💼 Fonction:", "📅 Date début:", "⏱ Durée (mois):", "📅 Date fin:"};
                dateDebutChooser = new JDateChooser();
                dateDebutChooser.setPreferredSize(new Dimension(200, 30));
                dateDebutChooser.setFont(BODY_FONT);
                dureeField = createModernTextField("Saisissez durée (mois)");
                dureeField.setPreferredSize(new Dimension(200, 30));
                dateFinField = createModernTextField("Date de fin (auto)");
                dateFinField.setPreferredSize(new Dimension(200, 30));
                dateFinField.setEditable(false);
                addModernStagiaireFields(formPanel, gbc, stagiaireLabels, dateDebutChooser, dureeField, dateFinField);
                break;
            case "Décoration":
                addModernDecorationFields(formPanel, gbc);
                dateDebutChooser = null;
                dureeField = null;
                dateFinField = null;
                break;
            case "Formation":
                addModernFormationFields(formPanel, gbc);
                dateDebutChooser = null;
                dureeField = null;
                dateFinField = null;
                break;
            default:
                dateDebutChooser = null;
                dureeField = null;
                dateFinField = null;
        }

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.setBackground(BACKGROUND_COLOR);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(12);
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 10, 0));

        JButton cancelButton = createSecondaryButton("Annuler");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = createPrimaryButton("💾 Enregistrer");
        saveButton.addActionListener(e -> {
            if (saveNewItem(dialog, dateDebutChooser, dureeField, dateFinField)) {
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

        if (dateDebutChooser != null && dureeField != null && dateFinField != null) {
            dureeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { updateDateFin(dateDebutChooser, dureeField, dateFinField); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { updateDateFin(dateDebutChooser, dureeField, dateFinField); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { updateDateFin(dateDebutChooser, dureeField, dateFinField); }
            });
            dateDebutChooser.getDateEditor().addPropertyChangeListener("date", e -> updateDateFin(dateDebutChooser, dureeField, dateFinField));
        }

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void updateDateFin(JDateChooser dateDebutChooser, JTextField dureeField, JTextField dateFinField) {
        try {
            Date dateDebut = dateDebutChooser.getDate();
            String dureeText = dureeField.getText();
            if (dateDebut != null && dureeText.matches("\\d+")) {
                int duree = Integer.parseInt(dureeText);
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateDebut);
                cal.add(Calendar.MONTH, duree);
                dateFinField.setText(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
            } else {
                dateFinField.setText("");
            }
        } catch (NumberFormatException ex) {
            dateFinField.setText("");
        }
    }

    private void showModernEditDialog(int row) {
        String itemType = getSingularForm(currentModule);
        int id = getRowId(row);
        JDialog dialog = new JDialog(this, "✏ Modifier " + itemType, true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(25, 25, 15, 25));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = new JLabel("Modifier " + itemType);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);

        JLabel descLabel = new JLabel("Modifiez les informations ci-dessous");
        descLabel.setFont(BODY_FONT);
        descLabel.setForeground(new Color(120, 120, 120));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, BORDER_COLOR),
                new EmptyBorder(25, 20, 25, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        final JTextField dateFinField;
        final JDateChooser dateDebutChooser;
        final JTextField dureeField;
        switch (currentModule) {
            case "Stagiaires":
                String[] stagiaireLabels = {"👤 Nom & Prénom:", "📚 Thème:", "🏢 Département:", "💼 Fonction:", "📅 Date début:", "⏱ Durée (mois):", "📅 Date fin:"};
                dateDebutChooser = new JDateChooser();
                dateDebutChooser.setPreferredSize(new Dimension(200, 30));
                dateDebutChooser.setFont(BODY_FONT);
                dureeField = createModernTextField("Saisissez durée (mois)");
                dureeField.setPreferredSize(new Dimension(200, 30));
                dateFinField = createModernTextField("Date de fin (auto)");
                dateFinField.setPreferredSize(new Dimension(200, 30));
                dateFinField.setEditable(false);
                addModernStagiaireFields(formPanel, gbc, stagiaireLabels, dateDebutChooser, dureeField, dateFinField);
                break;
            case "Décoration":
                addModernDecorationFields(formPanel, gbc);
                dateDebutChooser = null;
                dureeField = null;
                dateFinField = null;
                break;
            case "Formation":
                addModernFormationFields(formPanel, gbc);
                dateDebutChooser = null;
                dureeField = null;
                dateFinField = null;
                break;
            default:
                dateDebutChooser = null;
                dureeField = null;
                dateFinField = null;
        }

        loadFormData(formPanel, id);

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.setBackground(BACKGROUND_COLOR);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(12);
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 10, 0));

        JButton cancelButton = createSecondaryButton("Annuler");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = createPrimaryButton("💾 Enregistrer");
        saveButton.addActionListener(e -> {
            if (updateItem(dialog, id, dateDebutChooser, dureeField, dateFinField)) {
                showSuccessMessage(dialog, itemType + " modifié avec succès!");
                dialog.dispose();
                updateModuleContent(currentModule);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        if (dateDebutChooser != null && dureeField != null && dateFinField != null) {
            dureeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { updateDateFin(dateDebutChooser, dureeField, dateFinField); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { updateDateFin(dateDebutChooser, dureeField, dateFinField); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { updateDateFin(dateDebutChooser, dureeField, dateFinField); }
            });
            dateDebutChooser.getDateEditor().addPropertyChangeListener("date", e -> updateDateFin(dateDebutChooser, dureeField, dateFinField));
        }

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void loadFormData(JPanel formPanel, int id) {
        try {
            String tableName = getTableName(currentModule);
            String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        switch (currentModule) {
                            case "Stagiaires":
                                ((JTextField) formPanel.getComponent(1)).setText(rs.getString("nom"));
                                ((JTextField) formPanel.getComponent(3)).setText(rs.getString("theme"));
                                ((JComboBox<?>) formPanel.getComponent(5)).setSelectedItem(rs.getString("departement"));
                                ((JTextField) formPanel.getComponent(7)).setText(rs.getString("fonction"));
                                String dateDebutStr = rs.getString("date_debut");
                                if (dateDebutStr != null) {
                                    Date dateDebut = new SimpleDateFormat("yyyy-MM-dd").parse(dateDebutStr);
                                    ((JDateChooser) formPanel.getComponent(9)).setDate(dateDebut);
                                }
                                ((JTextField) formPanel.getComponent(11)).setText(rs.getString("duree"));
                                ((JTextField) formPanel.getComponent(13)).setText(rs.getString("date_fin"));
                                break;
                            case "Décoration":
                                ((JTextField) formPanel.getComponent(1)).setText(rs.getString("nom_projet"));
                                ((JTextField) formPanel.getComponent(3)).setText(rs.getString("client"));
                                ((JComboBox<?>) formPanel.getComponent(5)).setSelectedItem(rs.getString("type"));
                                ((JTextField) formPanel.getComponent(7)).setText(rs.getString("budget"));
                                ((JTextField) formPanel.getComponent(9)).setText(rs.getString("date_debut"));
                                ((JTextField) formPanel.getComponent(11)).setText(rs.getString("date_fin"));
                                break;
                            case "Formation":
                                ((JTextField) formPanel.getComponent(1)).setText(rs.getString("nom_formation"));
                                ((JTextField) formPanel.getComponent(3)).setText(rs.getString("formateur"));
                                ((JComboBox<?>) formPanel.getComponent(5)).setSelectedItem(rs.getString("domaine"));
                                ((JTextField) formPanel.getComponent(7)).setText(String.valueOf(rs.getInt("participants")));
                                ((JTextField) formPanel.getComponent(9)).setText(rs.getString("date_debut"));
                                ((JTextField) formPanel.getComponent(11)).setText(rs.getString("duree"));
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement des données pour édition", e);
        }
    }

    private void addModernStagiaireFields(JPanel panel, GridBagConstraints gbc, String[] labels, JDateChooser dateDebutChooser, JTextField dureeField, JTextField dateFinField) {
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            JLabel label = new JLabel(labels[i]);
            label.setFont(SUBTITLE_FONT);
            label.setForeground(TEXT_COLOR);
            panel.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            if (i == 2) {
                JComboBox<String> combo = createModernComboBox(new String[]{"IT", "Marketing", "Finance", "RH", "Production"});
                combo.setPreferredSize(new Dimension(200, 30));
                panel.add(combo, gbc);
            } else if (i == 4) {
                panel.add(dateDebutChooser, gbc);
            } else if (i == 5) {
                panel.add(dureeField, gbc);
            } else if (i == 6) {
                panel.add(dateFinField, gbc);
            } else {
                JTextField field = createModernTextField("Saisissez " + labels[i].replaceAll("[^a-zA-Z\\s]", "").toLowerCase());
                field.setPreferredSize(new Dimension(200, 30));
                panel.add(field, gbc);
            }
        }
    }

    private void addModernDecorationFields(JPanel panel, GridBagConstraints gbc) {
        String[] labels = {"🎨 Nom du Projet:", "👥 Client:", "🎭 Type:", "💰 Budget (FCFA):", "📅 Date début:", "📅 Date fin:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            JLabel label = new JLabel(labels[i]);
            label.setFont(SUBTITLE_FONT);
            label.setForeground(TEXT_COLOR);
            panel.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            if (i == 2) {
                JComboBox<String> combo = createModernComboBox(new String[]{"Mariage", "Corporate", "Anniversaire", "Conférence", "Autre"});
                combo.setPreferredSize(new Dimension(200, 30));
                panel.add(combo, gbc);
            } else {
                JTextField field = createModernTextField("Saisissez " + labels[i].replaceAll("[^a-zA-Z\\s]", "").toLowerCase());
                field.setPreferredSize(new Dimension(200, 30));
                panel.add(field, gbc);
            }
        }
    }

    private void addModernFormationFields(JPanel panel, GridBagConstraints gbc) {
        String[] labels = {"📖 Nom Formation:", "👨‍🏫 Formateur:", "🎯 Domaine:", "👥 Nb Participants:", "📅 Date début:", "⏱ Durée:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            JLabel label = new JLabel(labels[i]);
            label.setFont(SUBTITLE_FONT);
            label.setForeground(TEXT_COLOR);
            panel.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            if (i == 2) {
                JComboBox<String> combo = createModernComboBox(new String[]{"Programmation", "Marketing", "Management", "Finance", "Design"});
                combo.setPreferredSize(new Dimension(200, 30));
                panel.add(combo, gbc);
            } else {
                JTextField field = createModernTextField("Saisissez " + labels[i].replaceAll("[^a-zA-Z\\s]", "").toLowerCase());
                field.setPreferredSize(new Dimension(200, 30));
                panel.add(field, gbc);
            }
        }
    }

    private boolean saveNewItem(JDialog dialog, JDateChooser dateDebutChooser, JTextField dureeField, JTextField dateFinField) {
        try {
            String tableName = getTableName(currentModule);
            String sql = getInsertSQL(currentModule);
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                switch (currentModule) {
                    case "Stagiaires":
                        pstmt.setString(1, getFieldValue(dialog, 0));
                        pstmt.setString(2, getFieldValue(dialog, 1));
                        pstmt.setString(3, getComboValue(dialog, 2));
                        pstmt.setString(4, getFieldValue(dialog, 3));
                        String dateDebut = dateDebutChooser.getDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(dateDebutChooser.getDate()) : "";
                        pstmt.setString(5, dateDebut);
                        String dureeText = dureeField.getText();
                        int duree = dureeText.matches("\\d+") ? Integer.parseInt(dureeText) : 0;
                        pstmt.setInt(6, duree);
                        String dateFin = dateFinField.getText();
                        pstmt.setString(7, dateFin);
                        String statut = new Date().after(new SimpleDateFormat("yyyy-MM-dd").parse(dateFin)) ? "Terminé" : "En cours";
                        pstmt.setString(8, statut);
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

    private boolean updateItem(JDialog dialog, int id, JDateChooser dateDebutChooser, JTextField dureeField, JTextField dateFinField) {
        try {
            String tableName = getTableName(currentModule);
            String sql = getUpdateSQL(currentModule);
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                switch (currentModule) {
                    case "Stagiaires":
                        pstmt.setString(1, getFieldValue(dialog, 0));
                        pstmt.setString(2, getFieldValue(dialog, 1));
                        pstmt.setString(3, getComboValue(dialog, 2));
                        pstmt.setString(4, getFieldValue(dialog, 3));
                        String dateDebut = dateDebutChooser.getDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(dateDebutChooser.getDate()) : "";
                        pstmt.setString(5, dateDebut);
                        String dureeText = dureeField.getText();
                        int duree = dureeText.matches("\\d+") ? Integer.parseInt(dureeText) : 0;
                        pstmt.setInt(6, duree);
                        String dateFin = dateFinField.getText();
                        pstmt.setString(7, dateFin);
                        String statut = new Date().after(new SimpleDateFormat("yyyy-MM-dd").parse(dateFin)) ? "Terminé" : "En cours";
                        pstmt.setString(8, statut);
                        pstmt.setInt(9, id);
                        break;
                    case "Décoration":
                        pstmt.setString(1, getFieldValue(dialog, 0));
                        pstmt.setString(2, getFieldValue(dialog, 1));
                        pstmt.setString(3, getComboValue(dialog, 2));
                        pstmt.setString(4, getFieldValue(dialog, 3));
                        pstmt.setString(5, getFieldValue(dialog, 4));
                        pstmt.setString(6, getFieldValue(dialog, 5));
                        pstmt.setString(7, "Planifié");
                        pstmt.setInt(8, id);
                        break;
                    case "Formation":
                        pstmt.setString(1, getFieldValue(dialog, 0));
                        pstmt.setString(2, getFieldValue(dialog, 1));
                        pstmt.setString(3, getComboValue(dialog, 2));
                        pstmt.setInt(4, Integer.parseInt(getFieldValue(dialog, 3)));
                        pstmt.setString(5, getFieldValue(dialog, 4));
                        pstmt.setString(6, getFieldValue(dialog, 5));
                        pstmt.setString(7, "Planifié");
                        pstmt.setInt(8, id);
                        break;
                }
                pstmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour", e);
            JOptionPane.showMessageDialog(dialog, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void deleteItem(int row) {
        int id = getRowId(row);
        String itemType = getSingularForm(currentModule);
        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cet " + itemType + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String tableName = getTableName(currentModule);
                String sql = "DELETE FROM " + tableName + " WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                    showSuccessMessage(this, itemType + " supprimé avec succès!");
                    updateModuleContent(currentModule);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erreur lors de la suppression", e);
                JOptionPane.showMessageDialog(this, "Erreur de suppression: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getRowId(int row) {
        return (int) currentTableModel.getValueAt(row, 0);
    }

    private String getUpdateSQL(String module) {
        switch (module) {
            case "Stagiaires":
                return "UPDATE stagiaires SET nom=?, theme=?, departement=?, fonction=?, date_debut=?, duree=?, date_fin=?, statut=? WHERE id=?";
            case "Décoration":
                return "UPDATE decoration SET nom_projet=?, client=?, type=?, budget=?, date_debut=?, date_fin=?, statut=? WHERE id=?";
            case "Formation":
                return "UPDATE formation SET nom_formation=?, formateur=?, domaine=?, participants=?, date_debut=?, duree=?, statut=? WHERE id=?";
            default:
                return "";
        }
    }

    private String getTableName(String module) {
        switch (module) {
            case "Stagiaires": return "stagiaires";
            case "Décoration": return "decoration";
            case "Formation": return "formation";
            default: return "";
        }
    }

    private String getInsertSQL(String module) {
        switch (module) {
            case "Stagiaires":
                return "INSERT INTO stagiaires (nom, theme, departement, fonction, date_debut, duree, date_fin, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            case "Décoration":
                return "INSERT INTO decoration (nom_projet, client, type, budget, date_debut, date_fin, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";
            case "Formation":
                return "INSERT INTO formation (nom_formation, formateur, domaine, participants, date_debut, duree, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";
            default: return "";
        }
    }

    private String getFieldValue(JDialog dialog, int index) {
        JPanel mainPanel = (JPanel) dialog.getContentPane().getComponent(0);
        JScrollPane formScrollPane = (JScrollPane) mainPanel.getComponent(1);
        JPanel formPanel = (JPanel) formScrollPane.getViewport().getView();
        Component fieldComponent = formPanel.getComponent(index * 2 + 1);
        if (fieldComponent instanceof JTextField) {
            return ((JTextField) fieldComponent).getText();
        } else if (fieldComponent instanceof JComboBox) {
            return (String) ((JComboBox<?>) fieldComponent).getSelectedItem();
        } else if (fieldComponent instanceof JDateChooser) {
            Date date = ((JDateChooser) fieldComponent).getDate();
            return date != null ? new SimpleDateFormat("yyyy-MM-dd").format(date) : "";
        }
        return "";
    }

    private String getComboValue(JDialog dialog, int index) {
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
        button.setPreferredSize(new Dimension(120, 30));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
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
        button.setPreferredSize(new Dimension(90, 30));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showSuccessMessage(Component parent, String message) {
        JDialog successDialog = new JDialog(this, "Succès", true);
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
        switch (module) {
            case "Stagiaires": return "🎓";
            case "Décoration": return "🎨";
            case "Formation": return "📚";
            default: return "📋";
        }
    }

    private String getSingularForm(String module) {
        switch (module) {
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
        currentTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };
        String[] columns;
        switch (module) {
            case "Stagiaires":
                columns = new String[]{"ID", "Nom & Prénom", "Thème", "Département", "Fonction", "Date début", "Durée", "Statut", "Actions"};
                break;
            case "Décoration":
                columns = new String[]{"ID", "Nom Projet", "Client", "Type Déco", "Budget", "Date Début", "Date Fin", "Statut", "Actions"};
                break;
            case "Formation":
                columns = new String[]{"ID", "Nom Formation", "Formateur", "Domaine", "Participants", "Date Début", "Durée", "Statut", "Actions"};
                break;
            default:
                columns = new String[]{"ID", "Col1", "Col2", "Col3", "Col4", "Col5", "Col6", "Col7", "Col8"};
        }
        currentTableModel.setColumnIdentifiers(columns);
        dataTable.setModel(currentTableModel);
        dataTable.getColumnModel().getColumn(0).setMinWidth(0);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(0);
        dataTable.getColumnModel().getColumn(0).setWidth(0);
        for (int i = 1; i < dataTable.getColumnCount() - 1; i++) {
            dataTable.getColumnModel().getColumn(i).setPreferredWidth(120);
        }
        dataTable.getColumnModel().getColumn(dataTable.getColumnCount() - 1).setPreferredWidth(150);
    }

    private void loadDataFromDatabase(String module) {
        currentTableModel.setRowCount(0);
        try {
            String tableName = getTableName(module);
            String sql = buildFilteredQuery(tableName);
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                setQueryParameters(pstmt);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] rowData = new Object[9];
                        rowData[0] = rs.getInt("id");
                        switch (module) {
                            case "Stagiaires":
                                rowData[1] = rs.getString("nom");
                                rowData[2] = rs.getString("theme");
                                rowData[3] = rs.getString("departement");
                                rowData[4] = rs.getString("fonction");
                                rowData[5] = rs.getString("date_debut");
                                rowData[6] = rs.getString("duree");
                                rowData[7] = rs.getString("statut");
                                rowData[8] = "Modifier | Supprimer";
                                break;
                            case "Décoration":
                                rowData[1] = rs.getString("nom_projet");
                                rowData[2] = rs.getString("client");
                                rowData[3] = rs.getString("type");
                                rowData[4] = rs.getString("budget");
                                rowData[5] = rs.getString("date_debut");
                                rowData[6] = rs.getString("date_fin");
                                rowData[7] = rs.getString("statut");
                                rowData[8] = "Modifier | Supprimer";
                                break;
                            case "Formation":
                                rowData[1] = rs.getString("nom_formation");
                                rowData[2] = rs.getString("formateur");
                                rowData[3] = rs.getString("domaine");
                                rowData[4] = rs.getInt("participants");
                                rowData[5] = rs.getString("date_debut");
                                rowData[6] = rs.getString("duree");
                                rowData[7] = rs.getString("statut");
                                rowData[8] = "Modifier | Supprimer";
                                break;
                        }
                        currentTableModel.addRow(rowData);
                    }
                }
            }
            updateListTitle(currentTableModel.getRowCount());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement des données", e);
            JOptionPane.showMessageDialog(this, "Erreur de chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String buildFilteredQuery(String tableName) {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " WHERE 1=1");
        String searchText = searchField.getText().trim();
        String department = (String) departmentCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();
        Date fromDate = fromDateChooser.getDate();
        Date toDate = toDateChooser.getDate();

        if (!searchText.isEmpty()) {
            switch (currentModule) {
                case "Stagiaires":
                    sql.append(" AND (nom LIKE ? OR theme LIKE ?)");
                    break;
                case "Décoration":
                    sql.append(" AND (nom_projet LIKE ? OR client LIKE ?)");
                    break;
                case "Formation":
                    sql.append(" AND (nom_formation LIKE ? OR formateur LIKE ?)");
                    break;
            }
        }

        if (!"Tous".equals(department)) {
            switch (currentModule) {
                case "Stagiaires":
                    sql.append(" AND departement = ?");
                    break;
                case "Décoration":
                    sql.append(" AND type = ?");
                    break;
                case "Formation":
                    sql.append(" AND domaine = ?");
                    break;
            }
        }

        if (!"Tous".equals(status)) {
            sql.append(" AND statut = ?");
        }

        if (fromDate != null) {
            sql.append(" AND date_debut >= ?");
        }

        if (toDate != null) {
            sql.append(" AND date_debut <= ?");
        }

        return sql.toString();
    }

    private void setQueryParameters(PreparedStatement pstmt) throws SQLException {
        int paramIndex = 1;
        String searchText = searchField.getText().trim();
        String department = (String) departmentCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();
        Date fromDate = fromDateChooser.getDate();
        Date toDate = toDateChooser.getDate();

        if (!searchText.isEmpty()) {
            pstmt.setString(paramIndex++, "%" + searchText + "%");
            pstmt.setString(paramIndex++, "%" + searchText + "%");
        }

        if (!"Tous".equals(department)) {
            pstmt.setString(paramIndex++, department);
        }

        if (!"Tous".equals(status)) {
            pstmt.setString(paramIndex++, status);
        }

        if (fromDate != null) {
            pstmt.setString(paramIndex++, new SimpleDateFormat("yyyy-MM-dd").format(fromDate));
        }

        if (toDate != null) {
            pstmt.setString(paramIndex++, new SimpleDateFormat("yyyy-MM-dd").format(toDate));
        }
    }

    private void performSearch() {
        loadDataFromDatabase(currentModule);
        updateStatistics();
    }

    private void applyFilters() {
        loadDataFromDatabase(currentModule);
        updateStatistics();
    }

    private void updateButtonStyles(String module) {
        for (JButton button : navigationButtons) {
            button.setBackground(button.getText().equals(module) ? PRIMARY_COLOR : CARD_COLOR);
            button.setForeground(button.getText().equals(module) ? Color.WHITE : TEXT_COLOR);
            button.repaint();
        }
    }

    private void updateStatistics() {
        try {
            String tableName = getTableName(currentModule);
            String[] queries = {
                    "SELECT COUNT(*) FROM " + tableName,
                    "SELECT COUNT(*) FROM " + tableName + " WHERE statut = 'En cours'",
                    "SELECT COUNT(*) FROM " + tableName + " WHERE statut = 'À venir'",
                    "SELECT COUNT(*) FROM " + tableName + " WHERE statut = 'Planifié'",
                    "SELECT COUNT(*) FROM " + tableName + " WHERE statut = 'Terminé'"
            };

            try (Statement stmt = connection.createStatement()) {
                for (int i = 0; i < statValues.length; i++) {
                    ResultSet rs = stmt.executeQuery(queries[i]);
                    if (rs.next()) {
                        statValues[i].setText(String.valueOf(rs.getInt(1)));
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour des statistiques", e);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            SwingUtilities.invokeLater(() -> new main(conn).setVisible(true));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur de connexion à la base de données", e);
            JOptionPane.showMessageDialog(null, "Erreur de connexion à la base de données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'initialisation", e);
        }
    }
}
   