package com.database_project.UI.utils;

import com.database_project.UI.Config.GlobalConfig;

public class Debug {

    private static boolean debugMode = GlobalConfig.debugMode();
    
    public Debug(){}

    public void print(String DebugTitle ,String ...args){
        if( !debugMode ) return;

        System.out.println("---------------- DEBUG: " + DebugTitle + " ----------------");
        for (String string : args) {
            System.out.println(string);
        }
        System.out.println("-----------------------------------------------------------\n");
    }
}
