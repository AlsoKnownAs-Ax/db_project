package com.database_project.UI;
import javax.imageio.ImageIO;
import javax.sql.DataSource;
import javax.swing.*;

import com.database_project.Database.DBConnectionPool;
import com.database_project.UI.Config.GlobalConfig;
import com.database_project.UI.Panels.NavigationPanel;
import com.database_project.UI.factory.UIfactory;
import com.database_project.main_files.Picture;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    private DataSource dataSource = DBConnectionPool.getDataSource();


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
        JPanel imageGridPanel = new JPanel(new GridLayout(0, 3, 2, 2)); // 3 columns, auto rows
        imageGridPanel.setBackground(this.secondaryColor); 

        String uploadedImagesDirectory = getUploadedImagesPath();
        File imageDir = new File( uploadedImagesDirectory );

        if (imageDir.exists() && imageDir.isDirectory()) {
            ArrayList<Picture> randomPictures = getRandomImages(9);
            if (randomPictures.isEmpty()) {
                return imageGridPanel;
            }
            
            for (Picture picture : randomPictures) {
                String imagePath = picture.getImagePath();
                ImageIcon imageIcon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH));
                JLabel imageLabel = new JLabel(imageIcon);
                imageLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        displayImage(picture); // Call method to display the clicked image
                    }
                });
                imageGridPanel.add(imageLabel);
            }
        }else{
            System.out.println("No directory found");
        }

        return imageGridPanel;
    }

    private String getUploadedImagesPath(){
        return getClass().getClassLoader().getResource("img/uploaded").getPath();
    }

    private ArrayList<Picture> getRandomImages(int count) {
        ArrayList<Picture> pictures = new ArrayList<>();

        try {
            Connection connection = dataSource.getConnection();
            String sql = "SELECT * FROM posts ORDER BY RAND() LIMIT ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, count);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String pictureName = resultSet.getString("backdrop_path");
                String bio = resultSet.getString("bio");
                int likes = resultSet.getInt("likes");
                int picture_id = resultSet.getInt("post_id");
                Timestamp timestamp = resultSet.getTimestamp("created_at");
                int owner_id = resultSet.getInt("user_id");

                Picture picture = new Picture.Builder()
                                            .ownerUserId(owner_id)
                                            .pictureID(picture_id)
                                            .backdrop_path(pictureName)
                                            .caption(bio)
                                            .likes(likes)
                                            .timestamp(timestamp)
                                            .build();
                pictures.add(picture);
           }

            return pictures;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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

    private void displayImage(Picture picture) {
        String imagePath = picture.getImagePath();
        String post_bio = picture.getCaption();
        int post_likes = picture.getLikesCount();

        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // Add the header and navigation panels back
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createNavigationPanel(), BorderLayout.SOUTH);

        User owner = picture.getOwnerObject();
        String username = owner.getUsername();
        String timeSincePosting = CalculateTimeSincePost(picture.getFormattedTimestamp());

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
        JTextArea bioTextArea = new JTextArea(post_bio);

        bioTextArea.setForeground(this.textColor);
        bioTextArea.setBackground(this.primaryColor);
        bioTextArea.setEditable(false);

        JLabel likesLabel = new JLabel("Likes: " + post_likes);
        likesLabel.setForeground(this.textColor);
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

        usernameLabel.addActionListener(e -> {
            InstaProfileUI profileUI = new InstaProfileUI(owner);
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
