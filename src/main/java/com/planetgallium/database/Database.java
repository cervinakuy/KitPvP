package com.planetgallium.database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private DataSource dataSource;
    private final Map<String, Table> tables;

    public Database(String localDatabaseFileName) {
        this("none", -1, localDatabaseFileName, "none", "none");
    }

    public Database(String host, int port, String database, String username, String password) {
        this.tables = new HashMap<>();

        if (!host.equals("none")) {
            setupMySQL(host, port, database, username, password);
        } else {
            setupSQLite(database);
        }
    }

    private void setupMySQL(String host, int port, String database, String username, String password) {
        try {
            MysqlDataSource dataSource = new MysqlConnectionPoolDataSource(); // new MysqlDataSource();
            dataSource.setServerName(host);
            dataSource.setPortNumber(port);
            dataSource.setCreateDatabaseIfNotExist(true); // idk
            dataSource.setDatabaseName(database);
            dataSource.setUser(username);
            dataSource.setPassword(password);

            this.dataSource = dataSource;

            testDatabaseConnection();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("[Database] Connection to MySQL database failed. Using SQLite instead...");
            setupSQLite("storage.db");
        }
    }

    private void setupSQLite(String databaseFileName) {
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl("jdbc:sqlite:" + databaseFileName);

        this.dataSource = sqLiteDataSource;
    }

    private void testDatabaseConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            if (!connection.isValid(1)) {
                throw new SQLException("Could not establish database connection");
            }
        }

    }

    public Table createTable(String tableName, Record masterRecord) {
        Table table = new Table(dataSource, tableName, masterRecord);
        tables.put(tableName, table);
        return table;
    }

    public void deleteTable(String tableName) {
        Table table = tables.get(tableName);
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

    public Table getTable(String tableName) {
        return tables.get(tableName);
    }

    public Map<String, Table> getTables() { return tables; }

}
