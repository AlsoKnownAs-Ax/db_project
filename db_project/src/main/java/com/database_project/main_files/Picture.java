
package com.database_project.main_files;

import java.util.List;
import java.util.ArrayList;

// Represents a picture on Quackstagram
public class Picture {
    private static final String BASE_PATH = "img/uploaded/";

    private String imagePath;
    private String caption;
    private int likesCount;
    private List<String> comments;

    public Picture(String pictureName, String caption) {
        this.imagePath = getImagePath(pictureName);
        this.caption = caption;
        this.likesCount = 0;
        this.comments = new ArrayList<>();
    }

    public Picture(String pictureName, String caption, int likes) {
        this.imagePath = getImagePath(pictureName);
        this.caption = caption;
        this.likesCount = likes;
        this.comments = new ArrayList<>();
    }

    private String getImagePath(String pictureName){
        try {
            return getClass().getClassLoader().getResource(BASE_PATH + pictureName).getPath();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid picture name \n" + e.getMessage());
        }
    }
    
    // Add a comment to the picture
    public void addComment(String comment) {
        comments.add(comment);
    }

    // Increment likes count
    public void like() {
        likesCount++;
    }

    // Getter methods for picture details
    public String getImagePath() { return imagePath; }
    public String getCaption() { return caption; }
    public int getLikesCount() { return likesCount; }
    public List<String> getComments() { return comments; }
}
