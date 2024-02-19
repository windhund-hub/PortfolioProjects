package de.unileipzig.nw18a.alfextract.treemapping;

/**
 * Abstract super class for one of the events speciation, duplication or lateral gene transfer.
 */
public abstract class Event {

  /** The type of the event (speciation, duplication, lgt). */
  protected EventType type;

  /**
   * Creates an event with the given type.
   * @param type event type (speciation, duplication, lgt)
   */
  public Event(EventType type) {
    this.type = type;
  }

  public EventType getType() {
    return type;
  }
}
