package seating;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

// COMPLEXITY ANALYSIS:
//
// TIME COMPLEXITY (separate explanations in the individual methods)
// - Group arrives: O(1)
// - Group leaves: O(n) on the number of tables
// - Locate a group: O(1)
//
// SPACE COMPLEXITY
// - O(n + m)
//
// EXPLANATION:
// - We have all Tables distributed between availability sets by size (from 0 to 6) and
//   a set of used Tables. Both structures together grow up to the number of tables (n).
// - We also have a pair of structures: seatingMap and waiting. 'seatingMap' maps every
//   seated CustomerGroup to the Table where it's seating, and 'waiting' stores the
//   waiting groups. Both structures, together, only grow up to the number of
//   CustomerGroups (m).
public class SeatingManager {

  private static final int MAX_AVAILABILITY = 6;

  private final Set<Table> usedTables = new HashSet<>();
  private final Map<Integer, Set<Table>> availableBySize = new HashMap<>();
  private final Map<CustomerGroup, Table> seatingMap = new HashMap<>();
  private final Set<CustomerGroup> waiting = new LinkedHashSet<>();

  // Having the table count helps us keep the complexity of seating waiting
  // groups down to the number of tables and not to the number of CustomerGroups
  private int tableCount = 0;

  // Requiring a Set (instead of List) of Table, avoids duplicates
  public SeatingManager(Set<Table> tables) {
    tables.forEach(this::addTable);
  }

  private void addTable(Table table) {
    availableBySize.computeIfAbsent(table.getSize(), k -> new HashSet<>())
                   .add(table);
    tableCount++;
  }

  // The `arrives` operation needs to check whether there's any table big enough
  // for the group that has just arrived and associate the group to the found
  // table accordingly.
  //
  // This is an O(1) operation (O(K) where K is a constant of the different table sizes)
  //
  // EXPLANATION:
  // - We keep tables organized in availability sets (indexed from 0 to 6)
  // - Searching for a table with enough seats for the group is a loop
  //   from the size of the group up to 6, which is an O(1) operation (with constant K,
  //   whose maximum value is 6). If no suitable table is found, then add the group
  //   to the waiting list, which is O(1) in time, too.
  // - Adding the group to the table assignment map is also O(1).
  // - Marking the table as occupied is also O(1).
  // - Hence, the whole operation is O(1)
  public void arrives(CustomerGroup group) {
    System.out.println("Arrives " + group);
    Optional<Table> freeTable = getFreeTable(group.getSize());
    if (freeTable.isPresent()) {
      seatGroup(group, freeTable.get());
    } else {
      waiting.add(group);
    }
  }

  // complexity:  O(K) == O(1) where K is the number of different table sizes
  private Optional<Table> getFreeTable(int minimumSeats) {
    for (int size = minimumSeats; size <= MAX_AVAILABILITY; size++) {
      Optional<Table> found = availableBySize.computeIfAbsent(size, s -> new HashSet<>())
                                             .stream()
                                             .findFirst();
      if (found.isPresent()) {
        return found;
      }
    }
    return Optional.empty();
  }

  // O(1)
  private void seatGroup(CustomerGroup group, Table table) {
    assert group.getSize() <= table.getSize() : String.format(
        "Cannot seat %d customers in a table with only %d available seats.",
        group.getSize(),
        table.getSize());

    seatingMap.put(group, table);
    useTable(table);
  }

  // O(1)
  private void useTable(Table table) {
    assert !usedTables.contains(table) : String.format("Table %s is already occupied", table);

    Set<Table> tableSet = availableBySize.get(table.getSize());
    tableSet.remove(table);
    usedTables.add(table);
  }

  // The `leaves` operation needs to remove from the seating map the group that's leaving
  // and free up the table. It also seats all waiting groups that can be accommodated.
  //
  // This is an O(n) operation, where n is the number of tables. It loops over the waiting
  // groups, but the loop is limited by the number of tables in the restaurant.
  //
  // EXPLANATION:
  // - We remove the group from the seating map, which is O(1) amortized.
  // - We free up the table, which is O(1).
  // - We move the table to the appropriate availability set, which is O(1) amortized.
  // - Then, we look for waiting groups that can be accommodated, which is O(n) on the
  //   number of tables.
  public void leaves(CustomerGroup group) {
    System.out.println("Leaves " + group);
    assert seatingMap.containsKey(group) : String.format("%s was not seated.", group);

    Table table = seatingMap.get(group);
    if (table != null) {
      seatingMap.remove(group);
      freeTable(table);
      tryToSeatWaitingGroups();
    }
  }

  // O(1)
  private void freeTable(Table table) {
    assert usedTables.contains(table) : String.format("%s is already available", table);

    usedTables.remove(table);
    availableBySize.get(table.getSize()).add(table);
  }

  // O(m) on the number of groups: it loops over the waiting groups, and although we try to
  // limit the size of the loop by checking the number of tables, the worst case scenario is
  // e.g. a restaurant with several tables of sizes 0 to 5, only 1 table of size 6, one
  // group of size 6 that never leaves, and a list of waiting groups with size 6 always
  // waiting while smaller groups come and go using the smaller tables. This scenario
  // will make the algorithm always check all waiting groups. Of course, we can improve that
  // by remembering which groups sizes couldn't be satisfied and avoiding the call to
  // getFreeTable for such sizes in subsequent loops, but the loop still goes over all
  // waiting groups.
  private void tryToSeatWaitingGroups() {
    Set<CustomerGroup> seatedGroups = new HashSet<>();
    for (CustomerGroup group : waiting) {
      if (usedTables.size() < tableCount) {
        Optional<Table> table = getFreeTable(group.getSize());
        if (table.isPresent()) {
          seatGroup(group, table.get());
          seatedGroups.add(group);
        }
      }
    }
    waiting.removeAll(seatedGroups);
  }

  // This operation needs to check on what table the provided group is sitting.
  //
  // This is an O(1) operation
  //
  // EXPLANATION:
  // - We keep a seating map, with groups as keys, and tables as values.
  // - Checking the table where a group is sitting is an O(1) (amortized) operation
  //   because it requires a simple Hashmap retrieval.
  // - Hence, the operation is O(1)
  public Optional<Table> locate(CustomerGroup group) {

    return Optional.ofNullable(seatingMap.get(group));
  }
}
