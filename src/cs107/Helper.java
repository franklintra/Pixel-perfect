package cs107;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Helper class. This class contains all the methods considered to be useful
 * and which cannot be implemented by the students.
 * (Outside the scope of the course).
 * Most of these methods can be implemented by the students after the
 * CS-108 course next semester.
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.2
 * @since 1.0
 */
public final class Helper {

    private static final String res_folder = "res";

    static {
        var file = new File(res_folder);
        if(file.exists()){
            if (!file.isDirectory()){
                fail("File %s is not a directory.", res_folder);
            }
        }else{
            var b = file.mkdir();
            if(!b)
                fail("Cannot create directory '%s'", res_folder);
        }
    }

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private Helper(){}

    /**
     * Record to store all the information of a given image
     * @param data (int[][]) - ARGB stored pixels
     * @param channels (byte) - number of channels
     * @param color_space (byte) - color space
     */
    public record Image(int[][] data, byte channels, byte color_space){
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Image im){
                return Arrays.deepEquals(data, im.data) && (channels == im.channels) && (color_space == im.color_space);
            }else
                return false;
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(data);
        }
    }

    // ==================================================================================
    // ========================== IMAGE MANIPULATION METHODS ============================
    // ==================================================================================

    /**
     * Generate a new Image using the given parameters
     * @param data (int[][]) - ARGB stored pixels
     * @param channels (byte) - number of channels
     * @param colorSpace (byte) - color space
     * @return (Image) - The corresponding Image
     */
    public static Image generateImage(int[][] data, byte channels, byte colorSpace){
        assert data != null;
        assert data.length > 0;
        assert data[0] != null;
        var width = data[0].length;
        assert width > 0;
        for (var p : data){
            assert p != null;
            assert p.length == width;
        }
        return new Image(data, channels, colorSpace);
    }

    /**
     * Read and decode an image from the disk. The image can be one of
     * the standard formats (png, jpeg ...)
     * @param path (String) - Relative or Absolute Path to the image
     * @return (Image) - The corresponding Image
     */
    public static Image readImage(String path) {
        try{
            var io = ImageIO.read(new File(path));
            var width  = io.getWidth();
            var height = io.getHeight();
            var array = new int[height][width];
            for(var x = 0; x < height;++x){
                for(var y = 0 ;y < width; ++y){
                    array[x][y] = io.getRGB(y, x);
                }
            }
            var nbrChannels = (byte) (io.getColorModel().hasAlpha() ? 4 : 3);
            return new Image(array, nbrChannels, (byte) 0);
        }catch (IOException e){
            return fail("An error occurred while trying to read from : \"%s\"%n", path);
        }

    }

    /**
     * Write an image as "PNG" in the disk. This function writes to the folder called "res/"
     * @param path (String) - Relative or Absolute path to the image
     * @param image (Image) - Image to store
     */
    public static void writeImage(String path, Image image) {
        int type = switch (image.channels){
            case 3 -> BufferedImage.TYPE_3BYTE_BGR;
            case 4 -> BufferedImage.TYPE_4BYTE_ABGR;
            default -> fail("Cannot write this image, image.channels() == %d", image.channels);
        };
        var buffer = new BufferedImage(image.data[0].length, image.data.length, type);
        for(var x = 0; x < buffer.getHeight(); ++x){
            for(var y = 0 ; y < buffer.getWidth(); ++y){
                buffer.setRGB(y, x, image.data[x][y]);
            }
        }
        var abs_path = res_folder + File.separator + path;
        try {
            ImageIO.write(buffer, "png", new File(abs_path));
        }catch (IOException e){
            fail("An error occurred while trying to write to : \"%s\"%n", abs_path);
        }
    }

    // ==================================================================================
    // ======================== BINARY FILE MANIPULATION METHODS ========================
    // ==================================================================================

    /**
     * Read a file stored in the disk
     * @param path (String) - Relative or Absolute path to the file
     * @return (byte[]) - File content as stored in memory
     */
    public static byte[] read(String path) {
        try(var input = new FileInputStream(path)){
            return input.readAllBytes();
        } catch (IOException e){
            return fail("An error occurred while trying to read from : \"%s\"%n", path);
        }
    }

    /**
     * Write a file to the disk. This function writes to the folder called "res/"
     * @param path (String) - Relative or Absolute path to the file
     * @param content (byte[]) - Content of the file.
     */
    public static void write(String path, byte[] content){
        var abs_path = res_folder + File.separator + path;
        try(var output = new FileOutputStream(abs_path)){
            for (var b : content){
                output.write(b);
            }
        }catch (IOException e){
            fail("An error occurred while trying to write to : \"%s\"%n", abs_path);
        }
    }

    // ==================================================================================
    // ============================= ERROR MANAGEMENT METHODS ===========================
    // ==================================================================================

    /**
     * Fails the program.
     * @apiNote A call to ths function will make the program stop
     * @param fmt (String) - format of the String
     * @param params (Object ...) - Objects to format the String
     * @return (T) - Nothing
     * @param <T> - Capture the return type of the function to satisfy the type checker
     * @throws RuntimeException
     */
    public static <T> T fail(String fmt, Object ... params){
        throw new RuntimeException(String.format(fmt, params));
    }

}