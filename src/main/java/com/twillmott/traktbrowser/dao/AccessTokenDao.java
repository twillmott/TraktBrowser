package com.twillmott.traktbrowser.dao;

import com.uwetrottmann.trakt5.entities.AccessToken;

import java.sql.*;

/**
 * Database access layer for the Trakt access token table.
 * Created by tomwi on 30/12/2016.
 */
public class AccessTokenDao {
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:~/traktbrowser";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    private static final String CREATE_TABLE_STATEMENT =
                    "CREATE TABLE IF NOT EXISTS " +
                    "`AccessToken` ( " +
                    "`id` INT NOT NULL, " +
                    "`accessToken` VARCHAR(255), " +
                    "`tokenType` VARCHAR(255) NOT NULL, " +
                    "`expiresIn` INT NOT NULL, " +
                    "`refreshToken` VARCHAR(255) NOT NULL, " +
                    "`scope` VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY (`id`) " +
                    ");";

    /**
     * @return The {@link com.uwetrottmann.trakt5.entities.AccessToken}.
     * We only return one token as there should just be one saved in the table.
     */
    public AccessToken getAccessToken() {

        String selectQuery = "SELECT * FROM Accesstoken LIMIT 1";

        Connection connection = getDBConnection();
        PreparedStatement preparedStatement;

        AccessToken accessToken = new AccessToken();

        try {
            preparedStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                accessToken.access_token = resultSet.getString("accessToken");
                accessToken.token_type = resultSet.getString("tokenType");
                accessToken.expires_in = resultSet.getInt("expiresIn");
                accessToken.refresh_token = resultSet.getString("refreshToken");
                accessToken.scope = resultSet.getString("scope");
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        }
        return accessToken;
    }

    /**
     * @param accessToken - The accessToken to save to the database.
     */
    public void saveAccessToken(AccessToken accessToken) {

        String deleteQuery = "DELETE FROM AccessToken";
        String insertQuery = "INSERT INTO AccessToken VALUES(?,?,?,?,?,?)";

        Connection connection = getDBConnection();
        PreparedStatement preparedStatement;

        try {
            // Create the table if it doesn't exist.
            connection.createStatement().execute(CREATE_TABLE_STATEMENT);
            // Delete all previous access tokens.
            connection.createStatement().execute(deleteQuery);
            // Save the new token.
            preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, accessToken.access_token);
            preparedStatement.setString(3, accessToken.token_type);
            preparedStatement.setInt(4, accessToken.expires_in);
            preparedStatement.setString(5, accessToken.refresh_token);
            preparedStatement.setString(6, accessToken.scope);
            preparedStatement.execute();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        }
    }

    /**
     * @return A connection to the database.
     */
    private Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER,
                    DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }
}
