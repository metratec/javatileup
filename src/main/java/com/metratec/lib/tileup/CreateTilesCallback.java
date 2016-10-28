package com.metratec.lib.tileup;

import java.io.IOException;

/**
 * @author man
 *
 */
public interface CreateTilesCallback {

  /**
   * value for finish with no errors
   */
  final int RESULT_OK = 0;
  /**
   * value for finish with errors
   */
  final int RESULT_ERROR = -1;

  /**
   * @param createdTile create tile name
   * @param count created tiles count
   * @param total total numbers of tiles to create
   */
  void createTilesProgress(String createdTile, int count, int total);

  /**
   * @param retval if finish without erros {@link #RESULT_OK}, otherwise {@link #RESULT_ERROR}
   * @param e if retval set to {@link #RESULT_ERROR} then this is set, otherwise null
   * 
   */
  void createTilesResult(int retval, IOException e);
}
