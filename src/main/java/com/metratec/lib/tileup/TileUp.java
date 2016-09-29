package com.metratec.lib.tileup;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TileUp {

  private BufferedImage image;
  private int tileWidth;
  private int tileHeight;
  private int imageWidth;
  private int imageHeight;
  private int zoomLevels;
  private String prefix;
  private String path;
  private boolean extended;

  public TileUp(String imagePath)
         throws IOException {
    this(imagePath, "", "");
  }

  public TileUp(String imagePath, String path, String prefix)
         throws IOException {
    this(imagePath, path, prefix, 256, 256);
  }

  public TileUp(String imagePath, String path, String prefix, int tw, int th)
         throws IOException {
    this(imagePath, path, prefix, tw, th, true);
  }

  public TileUp(String imagePath, String path, String prefix, int tw, int th,
                boolean extended)
         throws IOException {
    this(imagePath, path, prefix, tw, th, extended, 1);
  }

  public TileUp(String imagePath, String path, String prefix, int tw, int th,
                boolean extended, int zoomLevels)
         throws IOException {
    this.image = ImageIO.read(new File(imagePath));
    this.imageWidth = this.image.getWidth();
    this.imageHeight = this.image.getHeight();

    this.path = path;
    this.prefix = prefix;
    this.tileWidth = tw;
    this.tileHeight = th;
    this.extended = extended;
    this.zoomLevels = zoomLevels;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPath() {
    return this.path;
  }

  public void setExtended(boolean extended) {
    this.extended = extended;
  }

  public boolean isExtended() {
    return this.extended;
  }

  public void setTileWidth(int tileWidth) {
    this.tileWidth = tileWidth;
  }

  public int getTileWidth() {
    return this.tileWidth;
  }

  public void setTileHeight(int tileHeight) {
    this.tileHeight = tileHeight;
  }

  public int getTileHeight() {
    return this.tileHeight;
  }

  public int getImageWidth() {
    return this.imageWidth;
  }

  public int getImageHeight() {
    return this.imageHeight;
  }

  public void setZoomLevels(int zoomLevels) {
    this.zoomLevels = Math.max(1, zoomLevels);
  }

  public int getZoomLevels() {
    return this.zoomLevels;
  }

  public void setAutoZoom() {
    int w_zoom = getZoom(imageWidth/tileWidth);
    int h_zoom = getZoom(imageHeight/tileWidth);
    setZoomLevels(Math.min(w_zoom, h_zoom));
  }

  private static int getZoom(int tiles) {
    int i = 0, tmp = 1;
    while (tmp <= tiles) {
      tmp *= 2;
      i++;
    }
    return i;
  }

  public void createTiles() throws IOException {
    if (!path.isEmpty()) {
      File outDir = new File(path);
      if (!outDir.exists()) {
        outDir.mkdir();
      }
    } else {
      path = ".";
    }

    for (int z = 0; z < zoomLevels; z++) {
      String subdirpath = String.format("%s/%d", path, 20 - z);
      File subdir = new File(subdirpath);
      if (!subdir.exists()) {
        subdir.mkdir();
      }

      double scale = Math.pow(2.0, z);
      double scale_inv = 1.0 / scale;
      BufferedImage sm = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
      AffineTransform at = new AffineTransform();
      at.scale(scale_inv, scale_inv);
      AffineTransformOp scaleOp =
         new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
      sm = scaleOp.filter(image, sm);

      BufferedImage img_ext;
      if (extended) {
        double x = Math.ceil((double)imageWidth/(double)tileWidth);
        double y = Math.ceil((double)imageHeight/(double)tileHeight);
        img_ext = new BufferedImage((int)x*tileWidth, (int)y*tileHeight, BufferedImage.TYPE_INT_ARGB);
      } else {
        img_ext = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
      }
      img_ext.setData(sm.getData());

      for (int i = 0; i*tileWidth*scale < imageWidth; i++) {
        for (int j = 0; j*tileHeight*scale < imageHeight; j++) {
          BufferedImage out;
          if (extended) {
            out = img_ext.getSubimage(i*tileWidth, j*tileHeight, tileWidth, tileHeight);
          } else {
            out = img_ext.getSubimage(i*tileWidth, j*tileHeight,
                                      Math.min(tileWidth, imageWidth - i*tileWidth),
                                      Math.min(tileHeight, imageHeight - j*tileHeight));
          }
          String filename = String.format("%s/%s_%d_%d.png", subdirpath, prefix,
                                          i, j);
          File outputfile = new File(filename);
          ImageIO.write(out, "png", outputfile);
          System.out.println("Created tile: " + filename);
        }
      }
    }
  }
}
