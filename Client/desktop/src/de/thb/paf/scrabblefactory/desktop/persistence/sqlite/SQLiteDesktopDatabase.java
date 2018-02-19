package de.thb.paf.scrabblefactory.desktop.persistence.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.thb.paf.scrabblefactory.persistence.entities.IDBEntity;
import de.thb.paf.scrabblefactory.persistence.sqlite.ISQLiteDatabase;
import de.thb.paf.scrabblefactory.persistence.sqlite.SQLiteORMapper;


/**
 * Represents a SQLite database for desktop systems.
 *
 * @author Dominic Schiller - Technische Hochschule Brandenburg
 * @version 1.0
 * @since 1.0
 */

public class SQLiteDesktopDatabase implements ISQLiteDatabase {

    /**
     * The database's URL including the database's name
     */
    private String databaseURL;

    /**
     * The database's version
     */
    private int databaseVersion;

    /**
     * The SQL statement to execute when creating the database
     */
    private String onCreateQuery;

    /**
     * The SQL statement to execute when upgrading the database
     */
    private String onUpgradeQuery;

    /**
     * The JDBC database connection instance
     */
    private Connection connection;

    /**
     * Constructor
     * @param databaseURL The database's URL including the database's name
     * @param databaseVersion The database's version
     * @param onCreateQuery The SQL statement to execute when creating the database
     * @param onUpgradeQuery The SQL statement to execute when upgrading the database
     */
    public SQLiteDesktopDatabase(
            String databaseURL,
            int databaseVersion,
            String onCreateQuery,
            String onUpgradeQuery
    ) {
        this.databaseURL = databaseURL;
        this.databaseVersion = databaseVersion;
        this.onCreateQuery = onCreateQuery;
        this.onUpgradeQuery = onUpgradeQuery;
    }

    @Override
    public void setup() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.executeDDL(this.onCreateQuery);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.databaseURL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeDDL(String sql) {
        this.open();

        try {
            Statement statement = this.connection.createStatement();

            String[] queries = sql.split(";");
            if(queries.length > 1) {
                for(String query : queries) {
                    statement.addBatch(query);
                }
                statement.executeBatch();
            } else {
                statement.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.close();
    }

    @Override
    public IDBEntity executeInsertOrUpdate(String sql, IDBEntity entity) {
        this.open();

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setID(generatedKeys.getInt(1));
                    }
                    else {
                        throw new SQLException("Create or updating the entity failed");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entity;
    }

    @Override
    public List<IDBEntity> executeSelect(String sql, Class<?> entityType) {
        this.open();

        List<IDBEntity> selectedEntities = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                Object entityInstance = SQLiteORMapper.createInstanceFromClassType(entityType);
                SQLiteORMapper.applyResultSet(rs, entityInstance);
                selectedEntities.add((IDBEntity)entityInstance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.close();

        return selectedEntities;
    }

    @Override
    public void close() {

    }
}