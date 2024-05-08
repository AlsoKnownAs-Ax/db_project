package com.database_project.UI;

import com.database_project.main_files.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;

import com.database_project.UI.Config.GlobalConfig;
import com.database_project.UI.Panels.*;
import com.database_project.UI.factory.UIfactory;
import com.database_project.UI.utils.Debug;
import com.database_project.managers.NavigationManager;

public class LoginPage extends JFrame {    

    private Debug debug = new Debug();

    private User newUser;

    private FieldsPanel fieldsPanel;
    private ButtonPanel buttonPanel;
    private JPanel      mainPanel;
    private JPanel      themePanel;
    private JRadioButton lightTheme, darkTheme;

    public LoginPage(UIfactory uIfactory) {
        setTitle("Quackstagram - Login");
        setSize(GlobalConfig.getWidth(), GlobalConfig.getHeight());
        setMinimumSize(new Dimension(GlobalConfig.getWidth(), GlobalConfig.getHeight()));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        add(uIfactory.createHeaderPanel(), BorderLayout.NORTH);
    
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(uIfactory.cretProfilePlaceholderPanel("/assets/logos/DACS.png"), BorderLayout.NORTH);
        
        fieldsPanel = uIfactory.createFieldsPanel();
        fieldsPanel.CreateLoginFileds();
        
        mainPanel.add(fieldsPanel);
        mainPanel.add(CreateThemeSelector());

        add(mainPanel, BorderLayout.CENTER);
    
        buttonPanel = uIfactory.createButtonPanel();
        buttonPanel.CreateLoginButtons(this::onSignInClicked, this::onRegisterNowClicked);
        add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().setBackground(uIfactory.getMainBackground());
        repaint();
        revalidate();
    }

    private JPanel CreateThemeSelector(){
        themePanel = new JPanel();
        UIfactory uIfactory = GlobalConfig.getConfigUIfactory();
        Color backgroundColor = uIfactory.getPrimaryColor();
        Color foregroundColor = uIfactory.getNormalTextColor();
        ButtonGroup buttonGroup = new ButtonGroup();
        lightTheme = new JRadioButton("Light Theme");
        darkTheme = new JRadioButton("Dark Theme");

        buttonGroup.add(lightTheme);
        buttonGroup.add(darkTheme);

        darkTheme.setSelected(true);

        themePanel.add(lightTheme);
        themePanel.add(darkTheme);

        themePanel.setBackground(backgroundColor);
        lightTheme.setBackground(backgroundColor);
        darkTheme.setBackground(backgroundColor);

        lightTheme.setForeground(foregroundColor);
        darkTheme.setForeground(foregroundColor);

        return themePanel;
    }
    
    private void onSignInClicked(ActionEvent event) {
        String enteredUsername = fieldsPanel.getUsername();
        String enteredPassword = fieldsPanel.getPassword();
        
        debug.print("Login Credentials", "Username: " + enteredUsername, "Password: " + enteredPassword);

        if (verifyCredentials(enteredUsername, enteredPassword)) {
            debug.print("Login Response", "Login Succesfully");
            
            dispose();
            
            SwingUtilities.invokeLater(() -> {
                GlobalConfig.setLightMode(lightTheme.isSelected());

                InstaProfileUI profileUI = new InstaProfileUI(newUser);
                // UIfactory uIfactory = GlobalConfig.getConfigUIfactory();
                // NavigationPanel navigationPanel = new NavigationPanel(uIfactory.getPrimaryColor());
                // navigationPanel.setCurrentProfilePage(profileUI);
                profileUI.setVisible(true);
            });
        } else {
            debug.print("Login Response", "User or password are incorrect.");
        }
    }

    private boolean verifyCredentials(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("db_project/data/credentials.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(":");
                if (credentials[0].equals(username) && credentials[1].equals(password)) {
                String bio = credentials[2];
                // Create User object and save information
                newUser = new User(username, bio, password); // Assuming User constructor takes these parameters
                saveUserInformation(newUser);
        
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveUserInformation(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("db_project/data/users.txt", false))) {
            writer.write(user.toString());  // Implement a suitable toString method in User class
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void onRegisterNowClicked(ActionEvent event) {
        // Open the SignUpUI frame

        SwingUtilities.invokeLater(() -> {
            NavigationManager.showRegisterPage();
        });
    }
    
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            NavigationManager.showLoginPage();
        });
    }
}