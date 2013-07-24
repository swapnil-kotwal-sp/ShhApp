/*
 * Copyright 2013 Swapnil Kotwal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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

  public DatabaseHelper(final Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
  }

  /**
   * This is called when the database is first created. Usually you should call
   * createTable statements here to create the tables that will store your data.
   */
  @Override
  public final void onCreate(final SQLiteDatabase db,
      final ConnectionSource connectionSource) {
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
  public final void onUpgrade(final SQLiteDatabase db,
      final ConnectionSource connectionSource, final int oldVersion,
      final int newVersion) {
    try {
      TableUtils.dropTable(connectionSource, Contact.class, true);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the Database Access Object (DAO) for our SimpleData class. It will
   * create it or just give the cached value.
   * 
   * @return simpleDao.
   */
  public final Dao<Contact, Integer> getDao() throws SQLException {
    if (simpleDao == null) {
      simpleDao = getDao(Contact.class);
    }
    return simpleDao;
  }

  /**
   * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao
   * for our SimpleData class. It will create it or just give the cached value.
   * RuntimeExceptionDao only through RuntimeExceptions.
   * 
   * @return simpleRuntimeDao.
   */
  public final RuntimeExceptionDao<Contact, Integer> getSimpleDataDao() {
    if (simpleRuntimeDao == null) {
      simpleRuntimeDao = getRuntimeExceptionDao(Contact.class);
    }
    return simpleRuntimeDao;
  }

  /**
   * Close the database connections and clear any cached DAOs.
   */
  @Override
  public final void close() {
    super.close();
    simpleRuntimeDao = null;
  }
}
