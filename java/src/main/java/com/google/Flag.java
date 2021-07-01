package com.google;

public class Flag {

  private boolean isFlagged;
  private String reason;


  public Flag(){
    isFlagged = false;
    reason = "Not supplied";
  }


  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public void setFlag(boolean flagged) {
    isFlagged = flagged;
  }

  public boolean isFlagged() {
    return isFlagged;
  }
}
