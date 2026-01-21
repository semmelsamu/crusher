package de.othr.crusher.model;

/**
 * Enum representing boulder color tags used in climbing gyms.
 *
 * <p>Each color can be associated with CSS classes for visual representation in the UI.
 */
public enum BoulderColor {
  YELLOW("Yellow"),
  BLUE("Blue"),
  BLACK("Black"),
  WHITE("White"),
  PINK("Pink"),
  DARK_GREEN("Dark Green"),
  RED("Red");

  private final String displayName;

  BoulderColor(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Gets the human-readable display name for the color.
   *
   * @return display name of the color
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Gets a Tailwind CSS color class for UI rendering.
   *
   * @return CSS color class string
   */
  public String getCssColorClass() {
    return switch (this) {
      case YELLOW -> "bg-yellow-400";
      case BLUE -> "bg-blue-500";
      case BLACK -> "bg-gray-900";
      case WHITE -> "bg-white";
      case PINK -> "bg-pink-400";
      case DARK_GREEN -> "bg-green-700";
      case RED -> "bg-red-500";
    };
  }
}
