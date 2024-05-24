package com.database_project.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.awt.event.MouseEvent;

import javax.sql.DataSource;
import javax.swing.*;

import com.database_project.Database.DBConnectionPool;
import com.database_project.UI.Config.GlobalConfig;
import com.database_project.UI.Panels.NavigationPanel;
import com.database_project.UI.factory.UIfactory;
import com.database_project.UI.utils.Debug;
import com.database_project.managers.NavigationManager;
import com.database_project.main_files.LoggedUserSingleton;
import com.database_project.main_files.Picture;
import com.database_project.main_files.User;

public class InstaProfileUI extends JFrame{
    private Debug debug = new Debug();

    // COLORS
    private Color textColor;
    private Color primaryColor;
    private Color secondaryColor;
    private Color followBtnColor;

    private static final int WIDTH = GlobalConfig.getWidth();
    private static final int HEIGHT = GlobalConfig.getHeight();
    private static final int PROFILE_IMAGE_SIZE = GlobalConfig.getProfileImageSize(); // Adjusted size for the profile image to match UI
    private static final int GRID_IMAGE_SIZE = GlobalConfig.getGridImageSize(); // Static size for grid images
    private JPanel contentPanel; // Panel to display the image grid or the clicked image
    private JPanel headerPanel;   // Panel for the header
    private JPanel navigationPanel; // Panel for the navigation
    private User currentUser; // User object to store the current user's information
    boolean isCurrentUser = false;
    private LoggedUserSingleton loggedUserInstance = LoggedUserSingleton.getInstance();
    String loggedInUsername = "";
    DataSource dataSource = DBConnectionPool.getDataSource();

    public InstaProfileUI(User user) {
        UIfactory uifactory = GlobalConfig.getConfigUIfactory();
        this.textColor = uifactory.getNormalTextColor();
        this.primaryColor = uifactory.getPrimaryColor();
        this.secondaryColor = uifactory.getSecondaryColor();
        this.followBtnColor = uifactory.getFollowBtnColor();

        this.currentUser = user;
        User loggedUser = loggedUserInstance.getLoggedUser();
        isCurrentUser = currentUser.isEqual(loggedUser);
        
        setInformantion();
        InitializeInstaProfileUI();
        NavigationManager.setProfilePage(this);


    }

    public void setInformantion(){
        debug.print("STATS",
            "Followers Count: " + currentUser.getFollowersCount(), 
            "Following Count: " + currentUser.getFollowingCount(), 
            "Posts Count: " + currentUser.getPostsCount(), 
            "bio: " + currentUser.getBio()
        );
    }

    public void InitializeInstaProfileUI(){
        setTitle("DACS Profile");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        headerPanel = createHeaderPanel();       // Initialize header panel
        navigationPanel = createNavigationPanel(); // Initialize navigation panel

        initializeUI(); 
    }

    private void initializeUI() {
        getContentPane().removeAll(); // Clear existing components
        
        // Re-add the header and navigation panels
        add(headerPanel, BorderLayout.NORTH);
        add(navigationPanel, BorderLayout.SOUTH);

        // Initialize the image grid
        initializeImageGrid();

        revalidate();
        repaint();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();

        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(this.textColor);
        // create all the other components via methods
        JPanel topHeader = createTopHeaderPanel();
        JPanel profileNameAndBioPanel = createProfileNameAndBio();
        // add the newly made components to the Header Panel
        headerPanel.add(topHeader);
        headerPanel.add(profileNameAndBioPanel);

        return headerPanel;
    }

     // Top Part of the Header (Profile Image, Stats, Follow Button)
    private JPanel createTopHeaderPanel(){
        JPanel topHeaderPanel = new JPanel(new BorderLayout(10, 0));
        topHeaderPanel.setBackground(this.primaryColor);

        JPanel stats = createStatsFollowPanel();
        JLabel profileImage = createProfileImage();
        topHeaderPanel.add(stats, BorderLayout.CENTER);
        topHeaderPanel.add(profileImage, BorderLayout.WEST);  
        return topHeaderPanel;
    }

