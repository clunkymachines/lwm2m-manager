package com.clunkymachines.lwm2m.manager.repository;

public class UniqueConstraintViolationException extends Exception {
    private final String field;
    public UniqueConstraintViolationException(String field) {
      super(field+ " is not unique");
      this.field = field;
    }

    public String getField() {
      return field;
    }
}
