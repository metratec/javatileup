package com.metratec.lib.tileup;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CLI {

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