    // Profile image
    private JLabel createProfileImage(){
        String username = currentUser.getUsername();
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resourceUrl = classLoader.getResource("img/storage/profile/" + username + ".png");

        if(resourceUrl == null){
            resourceUrl = classLoader.getResource("img/storage/profile/default.png");
        }

        ImageIcon profileIcon = new ImageIcon(resourceUrl);
        Image iconScaled = profileIcon.getImage().getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH);
        JLabel profileImage = new JLabel(new ImageIcon(iconScaled));
        profileImage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return profileImage;
    }

    // Follower count, Followers count, Posts count and Follow Button

    JButton followButton;

    private JPanel createStatsFollowPanel(){
        JPanel statsFollowPanel = new JPanel();
        statsFollowPanel.setLayout(new BoxLayout(statsFollowPanel, BoxLayout.Y_AXIS));
        
        // Follower count, Followers count, Posts count
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        statsPanel.setBackground(this.primaryColor);

        statsPanel.add(createStatLabel(Integer.toString(currentUser.getPostsCount()) , "Posts"));
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getFollowersCount()), "Followers"));
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getFollowingCount()), "Following"));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0)); // Add some vertical padding

        //Follow Button;
        followButton = new JButton();
        if (isCurrentUser) {
            followButton = new JButton("Edit Profile");
        } else {
            followButton = new JButton("Follow");

            // Check if the current user is already being followed by the logged-in user
            int loggedUserID = loggedUserInstance.getLoggedUser().getUserId(); 

            if(isUserFollowingAnotherUser(loggedUserID, currentUser.getUserId())){
                followButton.setText("Following");
            }else{
                followButton.addActionListener(e -> {
                    handleFollowAction(currentUser);
                    followButton.setText("Following");
                });
            }

        }
 
        followButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        followButton.setFont(new Font("Arial", Font.BOLD, 12));
        followButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, followButton.getMinimumSize().height)); // Make the button fill the horizontal space
        followButton.setBackground(this.followBtnColor); // A soft, appealing color that complements the UI
        followButton.setForeground(this.textColor);
        followButton.setOpaque(true);
        followButton.setBorderPainted(false);
        followButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add some vertical padding
        followButton.setFocusable(false);

        statsFollowPanel.add(statsPanel);
        statsFollowPanel.add(followButton);

        return statsFollowPanel;
    }

    private HashMap<Integer, Set<Integer>> followingCache = new HashMap<>();

    private boolean isUserFollowingAnotherUser(int loggedUserID, int currentUserID){
        if(followingCache.containsKey(loggedUserID)){
            debug.print("Following Cache", "Cache Hit", "User: " + loggedUserID + " -> " + currentUserID);
            return followingCache.get(loggedUserID).contains(currentUserID);
        }


        try{
            Connection connection = dataSource.getConnection();

            String query = """
                            SELECT EXISTS (
                                SELECT 1 
                                FROM follows 
                                WHERE following_user_id = ? AND followed_user_id = ?) 
                                AS is_following
                            """;
            PreparedStatement isFollowingStmt = connection.prepareStatement(query);
            isFollowingStmt.setInt(1, loggedUserID);
            isFollowingStmt.setInt(2, currentUserID);
            ResultSet resultSet = isFollowingStmt.executeQuery();

            if (resultSet.next()) {
                boolean isFollowing = resultSet.getBoolean("is_following");
                if(isFollowing){
                    followingCache.computeIfAbsent(loggedUserID, k -> new HashSet<>()).add(currentUserID);
                }
                return isFollowing;
            } 
            
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    private JPanel createProfileNameAndBio(){
        JPanel profileNameAndBioPanel = new JPanel();
        profileNameAndBioPanel.setLayout(new BorderLayout());
        profileNameAndBioPanel.setBackground(this.primaryColor);
        profileNameAndBioPanel.setForeground(this.textColor);

        JLabel profileNameLabel = new JLabel(currentUser.getUsername());
        profileNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        profileNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Padding on the sides
        profileNameLabel.setForeground(this.textColor);

        JTextArea profileBio = new JTextArea(currentUser.getBio());
        profileBio.setEditable(false);
        profileBio.setFont(new Font("Arial", Font.PLAIN, 12));
        profileBio.setBackground(this.primaryColor);
        profileBio.setForeground(this.textColor);
        profileBio.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding on the sides

        profileNameAndBioPanel.add(profileNameLabel, BorderLayout.NORTH);
        profileNameAndBioPanel.add(profileBio, BorderLayout.CENTER);
        return profileNameAndBioPanel;
    }

    private void handleFollowAction(User userToFollow) {
        User loggedUser = loggedUserInstance.getLoggedUser();
        int loggedUserID = loggedUser.getUserId();
        int target_id = userToFollow.getUserId();
    
        try {
            followUser(loggedUserID, target_id);
            debug.print("Following Action Fired", "Started following: " + loggedUser.getUsername() + " -> " + userToFollow.getUsername());
        } catch (Exception e) {
            System.out.println("Error following user, please try again...");
            e.printStackTrace();
        }
    }

    private void followUser(int loggedUserID, int target_id) throws SQLException {
    if(isUserFollowingAnotherUser(loggedUserID, target_id)){
        return;
    }

    String query = """
                    INSERT INTO follows (following_user_id, followed_user_id)
                    VALUES (?, ?)
                    """;

    try (Connection connection = dataSource.getConnection();
         PreparedStatement followStmt = connection.prepareStatement(query)) {

        followStmt.setInt(1, loggedUserID);
        followStmt.setInt(2, target_id);
        followStmt.executeUpdate();
    }
}

    private NavigationPanel navigationPanelCreator;
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

    private void initializeImageGrid() {
        contentPanel.removeAll(); // Clear existing content
        contentPanel.setLayout(new GridLayout(0, 3, 5, 5)); // Grid layout for image grid
        contentPanel.setBackground(this.secondaryColor);

        List<Picture> pictures = currentUser.getPictures();

        for (Picture picture : pictures) {
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(picture.getImagePath()).getImage().getScaledInstance(GRID_IMAGE_SIZE, GRID_IMAGE_SIZE, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    displayImage(imageIcon);
                }
            });
            contentPanel.add(imageLabel);
        }


        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center

        revalidate();
        repaint();
    }


    private void displayImage(ImageIcon imageIcon) {
        contentPanel.removeAll(); // Remove existing content
        contentPanel.setLayout(new BorderLayout()); // Change layout for image display

        JLabel fullSizeImageLabel = new JLabel(imageIcon);
        fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(fullSizeImageLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            getContentPane().removeAll(); // Remove all components from the frame
            initializeUI(); // Re-initialize the UI
        });
        contentPanel.add(backButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JLabel createStatLabel(String number, String text) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + number + "<br/>" + text + "</div></html>", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(this.textColor);
        return label;
    }

}
