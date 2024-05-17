package com.database_project.main_files;

/*
 * This class is used to store the user that is currently logged in.
 */

public class LoggedUserSingleton {
    private static LoggedUserSingleton instance;
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getLoggedUser() {
        return user;
    }

    public static LoggedUserSingleton getInstance() {
        if(instance == null) instance = new LoggedUserSingleton();
        
        return instance;
    }

}
