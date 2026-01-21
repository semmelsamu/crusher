package de.othr.crusher.model;

public enum EventFrequency {
  WEEKLY("Weekly"),
  BI_WEEKLY("Bi-weekly"),
  MONTHLY("Monthly");

  private final String label;

  EventFrequency(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
