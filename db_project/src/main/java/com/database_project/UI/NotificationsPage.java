package com.database_project.UI;
import javax.sql.DataSource;
import javax.swing.*;

import com.database_project.Database.DBConnectionPool;
import com.database_project.UI.Config.GlobalConfig;
import com.database_project.UI.Panels.NavigationPanel;
import com.database_project.UI.factory.UIfactory;
import com.database_project.main_files.LoggedUserSingleton;
import com.database_project.main_files.User;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
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

    private DataSource dataSource = DBConnectionPool.getDataSource();
    private LoggedUserSingleton loggedUserInstance = LoggedUserSingleton.getInstance();
    
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

    public void initializeUI() {
        JPanel headerPanel = createHeaderPanel();
        JPanel navigationPanel = createNavigationPanel();
        JScrollPane scrollPane = createNotificationScrollPane();
    
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);
    }
    
    private User getLoggedUser() {
        return loggedUserInstance.getLoggedUser();
    }

    private String getFormattedTimestamp(Timestamp timestamp) {
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }

    //refactoring: created a separate method for the notification scroll pane and content
    private JScrollPane createNotificationScrollPane() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(backgroundColor);
        User loggedUser = getLoggedUser();

        try {
            Connection connection = dataSource.getConnection();
            String sql = """
                            SELECT notifications.*, users.username
                            FROM notifications
                            RIGHT JOIN users
                            ON notifications.user_id = users.id
                            WHERE users.id = ?;
                        """;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, loggedUser.getUserId());
            stmt.executeQuery();
            ResultSet resultSet = stmt.getResultSet();
            
            while(resultSet.next()){
                String userWhoLiked = resultSet.getString("username");
                Timestamp timestamp = resultSet.getTimestamp("created_at");
                String formattedTimeStamp = getFormattedTimestamp(timestamp);

                String notificationMessage = userWhoLiked + " liked your picture - " + getElapsedTime(formattedTimeStamp) + " ago";

                JPanel notificationPanel = new JPanel(new BorderLayout());
                notificationPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                JLabel notificationLabel = new JLabel(notificationMessage);
                notificationPanel.add(notificationLabel, BorderLayout.CENTER);
                notificationPanel.setBackground(this.backgroundColor);
                notificationLabel.setForeground(this.textColor);

                contentPanel.add(notificationPanel);
            }
        } catch (Exception e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
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
