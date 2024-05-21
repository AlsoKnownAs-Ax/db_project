
package com.database_project.main_files;
import java.util.List;

import javax.sql.DataSource;

import com.database_project.UI.Database.DBConnectionPool;

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
    private int followersCount;
    private int followingCount;
    private List<Picture> pictures;
    private DataSource dataSource = DBConnectionPool.getDataSource();

    public User(String username, String bio, String password) {
        this.username = username;
        this.bio = bio;
        this.password = password;
        fetchUserInformation();
    }

    public User(int user_id, String username, String bio, String password, int followersCount, int followingCount) {
        this.user_id = user_id;
        this.username = username;
        this.bio = bio;
        this.password = password;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
    }

    public User(String username){
        this.username = username;
        fetchUserInformation();
    }

    // Add a picture to the user's profile
    public void addPicture(Picture picture) {
        pictures.add(picture);
    }

    public boolean fetchUserPosts(int user_id){
        this.pictures = new ArrayList<>();

        Connection connection = null;
        PreparedStatement getPostsStmt = null;

        try {
            connection = dataSource.getConnection();

            String getPostsSql = "SELECT * FROM posts WHERE user_id = ?";
            getPostsStmt = connection.prepareStatement(getPostsSql);
            getPostsStmt.setInt(1, user_id);
            ResultSet resultSet = getPostsStmt.executeQuery();

            while (resultSet.next()) {
                String pictureName = resultSet.getString("backdrop_path");
                String bio = resultSet.getString("bio");
                int likes = resultSet.getInt("likes");
                Picture picture = new Picture(pictureName, bio, likes);
                this.pictures.add(picture);
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (getPostsStmt != null) getPostsStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private boolean fetchUserInformation(){
        if(this.username == null) return false;

        Connection connection = null;
        PreparedStatement getUserStmt = null;
        CallableStatement getUserStatsStmt = null;

        try {
            connection = dataSource.getConnection();

            String getUserSql = "SELECT id, bio, password FROM users WHERE username = ?";
            getUserStmt = connection.prepareStatement(getUserSql);
            getUserStmt.setString(1, this.username);
            ResultSet resultSet = getUserStmt.executeQuery();

            if (resultSet.next()) {
                this.user_id = resultSet.getInt("id");
                this.bio = resultSet.getString("bio");
                this.password = resultSet.getString("password");

                String getUserStatsSql = "{CALL get_user_stats(?, ?, ?)}";
                getUserStatsStmt = connection.prepareCall(getUserStatsSql);
                getUserStatsStmt.setInt(1, this.user_id);
                getUserStatsStmt.registerOutParameter(2, Types.INTEGER);
                getUserStatsStmt.registerOutParameter(3, Types.INTEGER);
                getUserStatsStmt.execute();

                this.followersCount = getUserStatsStmt.getInt(2);
                this.followingCount = getUserStatsStmt.getInt(3);

                if(fetchUserPosts(this.user_id)){
                    return true;
                }

                System.out.println("Failed to fetch user posts");
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
    public int getPostsCount() { return this.pictures.size(); }
    public int getFollowersCount() { return followersCount; }
    public int getFollowingCount() { return followingCount; }
    public List<Picture> getPictures() { return pictures; }
    public String getPassword() { return password; }
    public int getUserId() { return user_id; }

    // Setter methods for followers and following counts
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }
    // Implement the toString method for saving user information

    public void addPost(Picture picture) {
        this.pictures.add(picture);
    }
    
    @Override
    public String toString() {
        return username + ":" + bio + ":" + password; // Format as needed
    }

    public boolean isEqual(User user) {
        return this.user_id == user.getUserId();
    }

}