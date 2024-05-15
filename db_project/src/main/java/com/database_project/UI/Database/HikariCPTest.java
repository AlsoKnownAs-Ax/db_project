package com.database_project.UI.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

import javax.sql.DataSource;

public class HikariCPTest {
    //Run this test to check if you successfully connect to the database
    public static void main(String[] args) {
        HikariConfig config = new HikariConfig();
        
        String url = "jdbc:mysql://localhost:3306/quackstagram";
        String user = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

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
