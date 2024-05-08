package com.database_project.UI.Panels;

import java.awt.Color;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.database_project.UI.InstaProfileUI;
import com.database_project.UI.Config.GlobalConfig;
import com.database_project.managers.NavigationManager;
import com.database_project.main_files.User;

public class NavigationPanel{

    enum Pages{
        HOME,
        EXPLORE,
        UPLOAD,
        NOTIFICATIONS,
        PROFILE
    }

    private int NAV_ICON_SIZE = GlobalConfig.getNavIconSize();
    private Color navigationBackground;

    public NavigationPanel(Color navigationBackground){
        this.navigationBackground = navigationBackground;
    }

    public JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel();
        
        navigationPanel.setBackground(navigationBackground);
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
 
        navigationPanel.add(createIconButton("/img/icons/home.png", Pages.HOME));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("/img/icons/search.png",Pages.EXPLORE));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("/img/icons/add.png",Pages.UPLOAD));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("/img/icons/heart.png",Pages.NOTIFICATIONS));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("/img/icons/profile.png", Pages.PROFILE));
 
        return navigationPanel;
    }

    private JButton createIconButton(String iconPath, Pages buttonType) {
        ImageIcon iconOriginal = new ImageIcon(iconPath);
        Image iconScaled = iconOriginal.getImage().getScaledInstance(NAV_ICON_SIZE, NAV_ICON_SIZE, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(iconScaled));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        
        button.addActionListener(e -> handleButtonClick(buttonType));
        
        return button;
    }
    
    //refactoring: made a general handler for each button click
    private void handleButtonClick(Pages buttonType) {
        switch (buttonType) {
            case HOME:
                NavigationManager.showHomePage();
                break;
            case PROFILE:
                NavigationManager.hideAllPages();
                openProfileUI();
                break;
            case NOTIFICATIONS:
                NavigationManager.showNotificationsPage();
                break;
            case EXPLORE:
                NavigationManager.showExplorePage();
                break;
            case UPLOAD:
                NavigationManager.showUploadPage();
                break;
            default:
                System.out.println("Unknown button type: " + buttonType);
        }

    }
    
    private void openUI(JFrame uiFrame) {
        uiFrame.dispose();
        uiFrame.setVisible(true);
    }
    
    private void openProfileUI() {
        String loggedInUsername = readLoggedInUsername();
        User user = new User(loggedInUsername);
        InstaProfileUI profileUI = new InstaProfileUI(user);
        openUI(profileUI);
    }
    
    //refactoring: extracted the method to only have one functionality
    private String readLoggedInUsername() {
        String loggedInUsername = "";
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("db_project","data","users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                loggedInUsername = line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loggedInUsername;
    }
}
