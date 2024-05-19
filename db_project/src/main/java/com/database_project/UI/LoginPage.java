package com.database_project.UI;

import com.database_project.main_files.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;
import javax.swing.*;

import com.database_project.UI.Config.GlobalConfig;
import com.database_project.UI.Database.DBConnectionPool;
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

    DataSource dataSource = DBConnectionPool.getDataSource();

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

                LoggedUserSingleton.getInstance().setUser(newUser);
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
        Connection connection = null;
        PreparedStatement getUserStmt = null;
        CallableStatement getUserStatsStmt = null;

        try {
            connection = dataSource.getConnection();

            String getUserSql = "SELECT id, bio FROM users WHERE username = ? AND password = ?";
            getUserStmt = connection.prepareStatement(getUserSql);
            getUserStmt.setString(1, username);
            getUserStmt.setString(2, password);
            ResultSet resultSet = getUserStmt.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String bio = resultSet.getString("bio");

                String getUserStatsSql = "{CALL get_user_stats(?, ?, ?, ?)}";
                getUserStatsStmt = connection.prepareCall(getUserStatsSql);
                getUserStatsStmt.setInt(1, userId);
                getUserStatsStmt.registerOutParameter(2, Types.INTEGER);
                getUserStatsStmt.registerOutParameter(3, Types.INTEGER);
                getUserStatsStmt.registerOutParameter(4, Types.INTEGER);
                getUserStatsStmt.execute();

                int followersCount = getUserStatsStmt.getInt(2);
                int followingCount = getUserStatsStmt.getInt(3);
                int postsCount = getUserStatsStmt.getInt(4);

                debug.print("User stats", "followers: " + followersCount, "following: " + followingCount, "posts: " + postsCount);

                newUser = new User(userId, username, bio, password, postsCount, followersCount, followingCount);

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (getUserStmt != null) getUserStmt.close();
                if (getUserStatsStmt != null) getUserStatsStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


    //THIS IS SUPPOSED TO STORE THE CONNECTED USER TO CHECK IF WE ARE ONE SOMEBODY'S PROFILE OR OURS
    // private void saveUserInformation(User user) {
    //     try (BufferedWriter writer = new BufferedWriter(new FileWriter("db_project/data/users.txt", false))) {
    //         writer.write(user.toString());  // Implement a suitable toString method in User class
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    //     // try (var connection = dataSource.getConnection()) {
    //     //     var query = "INSERT INTO users (username, bio, password, profile_path ) VALUES (?, ?, ?, ?)";
    //     //     try (var statement = connection.prepareStatement(query)) {
    //     //         statement.setString(1, user.getUsername());
    //     //         statement.setString(2, user.getBio());
    //     //         statement.setString(3, user.getPassword());

    //     //         //TODO: Implement profile path in User class
    //     //         // statement.setString(4, user.getProfilePath());
    //     //         statement.executeUpdate();
    //     //     }
    //     // } catch (SQLException e) {
    //     //     e.printStackTrace();
    //     // }
    // }

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