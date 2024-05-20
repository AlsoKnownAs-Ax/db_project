
package com.database_project.main_files;
import java.util.List;

import javax.sql.DataSource;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

// Represents a user on Quackstagram
public class User {
    private int user_id;
    private String username;
    private String bio;
    private String password;
    private int postsCount;
    private int followersCount;
    private int followingCount;
    private List<Picture> pictures;
    private DataSource dataSource;

    public User(String username, String bio, String password) {
        this.username = username;
        this.bio = bio;
        this.password = password;
        this.pictures = new ArrayList<>();
        fetchUserInformation();
    }

    public User(int user_id, String username, String bio, String password, int postsCount, int followersCount, int followingCount) {
        this.user_id = user_id;
        this.username = username;
        this.bio = bio;
        this.password = password;
        this.pictures = new ArrayList<>();
        this.postsCount = postsCount;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
    }

    public User(String username){
        this.username = username;
        this.pictures = new ArrayList<>();
        fetchUserInformation();
    }

    // Add a picture to the user's profile
    public void addPicture(Picture picture) {
        pictures.add(picture);
        postsCount++;
    }

    private boolean fetchUserInformation(){
        if(this.username == null) return false;

        Connection connection = null;
        PreparedStatement getUserStmt = null;
        CallableStatement getUserStatsStmt = null;

        try {
            connection = dataSource.getConnection();

            String getUserSql = "SELECT id, bio FROM users WHERE username = ?";
            getUserStmt = connection.prepareStatement(getUserSql);
            getUserStmt.setString(1, this.username);
            ResultSet resultSet = getUserStmt.executeQuery();

            if (resultSet.next()) {
                this.user_id = resultSet.getInt("id");
                this.bio = resultSet.getString("bio");

                String getUserStatsSql = "{CALL get_user_stats(?, ?, ?, ?)}";
                getUserStatsStmt = connection.prepareCall(getUserStatsSql);
                getUserStatsStmt.setInt(1, this.user_id);
                getUserStatsStmt.registerOutParameter(2, Types.INTEGER);
                getUserStatsStmt.registerOutParameter(3, Types.INTEGER);
                getUserStatsStmt.registerOutParameter(4, Types.INTEGER);
                getUserStatsStmt.execute();

                this.followersCount = getUserStatsStmt.getInt(2);
                this.followingCount = getUserStatsStmt.getInt(3);
                this.postsCount = getUserStatsStmt.getInt(4);

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

        System.out.println("User not found");
        return false;
    }

    // Getter methods for user details
    public String getUsername() { return username; }
    public String getBio() { return bio; }
    public void setBio(String bio) {this.bio = bio; }
    public int getPostsCount() { return postsCount; }
    public int getFollowersCount() { return followersCount; }
    public int getFollowingCount() { return followingCount; }
    public List<Picture> getPictures() { return pictures; }
    public String getPassword() { return password; }
    public int getUserId() { return user_id; }

    // Setter methods for followers and following counts
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }
    public void setPostCount(int postCount) { this.postsCount = postCount;}
    // Implement the toString method for saving user information
    
    @Override
    public String toString() {
        return username + ":" + bio + ":" + password; // Format as needed
    }

    public boolean isEqual(User user) {
        return this.user_id == user.getUserId();
    }

}