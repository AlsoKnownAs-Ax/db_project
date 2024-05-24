
package com.database_project.main_files;

import java.util.List;

import javax.sql.DataSource;

import com.database_project.Database.DBConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// Represents a picture on Quackstagram
public class Picture {
    private static final String BASE_PATH = "img/uploaded/";
    private DataSource dataSource = DBConnectionPool.getDataSource();

    private int pictureID;
    private String imagePath;
    private String caption;
    private int likesCount;
    private List<String> comments;
    private Timestamp timestamp;
    private int ownerUserId;

    public Picture(Builder builder) {
        this.ownerUserId = builder.ownerUserId;
        this.pictureID = builder.pictureID;
        this.imagePath = getImagePath(builder.pictureName);
        this.caption = builder.caption;
        this.likesCount = builder.likes;
        this.comments = new ArrayList<>();
        this.timestamp = builder.timestamp;
    }

    private String getImagePath(String pictureName){
        try {
            return getClass().getClassLoader().getResource(BASE_PATH + pictureName).getPath();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid picture name \n" + e.getMessage() + "\n" + "for picture: " + pictureName);
        }
    }
    
    // Add a comment to the picture
    public void addComment(String comment) {
        comments.add(comment);
    }

    // Increment likes count
    private boolean likesIncremented = false;
    public boolean incrementLikeCount() {
        if (likesIncremented) return false;

        try {
            Connection connection = dataSource.getConnection();
            String sql = "UPDATE posts SET likes = likes + 1 WHERE post_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setInt(1, pictureID);
            stmt.executeUpdate();
            likesCount++;
            likesIncremented = true;

            return true;
        } catch (Exception e) {
            System.out.println("Error incrementing likes count: " + e.getMessage());
        }

        return false;
    }

    // Getter methods for picture details
    public String getImagePath() { return imagePath; }
    public String getCaption() { return caption; }
    public int getLikesCount() { return likesCount; }
    public List<String> getComments() { return comments; }
    public int getPictureID() { return pictureID; }
    public Timestamp getTimestamp() { return timestamp; }
    public int getOwnerUserId() { return ownerUserId; }

    public String getFormattedTimestamp() {
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }

    public User getOwnerObject() {
        return new User(this.ownerUserId) ;
    }

    public static class Builder {
        private int ownerUserId;
        private int pictureID;
        private String pictureName;
        private String caption;
        private int likes;
        private Timestamp timestamp;

        public Builder ownerUserId(int ownerUserId) {
            this.ownerUserId = ownerUserId;
            return this;
        }

        public Builder pictureID(int pictureID) {
            this.pictureID = pictureID;
            return this;
        }

        public Builder backdrop_path(String pictureName) {
            this.pictureName = pictureName;
            return this;
        }

        public Builder caption(String caption) {
            this.caption = caption;
            return this;
        }

        public Builder likes(int likes) {
            this.likes = likes;
            return this;
        }

        public Builder timestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Picture build() {
            return new Picture(this);
        }
    }
}
