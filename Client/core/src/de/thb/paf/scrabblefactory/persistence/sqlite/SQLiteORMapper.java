package de.thb.paf.scrabblefactory.persistence.sqlite;


import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Basic O/R-Mapper which helps to map a SQLite result set to a class' instance.
 *
 * @author Dominic Schiller - Technische Hochschule Brandenburg
 * @version 1.0
 * @since 1.0
 */

public class SQLiteORMapper {

    /**
     * Loop over a SQLite result set and fill a specified instance with the retrieved data.
     * @param resultSet The SQLite result set
     * @param instance The instance to fill with the retrieved data
     */
    public static synchronized void applyResultSet(ResultSet resultSet, Object instance) {
        Field[] instanceFields = instance.getClass().getDeclaredFields();
        for(Field field : instanceFields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String fieldType = field.getType().getName();

            try {
                if(fieldType.contains("int")) {
                    field.set(instance, resultSet.getInt(fieldName));
                } else if(fieldType.contains("String")) {
                    field.set(instance, resultSet.getString(fieldName));
                } else if(fieldType.contains("Date")) {
                    Date date = new Date(resultSet.getLong(fieldName));
                    field.set(instance, date);
                } else {
                    Object customPropertyInstance = createInstanceFromClassType(Class.forName(fieldType));
                    if(customPropertyInstance != null) {
                        applyResultSet(resultSet, customPropertyInstance);
                        field.set(instance, customPropertyInstance);
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create an instance from a specified class type.
     * @param classType The class type to create an instance from
     * @return The created instance
     */
    public static synchronized Object createInstanceFromClassType(Class<?> classType) {
        Object instance = null;
        try {
            instance = classType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return instance;
    }
}
