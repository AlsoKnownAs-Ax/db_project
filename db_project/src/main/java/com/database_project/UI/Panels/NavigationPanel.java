package com.database_project.UI.Panels;

import java.awt.Color;
import java.awt.Image;
import java.net.URL;

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
import com.database_project.main_files.LoggedUserSingleton;
import com.database_project.main_files.User;

public class NavigationPanel{
    LoggedUserSingleton loggedUserInstance = LoggedUserSingleton.getInstance();

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
 
        navigationPanel.add(createIconButton("home.png", Pages.HOME));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("search.png",Pages.EXPLORE));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("add.png",Pages.UPLOAD));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("heart.png",Pages.NOTIFICATIONS));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("profile.png", Pages.PROFILE));
 
        return navigationPanel;
    }

    private JButton createIconButton(String iconName, Pages buttonType) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resourceUrl = classLoader.getResource("img/icons/" + iconName);

        ImageIcon iconOriginal = new ImageIcon(resourceUrl);
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
        User user = loggedUserInstance.getLoggedUser();
        InstaProfileUI profileUI = new InstaProfileUI(user);
        openUI(profileUI);
    }
}
