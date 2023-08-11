package org.mycrimes.insecuretests.groups;

public final class GroupDoesNotExistException extends GroupChangeException {

  public GroupDoesNotExistException(Throwable throwable) {
    super(throwable);
  }
}
