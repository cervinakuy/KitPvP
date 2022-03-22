package com.planetgallium.database;

public class Test {

    public static void main(String[] args) {

        Database database = new Database("storage.db");
        Table statsTable = database.createTable("stats",
                new Record(new Field("uuid", DataType.STRING),
                        new Field("kills", DataType.INTEGER),
                        new Field("deaths", DataType.INTEGER)));

        statsTable.insertRecord(
                new Field("uuid", DataType.STRING, "uuid-is-here-like-this"),
                new Field("kills", DataType.INTEGER, -1),
                new Field("deaths", DataType.INTEGER, 3434));

    }

}
