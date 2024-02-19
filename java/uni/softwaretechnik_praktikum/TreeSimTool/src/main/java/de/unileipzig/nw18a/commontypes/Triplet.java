package de.unileipzig.nw18a.commontypes;

import de.unileipzig.nw18a.commontypes.TripletEntry;

import java.lang.Comparable;

import java.util.Arrays;

/**
 * Represents a triplet of the form ab|c or abc depending on the value of constraint.
 */
public class Triplet implements Comparable<Triplet> {

  private TripletEntry first;
  private TripletEntry second;
  private TripletEntry third;
  private boolean constraint;

  /**
   * Overloaded constructor. Will create a triplet that is a constraint.
   *
   * @see Triplet#Triplet(TripletEntry first, TripletEntry second, TripletEntry third, boolean
   *  constraint)
   *
   * @param first      First entry of this triplet. That is one of the more closely related entries.
   * @param second     Second entry of this triplet. That is one of the more closely related
   *                   entries.
   * @param third      Third entry of this triplet. That is the more distantly related entry.
   */
  public Triplet(TripletEntry first, TripletEntry second, TripletEntry third) {
    this(first, second, third, true);
  }

  /**
   * Creates a triplet from three entries.
   *
   * <p>The entries will be reordered using the compareTo() method of the TripletEntry. This way
   * triplets ab|c and ba|c are stored the same way, as well as abc, acb, bac, bca, cab, cba.
   *
   * @param first      First entry of this triplet. That is for constraint == true, one of the more
   *                   closely related entries.
   * @param second     Second entry of this triplet. That is for constraint == true, one of the more
   *                   closely related entries.
   * @param third      Third entry of this triplet. That is for constraint == true, the more
   *                   distantly related entry.
   * @param constraint Whether or not this triplet represents a constraint, that is two of the
   *                   entries are more closely related as in ab|c rather than abc.
   */
  public Triplet(TripletEntry first, TripletEntry second, TripletEntry third, boolean constraint) {
    if (first.getClass() != second.getClass() || second.getClass() != third.getClass()) {
      throw new IllegalArgumentException("Cannot create triplet from different entities of type"
          + "TripletEntry: " + first.getClass().getSimpleName() + " vs. "
          + second.getClass().getSimpleName() + " vs. " + third.getClass().getSimpleName());
    }

    if (constraint) {
      this.first  = (first.compareTo(second) <= 0 ? first : second);
      this.second = (first.compareTo(second) <= 0 ? second : first);
      this.third  = third;
    } else {
      TripletEntry[] orderedList = new TripletEntry[] {first, second, third};
      Arrays.sort(orderedList);
      this.first  = orderedList[0];
      this.second = orderedList[1];
      this.third  = orderedList[2];
    }
    this.constraint = constraint;
  }

  public TripletEntry getFirst() {
    return first;
  }

  public TripletEntry getSecond() {
    return second;
  }

  public TripletEntry getThird() {
    return third;
  }

  /**
   * Returns whether or not this triplet induces a constraint, that is whether it is a triplet of
   * the form ab|c or abc.
   * @return true iff this triplet induces a constraint, i.e. is of the form ab|c
   */
  public boolean isConstraint() {
    return constraint;
  }

  /**
   * Compares two triplets for equality on the basis of their entries and the fact whether they are
   * constraints or not.
   * @param  otherTriplet Triplet with which to compare this one
   * @return              true iff this triplet and the given one are both constraints or both not,
   *                      i.e. this.constraint == otherTriplet.constraint and all three entries are
   *                      reference the same object in memory, that is this.entry ==
   *                      otherTriplet.entry.
   */
  @Override
  public boolean equals(Object otherTriplet) {
    if (otherTriplet == this) {
      return true;
    }
    if (otherTriplet == null || getClass() != otherTriplet.getClass()) {
      return false;
    }

    Triplet other = (Triplet)otherTriplet;

    // two triplets are consider equal if they are of the same form (ab|c vs abc) and all three
    // entries are the same (using their memory address rather than their equals method)
    if (constraint == other.constraint
        && first  == other.first
        && second == other.second
        && third  == other.third) {
      return true;
    }
    return false;
  }

  /**
   * Returns a hash-code value for this triplet, computed on the basis of all three entries and the
   * type of this triplet, i.e. a constraint or not.
   * @return hash-code value of this triplet
   */
  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + first.hashCode();
    result = 31 * result + second.hashCode();
    result = 31 * result + third.hashCode();
    result = 31 * result + (constraint ? 1 : 0);
    return result;
  }

  /**
   * Compares this triplet with another one. Determining whether they are equal or one is larger
   * then the other.
   *
   * <p>Comparison is done by comparing the first, second and third TripletEntry in this order.
   * @param  otherTriplet               Triplet with which to compare this one
   * @return                            0 if this = otherTriplet, -1 if this &lt; otherTriplet,
   *                                    1 if this &gt; otherTriplet
   * @throws IllegalArgumentException  When this and otherTriplet do not contain the same kind of
   *                                    entities of type TripletEntry
   */
  @Override
  public int compareTo(Triplet otherTriplet) {
    // check whether we can compare these two instances
    if (this.first.getClass() != otherTriplet.first.getClass()) {
      throw new IllegalArgumentException("Cannot compare triplets containing differnt kinds of "
          + "TripletEntries: " + first.getClass().getSimpleName() + " vs. "
          + otherTriplet.first.getClass().getSimpleName());
    }
    if (constraint != otherTriplet.constraint) {
      throw new IllegalArgumentException("Cannot compare triplets of differnet types "
          + "(ab|c vs. abc)!");
    }

    // when they reference the same object in memory we can directly return "equal"
    if (this == otherTriplet) {
      return 0;
    }

    // else consider that one smaller which in the first different element (starting with first) is
    // smaller than the other
    int compFirst = first.compareTo(otherTriplet.first);
    if (compFirst != 0) {
      return compFirst;
    }
    int compSecond = second.compareTo(otherTriplet.second);
    if (compSecond != 0) {
      return compSecond;
    }
    int compThird = third.compareTo(otherTriplet.third);
    if (compThird != 0) {
      return compThird;
    }
    // all three elements are equal, so the triplets are equal
    return 0;
  }

  @Override
  public String toString() {
    return "(" + first + "," + second + (constraint ? "|" : ",") + third + ")";
  }

}
