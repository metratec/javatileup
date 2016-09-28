package com.metratec.lib.tileup;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

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

  public static void main(String[] args) {
    Options options = new Options();

    Option input = new Option("i", "in", true, "Required input file, your large image to tile up.");
    input.setRequired(true);
    options.addOption(input);

    Option outputDir = new Option("o", "output-dir", true, "Output directory (will be created if it doesn't exist).");
    outputDir.setRequired(false);
    options.addOption(outputDir);

    Option prefix = new Option("p", "prefix", true, "Prefix to append to tile files, e.g. --prefix=my_tile => my_tile_[XN]_[YN].png.");
    prefix.setRequired(false);
    options.addOption(prefix);

    Option width = new Option("tw", "tile-width", true, "Tile width, should normally equal tile height.");
    width.setRequired(false);
    options.addOption(width);

    Option height = new Option("th", "tile-height", true, "Tile height, should normally equal tile width.");
    height.setRequired(false);
    options.addOption(height);

    Option auto = new Option("a", "auto-zoom", false, "Automatically scale input images based on image size and tile size.");
    auto.setRequired(false);
    options.addOption(auto);

    Option zoom = new Option("z", "zoom-levels", true, "Scale input images specified number of times.");
    zoom.setRequired(false);
    options.addOption(zoom);

    Option extend = new Option("n", "dont-extend-incomplete-tiles", false,
                               "Do not extend edge tiles if they do not fill an entire tile_width x tile_height.");
    extend.setRequired(false);
    options.addOption(extend);

    Option help = new Option("h", "help", false, "Shows help.");
    help.setRequired(false);
    options.addOption(help);

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd;

    try {
        cmd = parser.parse(options, args);
    } catch (Exception e) {
        System.out.println(e.getMessage());
        formatter.printHelp("java -jar TileUp.jar", options);

        System.exit(1);
        return;
    }

    String inputFilePath = cmd.getOptionValue("in");
    String outputDirPath = cmd.getOptionValue("output-dir", ".");
    String prefixName = cmd.getOptionValue("prefix", "");
    String tileWidth = cmd.getOptionValue("tile-width", "256");
    String tileHeight = cmd.getOptionValue("tile-height", "256");
    String zoomlevel = cmd.getOptionValue("zoom-levels");
    boolean autozoom = cmd.hasOption("auto-zoom");
    boolean ext = !cmd.hasOption("dont-extend-incomplete-tiles");
    boolean helpSet = cmd.hasOption("help");

    if (helpSet) {
      formatter.printHelp("java -jar TileUp.jar", options);
      System.exit(0);
    }

    int w = Integer.parseInt(tileWidth);
    int h = Integer.parseInt(tileHeight);

    try {
      TileUp tu = new TileUp(inputFilePath, outputDirPath, prefixName, w, h, ext);
      System.out.println("Image width: " + tu.getImageWidth());
      System.out.println("Image height: " + tu.getImageHeight());

      if (zoomlevel != null) {
        tu.setZoomLevels(Integer.parseInt(zoomlevel));
      } else if (autozoom) {
        tu.setAutoZoom();
      }

      tu.createTiles();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }
}
