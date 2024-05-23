package com.database_project.UI.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;

import javax.sql.DataSource;
import java.net.URL;
import java.io.File;

public class HikariCPTest {
    //Run this test to check if you successfully connect to the database

    private static String getResourcePath() {
        URL resource = HikariCPTest.class.getClassLoader().getResource("");
        if (resource == null) {
            throw new IllegalArgumentException("Resource directory not found.");
        }
        return new File(resource.getFile()).getAbsolutePath();
    }
    public static void main(String[] args) {
        HikariConfig config = new HikariConfig();
        
        String url = "jdbc:mysql://localhost:3306/quackstagram";
        Dotenv dotenv = Dotenv.configure()
                              .directory(getResourcePath())  // Get the path from the classloader
                              .load();
        
        String user = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");

        System.out.println("\n=== Database Connection Test ===");
        System.out.println("Username: " + user);
        System.out.println("password: " + password);
        System.out.println("=================================");

        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);

        @SuppressWarnings("resource")
        DataSource dataSource = new HikariDataSource(config);

        try (Connection con = dataSource.getConnection()) {
            System.out.println("HikariCP connection test successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
