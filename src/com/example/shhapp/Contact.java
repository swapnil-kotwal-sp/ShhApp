package com.example.shhapp;

import com.j256.ormlite.field.DatabaseField;

/**
 * A simple model object to store a Contact
 */
public class Contact {

  @DatabaseField(generatedId = true)
  int id;
  @DatabaseField(canBeNull = false)
  String password;
  @DatabaseField
  String email;

  Contact() {
    // needed by ormlite
  }

  public Contact(String email, String password) {
    this.password = password;
    this.email = email;
  }

  @Override
  public String toString() {
    return password + ": " + email;
  }
}
