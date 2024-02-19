package de.unileipzig.nw18a.alfextract.treeutils;

@SuppressWarnings("serial")
public class IllegalTreeStateException extends Exception {

  public IllegalTreeStateException() {
  }

  public IllegalTreeStateException(String message) {
    super(message);
  }

  public IllegalTreeStateException(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalTreeStateException(Throwable cause) {
    super(cause);
  }
}
