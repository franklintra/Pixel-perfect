package cs107;

//import java.util.Arrays;

import static cs107.QOISpecification.*;

/**
 * Utility class to manipulate arrays.
 * @apiNote First Task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.2
 * @since 1.0
 */
public final class ArrayUtils {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private ArrayUtils(){}

    // ==================================================================================
    // =========================== ARRAY EQUALITY METHODS ===============================
    // ==================================================================================

    /**
     * Check if the content of both arrays is the same
     * @param a1 (byte[]) - First array
     * @param a2 (byte[]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[] a1, byte[] a2){
        /*
        * If one parameter is null, throw an AssertionError
        * If both parameters are null, return true
        * If both parameters have different length, return false
        * If both parameters have the same length, check if the content is the same
         */
        if (a1.length != a2.length) return false;
        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) return false;
        }
        // useless here : if (a1 == null && a2 == null) return true;
        return true;
    }

    /**
     * Check if the content of both arrays is the same
     * @param a1 (byte[][]) - First array
     * @param a2 (byte[][]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[][] a1, byte[][] a2){
        /*
        * If one parameter is null, throw an AssertionError
        * If both parameters have different length, return false
        * TODO: reflechir à l'utilité du assert ci-dessous ainsi que des tests de longueur (redondance de code?)
         */
        assert a1 != null && a2 != null : "There is one null parameter";
        if (a1.length != a2.length) return false;
        for (int i = 0; i < a1.length; i++) {
            if (!equals(a1[i], a2[i])) return false;
        }
        return true;
    }

    // ==================================================================================
    // ============================ ARRAY WRAPPING METHODS ==============================
    // ==================================================================================

    /**
     * Wrap the given value in an array
     * @param value (byte) - value to wrap
     * @return (byte[]) - array with one element (value)
     */
    public static byte[] wrap(byte value){
        /* Converts byte to [byte] */
        return new byte[]{value};
    }

    // ==================================================================================
    // ========================== INTEGER MANIPULATION METHODS ==========================
    // ==================================================================================

    /**
     * Create an Integer using the given array. The input needs to be considered
     * as "Big Endian"
     * (See handout for the definition of "Big Endian")
     * @param bytes (byte[]) - Array of 4 bytes
     * @return (int) - Integer representation of the array
     * @throws AssertionError if the input is null or the input's length is different from 4
     */
    public static int toInt(byte[] bytes){
        /* Converts [byte] to int */
        assert bytes != null && bytes.length == 4 : "The input is null or its length is not 4";
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) | (bytes[i] & 0xFF);
        }
        return result;
    }

    /**
     * Separate the Integer (word) to 4 bytes. The Memory layout of this integer is "Big Endian"
     * (See handout for the definition of "Big Endian")
     * @param value (int) - The integer
     * @return (byte[]) - Big Endian representation of the integer
     */
    public static byte[] fromInt(int value){
        /* convert int to [byte] using Big Endian */
        byte[] res = new byte[4];
        res[0] = (byte) (value >>> 24);
        res[1] = (byte) (value >>> 16);
        res[2] = (byte) (value >>> 8);
        res[3] = (byte) value;
        return res;
    }

    // ==================================================================================
    // ========================== ARRAY CONCATENATION METHODS ===========================
    // ==================================================================================

    /**
     * Concatenate a given sequence of bytes and stores them in an array
     * @param bytes (byte ...) - Sequence of bytes to store in the array
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     */
    public static byte[] concat(byte ... bytes){
        assert bytes != null: "The input is null";
        return bytes;
    }

    /**
     * Concatenate a given sequence of arrays into one array
     * @param tabs (byte[] ...) - Sequence of arrays
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     * or one of the inner arrays of input is null.
     */
    public static byte[] concat(byte[] ... tabs){
        assert tabs != null: "Tabs is null";
        int length = 0;
        for (byte[] line: tabs) {
            assert line != null: "One of the inner arrays of input is null";
            length += line.length;
        }
        byte[] temp = new byte[length];
        int i = 0;
        for (byte[] line: tabs) {
            assert line != null;
            for (byte element: line) {
                temp[i++] = element;
            }
        }
        return temp;
    }

    // ==================================================================================
    // =========================== ARRAY EXTRACTION METHODS =============================
    // ==================================================================================

    /**
     * Extract an array from another array
     * @param input (byte[]) - Array to extract from
     * @param start (int) - Index in the input array to start the extract from
     * @param length (int) - The number of bytes to extract
     * @return (byte[]) - The extracted array
     * @throws AssertionError if the input is null or start and length are invalid.
     * start + length should also be smaller than the input's length
     */
    public static byte[] extract(byte[] input, int start, int length){
        assert input != null && start >= 0 && length >= 0 && start + length <= input.length: "The input is null or start and length are invalid (result in an out of bound)";
        byte[] temp = new byte[length];
        int index = 0;
        for (int i = start; i < length + start; ++i) {
            temp[index++] = input[i];
        }
        return temp;
    }

    /**
     * Create a partition of the input array.
     * (See handout for more information on how this method works)
     * @param input (byte[]) - The original array
     * @param sizes (int ...) - Sizes of the partitions
     * @return (byte[][]) - Array of input's partitions.
     * The order of the partition is the same as the order in sizes
     * @throws AssertionError if one of the parameters is null
     * or the sum of the elements in sizes is different from the input's length
     */
    public static byte[][] partition(byte[] input, int ... sizes) {
        int sum = 0;
        for (int size : sizes) {
            assert size >= 0 : "One of the sizes is negative";
            sum += size;
        }
        assert input != null && sum == input.length : "One of the parameters is null or the sum of the elements in sizes is different from the input's length";

        byte[][] temp = new byte[sizes.length][];
        int start = 0;
        for (int i = 0; i < sizes.length; ++i) {
            temp[i] = extract(input, start, sizes[i]);
            start += sizes[i];
        }
        return temp;
    }

    // ==================================================================================
    // ============================== ARRAY FORMATTING METHODS ==========================
    // ==================================================================================

    /**
     * Format a 2-dim integer array
     * where each dimension is a direction in the image to
     * a 2-dim byte array where the first dimension is the pixel
     * and the second dimension is the channel.
     * See handouts for more information on the format.
     * @param input (int[][]) - image data
     * @return (byte [][]) - formatted image data
     * @throws AssertionError if the input is null
     * or one of the inner arrays of input is null
     */
    public static byte[][] imageToChannels(int[][] input){
        /* This function takes a matrix of int and returns a matrix of byte
        * Throw assertion error if the input is null or one of the inner arrays of input is null
        *  input[a][b] input is a 2-dim array of integers containing the A R G B values of each pixel(a, b) as a 32-bit integer. Each of the value corresponds to 8 caracters in binary
        *  output[n] contains the n-th pixel of the image. Each pixel is represented by a 4-dim array of bytes containing the RGBA values of the pixel as 8-bit integers.
        */
        assert input != null: "The input is null"; // Checks if the input itself is null
        byte[][] temp = new byte[input.length * input[0].length][4];
        int index = 0;
        for (int[] ints : input) {
            assert ints != null: "One inner channel is null"; // Checks if one the inner arrays is null
            for (int anInt : ints) {
                temp[index++] = concat(partition(fromInt(anInt), 1, 1, 1, 1));
            }
        }
        return ARGBtoRGBA(temp); // Indices correction from ARGB space to RGBA space;
    }

    /**
     * and the second is the channel to a 2-dim int array where the first
     * dimension is the height and the second is the width
     * @param input (byte[][]) : linear representation of the image
     * @param height (int) - Height of the resulting image
     * @param width (int) - Width of the resulting image
     * @return (int[][]) - the image data
     * @throws AssertionError if the input is null
     * or one of the inner arrays of input is null
     * or input's length differs from width * height
     * or height is invalid
     * or width is invalid
     */
    public static int[][] channelsToImage(byte[][] input, int height, int width){
        /*
        * This function takes a linear representation of the image and returns a 2-dim array of int
        * Throw assertion error if the input is null or one of the inner arrays of input is null
        * output[a][b] output is a 2-dim array of integers containing the value of each pixel.
         */
        assert input != null && input.length == width * height && height > 0 && width > 0 : "One of the parameters is null or input's length differs from width * height";
        input = RGBAtoARGB(input); // Indices correction from RGBA space to ARGB space;
        int[][] temp = new int[height][width];
        int index = 0;
        for (int i=0; i<temp.length; i++) {
            assert input[i] != null; // Checks if one the inner arrays is null
            assert input[i].length == 4; // Checks if the inner arrays are of length 4
            for (int j = 0; j<temp[i].length; j++) {
                temp[i][j] = toInt(input[index++]);
            }
        }
        return temp;
    }

    /**
    * This functions takes a matrix of pixels in ARGB format and returns a pixel matrix in RGBA format
    * @param input (byte[][]) - pixel matrix in ARGB format
    * @return (byte[][]) - pixel matrix in RGBA format
    * @throws AssertionError if the input is null
    * or the pixels are incomplete (not 4 channels)
     */
    public static byte[][] ARGBtoRGBA(byte[][] input) {
        assert input != null : "The input is null";
        assert input[0].length == 4 : "The pixels are incomplete (not 4 channels)";
        byte[][] temp = new byte[input.length][4];
        for (int i = 0; i < input.length; ++i) {
            temp[i][r] = input[i][1];
            temp[i][g] = input[i][2];
            temp[i][b] = input[i][3];
            temp[i][a] = input[i][0];
        }
        return temp;
    }
    /**
     * This functions takes a matrix of pixels in RGBA format and returns a pixel matrix in RGBA format
     * @param input (byte[][]) - pixel matrix in ARGB format
     * @return (byte[][]) - pixel matrix in RGBA format
     * @throws AssertionError if the input is null
     * or the pixels are incomplete (not 4 channels)
     */
    public static byte[][] RGBAtoARGB(byte[][] input) {
        assert input != null : "The input is null";
        assert input[0].length == 4 : "The pixels are incomplete (not 4 channels)";
        byte[][] temp = new byte[input.length][4];
        for (int i = 0; i < input.length; ++i) {
            temp[i][r] = input[i][3];
            temp[i][g] = input[i][0];
            temp[i][b] = input[i][1];
            temp[i][a] = input[i][2];
        }
        return temp;
    }

    /**
    * This function calculates the difference per element between two bytes arrays
     * @param a (byte[][]) - first byte array
     * @param b (byte[][]) - second byte array
     * @return (byte[][]) - the difference between the two arrays
     * @throws AssertionError if the arrays are null
     */
    public static byte[] calculateDelta(byte[] a, byte[] b){
        assert a != null && b != null : "One of the arrays is null";
        byte[] delta = new byte[a.length];
        for (int i=0; i<a.length; i++){
            delta[i] = (byte) (a[i]-b[i]);
        }
        return delta;
    }

    /*
    * This function checks that the content byte array ends with the bytes byte array
    * @param content (byte[]) - the content
    * @param bytes (byte[]) - the bytes that should be at the end of the content
    * @return (boolean) - true if the content ends with the bytes, false otherwise
    * @throws AssertionError if the content or the bytes are null
     */
    public static boolean endsWith(byte[] content, byte[] bytes) {
        assert content != null && bytes != null : "One of the arrays is null";
        if (content.length < bytes.length) {
            return false;
        }
        for (int i = 0; i < bytes.length; i++) {
            if (content[content.length - bytes.length + i] != bytes[i]) {
                return false;
            }
        }
        return true;
    }
}