package com.clunkymachines.lwm2m.manager.repository;

import java.sql.*;

import org.slf4j.*;

public class DatabaseManager {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);

    private static final String DB_FILENAME="database.db";
    private static final String MIGRATION_TABLE="migration";
    private static final String SCHEMA_FILENAME="sql/schema.sql";

    private Connection connection;

    public void start() {

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:"+DB_FILENAME);

            // we found the DB
            LOG.info("SQLite Database opened '{}'", DB_FILENAME);
            // check if the migration table exists, if not we need to create the schema

            try (var st = connection.createStatement()) {
                var result = st.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+MIGRATION_TABLE+"'");
                if(!result.next()) {
                    LOG.info("Schema not initialized, creating");
                    SQLRunner.run(connection, SCHEMA_FILENAME);
                } else {
                    LOG.info("Schema already initialized");
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
