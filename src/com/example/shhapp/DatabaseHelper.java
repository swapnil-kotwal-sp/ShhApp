package com.example.shhapp;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

  // name of the database file for your application
  private static final String DATABASE_NAME = "contact.db";

  // any time you make changes to your database objects, you may have to
  // increase the database version
  private static final int DATABASE_VERSION = 1;

  // the DAO object we use to access the SimpleData table
  private Dao<Contact, Integer> simpleDao;

  private RuntimeExceptionDao<Contact, Integer> simpleRuntimeDao;

  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
  }

  /**
   * This is called when the database is first created. Usually you should call
   * createTable statements here to create the tables that will store your data.
   */
  @Override
  public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
    try {
      TableUtils.createTable(connectionSource, Contact.class);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * This is called when your application is upgraded and it has a higher
   * version number. This allows you to adjust the various data to match the new
   * version number.
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
      int oldVersion, int newVersion) {
    try {
      TableUtils.dropTable(connectionSource, Contact.class, true);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the Database Access Object (DAO) for our SimpleData class. It will
   * create it or just give the cached value.
   */
  public Dao<Contact, Integer> getDao() throws SQLException {
    if (simpleDao == null) {
      simpleDao = getDao(Contact.class);
    }
    return simpleDao;
  }

  /**
   * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao
   * for our SimpleData class. It will create it or just give the cached value.
   * RuntimeExceptionDao only through RuntimeExceptions.
   */
  public RuntimeExceptionDao<Contact, Integer> getSimpleDataDao() {
    if (simpleRuntimeDao == null) {
      simpleRuntimeDao = getRuntimeExceptionDao(Contact.class);
    }
    return simpleRuntimeDao;
  }

  /**
   * Close the database connections and clear any cached DAOs.
   */
  @Override
  public void close() {
    super.close();
    simpleRuntimeDao = null;
  }
}
