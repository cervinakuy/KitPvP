package com.zp4rker.localdb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.planetgallium.kitpvp.util.PlayerEntry;
import com.zp4rker.localdb.db.SQLite;

public class Table {

    private String name;
    private List<Column> columns = new ArrayList<>();
    private Column primaryKey;
    private SQLite sqLite;

    public Table(String name, Column... columns) {
        this.name = name;
        this.primaryKey = columns[0];
        this.columns.addAll(Arrays.asList(columns));
        this.primaryKey = this.columns.get(0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public Column getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Column primaryKey) {
        this.primaryKey = primaryKey;
    }

    private static String getStringType(DataType dataType) {
        switch(dataType) {
            case STRING: return "VARCHAR";
            case INTEGER: return "INT";
            case FLOAT: return "FLOAT";
            default: return null;
        }
    }

    public void setSqLite(SQLite sqLite) {
        this.sqLite = sqLite;
    }

    public String getQuery() {
        String query = "CREATE TABLE IF NOT EXISTS " + getName() + " (";
        for (Column column : getColumns()) {
            query += "`" + column.name + "` ";
            query += Table.getStringType(column.dataType);
            query += (column.limit > 0 ? " (" + column.limit + "), " : ", ");
        }
        query += "PRIMARY KEY (`" + primaryKey.getName() + "`)";
        query += ");";
        return query;
    }

    public String getDeleteQuery() {
        String query = "DROP TABLE IF EXISTS " + getName();
        return query;
    }

    public void insert(Column... columns) {
        List<Column> columnList = new ArrayList<>(Arrays.asList(columns));
        insert(columnList);
    }

    private void insert(List<Column> columns) {
        if (getExact(columns.get(0)) == null) {
            String query = "INSERT INTO " + getName() + " (";
            for (Column column : columns) {
                if (columns.indexOf(column) < columns.size() - 1) {
                    query += "`" + column.getName() + "`, ";
                } else {
                    query += "`" + column.getName() + "`) ";
                }
            }
            query += "VALUES (";
            for (int i = 0; i < columns.size(); i++) {
                if (i < columns.size() - 1) {
                    query += "?, ";
                } else {
                    query += "?)";
                }
            }
            query += ";";
            try {
                PreparedStatement s = sqLite.getSQLConnection().prepareStatement(query);
                for (int i = 0; i < columns.size(); i++) {
                    if (columns.get(i).dataType == DataType.STRING) {
                        s.setString(i + 1, columns.get(i).getValue().toString());
                    } else if (columns.get(i).dataType == DataType.INTEGER) {
                        s.setInt(i + 1, Integer.parseInt(columns.get(i).getValue().toString()));
                    } else {
                        s.setFloat(i + 1, Float.parseFloat(columns.get(i).getValue().toString()));
                    }
                }
                s.executeUpdate();
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("A row with that name already exists!");
        }
    }
    
    public List<Column> getExact(Column column) {
        List<Column> result = new ArrayList<>();
        String query = "SELECT * FROM " + getName() + " WHERE `" + column.getName() + "`=?";
        try {
            PreparedStatement s = sqLite.getSQLConnection().prepareStatement(query);
            if (column.dataType == DataType.STRING) {
                s.setString(1, column.getValue().toString());
            } else if (column.dataType == DataType.INTEGER) {
                s.setInt(1, Integer.parseInt(column.getValue().toString()));
            } else {
                s.setFloat(1, Float.parseFloat(column.getValue().toString()));
            }
            ResultSet rs = s.executeQuery();
            try {
                for (int i = 0; i < getColumns().size(); i++) {
                    Column rCol = new Column(getColumns().get(i).getName(), getColumns().get(i).dataType,
                            getColumns().get(i).limit);
                    if (rCol.dataType == DataType.STRING) {
                        rCol.setValue(rs.getString(i + 1));
                    } else if (rCol.dataType == DataType.INTEGER) {
                        rCol.setValue(rs.getInt(i + 1));
                    } else {
                        rCol.setValue(rs.getFloat(i + 1));
                    }
                    result.add(rCol);
                }
                sqLite.close(s, rs);
            } catch (SQLException e) {
                s.close();
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<PlayerEntry> getTopN(Column sortColumn, Column returnColumn, int n) {
        // Admittedly this method is more tailored towards KitPvP leaderboards, but can be repurposed for other uses
        String query = "SELECT %return_column_name%, %sort_column_name% FROM %table_name% ORDER BY %sort_column_name% DESC LIMIT %n%"
                .replace("%return_column_name%", returnColumn.getName())
                .replace("%table_name%", getName())
                .replace("%sort_column_name%", sortColumn.getName())
                .replace("%n%", String.valueOf(n));
        List<PlayerEntry> topNResults = new ArrayList<>();

        try {
            PreparedStatement statement = sqLite.getSQLConnection().prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                topNResults.add(new PlayerEntry(resultSet.getString(returnColumn.getName()),
                                                resultSet.getInt(sortColumn.getName())));
            }
            sqLite.close(statement, resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topNResults;
    }
    
    public List<List<Column>> search(Column column) {
        List<List<Column>> results = new ArrayList<>();
        if (!column.getName().equalsIgnoreCase(primaryKey.getName())) {
            String query = "SELECT * FROM " + getName() + " WHERE `" + column.getName() + "`=?";
            try {
                PreparedStatement s = sqLite.getSQLConnection().prepareStatement(query);
                // TODO: use setStatementType
                if (column.dataType == DataType.STRING) {
                    s.setString(1, column.getValue().toString());
                } else if (column.dataType == DataType.INTEGER) {
                    s.setInt(1, Integer.parseInt(column.getValue().toString()));
                } else {
                    s.setFloat(1, Float.parseFloat(column.getValue().toString()));
                }
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    List<Column> result = new ArrayList<>();
                    for (int i = 0; i < getColumns().size(); i++) {
                        Column rCol = new Column(getColumns().get(i).getName(), getColumns().get(i).dataType,
                                getColumns().get(i).limit);
                        if (getColumns().get(i).dataType == DataType.STRING) {
                            rCol.setValue(rs.getString(i + 1));
                        } else if (getColumns().get(i).dataType == DataType.INTEGER) {
                            rCol.setValue(rs.getInt(i + 1));
                        } else {
                            rCol.setValue(rs.getFloat(i + 1));
                        }
                        result.add(rCol);
                    }
                    results.add(result);
                }
                sqLite.close(s, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return results;
        } else {
            return null;
        }
    }

    public boolean containsColumn(Column column) {

        return getExact(column) != null;

    }

    private void setStatementType(PreparedStatement preparedStatement, Column column) {

        Object value = column.getValue();
        DataType type = column.getDataType();

        try {
            if (type == DataType.STRING) {
                preparedStatement.setString(1, value.toString());
            } else if (type == DataType.INTEGER) {
                preparedStatement.setInt(1, Integer.parseInt(value.toString()));
            } else if (type == DataType.FLOAT) {
                preparedStatement.setFloat(1, Float.parseFloat(value.toString()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<List<Column>> getAll() {
        List<List<Column>> results = new ArrayList<>();
        String query = "SELECT * FROM " + getName();
        try {
            PreparedStatement s = sqLite.getSQLConnection().prepareStatement(query);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                List<Column> result = new ArrayList<>();
                for (int i = 0; i < getColumns().size(); i++) {
                    Column rCol = new Column(getColumns().get(i).getName(), getColumns().get(i).dataType,
                            getColumns().get(i).limit);
                    if (getColumns().get(i).dataType == DataType.STRING) {
                        s.setString(1, getColumns().get(i).getValue().toString());
                    } else if (getColumns().get(i).dataType == DataType.INTEGER) {
                        s.setInt(1, Integer.parseInt(getColumns().get(i).getValue().toString()));
                    } else {
                        s.setFloat(1, Float.parseFloat(getColumns().get(i).getValue().toString()));
                    }
                    result.add(rCol);
                }
                results.add(result);
            }
            sqLite.close(s, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void delete(Column column) {
        if (column.getName().equalsIgnoreCase(primaryKey.getName())) {
            String query = "DELETE FROM " + getName() + " WHERE `" + column.getName() + "`=?";
            try {
                PreparedStatement s = sqLite.getSQLConnection().prepareStatement(query);
                if (column.dataType == DataType.STRING) {
                    s.setString(1, column.getValue().toString());
                } else if (column.dataType == DataType.INTEGER) {
                    s.setInt(1, Integer.parseInt(column.getValue().toString()));
                } else {
                    s.setFloat(1, Float.parseFloat(column.getValue().toString()));
                }
                s.executeUpdate();
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Primary key must be used!");
        }
    }

    public void update(Column primaryKey, Column... columns) {
        List<Column> columnList = new ArrayList<>(Arrays.asList(columns));
        update(primaryKey, columnList);
    }

    private void update(Column primaryKey, List<Column> columns) {
        if (!containsKey(columns)) {
            String query = "UPDATE " + getName() + " SET ";
            for (Column column : columns) {
                if (column.dataType == DataType.STRING) {
                    query += "`" + column.getName() + "`='" + column.getValue().toString() + "'";
                } else {
                    query += "`" + column.getName() + "`=" + column.getValue().toString();
                }
                if (columns.indexOf(column) == columns.size() - 1) {
                    query += " ";
                } else {
                    query += ", ";
                }
            }
            query += "WHERE `" + primaryKey.getName() + "`=";
            if (primaryKey.dataType == DataType.STRING) {
                query += "'" + primaryKey.getValue().toString() + "'";
            } else {
                query += primaryKey.getValue().toString();
            }
            try {
                PreparedStatement s = sqLite.getSQLConnection().prepareStatement(query);
                s.executeUpdate();
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            List<Column> newColumns = getExact(primaryKey);
            for (Column column : columns) {
                if (column.getName().equalsIgnoreCase(primaryKey.getName())) {
                    newColumns.set(0, column);
                } else {
                    newColumns.set(columns.indexOf(column), column);
                }
            }
            delete(primaryKey);
            insert(newColumns);
        }
    }

    private boolean containsKey(List<Column> columns) {
        for (Column column : columns) {
            if (column.getName().equalsIgnoreCase(primaryKey.getName())) {
                return true;
            }
        }
        return false;
    }

}
