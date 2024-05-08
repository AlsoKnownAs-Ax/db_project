package com.database_project.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.database_project.UI.Config.GlobalConfig;
import com.database_project.UI.Panels.ButtonPanel;
import com.database_project.UI.Panels.FieldsPanel;
import com.database_project.UI.factory.UIfactory;
import com.database_project.managers.NavigationManager;

public class ResgisterPage extends JFrame {

    private static final int WIDTH = GlobalConfig.getWidth();
    private static final int HEIGHT = GlobalConfig.getHeight();

    private final String credentialsFilePath = "db_project/data/credentials.txt";
    private final String profilePhotoStoragePath = "/img/storage/profile/";
    private final String logoPhotoFilePath = "/assets/logos/DACS.png";
    
    private FieldsPanel fieldsPanel;
    private ButtonPanel buttonPanel;
    private JPanel      mainPanel;

    public ResgisterPage(UIfactory uIfactory) {
        setTitle("Quackstagram - Register");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(uIfactory.createHeaderPanel(), BorderLayout.NORTH);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(uIfactory.cretProfilePlaceholderPanel(logoPhotoFilePath), BorderLayout.NORTH);

        fieldsPanel = uIfactory.createFieldsPanel();
        fieldsPanel.CreateRegisterFields();

        mainPanel.add(fieldsPanel);
            
        add(mainPanel, BorderLayout.CENTER);
    
        buttonPanel = uIfactory.createButtonPanel();
        buttonPanel.CreateRegisterButtons(UploadPhotoListener(), this::onRegisterClicked, e -> openLoginPage());
        add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().setBackground(uIfactory.getMainBackground());
        repaint();
        revalidate();
    }

    //BUTTONS LISTENERS

    private ActionListener UploadPhotoListener(){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleProfilePictureUpload();
            }
        };
    }
    
    private void onRegisterClicked(ActionEvent event) {
        String username = fieldsPanel.getUsername();
        String password = fieldsPanel.getPassword();
        String bio      = fieldsPanel.getBio();

        if (doesUsernameExist(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            saveCredentials(username, password, bio);
            handleProfilePictureUpload();

            openLoginPage();
        }
    }
            
    private void openLoginPage() {
        // Close the RegisterPage frame
        // dispose();

        // Open the LoginPage frame
        SwingUtilities.invokeLater(() -> {
            NavigationManager.showLoginPage();
        });
    }
    
    private boolean doesUsernameExist(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(username + ":")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

     // Method to handle profile picture upload
     private void handleProfilePictureUpload() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            saveProfilePicture(selectedFile, fieldsPanel.getUsername());
        }
    }

    private void saveProfilePicture(File file, String username) {
        try {
            BufferedImage image = ImageIO.read(file);
            File outputFile = new File(profilePhotoStoragePath + username + ".png");
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveCredentials(String username, String password, String bio) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("db_project/data/credentials.txt", true))) {
            writer.write(username + ":" + password + ":" + bio);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
