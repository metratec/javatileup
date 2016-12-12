package com.metratec.lib.tileup;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * This class implements a command line interface (CLI) that allows users to create tiles from a
 * large image file. This class basically creates an instance of {@link TileUp TileUp} based on the
 * arguments provided by the user and calls {@link TileUp#createTiles() createTiles()}.
 */
public class CLI {

  public static void main(String[] args) {
    String usage = "java -jar TileUp.jar [options]";

    Options options = new Options();

    Option input = new Option("i", "in", true, "Required input file, your large image to tile up.");
    input.setRequired(true);
    options.addOption(input);

    Option outputDir = new Option("o", "output-dir", true, "Output directory (will be created if it doesn't exist).");
    outputDir.setRequired(false);
    options.addOption(outputDir);

    Option prefix = new Option("p", "prefix", true,
        "Prefix to append to tile files, e.g. --prefix=my_tile => my_tile_[XN]_[YN].png.");
    prefix.setRequired(false);
    options.addOption(prefix);

    Option width =
        new Option("tw", "tile-width", true, "Tile width, should normally equal tile height. Default is 256 pixels.");
    width.setRequired(false);
    options.addOption(width);

    Option height =
        new Option("th", "tile-height", true, "Tile height, should normally equal tile width. Default is 256 pixels.");
    height.setRequired(false);
    options.addOption(height);

    Option auto =
        new Option("a", "auto-zoom", false, "Automatically scale input images based on image size and tile size.");
    auto.setRequired(false);
    options.addOption(auto);

    Option zoom =
        new Option("z", "zoom-levels", true, "Scale input images specified number of times. Default value is 1.");
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

    // Check if help is requested, if yes show help and exit
    int len = args.length;
    if (len > 0) {
      for (int i = 0; i < len; i++) {
        if (args[i].equals("-h") || args[i].equals("--help")) {
          formatter.printHelp(usage, options);
          System.exit(0);
        }
      }
    }

    try {
      cmd = parser.parse(options, args);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      formatter.printHelp(usage, options);

      System.exit(1);
      return;
    }

    String inputFilePath = cmd.getOptionValue("in");
    String outputDirPath = cmd.getOptionValue("output-dir", ".");
    String prefixName = cmd.getOptionValue("prefix", "");
    String tileWidth = cmd.getOptionValue("tile-width", "256");
    String tileHeight = cmd.getOptionValue("tile-height", "256");
    String zoomlevel = cmd.getOptionValue("zoom-levels", "1");
    boolean autozoom = cmd.hasOption("auto-zoom");
    boolean ext = !cmd.hasOption("dont-extend-incomplete-tiles");

    int w = Integer.parseInt(tileWidth);
    int h = Integer.parseInt(tileHeight);
    int z = Integer.parseInt(zoomlevel);

    try {
      TileUp tu = new TileUp(inputFilePath, outputDirPath, prefixName, w, h, z, ext);
      if (autozoom) {
        tu.setAutoZoom();
      }

      System.out.println("Image width: " + tu.getImageWidth());
      System.out.println("Image height: " + tu.getImageHeight());

      tu.createTiles();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
