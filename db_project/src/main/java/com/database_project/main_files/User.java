
package com.database_project.main_files;
import java.util.List;

import javax.sql.DataSource;

import com.database_project.UI.Database.DBConnectionPool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

// Represents a user on Quackstagram
public class User {
    private int user_id = -1;   // Default value for undefined user_id
    private String username;
    private String bio;
    private int followersCount;
    private int followingCount;
    private List<Picture> pictures;
    private DataSource dataSource = DBConnectionPool.getDataSource();

    public User(int user_id) {
        this.user_id = user_id;

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
                int picture_id = resultSet.getInt("post_id");
                Timestamp timestamp = resultSet.getTimestamp("created_at");
                int owner_id = resultSet.getInt("user_id");

                Picture picture = new Picture.Builder()
                                            .ownerUserId(owner_id)
                                            .pictureID(picture_id)
                                            .backdrop_path(pictureName)
                                            .caption(bio)
                                            .likes(likes)
                                            .timestamp(timestamp)
                                            .build();
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
        if(this.user_id == -1) return false;

        Connection connection = null;
        PreparedStatement getUserStmt = null;
        CallableStatement getUserStatsStmt = null;

        try {
            connection = dataSource.getConnection();

            String getUserSql = "SELECT username, bio FROM users WHERE id = ?";
            getUserStmt = connection.prepareStatement(getUserSql);
            getUserStmt.setInt(1, this.user_id);
            ResultSet resultSet = getUserStmt.executeQuery();

            if (resultSet.next()) {
                this.username = resultSet.getString("username");
                this.bio = resultSet.getString("bio");

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
    public int getUserId() { return user_id; }

    // Setter methods for followers and following counts
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }
    // Implement the toString method for saving user information

    public void addPost(Picture picture) {
        this.pictures.add(picture);
    }

    public boolean isEqual(User user) {
        return this.user_id == user.getUserId();
    }

}