package com.database_project.UI;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.database_project.UI.Config.GlobalConfig;
import com.database_project.UI.Panels.NavigationPanel;
import com.database_project.UI.factory.UIfactory;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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
         String[][] sampleData = createSampleData(); 
        populateContentPanel(contentPanel, sampleData);
        add(scrollPane, BorderLayout.CENTER);
        
        // Set up the home panel
        
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

    private void populateContentPanel(JPanel panel, String[][] sampleData) {

        for (String[] postData : sampleData) {
            JPanel itemPanel = createItemPanel(postData);
            JLabel nameLabel = createLabel(postData[0]);
            JLabel imageLabel = createImageLabel(postData[3]);
            
            String imageId = new File(postData[3]).getName().split("\\.")[0];

            JLabel descriptionLabel = new JLabel(postData[1]);
            descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel likesLabel = new JLabel(postData[2]);
            likesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            itemPanel.add(nameLabel);
            itemPanel.add(imageLabel);
            itemPanel.add(descriptionLabel);
            itemPanel.add(likesLabel);
            itemPanel.add(createLikeButton(imageId, likesLabel));

            panel.add(itemPanel);
            panel.setBackground(this.primaryColor);

            // Make the image clickable
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    displayImage(postData); // Call a method to switch to the image view
                }
            });
            panel.add(spacingPanel());
            
        }
    }

    //Refactoring: broke down the populateContentPanel into several methods

    private JPanel createItemPanel(String[] postData){
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
        imageLabel.setBorder(BorderFactory.createLineBorder(this.secondaryColor)); // Add border to image label
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            BufferedImage croppedImage = originalImage.getSubimage(0, 0, Math.min(originalImage.getWidth(), IMAGE_WIDTH), Math.min(originalImage.getHeight(), IMAGE_HEIGHT));
            ImageIcon imageIcon = new ImageIcon(croppedImage);
            imageLabel.setIcon(imageIcon);
        } catch (IOException ex) {
            // Handle exception: Image file not found or reading error
            imageLabel.setText("Image not found");
        }
        return imageLabel;
    }

    private JButton createLikeButton(String imageId, JLabel likesLabel){
        JButton likeButton = new JButton("❤");
        likeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        likeButton.setBackground(LIKE_BUTTON_COLOR); // Set the background color for the like button
        likeButton.setOpaque(true);
        likeButton.setBorderPainted(false); // Remove border

        //Used lambda expression for the action listener
        likeButton.addActionListener(e -> handleLikeAction(imageId, likesLabel));

        return likeButton;
    }

    private JPanel spacingPanel(){
        // Grey spacing panel
        JPanel spacingPanel = new JPanel();
        spacingPanel.setPreferredSize(new Dimension(WIDTH-10, 5)); // Set the height for spacing
        spacingPanel.setBackground(this.primaryColor); // Grey color for spacing
        return spacingPanel;
    }

