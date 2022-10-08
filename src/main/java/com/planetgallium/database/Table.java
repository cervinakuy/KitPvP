package com.planetgallium.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Table {

    private final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS {table_name} " +
            "({columns_with_data_types}, PRIMARY KEY ({primary_key}));";
    public final static String DELETE_TABLE_QUERY = "DROP TABLE IF EXISTS {table_name};";
    private final static String INSERT_RECORD_QUERY = "INSERT INTO {table_name} ({columns}) VALUES ({values});";
    private final static String UPDATE_RECORD_QUERY = "UPDATE {table_name} SET {columns_with_values} " +
            "WHERE `{primary_key}`='{primary_key_value}';";
    private final static String SEARCH_RECORD_QUERY = "SELECT * FROM {table_name} WHERE {column_to_search_name}=?;";
    private final static String GET_RECORD_QUERY = "SELECT * FROM {table_name} WHERE `{primary_field_name}`=?;";
    private final static String DELETE_RECORD_QUERY = "DELETE FROM {table_name} WHERE `{primary_field_name}`=?;";
    private final static String TOP_N_RECORD_QUERY = "SELECT {return_column_name}, {sort_column_name} FROM " +
            "{table_name} ORDER BY {sort_column_name} DESC LIMIT {n};";

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
        if (!fields[0].getName().equals(primaryKey.getName())) {
            System.out.printf("[Database] Failed to insert record; primary key %s does not match table key\n",
                    fields[0].getName());
            return;
        }

        Record record = new Record();
        for (Field field : fields) {
            record.addOrUpdateData(field.getName(), field.getDataType(), field.getValue());
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

    /**
     * Updates an existing record (row).
     * If the fields parameter contains the primary key field (ex: uuid), it will update its key value to that
     * given in the fields
     * It will NOT change the primary key column for all database entries in any way
     */
    public void updateRecord(Field keyOfRecordToUpdate, Field ... fields) {
        if (!containsPrimaryKey(fields)) {
            String updateQuery = Table.UPDATE_RECORD_QUERY.replace("{table_name}", this.getName())
                    .replace("{columns_with_values}", formatFieldNamesWithValues(fields))
                    .replace("{primary_key}", primaryKey.getName())
                    .replace("{primary_key_value}", keyOfRecordToUpdate.getValue().toString());
            try (Connection connection = dataSource.getConnection() ;
                 PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } else {
            Record newUpdatedRecord = getRecord(keyOfRecordToUpdate);
            for (Field field : fields) {
                newUpdatedRecord.addOrUpdateData(field.getName(), field.getDataType(), field.getValue());
            }

            deleteRecord(keyOfRecordToUpdate);
            insertRecord(newUpdatedRecord);
        }
    }

    public void updateRecord(Record record) {
        Field keyOfRecordToUpdate = record.getFields().get(0); // NOTE: assuming first field is always key
        List<Field> fieldsWithoutKey = record.getFieldsWithoutPrimaryKey();

        updateRecord(keyOfRecordToUpdate, fieldsWithoutKey.toArray(new Field[0]));
    }

    public void updateOrInsertRecord(Record record) {
        if (getRecord(record.getFields().get(0)) == null) {
            insertRecord(record);
        } else {
            updateRecord(record);
        }
    }

    /**
     * Searches the database for any records with the matching field value. For example, the field value
     * could be a username "cervinakuy", of which there should only be 1 record that contains that username
     * value. Alternatively, could return a list of multiple elements with all matching records. For example,
     * searching the database with the field name "kills" and value 6 would return all the records that have
     * 6 kills.
     *
     * Do NOT use if fieldToSearchFor is the primary key. In that case, use getRecord instead
     */
    public List<Record> searchRecords(Field fieldToSearchFor) {
        List<Record> matchingRecords = new ArrayList<>();
        if (fieldToSearchFor.getName().equals(this.getPrimaryKey().getName())) {
            System.out.println("[DATABASE] Primary key search detected. Should use getRecord instead.");
            return matchingRecords; // should use getRecord instead
        }

        String searchQuery = Table.SEARCH_RECORD_QUERY
                .replace("{table_name}", this.getName())
                .replace("{column_to_search_name}", fieldToSearchFor.getName());
        try (Connection connection = dataSource.getConnection() ;
             PreparedStatement statement = connection.prepareStatement(searchQuery)) {
            setStatementParameterToType(statement, 1, fieldToSearchFor);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Record recordResult = new Record();
                for (int i = 0; i < masterRecord.getFields().size(); i++) {
                    Field field = masterRecord.getFields().get(i);
                    recordResult.addOrUpdateData(field.getName(), field.getDataType(),
                            setObjectType(field, resultSet));
                }
                matchingRecords.add(recordResult);
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return matchingRecords;
    }

    public Record getRecord(Field keyToRecord) {
        if (!verifyPrimaryKey(this, keyToRecord)) return null;

        String getRecordQuery = Table.GET_RECORD_QUERY
                .replace("{table_name}", this.getName())
                .replace("{primary_field_name}", keyToRecord.getName());
        try (Connection connection = dataSource.getConnection() ;
             PreparedStatement statement = connection.prepareStatement(getRecordQuery)) {
            setStatementParameterToType(statement, 1, keyToRecord);

            ResultSet resultSet = statement.executeQuery();
            Record recordToReturn = new Record();
            if (resultSet.next()) {
                for (int i = 0; i < masterRecord.getFields().size(); i++) {
                    Field field = masterRecord.getFields().get(i);
                    recordToReturn.addOrUpdateData(field.getName(), field.getDataType(),
                            setObjectType(field, resultSet));
                }
                return recordToReturn;
            }
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

    public List<TopEntry> getTopN(Field sortField, Field returnField, int n) {
        String topNQuery = Table.TOP_N_RECORD_QUERY.replace("{table_name}", this.getName())
                .replace("{return_column_name}", returnField.getName())
                .replace("{sort_column_name}", sortField.getName())
                .replace("{n}", String.valueOf(n));

        List<TopEntry> topNResults = new ArrayList<>();
        try (Connection connection = dataSource.getConnection() ;
             PreparedStatement statement = connection.prepareStatement(topNQuery)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                topNResults.add(new TopEntry(resultSet.getString(returnField.getName()),
                        resultSet.getInt(sortField.getName())));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return topNResults;
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
            if (field.getDataType() == DataType.FIXED_STRING ||
                    field.getDataType() == DataType.STRING) {
                result += String.format("(%d)", field.getLimit()); // for "VARCHAR(25)" or "CHAR(25)" for example
            }
        }
        return result.length() > 0 ? result.substring(2) : result;
    }

    /**
     * Converts fields with values (ex: kills=16, experience=85, etc) to one string
     * "kills=16 experience=85"
     * to be used in SQL statements
     */
    private String formatFieldNamesWithValues(Field ... fields) {
        String result = "";
        for (Field field : fields) {
            result += String.format(", %s=%s", field.getName(), field.getValue().toString());
        }
        return result.length() > 0 ? result.substring(2) : result;
    }

    private Object setObjectType(Field field, ResultSet resultSet) throws SQLException {
        DataType dataType = field.getDataType();
        String fieldName = field.getName();
        Object data = null;

        if (dataType == DataType.STRING) {
            data = resultSet.getString(fieldName);
        } else if (dataType == DataType.INTEGER) {
            data = resultSet.getInt(fieldName);
        } else if (dataType == DataType.FLOAT) {
            data = resultSet.getFloat(fieldName);
        }
        return data;
    }

    private void setStatementParameterToType(PreparedStatement statement, int parameterIndex, Field field)
            throws SQLException {
        DataType dataType = field.getDataType();
        String valueAsString = field.getValue().toString();

        if (dataType == DataType.STRING || dataType == DataType.FIXED_STRING) {
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

    private boolean containsPrimaryKey(Field ... fields) {
        for (Field field : fields) {
            if (field.getName().equals(primaryKey.getName())) {
                return true;
            }
        }
        return false;
    }

    public String getName() { return name; }

    public Field getPrimaryKey() { return primaryKey; }

}
