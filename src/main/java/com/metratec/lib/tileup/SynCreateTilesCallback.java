package com.metratec.lib.tileup;

/**
 * @author man
 *
 */
class SynCreateTilesCallback implements CreateTilesCallback {

  private Integer status;
  private Exception exception;

  @Override
  public void createTilesProgress(String createdTile, int count, int total) {
    System.out.println(new StringBuilder().append("Created tile: ").append(createdTile).append(" (").append(count)
        .append('/').append(total).append(')').toString());
  }

  @Override
  public void createTilesResult(int retval, Exception e) {
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
   * @return the {@link Exception}, if an error occurs
   */
  protected Exception getException() {
    return exception;
  }
}
