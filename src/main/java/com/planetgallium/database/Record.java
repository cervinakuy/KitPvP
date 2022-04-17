package com.planetgallium.database;

import java.util.*;

// aka a "row" or "line" in a table
public class Record {

    private final List<Field> fields;

    public Record() {
        this.fields = new ArrayList<>();
    }

    public Record(Field ... fields) {
        this.fields = new ArrayList<>();
        this.fields.addAll(Arrays.asList(fields));
    }

    /**
     * Adds data to a row. If a column is already present, its data will
     * be updated.
     */
    public void addOrUpdateData(String columnName, DataType dataType, Object data) {
        Field field = new Field(columnName, dataType, data);
        int fieldIndex = getFieldIndexByName(columnName);
        if (fieldIndex == -1) {
            this.fields.add(field);
        } else {
            this.fields.set(fieldIndex, field);
        }
    }

    public Object getFieldValue(String fieldName) {
        Field field = getFieldByName(fieldName);
        if (field != null) {
            return field.getValue();
        }
        return null;
    }

    private int getFieldIndexByName(String fieldName) {
        for (int i = 0; i < this.fields.size(); i++) {
            if (fields.get(i).getName().equals(fieldName)) {
                return i;
            }
        }
        return -1;
    }

    private Field getFieldByName(String fieldName) {
        int fieldIndex = getFieldIndexByName(fieldName);
        if (fieldIndex != -1) {
            return this.fields.get(fieldIndex);
        }
        return null;
    }

    public List<Field> getFields() { return fields; }

    public List<Field> getFieldsWithoutPrimaryKey() { return fields.subList(1, fields.size()); }

}
