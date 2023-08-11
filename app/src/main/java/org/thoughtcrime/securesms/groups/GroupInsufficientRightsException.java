package org.mycrimes.insecuretests.groups;

public final class GroupInsufficientRightsException extends GroupChangeException {

  GroupInsufficientRightsException(Throwable throwable) {
    super(throwable);
  }
}
