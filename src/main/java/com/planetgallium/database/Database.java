package com.planetgallium.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {

    private final DataSource dataSource;
    private final List<Table> tables;

    public Database(String host, int port, String database, String username, String password) {
        this.tables = new ArrayList<>();

        if (!host.equals("none")) {
            // TODO: how to make things async?
            // MySQL
            Properties properties = new Properties();
            properties.setProperty("dataSourceClassName", "org.mariadb.jdbc.MariaDbDataSource");
            properties.setProperty("dataSource.serverName", host);
            properties.setProperty("dataSource.portNumber", String.valueOf(port));
            properties.setProperty("dataSource.user", username);
            properties.setProperty("dataSource.password", password);
            properties.setProperty("dataSource.databaseName", database);

            HikariConfig config = new HikariConfig(properties);
            config.setMaximumPoolSize(10);

            this.dataSource = new HikariDataSource(config);
        } else {
            // SQLite
            SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
            sqLiteDataSource.setUrl("jdbc:sqlite:" + database);

            this.dataSource = sqLiteDataSource;
        }
    }

    public Database(String localDatabaseFileName) {
        this("none", -1, localDatabaseFileName, "none", "none");
    }

    public Table createTable(String tableName, Record masterRecord) {
        Table table = new Table(dataSource, tableName, masterRecord);

        tables.add(table);
        return table;
    }

    public void deleteTable(String tableName) {
        Table table = getTableByName(tableName);
        if (table == null) {
            System.out.printf("[Database] Failed to delete table %s; table not found\n", tableName);
            return;
        }

        String deleteTableQuery = Table.DELETE_TABLE_QUERY.replace("{table_name}", tableName);
        try (Connection connection = dataSource.getConnection() ;
             PreparedStatement statement = connection.prepareStatement(deleteTableQuery)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public Table getTableByName(String tableName) {
        for (Table table : this.tables) {
            if (table.getName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }

}
