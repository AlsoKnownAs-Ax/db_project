package com.database_project.UI;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.database_project.UI.Config.GlobalConfig;
import com.database_project.UI.Panels.NavigationPanel;
import com.database_project.UI.factory.UIfactory;
import com.database_project.main_files.User;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

public class ExplorePage extends JFrame {

    private Color textColor;
    private Color primaryColor;
    private Color secondaryColor;
    private Color headerBG;
    private Color headerForegroud;

    private static final int WIDTH = GlobalConfig.getWidth();
    private static final int HEIGHT = GlobalConfig.getHeight();
    private static final int IMAGE_SIZE = GlobalConfig.getImageSize(); // Size for each image in the grid


    public ExplorePage() {
        UIfactory uifactory = GlobalConfig.getConfigUIfactory();
        this.textColor = uifactory.getNormalTextColor();
        this.primaryColor = uifactory.getPrimaryColor();
        this.secondaryColor = uifactory.getSecondaryColor();
        this.headerBG = uifactory.getHeaderBackground();
        this.headerForegroud = uifactory.getHeaderForeground();
        setTitle("Explore");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initializeUI();
    }

    private void initializeUI() {
        
        getContentPane().removeAll(); // Clear existing components
        setLayout(new BorderLayout()); // Reset the layout manager

        JPanel headerPanel = createHeaderPanel(); // Method from your InstagramProfileUI class
        JPanel navigationPanel = createNavigationPanel(); // Method from your InstagramProfileUI class
        JPanel mainContentPanel = createMainContentPanel();

        // Add panels to the frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }
    
    /**
     * Create the main content panel with search and image grid
     * @return
     */
    private JPanel createMainContentPanel() {
        // Search bar at the top
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField(" Search Users");
        searchField.setBackground(this.secondaryColor); // Dark background color
        searchField.setForeground(this.textColor);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchField.getPreferredSize().height)); // Limit the height
    
        JPanel imageGridPanel = createExploreImageGrid();
        JScrollPane scrollPane = createScrollPane(imageGridPanel);

        // Main content panel that holds both the search bar and the image grid
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.add(searchPanel);
        mainContentPanel.add(scrollPane); // This will stretch to take up remaining space

