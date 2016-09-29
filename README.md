Tile Up
=======

*Tile Up* is Java implementation of the Ruby gem Tile Up(https://github.com/rktjmp/tileup).

Compiling
-----
Compiling the source code is pretty straight forward assuming you have `maven` installed on your system.
Simply run the following command (from the folder containing `pom.xml`):
```
mvn package
```
You can find the built jar in folder:
`target/TileUp-1.0-bin-with-dependencies/`

Usage
-----
Usage is very similar to the Ruby gem Tile Up. Following options are available.

```
 -a,--auto-zoom                      Automatically scale input images
                                     based on image size and tile size.
 -h,--help                           Shows help.
 -i,--in <arg>                       Required input file, your large image
                                     to tile up.
 -n,--dont-extend-incomplete-tiles   Do not extend edge tiles if they do
                                     not fill an entire tile_width x
                                     tile_height.
 -o,--output-dir <arg>               Output directory (will be created if
                                     it doesn't exist).
 -p,--prefix <arg>                   Prefix to append to tile files, e.g.
                                     --prefix=my_tile =>
                                     my_tile_[XN]_[YN].png.
 -th,--tile-height <arg>             Tile height, should normally equal
                                     tile width. Default is 256 pixels.
 -tw,--tile-width <arg>              Tile width, should normally equal
                                     tile height. Default is 256 pixels.
 -z,--zoom-levels <arg>              Scale input images specified number
                                     of times. Default value is 1.
```
To generate some tiles from a large image, you can use something like:
```
java -jar TileUp.jar --in huge_image.png --output-dir image_tiles --prefix my_tiles
```

This will split `huge_image.png` up into `256x256` (default) sized tiles, and save them into the directory `image_tiles`. The images will be saved as `my_tiles_[COLUMN]_[ROW].png`

```
image_tiles/20/my_tiles_0_0.png
image_tiles/20/my_tiles_0_1.png
image_tiles/20/my_tiles_0_2.png
...
```

### Zoom Levels

`tileup` can also scale your image for a number of zoom levels (max 20 levels). This is done by *scaling down* the original image, so make sure it is pretty big. Zoom level of 1 (default) means that the image will be saved under the subfolder `20/`.

```
java -jar TileUp.jar --in really_huge_image.png --zoom-levels 4 \
                     --output-dir map_tiles --prefix map_tile
```

`--zoom-levels 4` means, make 4 levels of zoom, starting from `really_huge_image.png` at zoom level 20, then scale that down for 19, etc.

You should see something like:

```
map_tiles/20/map_tile_0_0.png
map_tiles/20/map_tile_0_1.png
map_tiles/20/map_tile_0_2.png
...
map_tiles/19/map_tile_0_0.png
map_tiles/19/map_tile_0_1.png
map_tiles/19/map_tile_0_2.png
...
```
*(where `20` is zoom level 20, the largest zoom, `19` is half the size of `20`, `18` is half the size of `19`, …)*

### Getting help

You can get help by running `java -jar TileUp.jar -h`.

