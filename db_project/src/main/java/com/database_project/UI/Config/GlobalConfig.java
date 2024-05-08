package com.database_project.UI.Config;

import java.awt.Color;

import com.database_project.UI.factory.DarkUIFactory;
import com.database_project.UI.factory.LightUIFactory;
import com.database_project.UI.factory.UIfactory;
import com.database_project.managers.NavigationManager;

public class GlobalConfig {
    private static final boolean DEBUG_MODE = true;
    private static boolean lightTheme = false;

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private static final int NAV_ICON_SIZE = 20; // Corrected static size for bottom icons
    private static final int IMAGE_WIDTH = WIDTH - 100; // Width for the image posts
    private static final int IMAGE_HEIGHT = 150; // Height for the image posts
    private static final Color LIKE_BUTTON_COLOR = new Color(255, 90, 95); // Color for the like button
    private static final int PROFILE_IMAGE_SIZE = 80; // Adjusted size for the profile image to match UI
    private static final int GRID_IMAGE_SIZE = WIDTH / 3; // Static size for grid images
    private static final int IMAGE_SIZE = WIDTH / 3; // Size for each image in the grid

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public static int getNavIconSize() {
        return NAV_ICON_SIZE;
    }

    public static int getImageWidth() {
        return IMAGE_WIDTH;
    }

    public static int getImageHeight() {
        return IMAGE_HEIGHT;
    }

    public static Color getLikeButtonColor() {
        return LIKE_BUTTON_COLOR;
    }

    public static int getProfileImageSize() {
        return PROFILE_IMAGE_SIZE;
    }

    public static int getGridImageSize() {
        return GRID_IMAGE_SIZE;
    }

    public static int getImageSize() {
        return IMAGE_SIZE;
    }

    public static boolean debugMode(){
        return DEBUG_MODE;
    }

    public static UIfactory getConfigUIfactory(){
        if(lightTheme) return new LightUIFactory();
        
        return new DarkUIFactory();
    }

    public static void setLightMode(boolean toggle){
        lightTheme = toggle;
        NavigationManager.updateTheme();
    }
}
