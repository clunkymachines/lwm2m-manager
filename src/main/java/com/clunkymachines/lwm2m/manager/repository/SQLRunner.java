package com.clunkymachines.lwm2m.manager.repository;

import java.io.*;
import java.sql.*;

import org.slf4j.*;

public class SQLRunner {

    private static final Logger LOG = LoggerFactory.getLogger(SQLRunner.class);

    public static void run(Connection connection, String scriptFilePath) {
        LOG.info("Running SQL script: '{}'", scriptFilePath);
        var classLoader = SQLRunner.class.getClassLoader();
        var inputStream = classLoader.getResourceAsStream(scriptFilePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("SQL script not found in the classpath: " + scriptFilePath);
        }


        // Read the file content
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line).append("\n");
            }

            // Split statements by semicolon (;), considering possible line breaks
            String[] sqlStatements = sqlBuilder.toString().split(";");

            // Execute each SQL statement
            try (var statement = connection.createStatement()) {
                for (String sql : sqlStatements) {
                    sql = sql.trim();
                    if (!sql.isEmpty()) {
                        statement.execute(sql);
                        LOG.debug("Executed: '{}'", sql);
                    }
                }
            }
            LOG.info("Running SQL script: '{}': done", scriptFilePath);

        } catch (IOException | SQLException ex) {
            throw new IllegalStateException("Impossible to run the SQL script: " + scriptFilePath, ex);
        }
    }
}
