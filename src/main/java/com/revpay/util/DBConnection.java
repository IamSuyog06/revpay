package com.revpay.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Properties props = new Properties();

    static{
        try{
            InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            props.load(input);
            Class.forName(props.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load DB Properties",e);
        }
    }

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(
                props.getProperty("jdbc.url"),
                props.getProperty("jdbc.user"),
                props.getProperty("jdbc.password")
        );
    }
}