        return mainContentPanel;
    }

    private JScrollPane createScrollPane(JPanel panel){
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        return scrollPane;
    }

    private JPanel createExploreImageGrid(){
        // Image Grid
        JPanel imageGridPanel = new JPanel(new GridLayout(0, 3, 2, 2)); // 3 columns, auto rows
        imageGridPanel.setBackground(this.secondaryColor); 
        // Load images from the uploaded folder
        File imageDir = new File("/img/uploaded");
        if (imageDir.exists() && imageDir.isDirectory()) {
            File[] imageFiles = imageDir.listFiles((dir, name) -> name.matches(".*\\.(png|jpg|jpeg)"));
            if (imageFiles != null) {
                for (File imageFile : imageFiles) {
                    ImageIcon imageIcon = new ImageIcon(new ImageIcon(imageFile.getPath()).getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH));
                    JLabel imageLabel = new JLabel(imageIcon);
                    imageLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            displayImage(imageFile.getPath()); // Call method to display the clicked image
                        }
                    });
                    imageGridPanel.add(imageLabel);
                }
            }
        }

        return imageGridPanel;
    }

   
    private JPanel createHeaderPanel() {
        // Header Panel (reuse from InstagramProfileUI or customize for home page)
        // Header with the Register label
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(this.headerBG); // Set a darker background for the header
        JLabel lblRegister = new JLabel(" Explore 🐥");
        lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
        lblRegister.setForeground(this.headerForegroud); // Set the text color to white
        headerPanel.add(lblRegister);
        headerPanel.setPreferredSize(new Dimension(WIDTH, 40)); // Give the header a fixed height

        return headerPanel;
   }

   
    private NavigationPanel navigationPanelCreator;
    private JPanel navigationPanel;
    /**
    * Create and return the navigation panel
    * @return
    */
    private JPanel createNavigationPanel() {
        if(navigationPanel != null){
            return navigationPanel;
        }

        navigationPanelCreator = new NavigationPanel(this.primaryColor);
        navigationPanel = navigationPanelCreator.createNavigationPanel();

        return navigationPanel;
    }

   private void displayImage(String imagePath) {
    getContentPane().removeAll();
    setLayout(new BorderLayout());

    // Add the header and navigation panels back
    add(createHeaderPanel(), BorderLayout.NORTH);
    add(createNavigationPanel(), BorderLayout.SOUTH);

    // Extract image ID from the imagePath
    String imageId = new File(imagePath).getName().split("\\.")[0];
    
    // Read image details
    String username = "";
    String bio = "";
    String timestampString = "";
    int likes = 0;
    Path detailsPath = Paths.get("img", "/img/image_details.txt");
    try (Stream<String> lines = Files.lines(detailsPath)) {
        String details = lines.filter(line -> line.contains("ImageID: " + imageId)).findFirst().orElse("");
        if (!details.isEmpty()) {
            String[] parts = details.split(", ");
            username = parts[1].split(": ")[1];
            bio = parts[2].split(": ")[1];
            timestampString = parts[3].split(": ")[1];
            likes = Integer.parseInt(parts[4].split(": ")[1]);
            System.out.println("Image Bio: " + bio + parts[3]);
        }
    } catch (IOException ex) {
        // Handle exception
        ex.printStackTrace();
        System.out.println("A error occured while opening the image, Please try again.");
    }

    // Calculate time since posting
    String timeSincePosting = CalculateTimeSincePost(timestampString);

    // Top panel for username and time since posting
    JPanel topPanel = new JPanel(new BorderLayout());
    JButton usernameLabel = new JButton(username);
    JLabel timeLabel = new JLabel(timeSincePosting);
    timeLabel.setForeground(this.textColor);
    timeLabel.setHorizontalAlignment(JLabel.RIGHT);

    topPanel.add(usernameLabel, BorderLayout.WEST);
    topPanel.add(timeLabel, BorderLayout.EAST);


    // Prepare the image for display
    JLabel imageLabel = new JLabel();
    imageLabel.setHorizontalAlignment(JLabel.CENTER);
    try {
        BufferedImage originalImage = ImageIO.read(new File(imagePath));
        ImageIcon imageIcon = new ImageIcon(originalImage);
        imageLabel.setIcon(imageIcon);
    } catch (IOException ex) {
        imageLabel.setText("Image not found");
    }

    // Bottom panel for bio and likes
    JPanel bottomPanel = new JPanel(new BorderLayout());
    JTextArea bioTextArea = new JTextArea(bio);
    bioTextArea.setForeground(this.textColor);
    bioTextArea.setBackground(this.primaryColor);
    bioTextArea.setEditable(false);
    JLabel likesLabel = new JLabel("Likes: " + likes);
    bottomPanel.add(bioTextArea, BorderLayout.CENTER);
    bottomPanel.add(likesLabel, BorderLayout.SOUTH);

    // Adding the components to the frame
    add(topPanel, BorderLayout.NORTH);
    add(imageLabel, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);

    // Re-add the header and navigation panels
    add(createHeaderPanel(), BorderLayout.NORTH);
    add(createNavigationPanel(), BorderLayout.SOUTH);

    // Panel for the back button
    JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton backButton = new JButton("Back");

    // Make the button take up the full width
    backButton.setPreferredSize(new Dimension(WIDTH-20, backButton.getPreferredSize().height));

    backButtonPanel.add(backButton);

    backButton.addActionListener(e -> {
        getContentPane().removeAll();
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    });
    final String finalUsername = username;

    usernameLabel.addActionListener(e -> {
        User user = new User(finalUsername); // Assuming User class has a constructor that takes a username
        InstaProfileUI profileUI = new InstaProfileUI(user);
        profileUI.setVisible(true);
        dispose(); // Close the current frame
    });

    // Container panel for image and details
    JPanel containerPanel = new JPanel(new BorderLayout());

    backButtonPanel.setBackground(this.primaryColor);
    topPanel.setBackground(this.primaryColor);
    imageLabel.setBackground(this.primaryColor);
    bottomPanel.setBackground(this.primaryColor);

    containerPanel.add(topPanel, BorderLayout.NORTH);
    containerPanel.add(imageLabel, BorderLayout.CENTER);
    containerPanel.add(bottomPanel, BorderLayout.SOUTH);

    // Add the container panel and back button panel to the frame
    add(backButtonPanel, BorderLayout.NORTH);
    add(containerPanel, BorderLayout.CENTER);

    revalidate();
    repaint();
}

   private String CalculateTimeSincePost(String timestampString){
    String timeSincePosting = "";

    if (!timestampString.isEmpty()) {
        LocalDateTime timestamp = LocalDateTime.parse(timestampString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime now = LocalDateTime.now();
        long days = ChronoUnit.DAYS.between(timestamp, now);
        timeSincePosting = days + " day" + (days != 1 ? "s" : "") + " ago";

        return timeSincePosting;
    }

    return "Unknown";
   }
 
}