private void handleLikeAction(String imageId, JLabel likesLabel) {
    StringBuilder newContent = new StringBuilder();
    boolean updated = false;
    String currentUser = retrieveCurrentUser();
    String imageOwner = "";
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    
    // Read and update image_details.txt
    Path detailsPath = Paths.get("img", "/img/image_details.txt");
    try (BufferedReader reader = Files.newBufferedReader(detailsPath)) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("ImageID: " + imageId)) {
                String[] parts = line.split(", ");
                imageOwner = parts[1].split(": ")[1];
                int likes = Integer.parseInt(parts[4].split(": ")[1]);
                likes++; // Increment the likes count 
                parts[4] = "Likes: " + likes;
                line = String.join(", ", parts);

                // Update the UI
                likesLabel.setText("Likes: " + likes);
                updated = true;
            }
            newContent.append(line).append("\n");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Write updated likes back to image_details.txt
    if (updated) {
        try (BufferedWriter writer = Files.newBufferedWriter(detailsPath)) {
            writer.write(newContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Record the like in notifications.txt
        String notification = String.format("%s; %s; %s; %s\n", imageOwner, currentUser, imageId, timestamp);
        
        try (BufferedWriter notificationWriter = Files.newBufferedWriter(Paths.get("db_project","data","notifications.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            notificationWriter.write(notification);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
private String retrieveCurrentUser() {
    String filePath = "db_project/data/users.txt";
    
    try (BufferedReader userReader = new BufferedReader(new FileReader(filePath))) {
        String line = userReader.readLine();
        if (line != null) {
            return line.split(":")[0].trim();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return "";
}

    //Refactoring: separation of concerns
    private String[][] createSampleData() {
        String followedUsers = retrieveFollowedUsers();
        return imageData(followedUsers, 100);
    }

    private String retrieveFollowedUsers(){
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
    }

    private String[][] imageData(String followedUsers, int maximumPosts){
        // Temporary structure to hold the data
        String[][] tempData = new String[maximumPosts][]; // Assuming a maximum of 100 posts for simplicity
        int count = 0;

        // Access the resource from the classpath
        InputStream is = getClass().getResourceAsStream("/img/image_details.txt");
        if (is == null) {
            System.out.println("Resource not found");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null && count < tempData.length) {
                String[] details = line.split(", ");
                String imagePoster = details[1].split(": ")[1];
                if (followedUsers.contains(imagePoster)) {
                    String imagePath = "/img/uploaded/" + details[0].split(": ")[1] + ".png"; // Assuming PNG format
                    String description = details[2].split(": ")[1];
                    String likes = "Likes: " + details[4].split(": ")[1];
    
                    tempData[count++] = new String[]{imagePoster, description, likes, imagePath};
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.copyOf(tempData, count);
    }
    
    private boolean inDisplayImageView = false;

    private void displayImage(String[] postData) {
        inDisplayImageView = true;
        imageViewPanel.removeAll(); // Clear previous content

       
        String imageId = new File(postData[3]).getName().split("\\.")[0];
        JLabel likesLabel = new JLabel(postData[2]); // Update this line



        // Display the image
        JLabel fullSizeImageLabel = new JLabel();
        fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);
      

         try {
                BufferedImage originalImage = ImageIO.read(new File(postData[3]));
                BufferedImage croppedImage = originalImage.getSubimage(0, 0, Math.min(originalImage.getWidth(), WIDTH-20), Math.min(originalImage.getHeight(), HEIGHT-40));
                ImageIcon imageIcon = new ImageIcon(croppedImage);
                fullSizeImageLabel.setIcon(imageIcon);
            } catch (IOException ex) {
                // Handle exception: Image file not found or reading error
                fullSizeImageLabel.setText("Image not found");
            }

        //User Info 
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel,BoxLayout.Y_AXIS));
        JLabel userName = new JLabel(postData[0]);
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
                   handleLikeAction(imageId, likesLabel); // Update this line
                   refreshDisplayImage(postData, imageId); // Refresh the view
                }
            });
       
        // Information panel at the bottom
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(new JLabel(postData[1])); // Description
        infoPanel.add(new JLabel(postData[2])); // Likes
        infoPanel.add(likeButton);

        imageViewPanel.add(fullSizeImageLabel, BorderLayout.CENTER);
        imageViewPanel.add(infoPanel, BorderLayout.SOUTH);
        imageViewPanel.add(userPanel,BorderLayout.NORTH);
            
        imageViewPanel.revalidate();
        imageViewPanel.repaint();


        cardLayout.show(cardPanel, "ImageView"); // Switch to the image view
    }

    private void refreshDisplayImage(String[] postData, String imageId) {
        InputStream is = getClass().getResourceAsStream("/img/image_details.txt");
        if (is == null) {
            System.out.println("File not found in resources.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ImageID: " + imageId)) {
                    String likes = line.split(", ")[4].split(": ")[1];
                    postData[2] = "Likes: " + likes;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Call displayImage with updated postData
        displayImage(postData);
    }

}
