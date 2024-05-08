package com.database_project.managers;

import com.database_project.UI.ExplorePage;
import com.database_project.UI.HomePage;
import com.database_project.UI.InstaProfileUI;
import com.database_project.UI.LoginPage;
import com.database_project.UI.NotificationsPage;
import com.database_project.UI.ResgisterPage;
import com.database_project.UI.UploadPage;
import com.database_project.UI.Config.GlobalConfig;
import com.database_project.UI.factory.UIfactory;

public class NavigationManager {
    private static UIfactory uIfactory = GlobalConfig.getConfigUIfactory();

    private static LoginPage loginPage = new LoginPage(uIfactory);
    private static ResgisterPage registerPage = new ResgisterPage(uIfactory);
    private static ExplorePage explorePage = new ExplorePage();
    private static UploadPage uploadPage = new UploadPage();
    private static NotificationsPage notificationsPage = new NotificationsPage();
    private static HomePage homePage = new HomePage();
    private static InstaProfileUI currentProfile;

    public static void setProfilePage(InstaProfileUI _profilePage) {
        currentProfile = _profilePage;
    }
    
    public NavigationManager(){}

    public static void showLoginPage(){
        hideAllPages();
        loginPage.setVisible(true);
    }

    public static void showRegisterPage(){
        hideAllPages();
        registerPage.setVisible(true);
    }

    public static void showExplorePage(){
        hideAllPages();
        explorePage.setVisible(true);
    }

    public static void showHomePage(){
        hideAllPages();
        homePage.setVisible(true);
    }

    public static void showUploadPage(){
        hideAllPages();
        uploadPage.setVisible(true);
    }

    public static void showNotificationsPage(){
        hideAllPages();
        notificationsPage.setVisible(true);
    }

    public static void hideAllPages(){
        loginPage.setVisible(false);
        registerPage.setVisible(false);
        explorePage.setVisible(false);
        homePage.setVisible(false);
        uploadPage.setVisible(false);
        notificationsPage.setVisible(false);
        
        if(currentProfile != null){
            currentProfile.dispose();
        }
    }

    public static void updateTheme(){
        uIfactory = GlobalConfig.getConfigUIfactory();

        loginPage = new LoginPage(uIfactory);
        registerPage = new ResgisterPage(uIfactory);
        explorePage = new ExplorePage();
        uploadPage = new UploadPage();
        notificationsPage = new NotificationsPage();
        homePage = new HomePage();
    }

}
