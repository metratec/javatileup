package com.metratec.lib.tileup;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class provides methods to split a large image into a grid of tiles.
 * <p>
 * Check {@link #createTiles() createTiles()} for details.
 */
public class TileUp {

  private BufferedImage image;
  private int tileWidth;
  private int tileHeight;
  private int imageWidth;
  private int imageHeight;
  private int zoomLevels;
  private String prefix;
  private String outDir;
  private boolean extended;

  /**
   * Constructs a {@link TileUp TileUp} from specified image file path.
   * <p>
   * It calls {@link #TileUp(String, String, String) TileUp(String, String, String)} with
   * {@link #outDir outDir} and {@link #prefix prefix} both set to the empty string.
   *
   * @param imagePath path to the large image file
   *
   * @throws IOException if the input image file can not be read/parsed
   */
  public TileUp(String imagePath) throws IOException {
    this(imagePath, "", "");
  }

  /**
   * Constructs a {@link TileUp TileUp} from specified image file path, {@link #outDir outDir} and
   * {@link #prefix prefix}.
   * <p>
   * It calls {@link #TileUp(String, String, String, int, int) TileUp(String, String, String, int,
   * int)} with {@link #tileWidth tileWidth} and {@link #tileHeight tileHeight} both set to 256.
   *
   * @param imagePath path to the large image file
   *
   * @param outDir path to the folder where the created tiles should be saved
   *
   * @param prefix the prefix to be added to the name of tiles
   *
   * @throws IOException if the input image file can not be read/parsed
   */
  public TileUp(String imagePath, String outDir, String prefix) throws IOException {
    this(imagePath, outDir, prefix, 256, 256);
  }

  /**
   * Constructs a {@link TileUp TileUp} from specified image file path, {@link #outDir outDir},
   * {@link #prefix prefix}, {@link #tileWidth tileWidth} and {@link #tileHeight tileHeight}.
   * <p>
   * It calls {@link #TileUp(String, String, String, int, int, int) TileUp(String, String, String,
   * int, int, int)} with {@link #zoomLevels zoomLevels} set to 1.
   *
   * @param imagePath path to the large image file
   *
   * @param outDir path to the folder where the created tiles should be saved
   *
   * @param prefix the prefix to be added to the name of tiles
   *
   * @param tileWidth the tile width (in pixels)
   *
   * @param tileHeight the tile height (in pixels)
   *
   * @throws IOException if the input image file can not be read/parsed
   */
  public TileUp(String imagePath, String outDir, String prefix, int tileWidth, int tileHeight) throws IOException {
    this(imagePath, outDir, prefix, tileWidth, tileHeight, 1);
  }

  /**
   * Constructs a {@link TileUp TileUp} from specified image file path, {@link #outDir outDir},
   * {@link #prefix prefix}, {@link #tileWidth tileWidth}, {@link #tileHeight tileHeight} and
   * {@link #zoomLevels zoomLevels}.
   * <p>
   * It calls {@link #TileUp(String, String, String, int, int, int, boolean) TileUp(String, String,
   * String, int, int, int, boolean)} with {@link #extended extended} set to true.
   *
   * @param imagePath path to the large image file
   *
   * @param outDir path to the folder where the created tiles should be saved
   *
   * @param prefix the prefix to be added to the name of tiles
   *
   * @param tileWidth the tile width (in pixels)
   *
   * @param tileHeight the tile height (in pixels)
   *
   * @param zoomLevels the number of zoom levels to create
   *
   * @throws IOException if the input image file can not be read/parsed
   */
  public TileUp(String imagePath, String outDir, String prefix, int tileWidth, int tileHeight, int zoomLevels)
      throws IOException {
    this(imagePath, outDir, prefix, tileWidth, tileHeight, zoomLevels, true);
  }

  /**
   * Constructs a {@link TileUp TileUp} from specified image file path, {@link #outDir outDir},
   * {@link #prefix prefix}, {@link #tileWidth tileWidth}, {@link #tileHeight tileHeight},
   * {@link #zoomLevels zoomLevels} and {@link #extended extended}.
   * <p>
   * 
   * @param imagePath path to the large image file
   *
   * @param outDir path to the folder where the created tiles should be saved
   *
   * @param prefix the prefix to be added to the name of tiles
   *
   * @param tileWidth the tile width (in pixels)
   *
   * @param tileHeight the tile height (in pixels)
   *
   * @param zoomLevels the number of zoom levels to create
   *
   * @param extended whether the edge tiles which do not fill an entire {@link #tileWidth tileWidth}
   *        × {@link #tileHeight tileHeight} should be extended
   *
   * @throws IOException if the input image file can not be read/parsed
   */
  public TileUp(String imagePath, String outDir, String prefix, int tileWidth, int tileHeight, int zoomLevels,
      boolean extended) throws IOException {
    this.image = ImageIO.read(new File(imagePath));
    this.imageWidth = this.image.getWidth();
    this.imageHeight = this.image.getHeight();

    this.outDir = outDir;
    this.prefix = prefix;
    this.tileWidth = Math.max(1, tileWidth);
    this.tileHeight = Math.max(1, tileHeight);
    this.zoomLevels = Math.max(1, zoomLevels);
    this.extended = extended;
  }

  /**
   * Sets the prefix to be added to the name of created tiles.
   *
   * @param prefix the prefix
   */
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  /**
   * Gets the prefix that will be added to the name of created tiles.
   *
   * @return the prefix
   */
  public String getPrefix() {
    return this.prefix;
  }

  /**
   * Sets the path to the folder where the created tiles should be saved. Set it to the empty string
   * to save the files in the current working directory.
   *
   * @param outDir the output directory
   */
  public void setOutDir(String outDir) {
    this.outDir = outDir;
  }

  /**
   * Gets the path to the folder where the created tiles will be saved.
   *
   * @return the output directory
   */
  public String getOutDir() {
    return this.outDir;
  }

  /**
   * Sets the tile width.
   *
   * @param tileWidth the tile width (in pixels)
   */
  public void setTileWidth(int tileWidth) {
    this.tileWidth = Math.max(1, tileWidth);
  }

  /**
   * Gets the tile width.
   *
   * @return the tile width (in pixels)
   */
  public int getTileWidth() {
    return this.tileWidth;
  }

  /**
   * Sets the tile height.
   *
   * @param tileHeight the tile height (in pixels)
   */
  public void setTileHeight(int tileHeight) {
    this.tileHeight = Math.max(1, tileHeight);
  }

  /**
   * Gets the tile height.
   *
   * @return the tile height (in pixels)
   */
  public int getTileHeight() {
    return this.tileHeight;
  }

  /**
   * Gets the image width.
   *
   * @return the image width (in pixels)
   */
  public int getImageWidth() {
    return this.imageWidth;
  }

  /**
   * Gets the image height.
   *
   * @return the image height (in pixels)
   */
  public int getImageHeight() {
    return this.imageHeight;
  }

  /**
   * Gets the number of zoom levels.
   * <p>
   * See also {@link #createTiles() createTiles()}.
   *
   * @return the zoom levels
   */
  public int getZoomLevels() {
    return this.zoomLevels;
  }

  /**
   * Sets the number of zoom levels. The minimum value is 1 which corresponds to a zoom of 20
   * (original image size).
   * <p>
   * See also {@link #createTiles() createTiles()}.
   *
   * @param zoomLevels the zoom Levels
   */
  public void setZoomLevels(int zoomLevels) {
    this.zoomLevels = Math.max(1, zoomLevels);
  }

  /**
   * Sets whether edge tiles should be extended if they are smaller than {@link #tileWidth
   * tileWidth} × {@link #tileHeight tileHeight}.
   *
   * @param extended whether the edge tiles should be extended if necessary
   */
  public void setExtended(boolean extended) {
    this.extended = extended;
  }

  /**
   * Returns whether edge tiles are extended if they are smaller than {@link #tileWidth tileWidth} ×
   * {@link #tileHeight tileHeight}.
   *
   * @return whether the edge tiles are extended if necessary
   */
  public boolean isExtended() {
    return this.extended;
  }

  /**
   * Automatically computes the necessary zoom level based on image dimensions and tile size.
   * <p>
   * The smallest zoom level will be the zoom level that exactly fits the whole image in a single
   * tile. If an exact fit is not possible, then the smallest zoom level is one zoom level higher
   * than the zoom level where the whole image fits in a single tile.
   * <p>
   * This method calls {@link #setZoomLevels(int) setZoomLevels(int)} with the computed
   * {@link #zoomLevels zoomLevels}.
   * <p>
   * See also {@link #createTiles() createTiles()}.
   */
  public void setAutoZoom() {
    int w_zoom = getZoom(imageWidth / tileWidth);
    int h_zoom = getZoom(imageHeight / tileWidth);
    setZoomLevels(Math.max(w_zoom, h_zoom));
  }

  private static int getZoom(int tiles) {
    int i = 0, tmp = 1;
    while (tmp <= tiles) {
      tmp *= 2;
      i++;
    }
    return i;
  }

  /**
   * Creates and saves tiles from the input image.
   * <p>
   * Each tile will be {@link #tileWidth tileWidth} pixels × {@link #tileHeight tileHeight} pixels.
   * The default tile size is 256 pixels × 256 pixels. By default, edge tiles smaller than the tile
   * size are extended to be the same size as the tile size. But this behavior can be overridden by
   * setting {@link #extended extended} to false. See {@link #setExtended(boolean)
   * setExtended(boolean)}.
   * <p>
   * Tiles are saved in the directory specified when the constructor was called or in the directory
   * specified later via a call to {@link #setOutDir(String) setOutDir(String)}. If the output
   * directory was not specified or was empty then the tiles will be saved in the current working
   * directory. The output directory will be created if it does not already exist.
   * <p>
   * When saving the tiles, a sub-folder for each zoom level will be created and all the tiles
   * belonging to that zoom level will go inside that sub-folder. The highest zoom is 20 which
   * corresponds to original image size. A zoom of 19 is half the size of of 20, 18 is half the size
   * of 19 and so on. If the {@link #zoomLevels zoomLevels} is unspecified or set to 1(default), the
   * tiles are only created for zoom of 20. If {@link #zoomLevels zoomLevels} is 4, tiles will be
   * generated for zooms of 20, 19, 18 and 17. See {@link #setZoomLevels(int) setZoomLevels(int)}
   * and {@link #setAutoZoom() setAutoZoom()}.
   * 
   * @param callback {@link CreateTilesCallback} callback for the progress information and result
   *
   */
  public void createTilesAsyn(CreateTilesCallback callback) {
    if (null != callback) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          createTiles(callback);
        }
      }).start();
    }
  }

  private void createTiles(CreateTilesCallback callback) {
    try {
      if (!outDir.isEmpty()) {
        File outputDir = new File(outDir);
        if (!outputDir.exists()) {
          outputDir.mkdir();
        }
      } else {
        outDir = ".";
      }
      int total = 0;
      int count = 1;
      for (int z = 0; z < zoomLevels; z++) {
        double scale = Math.pow(2.0, z);
        total += (int) (Math.ceil(imageWidth / (tileWidth * scale)) * Math.ceil(imageHeight / (tileHeight * scale)));
      }
      for (int z = 0; z < zoomLevels; z++) {
        String subdirpath = String.format("%s/%d", outDir, 20 - z);
        File subdir = new File(subdirpath);
        if (!subdir.exists()) {
          subdir.mkdir();
        }

        double scale = Math.pow(2.0, z);
        double scale_inv = 1.0 / scale;
        BufferedImage sm = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale_inv, scale_inv);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        sm = scaleOp.filter(image, sm);

        BufferedImage img_ext;
        if (extended) {
          double x = Math.ceil((double) imageWidth / (double) tileWidth);
          double y = Math.ceil((double) imageHeight / (double) tileHeight);
          img_ext = new BufferedImage((int) x * tileWidth, (int) y * tileHeight, BufferedImage.TYPE_INT_ARGB);
        } else {
          img_ext = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        }
        img_ext.setData(sm.getData());
        for (int i = 0; i * tileWidth * scale < imageWidth; i++) {
          for (int j = 0; j * tileHeight * scale < imageHeight; j++) {
            BufferedImage out;
            if (extended) {
              out = img_ext.getSubimage(i * tileWidth, j * tileHeight, tileWidth, tileHeight);
            } else {
              out = img_ext.getSubimage(i * tileWidth, j * tileHeight, Math.min(tileWidth, imageWidth - i * tileWidth),
                  Math.min(tileHeight, imageHeight - j * tileHeight));
            }
            String filename = String.format("%s/%s_%d_%d.png", subdirpath, prefix, i, j);
            File outputfile = new File(filename);
            ImageIO.write(out, "png", outputfile);
            callback.createTilesProgress(filename, count, total);
            count++;
          }
        }
      }
      callback.createTilesResult(CreateTilesCallback.RESULT_OK, null);
    } catch (IOException e) {
      callback.createTilesResult(CreateTilesCallback.RESULT_ERROR, e);
    }
  }

  /**
   * Creates and saves tiles from the input image.
   * <p>
   * Each tile will be {@link #tileWidth tileWidth} pixels × {@link #tileHeight tileHeight} pixels.
   * The default tile size is 256 pixels × 256 pixels. By default, edge tiles smaller than the tile
   * size are extended to be the same size as the tile size. But this behavior can be overridden by
   * setting {@link #extended extended} to false. See {@link #setExtended(boolean)
   * setExtended(boolean)}.
   * <p>
   * Tiles are saved in the directory specified when the constructor was called or in the directory
   * specified later via a call to {@link #setOutDir(String) setOutDir(String)}. If the output
   * directory was not specified or was empty then the tiles will be saved in the current working
   * directory. The output directory will be created if it does not already exist.
   * <p>
   * When saving the tiles, a sub-folder for each zoom level will be created and all the tiles
   * belonging to that zoom level will go inside that sub-folder. The highest zoom is 20 which
   * corresponds to original image size. A zoom of 19 is half the size of of 20, 18 is half the size
   * of 19 and so on. If the {@link #zoomLevels zoomLevels} is unspecified or set to 1(default), the
   * tiles are only created for zoom of 20. If {@link #zoomLevels zoomLevels} is 4, tiles will be
   * generated for zooms of 20, 19, 18 and 17. See {@link #setZoomLevels(int) setZoomLevels(int)}
   * and {@link #setAutoZoom() setAutoZoom()}.
   *
   * @throws IOException if output files or directories can not be created
   */
  public void createTiles() throws IOException {
    SynCreateTilesCallback callback = new SynCreateTilesCallback();
    createTiles(callback);
    while (!callback.isFinish()) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
      }
    }
    if (null != callback.getIOException()) {
      throw callback.getIOException();
    }
  }
}
