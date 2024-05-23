package com.database_project.UI.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.net.URL;

import javax.sql.DataSource;

public class DBConnectionPool {
    private static final HikariDataSource dataSource;

    private static String getResourcePath() {
        URL resource = DBConnectionPool.class.getClassLoader().getResource("");
        if (resource == null) {
            throw new IllegalArgumentException("Resource directory not found.");
        }
        return new File(resource.getFile()).getAbsolutePath();
    }

    static {
        HikariConfig config = new HikariConfig();
        
        String url = "jdbc:mysql://localhost:3306/quackstagram";
        Dotenv dotenv = Dotenv.configure()
                              .directory(getResourcePath())  // Get the path from the classloader
                              .load();
        
        String user = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");

        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        
        config.setMaximumPoolSize(10);
        config.setAutoCommit(true);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
