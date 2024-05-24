package com.database_project.UI;
import javax.imageio.ImageIO;
import javax.sql.DataSource;
import javax.swing.*;

import com.database_project.Database.DBConnectionPool;
import com.database_project.UI.Config.GlobalConfig;
import com.database_project.UI.Panels.NavigationPanel;
import com.database_project.UI.factory.UIfactory;
import com.database_project.main_files.LoggedUserSingleton;
import com.database_project.main_files.Picture;
import com.database_project.main_files.User;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class HomePage extends JFrame {
    private static final int WIDTH = GlobalConfig.getWidth();
    private static final int HEIGHT = GlobalConfig.getHeight();
    private static final int IMAGE_WIDTH = GlobalConfig.getImageWidth(); // Width for the image posts
    private static final int IMAGE_HEIGHT = GlobalConfig.getImageHeight(); // Height for the image posts
    private static final Color LIKE_BUTTON_COLOR = GlobalConfig.getLikeButtonColor(); // Color for the like button
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel homePanel;
    private JPanel imageViewPanel;

    private Color primaryColor;
    private Color secondaryColor;
    private Color textColor;
    private Color headerBG;
    private Color headerTextColor;
    private LoggedUserSingleton loggedUserInstance = LoggedUserSingleton.getInstance();
    private DataSource dataSource = DBConnectionPool.getDataSource();

    public HomePage() {
        UIfactory uIfactory = GlobalConfig.getConfigUIfactory();
        this.primaryColor = uIfactory.getPrimaryColor();
        this.secondaryColor = uIfactory.getSecondaryColor();
        this.textColor = uIfactory.getNormalTextColor();
        this.headerBG = uIfactory.getHeaderBackground();
        this.headerTextColor = uIfactory.getHeaderForeground();

        initializeFrame();
        initializeCardLayout();
        
        homePanel = new JPanel(new BorderLayout());
        imageViewPanel = new JPanel(new BorderLayout());

        initializeUI();
        componentsToFrame();
        
        cardLayout.show(cardPanel, "Home"); // Start with the home view
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createNavigationPanel(), BorderLayout.SOUTH);
    }

    //Refactoring: Separation of concerns (broke down the QuakstagramHomeUI into several methods)

    private void initializeFrame(){
        setTitle("Quakstagram Home");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void initializeCardLayout(){
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
    }

    private void initializeUI() {
        // Content Scroll Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Vertical box layout
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Never allow horizontal scrolling
        ArrayList<Picture> picturesData = createSampleData(); 

        populateContentPanel(contentPanel, picturesData);
        add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        homePanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void componentsToFrame(){
        cardPanel.add(homePanel, "Home");
        cardPanel.add(imageViewPanel, "ImageView");
        add(cardPanel, BorderLayout.CENTER);
    }

    // Header Panel (reuse from InstagramProfileUI or customize for home page)
    // Header with the Register label
    private JPanel createHeaderPanel(){
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(this.headerBG); // Set a darker background for the header
        JLabel lblRegister = new JLabel("🐥 Quackstagram 🐥");
        lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
        lblRegister.setForeground(this.headerTextColor); // Set the text color to white
        headerPanel.add(lblRegister);
        headerPanel.setPreferredSize(new Dimension(WIDTH, 40)); // Give the header a fixed height
        
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(inDisplayImageView == true){
                    cardLayout.show(cardPanel, "Home");              
                }
            }
        });

        return headerPanel;
    }

    private NavigationPanel navigationPanelClass;
    private JPanel navigationPanel;
    /**
    * Create and return the navigation panel
    * @return
    */
    private JPanel createNavigationPanel() {
        if(navigationPanel != null){
            return navigationPanel;
        }

        navigationPanelClass = new NavigationPanel(this.primaryColor);
        navigationPanel = navigationPanelClass.createNavigationPanel();

        return navigationPanel;
    }

    private void populateContentPanel(JPanel panel, ArrayList<Picture> picturesData) {

        for (Picture picture : picturesData) {
            String caption = picture.getCaption();
            String imagePath = picture.getImagePath();
            int likes = picture.getLikesCount();

            JPanel itemPanel = createItemPanel();
            JLabel nameLabel = createLabel(caption);
            JLabel imageLabel = createImageLabel(imagePath);
            
            JLabel descriptionLabel = new JLabel(caption);
            descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel likesLabel = new JLabel("Likes: "+ likes);
            likesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            itemPanel.add(nameLabel);
            itemPanel.add(imageLabel);
            itemPanel.add(descriptionLabel);
            itemPanel.add(likesLabel);
            itemPanel.add(createLikeButton(picture, likesLabel));

            panel.add(itemPanel);
            panel.setBackground(this.primaryColor);

            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    displayImage(picture); // Call a method to switch to the image view
                }
            });
            panel.add(spacingPanel());
            
        }
    }

    //Refactoring: broke down the populateContentPanel into several methods

    private JPanel createItemPanel(){
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(this.textColor); // Set the background color for the item panel
        itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        itemPanel.setAlignmentX(CENTER_ALIGNMENT);

        return itemPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createImageLabel(String imagePath) {
        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        imageLabel.setBorder(BorderFactory.createLineBorder(this.secondaryColor));
        
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            BufferedImage croppedImage = originalImage.getSubimage(0, 0, Math.min(originalImage.getWidth(), IMAGE_WIDTH), Math.min(originalImage.getHeight(), IMAGE_HEIGHT));
            ImageIcon imageIcon = new ImageIcon(croppedImage);
            imageLabel.setIcon(imageIcon);
        } catch (IOException ex) {
            imageLabel.setText("Image not found");
        }

        return imageLabel;
    }

    private JButton createLikeButton(Picture picture, JLabel likesLabel){
        JButton likeButton = new JButton("❤");
        likeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        likeButton.setBackground(LIKE_BUTTON_COLOR); // Set the background color for the like button
        likeButton.setOpaque(true);
        likeButton.setBorderPainted(false); // Remove border

        //Used lambda expression for the action listener
        likeButton.addActionListener(e -> handleLikeAction(picture, likesLabel));

        return likeButton;
    }

    private JPanel spacingPanel(){
        // Grey spacing panel
        JPanel spacingPanel = new JPanel();
        spacingPanel.setPreferredSize(new Dimension(WIDTH-10, 5)); // Set the height for spacing
        spacingPanel.setBackground(this.primaryColor); // Grey color for spacing
        return spacingPanel;
    }

    private void handleLikeAction(Picture image, JLabel likesLabel) {
        User currentUser = retrieveCurrentUser();
        User Owner = image.getOwnerObject();
        
        if(!image.incrementLikeCount()) return;
            
        likesLabel.setText("Likes: " + image.getLikesCount());
        notify(Owner, currentUser, image);
    }

    private boolean notify(User targetUser, User currentUser, Picture picture){
        try {
            Connection connection = dataSource.getConnection();
            String sql = "INSERT INTO notifications (post_id, user_id, target_id) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, picture.getPictureID());
            stmt.setInt(2, currentUser.getUserId());
            stmt.setInt(3, targetUser.getUserId());
            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            System.out.println("Error notifying user: " + e.getMessage());
        }

        return false;
    }

    private User retrieveCurrentUser() {
        User loggedUser = loggedUserInstance.getLoggedUser();
        return loggedUser;
    }

    private ArrayList<Picture> createSampleData() {
        ArrayList<Integer> followedUsers = retrieveFollowedUserIDs();
        return imageData(followedUsers, 100);
    }

    /*private String retrieveFollowedUsers(){
        String followedUsers = "";
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("db_project","data", "following.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(retrieveCurrentUser() + ":")) {
                    followedUsers = line.split(":")[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return followedUsers;
    }*/
    private ArrayList<Integer> retrieveFollowedUserIDs() {
        ArrayList<Integer> followedUsers = new ArrayList<>();
        User currentUser = retrieveCurrentUser();

        String query = """
                        SELECT followed_user_id
                        FROM follows
                        WHERE following_user_id = ?;
                       """;
                               

        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, currentUser.getUserId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                followedUsers.add(resultSet.getInt("followed_user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return followedUsers;
    }

    private String buildIdsString(ArrayList<Integer> followedUsersIDs){
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < followedUsersIDs.size(); i++) {
            ids.append(followedUsersIDs.get(i));
            if (i < followedUsersIDs.size() - 1) {
                ids.append(",");
            }
        }

        return ids.toString();
    }

    private ArrayList<Picture> imageData(ArrayList<Integer> followedUsersIDs, int maximumPosts){
        ArrayList<Picture> pictures = new ArrayList<>(maximumPosts);

        try {
            Connection connection = dataSource.getConnection();
            String query = """
                            SELECT *
                            FROM posts
                            WHERE user_id IN (?)
                            ORDER BY RAND()
                            LIMIT ?;
                           """;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, buildIdsString(followedUsersIDs));
            preparedStatement.setInt(2, maximumPosts);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
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
                                            .build();;
                pictures.add(picture);
            }

            return pictures;
        } catch (Exception e) {
            System.out.println("Error retrieving images data: " + e.getMessage());
        }

        return null;
    }
    
    private boolean inDisplayImageView = false;

    private void displayImage(Picture postData) {
        inDisplayImageView = true;
        imageViewPanel.removeAll(); // Clear previous content
       
        String imagePath = postData.getImagePath();
        User owner = postData.getOwnerObject();

        JLabel likesLabel = new JLabel("Likes: " + postData.getLikesCount()); // Update this line

        JLabel fullSizeImageLabel = new JLabel();
        fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);

        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            BufferedImage croppedImage = originalImage.getSubimage(0, 0, Math.min(originalImage.getWidth(), WIDTH-20), Math.min(originalImage.getHeight(), HEIGHT-40));
            ImageIcon imageIcon = new ImageIcon(croppedImage);
            fullSizeImageLabel.setIcon(imageIcon);
        } catch (IOException ex) {
            fullSizeImageLabel.setText("Image not found");
        }

        //User Info 
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel,BoxLayout.Y_AXIS));
        JLabel userName = new JLabel(owner.getUsername());
        userName.setFont(new Font("Arial", Font.BOLD, 18));
        userPanel.add(userName);//User Name

        JButton likeButton = new JButton("❤");
        likeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        likeButton.setBackground(LIKE_BUTTON_COLOR); // Set the background color for the like button
        likeButton.setOpaque(true);
        likeButton.setBorderPainted(false); // Remove border
        likeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLikeAction(postData, likesLabel); // Update this line
                refreshDisplayImage(postData); // Refresh the view
            }
        });
       
        // Information panel at the bottom
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(new JLabel(postData.getCaption())); // Description
        infoPanel.add(new JLabel("Likes: " + postData.getLikesCount())); // Likes
        infoPanel.add(likeButton);

        imageViewPanel.add(fullSizeImageLabel, BorderLayout.CENTER);
        imageViewPanel.add(infoPanel, BorderLayout.SOUTH);
        imageViewPanel.add(userPanel,BorderLayout.NORTH);
            
        imageViewPanel.revalidate();
        imageViewPanel.repaint();

        cardLayout.show(cardPanel, "ImageView"); // Switch to the image view
    }

    private void refreshDisplayImage(Picture picture) {
        displayImage(picture);
    }

}
