package com.database_project.UI.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DBConnectionPool {
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();

        //be sure to set the environment variables DB_USERNAME and DB_PASSWORD
        //if u don't know how, contact me
        
        String url = "jdbc:mysql://localhost:3306/transitor";
        String user = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

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
