package cs107;

import java.util.Arrays;

import static cs107.Helper.Image;
import static cs107.QOISpecification.*;


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
        // Check if the header is valid
        assert header != null : "Header is null";
        assert header.length == QOISpecification.HEADER_SIZE : "Header length is not the expected one";
        assert Arrays.equals(ArrayUtils.extract(header, 0, 4), QOISpecification.QOI_MAGIC) : "Magic number is not correct";

        // This function extract the width, height, channels and color space from the header according to the specification
        byte[] tempheader = ArrayUtils.extract(header, 0, QOISpecification.HEADER_SIZE);
        int width = ArrayUtils.toInt(ArrayUtils.extract(tempheader, 4, 4));
        int height = ArrayUtils.toInt(ArrayUtils.extract(tempheader, 8, 4));
        byte channels = tempheader[12];
        byte color_space = tempheader[13];
        //Check if the header is valid part 2
        assert color_space == QOISpecification.sRGB || color_space == QOISpecification.ALL : "Color space is not correct";
        assert channels == QOISpecification.RGB || channels == QOISpecification.RGBA : "Channels are not correct";
        // Return the header information
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
        //This function decode the QOI_OP_RGB_TAG
        //Check if the parameters are valid
        assert buffer != null && input != null;
        assert position >= 0 && position < buffer.length;
        assert idx >= 0 && idx < input.length;
        assert input.length - idx >= 3;

        //Store the pixel in the buffer
        buffer[position] = ArrayUtils.concat(ArrayUtils.extract(input, idx, QOISpecification.RGB), new byte[]{alpha});
        //Return the number of consumed bytes which is a constant
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
        // This function decode the QOI_OP_RGBA_TAG
        // Check if the parameters are valid
        assert buffer != null && input != null;
        assert position >= 0 && position < buffer.length;
        assert idx >= 0 && idx < input.length;
        assert input.length - idx >= 4;

        // Store the pixel in the buffer
        buffer[position] = ArrayUtils.concat(ArrayUtils.extract(input, idx , QOISpecification.RGBA));
        // Return the number of consumed bytes which is a constant
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
        // This function decode the QOI_OP_DIFF_TAG
        // Check if the parameters are valid
        assert previousPixel != null;
        assert previousPixel.length == 4;
        assert (byte) (chunk >> 6) == QOISpecification.QOI_OP_DIFF_TAG >> 6;

        // Create a new pixel following the "QOI_OP_DIFF" schema
        byte[] newPixel = new byte[4];
        newPixel[3] = previousPixel[3];
        int[] diff = new int[]{(chunk >> 4) & 0b11, (chunk >> 2) & 0b11, (chunk) & 0b11};
        for (int i = 0; i < 3; i++) {
            newPixel[i] = (byte) (previousPixel[i] + diff[i] - 2);
        }
        // Return the newly created pixel
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
        // This function decode the QOI_OP_LUMA_TAG
        // Check if the parameters are valid
        assert previousPixel != null && data != null;
        assert previousPixel.length == 4;
        assert data[0]>>6 == QOISpecification.QOI_OP_LUMA_TAG>>6;

        // Create a new pixel following the "QOI_OP_LUMA" schema
        byte[] newPixel = new byte[4];
        newPixel[a] = previousPixel[3];
        int dg = (data[0] & 0b111111);
        newPixel[g] = (byte) (previousPixel[1] + dg - 32); // The green value is the first one to calculate because it is used in the calculation of the red and blue values
        newPixel[r] = (byte) ((previousPixel[0] + ((data[1]>>4)&0b1111) + dg -32 -8 ));
        newPixel[b] = (byte) (previousPixel[2] + (data[1] & 0b1111) +dg -32 -8);

        // Return the newly created pixel
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
        // This function decode the QOI_OP_RUN_TAG
        // Check if the parameters are valid
        assert buffer != null && pixel != null;
        assert position >= 0 && position < buffer.length;
        assert pixel.length == 4;
        assert buffer.length - position > (chunk & 0b111111);

        // Store the given pixel in the buffer chunk & 0b111111 times
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
    public static byte[][] decodeData(byte[] data, int width, int height) {
        // Check if the parameters are valid
        assert data != null;
        assert width > 0 && height > 0;
        assert data.length >= 4;
        //Initialization
        byte[][] buffer = new byte[width * height][4];
        byte[][] hashTable = new byte[64][4];
        byte[] previous = QOISpecification.START_PIXEL;
        int counter = 0;

        // Iterate over the encoded data and store the decoded pixels in the buffer
        for (int idx = 0; idx < data.length; idx++) {
            byte chunk = data[idx];
            if (chunk == QOISpecification.QOI_OP_RGB_TAG) { // If the chunk is a QOI_OP_RGB_TAG than decode the next 3 bytes as a pixel
                idx += decodeQoiOpRGB(buffer, data, previous[3], counter, idx + 1); // The idx is incremented by 4 because the next 3 bytes are a pixel
                hashTable[QOISpecification.hash(buffer[counter])] = buffer[counter];
                previous = buffer[counter++];
            } else if (chunk == QOISpecification.QOI_OP_RGBA_TAG) { // If the chunk is a QOI_OP_RGBA_TAG than decode the next 4 bytes as a pixel
                idx += decodeQoiOpRGBA(buffer, data, counter, idx + 1); // The idx is incremented by 4 because the next 4 bytes are decoded as a pixel
                hashTable[QOISpecification.hash(buffer[counter])] = buffer[counter];
                previous = buffer[counter++];
            } else {
                if ((byte) (chunk & 0b11000000) == QOISpecification.QOI_OP_INDEX_TAG) { // If the beginning of the chunk is a QOI_OP_INDEX_TAG than decode the rest of the chunk as an index
                    buffer[counter] = hashTable[chunk & 0b111111];
                    previous = buffer[counter++];
                }
                else if ((byte) (chunk & 0b11000000) == QOISpecification.QOI_OP_DIFF_TAG) { // If the beginning of the chunk is a QOI_OP_DIFF_TAG than decode the rest of the chunk as a pixel
                buffer[counter] = decodeQoiOpDiff(previous, chunk); // A pixel is decoded
                hashTable[QOISpecification.hash(buffer[counter])] = buffer[counter];
                previous = buffer[counter++];
            }
                else if ((byte) (chunk & 0b11000000) == QOISpecification.QOI_OP_LUMA_TAG) { // If the beginning of the chunk is a QOI_OP_LUMA_TAG than decode the rest of the chunk and the next chunk as a pixel
                buffer[counter] = decodeQoiOpLuma(previous, new byte[]{data[idx], data[idx + 1]}); // A pixel is decoded
                hashTable[QOISpecification.hash(buffer[counter])] = buffer[counter];
                previous = buffer[counter++];
                idx++;
            }
                else if ((byte) (chunk & 0b11000000) == QOISpecification.QOI_OP_RUN_TAG) { // If the beginning of the chunk is a QOI_OP_RUN_TAG than decode the rest of the chunk as a run
                counter += decodeQoiOpRun(buffer, previous, chunk, counter); // The counter is incremented by the number of pixels written in the buffer
                hashTable[QOISpecification.hash(buffer[counter])] = buffer[counter];
                previous = buffer[counter++];
            }
        }
    }
        return buffer;
    }
    /**
     * Decode a file using the "Quite Ok Image" Protocol
     * @param content (byte[]) - Content of the file to decode
     * @return (Image) - Decoded image
     * @throws AssertionError if content is null
     */
    public static Image decodeQoiFile(byte[] content){
        // This function decode a file using the "Quite Ok Image" Protocol
        // Check if the parameters are valid
        assert content != null;
        assert ArrayUtils.endsWith(content, QOISpecification.QOI_EOF);

        byte[] temp = ArrayUtils.extract(content, 0, QOISpecification.HEADER_SIZE);
        int[] header = decodeHeader(temp); // Decode the header
        int width = header[0];
        int height = header[1];
        byte channels = (byte) header[2];
        byte colorSpace = (byte) header[3];
        // Decode the data in the form of a byte[][] linearised buffer that contains the channels of the pixels
        byte[][] pixels = decodeData(ArrayUtils.extract(content, QOISpecification.HEADER_SIZE, content.length - QOISpecification.QOI_EOF.length - QOISpecification.HEADER_SIZE), width, height);
        // Convert the linearised buffer to a 2D array of pixels, the channels are converted to integers
        int[][] image = ArrayUtils.channelsToImage(pixels, height, width);
        // Convert the 2D array of pixels to a BufferedImage
        return Helper.generateImage(image, channels, colorSpace);
    }
}