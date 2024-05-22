package com.database_project.UI;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.database_project.UI.Config.GlobalConfig;
import com.database_project.UI.Database.DBConnectionPool;
import com.database_project.UI.Panels.NavigationPanel;
import com.database_project.UI.factory.UIfactory;
import com.database_project.main_files.LoggedUserSingleton;
import com.database_project.main_files.Picture;
import com.database_project.main_files.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.ResultSet;
public class UploadPage extends JFrame {
    private static final int WIDTH = GlobalConfig.getWidth();
    private static final int HEIGHT = GlobalConfig.getHeight();
    private JLabel imagePreviewLabel;
    private JTextArea bioTextArea;
    private JButton uploadButton;
    private JButton saveButton;

    private LoggedUserSingleton loggedUserSingleton = LoggedUserSingleton.getInstance();
    private DataSource dataSource = DBConnectionPool.getDataSource();

    //COLORS
    private Color HeaderBackground;
    private Color HeaderForeground;
    private Color ContentBackground;

    public UploadPage() {
        UIfactory uifactory = GlobalConfig.getConfigUIfactory();
        this.HeaderBackground=uifactory.getHeaderBackground();
        this.HeaderForeground=uifactory.getHeaderForeground();
        this.ContentBackground = uifactory.getPrimaryColor();

        setTitle("Upload Image");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initializeUI();
    }

    private void initializeUI() {
        JPanel headerPanel = createHeaderPanel(); // Reuse the createHeaderPanel method
        JPanel navigationPanel = createNavigationPanel(); // Reuse the createNavigationPanel method

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(this.ContentBackground);

        // Image preview
        imagePreviewLabel = createImagePreviewLabel();

        // Set an initial empty icon to the imagePreviewLabel
        ImageIcon emptyImageIcon = new ImageIcon();
        imagePreviewLabel.setIcon(emptyImageIcon);

        // Bio text area
        bioTextArea = createBioTextArea("Enter a caption...");


        JScrollPane bioScrollPane = new JScrollPane(bioTextArea);
        bioScrollPane.setPreferredSize(new Dimension(WIDTH - 50, HEIGHT / 6));

        // Upload button
        uploadButton = createUploadButton("Upload Image");

        // Save button (for bio)
        saveButton = createSaveButton("Save Caption");
        
        contentPanel.add(imagePreviewLabel);
        contentPanel.add(bioScrollPane);
        contentPanel.add(uploadButton);

        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);
    }

    private JButton createUploadButton(String buttonLabel){
        uploadButton = new JButton(buttonLabel);
        uploadButton.setBackground(this.HeaderBackground);
        uploadButton.setForeground(this.HeaderForeground);
        uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadButton.addActionListener(this::uploadAction);

        return uploadButton;
    }

    private JButton createSaveButton(String buttonLabel){
        saveButton = new JButton(buttonLabel);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(this::saveBioAction);

        return saveButton;
    }

    private JLabel createImagePreviewLabel(){
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setBackground(this.HeaderBackground);
        imagePreviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagePreviewLabel.setPreferredSize(new Dimension(WIDTH, HEIGHT / 3));

        return imagePreviewLabel;
    }

    private JTextArea createBioTextArea(String text){
        bioTextArea = new JTextArea(text);
        bioTextArea.setBackground(this.ContentBackground);
        bioTextArea.setForeground(Color.gray);
        bioTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);

        return bioTextArea;
    }
    

    private void uploadAction(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an image file");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg");
        fileChooser.addChoosableFileFilter(filter);
    
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                User loggedUser = loggedUserSingleton.getLoggedUser();

                int imageId = getNextImageID();
                String fileExtension = getFileExtension(selectedFile);
                String newFileName = "post" + imageId + "." + fileExtension;
                
                Path destPath = getSavingDirectoryPath(newFileName);
                Files.copy(selectedFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
    
                if(!saveImageInfo(newFileName, loggedUser, bioTextArea.getText(), imageId)) {
                    JOptionPane.showMessageDialog(this, "Error saving image information", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Load the image from the saved path
                ImageIcon imageIcon = new ImageIcon(destPath.toString());
    
                // Check if imagePreviewLabel has a valid size
                if (imagePreviewLabel.getWidth() > 0 && imagePreviewLabel.getHeight() > 0) {
                    Image image = imageIcon.getImage();
    
                    // Calculate the dimensions for the image preview
                    int previewWidth = imagePreviewLabel.getWidth();
                    int previewHeight = imagePreviewLabel.getHeight();
                    int imageWidth = image.getWidth(null);
                    int imageHeight = image.getHeight(null);
                    double widthRatio = (double) previewWidth / imageWidth;
                    double heightRatio = (double) previewHeight / imageHeight;
                    double scale = Math.min(widthRatio, heightRatio);
                    int scaledWidth = (int) (scale * imageWidth);
                    int scaledHeight = (int) (scale * imageHeight);
    
                    // Set the image icon with the scaled image
                    imageIcon.setImage(image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH));
                }
    
                imagePreviewLabel.setIcon(imageIcon);
    
                uploadButton.setText("Upload Another Image");
    
                JOptionPane.showMessageDialog(this, "Image uploaded and preview updated!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Path getSavingDirectoryPath(String newFileName){
        URL resourceUrl = UploadPage.class.getClassLoader().getResource("img/uploaded");

        if (resourceUrl != null) {
            try {
                Path destPath = Paths.get(resourceUrl.toURI()).resolve(newFileName);
            
                return destPath;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return null;
    } 
    
    private int getNextImageID() {
        try {
            Connection connection = dataSource.getConnection();
            String query = """
                    SELECT MAX(post_id) AS max_id FROM posts
                    """;
            try (var statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("max_id") + 1;
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting the next image ID: " + e.getMessage());
        }

        return 0;
    }
    
    private boolean saveImageInfo(String backdrop_path, User user, String post_bio, int imageID) throws IOException {
        try {
            Connection connection = dataSource.getConnection();
            int uid = user.getUserId();
            String query = """
                    INSERT INTO posts (likes, bio, user_id, backdrop_path)
                    VALUES (?, ?, ?, ?)
                    """;
            var statement = connection.prepareStatement(query);
            
            statement.setInt(1, 0);
            statement.setString(2, post_bio);
            statement.setInt(3, uid);
            statement.setString(4, backdrop_path);
            statement.executeUpdate();

            loggedUserSingleton.getLoggedUser().addPost(new Picture(imageID, backdrop_path, post_bio));

            return true;
        } catch (Exception e) {
            System.out.println("Failed to save image information: " + e.getMessage());
        }

        return false;
    }


    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }

        return name.substring(lastIndexOf + 1);
    }

    private void saveBioAction(ActionEvent event) {
        // Here you would handle saving the bio text
        String bioText = bioTextArea.getText();
        // For example, save the bio text to a file or database
        JOptionPane.showMessageDialog(this, "Caption saved: " + bioText);
    }
   
    private JPanel createHeaderPanel() {
        // Header Panel (reuse from InstagramProfileUI or customize for home page)
        // Header with the Register label
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(this.HeaderBackground); // Set a darker background for the header
        JLabel lblRegister = new JLabel(" Upload Image 🐥");
        lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
        lblRegister.setForeground(this.HeaderForeground); // Set the text color to white
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

        navigationPanelCreator = new NavigationPanel(this.ContentBackground);
        navigationPanel = navigationPanelCreator.createNavigationPanel();

        return navigationPanel;
    }
}
