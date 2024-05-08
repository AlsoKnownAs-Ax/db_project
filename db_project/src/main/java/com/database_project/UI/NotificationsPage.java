package com.database_project.UI;
import javax.swing.*;

import com.database_project.UI.Config.GlobalConfig;
import com.database_project.UI.Panels.NavigationPanel;
import com.database_project.UI.factory.UIfactory;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class NotificationsPage extends JFrame {

    private static final int WIDTH = GlobalConfig.getWidth();
    private static final int HEIGHT = GlobalConfig.getHeight();

    private Color headerPanColor;
    private Color backgroundColor;
    private Color foregroundColor;
    private Color textColor;

    
    public NotificationsPage() {
        UIfactory uifactory = GlobalConfig.getConfigUIfactory();
        this.headerPanColor = uifactory.getHeaderBackground();
        this.backgroundColor = uifactory.getPrimaryColor();
        this.foregroundColor = uifactory.getHeaderForeground();
        this.textColor = uifactory.getNormalTextColor();
        setTitle("Notifications");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initializeUI();
    }

    //refactoring: shortened the initializeUI method taken from the interface InitUI
    public void initializeUI() {
        JPanel headerPanel = createHeaderPanel();
        JPanel navigationPanel = createNavigationPanel();
        JScrollPane scrollPane = createNotificationScrollPane();
    
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);
    }
    
    //refactoring: created a separate method for reading the current username
    private String readCurrentUsername() {
        String currentUsername = "";
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("db_project","data","users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                currentUsername = line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentUsername;
    }
    //refactoring: created a separate method for the notification scroll pane and content
    private JScrollPane createNotificationScrollPane() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(backgroundColor);
        String currentUsername = readCurrentUsername();

        Path filePath = Paths.get("db_project", "data", "notifications.txt");
    
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 4 && parts[0].trim().equals(currentUsername)) {
                    String userWhoLiked = parts[1].trim();
                    String timestamp = parts[3].trim();
                    String notificationMessage = userWhoLiked + " liked your picture - " + getElapsedTime(timestamp) + " ago";
    
                    JPanel notificationPanel = new JPanel(new BorderLayout());
                    notificationPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    
                    JLabel notificationLabel = new JLabel(notificationMessage);
                    notificationPanel.add(notificationLabel, BorderLayout.CENTER);
                    notificationPanel.setBackground(this.backgroundColor);
                    notificationLabel.setForeground(this.textColor);
    
                    contentPanel.add(notificationPanel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    
        return scrollPane;
    }
    

private String getElapsedTime(String timestamp) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime timeOfNotification = LocalDateTime.parse(timestamp, formatter);
    LocalDateTime currentTime = LocalDateTime.now();

    long daysBetween = ChronoUnit.DAYS.between(timeOfNotification, currentTime);
    long minutesBetween = ChronoUnit.MINUTES.between(timeOfNotification, currentTime) % 60;

    StringBuilder timeElapsed = new StringBuilder();
    if (daysBetween > 0) {
        timeElapsed.append(daysBetween).append(" day").append(daysBetween > 1 ? "s" : "");
    }
    if (minutesBetween > 0) {
        if (daysBetween > 0) {
            timeElapsed.append(" and ");
        }
        timeElapsed.append(minutesBetween).append(" minute").append(minutesBetween > 1 ? "s" : "");
    }
    return timeElapsed.toString();
}

    private JPanel createHeaderPanel() {
       
         // Header Panel (reuse from InstagramProfileUI or customize for home page)
          // Header with the Register label
          JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
          headerPanel.setBackground(this.headerPanColor); // Set a darker background for the header
          JLabel lblRegister = new JLabel(" Notifications 🐥");
          lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
          lblRegister.setForeground(this.foregroundColor); // Set the text color to white
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

        navigationPanelCreator = new NavigationPanel(this.backgroundColor);
        navigationPanel = navigationPanelCreator.createNavigationPanel();

        return navigationPanel;
    }

}
