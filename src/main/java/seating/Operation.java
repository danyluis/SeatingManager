package seating;

import lombok.Value;

@Value
public class Operation {

  Action action;
  int who;

  public Operation(Action action, int who) {
    this.action = action;
    this.who = who;
  }

  public Action getAction() {
    return action;
  }

  public int getWho() {
    return who;
  }
}
