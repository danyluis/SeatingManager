package seating;

public class Table {

  private static int ID_GEN = 1;

  private final int id;

  private final int size;

  public Table(int size) {
    this.size = size;
    this.id = ID_GEN++;
  }

  public int getSize() {
    return size;
  }

  @Override
  public String toString() {
    return String.format("Table(%d): (%d chairs)", this.id, size);
  }
}
