package seating;

public class CustomerGroup {

  private final int size;

  public CustomerGroup(int size) {
    this.size = size;
  }

  public int getSize() {
    return size;
  }

  @Override
  public String toString() {
    return String.format("Group(%d): (%d customers)", this.hashCode(), size);
  }
}
