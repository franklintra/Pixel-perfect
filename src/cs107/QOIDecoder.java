package cs107;

import static cs107.Helper.Image;

/**
 * "Quite Ok Image" Decoder
 * @apiNote Third task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.2
 * @since 1.0
 */
public final class QOIDecoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIDecoder(){}

    // ==================================================================================
    // =========================== QUITE OK IMAGE HEADER ================================
    // ==================================================================================

    /**
     * Extract useful information from the "Quite Ok Image" header
     * @param header (byte[]) - A "Quite Ok Image" header
     * @return (int[]) - Array such as its content is {width, height, channels, color space}
     * @throws AssertionError See handouts section 6.1
     */
    public static int[] decodeHeader(byte[] header){
        /* This function extract the width, height, channels and color space from the header */
        var decodeHeader = new byte[4];
        decodeHeader = ArrayUtils.extract(header, 4, 10);
        int width = ArrayUtils.toInt(ArrayUtils.extract(decodeHeader, 0, 4));
        int height = ArrayUtils.toInt(ArrayUtils.extract(decodeHeader, 4, 4));
        int channels = decodeHeader[8];
        int color_space = decodeHeader[9];
        return new int[]{width, height, channels, color_space};
    }

    // ==================================================================================
    // =========================== ATOMIC DECODING METHODS ==============================
    // ==================================================================================

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param input (byte[]) - Stream of bytes to read from
     * @param alpha (byte) - Alpha component of the pixel
     * @param position (int) - Index in the buffer
     * @param idx (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.1
     */
    public static int decodeQoiOpRGB(byte[][] buffer, byte[] input, byte alpha, int position, int idx){
        /* This function decode the QOI_OP_RGB_TAG */
        assert buffer != null && input != null;
        assert position >= 0 && position < buffer.length;
        assert idx >= 0 && idx < input.length;
        assert alpha >= 0 && alpha <= 255;
        assert input.length - idx >= 3;
        buffer[position] = ArrayUtils.concat(ArrayUtils.extract(input, idx , 3), new byte[]{alpha});
        return 3;
    }

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param input (byte[]) - Stream of bytes to read from
     * @param position (int) - Index in the buffer
     * @param idx (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.2
     */
    public static int decodeQoiOpRGBA(byte[][] buffer, byte[] input, int position, int idx){
        /* This function decode the QOI_OP_RGBA_TAG */
        assert buffer != null && input != null;
        assert position >= 0 && position < buffer.length;
        assert idx >= 0 && idx < input.length;
        assert input.length - idx >= 4;
        buffer[position] = ArrayUtils.concat(ArrayUtils.extract(input, idx , 4));
        return 4;
    }

    /**
     * Create a new pixel following the "QOI_OP_DIFF" schema.
     * @param previousPixel (byte[]) - The previous pixel
     * @param chunk (byte) - A "QOI_OP_DIFF" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.4
     */
    public static byte[] decodeQoiOpDiff(byte[] previousPixel, byte chunk){
        /* This function decode the QOI_OP_DIFF_TAG */
        //TODO: comprendre le -3 du diff
        assert previousPixel != null;
        assert previousPixel.length == 4;
        assert (byte) (chunk >> 6) == QOISpecification.QOI_OP_DIFF_TAG >> 6;
        var newPixel = new byte[4];
        newPixel[0] = (byte) (previousPixel[0]+1);
        assert previousPixel != null;
        //chunk = (byte)(chunk+2);
        int[] diff = new int[]{(chunk >> 4) & 0b11 -2, (chunk >> 2) & 0b11 -2, (chunk) & 0b11 -3};
        for (int i = 0; i < 3; i++) {
            newPixel[i+1] = (byte) (previousPixel[i+1] + diff[i]);
        }
        return newPixel;
    }

    /**
     * Create a new pixel following the "QOI_OP_LUMA" schema
     * @param previousPixel (byte[]) - The previous pixel
     * @param data (byte[]) - A "QOI_OP_LUMA" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.5
     */
    public static byte[] decodeQoiOpLuma(byte[] previousPixel, byte[] data){
        //TODO: finir la fonction
        assert previousPixel != null && data != null;
        assert previousPixel.length == 4;
        //assert (byte) data[0] == QOISpecification.QOI_OP_LUMA_TAG;
        var newPixel = new byte[4];
        newPixel[0] = (byte) (previousPixel[0]);
        int[] diff = new int[]{ data[2] + data[1] ,data[1] , data[3] + data[1]};
        for (int i = 0; i < 3; i++) {
            newPixel[i+1] = (byte) (previousPixel[i+1] + diff[i]);
        }
        return newPixel;
    }

    /**
     * Store the given pixel in the buffer multiple times
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param pixel (byte[]) - The pixel to store
     * @param chunk (byte) - a QOI_OP_RUN data chunk
     * @param position (int) - Index in buffer to start writing from
     * @return (int) - number of written pixels in buffer
     * @throws AssertionError See handouts section 6.2.6
     */
    public static int decodeQoiOpRun(byte[][] buffer, byte[] pixel, byte chunk, int position){
        return Helper.fail("Not Implemented");
    }

    // ==================================================================================
    // ========================= GLOBAL DECODING METHODS ================================
    // ==================================================================================

    /**
     * Decode the given data using the "Quite Ok Image" Protocol
     * @param data (byte[]) - Data to decode
     * @param width (int) - The width of the expected output
     * @param height (int) - The height of the expected output
     * @return (byte[][]) - Decoded "Quite Ok Image"
     * @throws AssertionError See handouts section 6.3
     */
    public static byte[][] decodeData(byte[] data, int width, int height){
        return Helper.fail("Not Implemented");
    }

    /**
     * Decode a file using the "Quite Ok Image" Protocol
     * @param content (byte[]) - Content of the file to decode
     * @return (Image) - Decoded image
     * @throws AssertionError if content is null
     */
    public static Image decodeQoiFile(byte[] content){
        return Helper.fail("Not Implemented");
    }

}