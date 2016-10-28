package com.metratec.lib.tileup;

import java.io.IOException;

/**
 * @author man
 *
 */
class SynCreateTilesCallback implements CreateTilesCallback {

  private Integer status;
  private IOException exception;

  @Override
  public void createTilesProgress(String createdTile, int count, int total) {
    System.out.println(new StringBuilder().append("Created tile: ").append(createdTile).append(" (").append(count)
        .append('/').append(total).append(')').toString());
  }

  @Override
  public void createTilesResult(int retval, IOException e) {
    status = retval;
    exception = e;
  }

  /**
   * @return true if finish
   */
  protected boolean isFinish() {
    return null != status;
  }

  /**
   * @return the {@link IOException}, if an error occurs
   */
  protected IOException getIOException() {
    return exception;
  }
}
