package cs107;

import java.lang.reflect.Array;
import java.util.Arrays;

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
        assert header != null : "Header is null";
        assert header.length == QOISpecification.HEADER_SIZE : "Header length is not 16";
        assert Arrays.equals(ArrayUtils.extract(header, 0, 4), QOISpecification.QOI_MAGIC) : "Magic number is not correct";

        /* This function extract the width, height, channels and color space from the header */
        byte[] tempheader = new byte[4];
        tempheader = ArrayUtils.extract(header, 0, QOISpecification.HEADER_SIZE);
        int width = ArrayUtils.toInt(ArrayUtils.extract(tempheader, 4, 4));
        int height = ArrayUtils.toInt(ArrayUtils.extract(tempheader, 8, 4));
        byte channels = tempheader[12];
        byte color_space = tempheader[13];
        assert color_space == QOISpecification.sRGB || color_space == QOISpecification.ALL : "Color space is not correct";
        assert channels == QOISpecification.RGB || channels == QOISpecification.RGBA : "Channels are not correct";
        return new int[]{width, height, (int) channels, (int) color_space};
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
        assert input.length - idx >= 3;
        buffer[position] = ArrayUtils.concat(ArrayUtils.extract(input, idx , QOISpecification.RGB), new byte[]{alpha});
        return QOISpecification.RGB;
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
        buffer[position] = ArrayUtils.concat(ArrayUtils.extract(input, idx , QOISpecification.RGBA));
        return QOISpecification.RGBA;
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
        assert previousPixel != null;
        assert previousPixel.length == 4;
        assert (byte) (chunk >> 6) == QOISpecification.QOI_OP_DIFF_TAG >> 6;
        byte[] newPixel = new byte[4];
        newPixel[3] = (byte) (previousPixel[3]);
        assert previousPixel != null;
        //chunk = (byte)(chunk+2);
        int[] diff = new int[]{(chunk >> 4) & 0b11 -2, (chunk >> 2) & 0b11 -2, (chunk) & 0b11 -2};
        for (int i = 0; i < 3; i++) {
            newPixel[i] = (byte) (previousPixel[i] + diff[i]);
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
        assert previousPixel != null && data != null;
        assert previousPixel.length == 4;
        assert (byte) data[0]>>6 == QOISpecification.QOI_OP_LUMA_TAG>>6;
        byte[] newPixel = new byte[4];
        newPixel[3] = (byte) (previousPixel[3]);
        int dg = (data[0] & 0b111111)-32;
        //to binary string
        int[] diff = new int[]{((data[1])>>4)+dg-8&0b1111,dg, data[1]+dg-8&0b1111};
        for (int i = 0; i < 3; i++) {
            newPixel[i] = (byte) (previousPixel[i] + diff[i]);
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
        assert buffer != null && pixel != null;
        assert position >= 0 && position < buffer.length;
        assert pixel.length == 4;
        assert buffer.length - position >= (chunk & 0b111111) + 1;

        chunk = (byte) (chunk & 0b111111);
        for (int i = 0; i <= chunk; i++) {
            buffer[position + i] = pixel;
        }
        return chunk;
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
        assert data != null;
        assert width > 0 && height > 0;
        assert data.length >= 4;
        byte[] previous = QOISpecification.START_PIXEL;
        byte[] current;
        int count = 0;
        byte[][] hashtable = new byte[width * height][4];

        int idx = 0;
        for (int i = 0; idx<data.length; i++) {
            byte chunk = data[idx];
            if (chunk == QOISpecification.QOI_OP_RGB_TAG) {
                idx++;
                decodeQoiOpRGB(hashtable, data, previous[0], count, idx);
                current = hashtable[count];
            } else if (chunk == QOISpecification.QOI_OP_RGBA_TAG) {
                idx++;
                decodeQoiOpRGBA(hashtable, data, count, idx);
                current = hashtable[count];
            } else {

            }
            idx++;
        }
        return hashtable;
    }

    /**
     * Decode a file using the "Quite Ok Image" Protocol
     * @param content (byte[]) - Content of the file to decode
     * @return (Image) - Decoded image
     * @throws AssertionError if content is null
     */
    public static Image decodeQoiFile(byte[] content){
        //Todo: check this works properly
        assert content != null;
        assert ArrayUtils.endsWith(content, QOISpecification.QOI_EOF);
        byte[] temp = ArrayUtils.extract(content, 0, QOISpecification.HEADER_SIZE);
        int[] header = decodeHeader(temp);
        int width = header[0];
        int height = header[1];
        byte channels = (byte) header[2];
        byte colorSpace = (byte) header[3];
        byte[][] pixels = decodeData(ArrayUtils.extract(content, temp.length, content.length - temp.length), width, height);
        int[][] image = ArrayUtils.channelsToImage(pixels, height, width);
        Image img = Helper.generateImage(image, channels, colorSpace);
        Helper.writeImage("image.png", img);
        return img;
    }
}