package seating;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SeatingManagerTest {

  @Test
  void test_happyPath() {
    Set<Table> tables = ImmutableSet.of(
        new Table(1),
        new Table(2),
        new Table(3),
        new Table(4),
        new Table(5),
        new Table(6));

    CustomerGroup[] groups = new CustomerGroup[]{
        new CustomerGroup(1),
        new CustomerGroup(2),
        new CustomerGroup(3),
        new CustomerGroup(4),
        new CustomerGroup(5),
        new CustomerGroup(6)
    };

    SeatingManager manager = new SeatingManager(tables);

    for (int i = 0; i <= 5; i++) {
      manager.arrives(groups[i]);
      Table table = manager.locate(groups[i]).get();
      assertThat(table.getSize()).isEqualTo(groups[i].getSize());
    }
  }

  @Test
  void test_waitingGroups() {
    Set<Table> tables = ImmutableSet.of(new Table(1));

    SeatingManager manager = new SeatingManager(tables);
    CustomerGroup group1 = new CustomerGroup(1);
    manager.arrives(group1);
    assertThat(manager.locate(group1).get().getSize()).isEqualTo(group1.getSize());

    CustomerGroup group2 = new CustomerGroup(1);
    manager.arrives(group2);
    assertThat(manager.locate(group1).get().getSize()).isEqualTo(group1.getSize());
    assertThat(manager.locate(group2).isPresent()).isFalse();

    manager.leaves(group1);
    assertThat(manager.locate(group1).isPresent()).isFalse();
    assertThat(manager.locate(group2).get().getSize()).isEqualTo(group1.getSize());
  }

  @Test
  void test_interchangingSizes() {
    Set<Table> tables = ImmutableSet.of(
        new Table(1),
        new Table(2),
        new Table(3),
        new Table(4),
        new Table(5),
        new Table(6)
    );

    SeatingManager manager = new SeatingManager(tables);
    CustomerGroup group1 = new CustomerGroup(1);
    manager.arrives(group1);
    assertThat(manager.locate(group1).get().getSize()).isEqualTo(group1.getSize());

    CustomerGroup group2 = new CustomerGroup(2);
    manager.arrives(group2);
    assertThat(manager.locate(group2).get().getSize()).isEqualTo(group2.getSize());

    CustomerGroup group3 = new CustomerGroup(3);
    manager.arrives(group3);
    assertThat(manager.locate(group3).get().getSize()).isEqualTo(group3.getSize());

    CustomerGroup group4 = new CustomerGroup(4);
    manager.arrives(group4);
    assertThat(manager.locate(group4).get().getSize()).isEqualTo(group4.getSize());

    CustomerGroup group5 = new CustomerGroup(5);
    manager.arrives(group5);
    assertThat(manager.locate(group5).get().getSize()).isEqualTo(group5.getSize());

    CustomerGroup group6 = new CustomerGroup(6);
    manager.arrives(group6);
    assertThat(manager.locate(group6).get().getSize()).isEqualTo(group6.getSize());

    CustomerGroup group62 = new CustomerGroup(6);
    manager.arrives(group62);
    assertThat(manager.locate(group62).isPresent()).isFalse();

    CustomerGroup group52 = new CustomerGroup(5);
    manager.arrives(group52);
    assertThat(manager.locate(group52).isPresent()).isFalse();

    manager.leaves(group5);
    assertThat(manager.locate(group5).isPresent()).isFalse();
    assertThat(manager.locate(group52).get().getSize()).isEqualTo(group52.getSize());

    manager.leaves(group6);
    assertThat(manager.locate(group6).isPresent()).isFalse();
    assertThat(manager.locate(group62).get().getSize()).isEqualTo(group62.getSize());

    CustomerGroup group32 = new CustomerGroup(3);
    manager.arrives(group32);
    assertThat(manager.locate(group32).isPresent()).isFalse();
    manager.leaves(group52);
    assertThat(manager.locate(group52).isPresent()).isFalse();
    assertThat(manager.locate(group32).get().getSize()).isEqualTo(5);
  }
}