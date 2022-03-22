package com.planetgallium.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Table {

    private final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS {table_name}" +
            "({columns_with_data_types}, PRIMARY KEY ({primary_key}));";
    public final static String DELETE_TABLE_QUERY = "DROP TABLE IF EXISTS {table_name}";
    private final static String INSERT_RECORD_QUERY = "INSERT INTO {table_name} ({columns}) VALUES ({values});";
    private final static String GET_RECORD_QUERY = "SELECT * FROM {table_name} WHERE `{primary_field_name}`=?;";
    private final static String DELETE_RECORD_QUERY = "DELETE FROM {table_name} WHERE `{primary_field_name}`=?;";

    private final DataSource dataSource;

    private final String name;
    private final Record masterRecord;
    private final Field primaryKey;

    public Table(DataSource dataSource, String name, Record masterRecord) {
        this.dataSource = dataSource;
        this.name = name;
        this.masterRecord = masterRecord;
        this.primaryKey = masterRecord.getFields().get(0);

        String createTableQuery = Table.CREATE_TABLE_QUERY.replace("{table_name}", this.getName())
                .replace("{columns_with_data_types}", formatFieldNamesWithDataTypes(masterRecord))
                .replace("{primary_key}", primaryKey.getName());
        try (Connection connection = dataSource.getConnection() ;
             PreparedStatement statement = connection.prepareStatement(createTableQuery)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void insertRecord(Field ... fields) {
        Record record = new Record();
        for (Field field : fields) {
            record.addData(field.getName(), field.getDataType(), field.getValue());
        }
        insertRecord(record);
    }

    public void insertRecord(Record recordToInsert) {
        if (getRecord(recordToInsert.getFields().get(0)) != null) {
            System.out.printf("[Database] Failed to perform action with record %s; already present in database\n",
                    recordToInsert.getFields().get(0).getName());
            return;
        }

        String columns = String.join(", ", recordFieldsToList(recordToInsert, true));
        String values = String.join(", ", recordFieldsToList(recordToInsert, false));
        String insertRecordQuery = Table.INSERT_RECORD_QUERY.replace("{table_name}", this.getName())
                .replace("{columns}", columns)
                .replace("{values}", values);
        try (Connection connection = dataSource.getConnection() ;
             PreparedStatement statement = connection.prepareStatement(insertRecordQuery)) {

            for (int i = 0; i < recordToInsert.getFields().size(); i++) {
                Field field = recordToInsert.getFields().get(i);
                setStatementParameterToType(statement, i + 1, field);
            }

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

    }

    public Record getRecord(Field keyToRecord) {
        if (!verifyPrimaryKey(this, keyToRecord)) return null;

        String getRecordQuery = Table.GET_RECORD_QUERY.replace("{table_name}", this.getName())
                .replace("{primary_field_name}", keyToRecord.getName());
        try (Connection connection = dataSource.getConnection() ;
             PreparedStatement statement = connection.prepareStatement(getRecordQuery)) {
            setStatementParameterToType(statement, 1, keyToRecord);

            ResultSet resultSet = statement.executeQuery();
            Record recordToReturn = new Record();
            if (!resultSet.next()) { // ResultSet (meaning usually database/table) is empty
                return null;
            }

            for (int i = 0; i < masterRecord.getFields().size(); i++) {
                Field field = masterRecord.getFields().get(i);
                Object data = null;
                if (field.getDataType() == DataType.STRING) {
                    data = resultSet.getString(field.getName());
                } else if (field.getDataType() == DataType.INTEGER) {
                    data = resultSet.getInt(field.getName());
                } else if (field.getDataType() == DataType.FLOAT) {
                    data = resultSet.getFloat(field.getName());
                }
                recordToReturn.addData(field.getName(), field.getDataType(), data);
            }
            return recordToReturn;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void deleteRecord(Field keyOfRecordToDelete) {
        if (!verifyPrimaryKey(this, keyOfRecordToDelete)) return;

        String deletionQuery = Table.DELETE_RECORD_QUERY.replace("{table_name}", this.getName())
                .replace("{primary_field_name}", keyOfRecordToDelete.getName());
        try (Connection connection = dataSource.getConnection() ;
             PreparedStatement statement = connection.prepareStatement(deletionQuery)) {
            setStatementParameterToType(statement, 1, keyOfRecordToDelete);
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private List<String> recordFieldsToList(Record record, boolean fieldNames) {
        List<String> fields = new ArrayList<>();
        for (Field field : record.getFields()) {
            fields.add(fieldNames ? field.getName() : "?");
        }
        return fields;
    }

    /**
     * Converts columns [fields] (ex: uuid, kills, deaths) to string:
     * "uuid VARCHAR, kills INT, deaths INT"
     * to be used in SQL statements
     */
    private String formatFieldNamesWithDataTypes(Record record) {
        String result = "";
        for (Field field : record.getFields()) {
            result += String.format(", %s %s", field.getName(), field.getSQLDataType());
        }
        return result.length() > 0 ? result.substring(2) : result;
    }

    private void setStatementParameterToType(PreparedStatement statement, int parameterIndex, Field primaryKey)
            throws SQLException {
        DataType dataType = primaryKey.getDataType();
        String valueAsString = primaryKey.getValue().toString();

        if (dataType == DataType.STRING) {
            statement.setString(parameterIndex, valueAsString);
        } else if (dataType == DataType.INTEGER) {
            statement.setInt(parameterIndex, Integer.parseInt(valueAsString));
        } else if (dataType == DataType.FLOAT) {
            statement.setFloat(parameterIndex, Float.parseFloat(valueAsString));
        }
    }

    private boolean verifyPrimaryKey(Table table, Field keyToValidate) {
        if (!keyToValidate.getName().equals(table.getPrimaryKey().getName())) {
            System.out.printf("[Database] Failed to perform action with key %s; did not match table primary key\n",
                    keyToValidate.getName());
            return false;
        }
        return true;
    }

    public String getName() { return name; }

    public Field getPrimaryKey() { return primaryKey; }

}
