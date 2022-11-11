package cs107;

import java.util.ArrayList;

/**
 * "Quite Ok Image" Encoder
 * @apiNote Second task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.2
 * @since 1.0
 */
public final class QOIEncoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIEncoder(){}

    // ==================================================================================
    // ============================ QUITE OK IMAGE HEADER ===============================
    // ==================================================================================

    /**
     * Generate a "Quite Ok Image" header using the following parameters
     * @param image (Helper.Image) - Image to use
     * @throws AssertionError if the colorspace or the number of channels is corrupted or if the image is null.
     *  (See the "Quite Ok Image" Specification or the handouts of the project for more information)
     * @return (byte[]) - Corresponding "Quite Ok Image" Header
     */
    public static byte[] qoiHeader(Helper.Image image){
        assert image != null;
        assert image.color_space() == QOISpecification.sRGB || image.color_space() == QOISpecification.ALL; // Check if the color satifies the specification
        assert image.channels() == QOISpecification.RGB || image.channels() == QOISpecification.RGBA; // Check if the number of channels satisfies the specification
        var header = new byte[4];
        header = ArrayUtils.concat(QOISpecification.QOI_MAGIC, ArrayUtils.fromInt(image.data()[0].length), ArrayUtils.fromInt(image.data().length), new byte[]{image.channels()}, new byte[]{(image.color_space())});
        // Concatenate the magic number, the width, the height, the number of channels and the color space as demanded per the specification
        return header;
    }

    // ==================================================================================
    // ============================ ATOMIC ENCODING METHODS =============================
    // ==================================================================================

    /**
     * Encode the given pixel using the QOI_OP_RGB schema
     * @param pixel (byte[]) - The Pixel to encode
     * @throws AssertionError if the pixel's length is not 4
     * @return (byte[]) - Encoding of the pixel using the QOI_OP_RGB schema
     */
    public static byte[] qoiOpRGB(byte[] pixel){
        assert pixel.length == 4;
        return ArrayUtils.concat(new byte[]{QOISpecification.QOI_OP_RGB_TAG}, ArrayUtils.extract(pixel, 0, 3));
        // Concatenate the QOI_OP_RGB_TAG and the first 3 bytes of the pixel as demanded per the specification
    }

    /**
     * Encode the given pixel using the QOI_OP_RGBA schema
     * @param pixel (byte[]) - The pixel to encode
     * @throws AssertionError if the pixel's length is not 4
     * @return (byte[]) Encoding of the pixel using the QOI_OP_RGBA schema
     */
    public static byte[] qoiOpRGBA(byte[] pixel){
        assert pixel.length == 4;
        return ArrayUtils.concat(new byte[]{QOISpecification.QOI_OP_RGBA_TAG},pixel);
        // Concatenate the QOI_OP_RGBA_TAG and the pixel as demanded per the specification
    }

    /**
     * Encode the index using the QOI_OP_INDEX schema
     * @param index (byte) - Index of the pixel
     * @throws AssertionError if the index is outside the range of all possible indices
     * @return (byte[]) - Encoding of the index using the QOI_OP_INDEX schema
     */
    public static byte[] qoiOpIndex(byte index){
        assert index >= 0 && index <= 63;
        return new byte[]{(byte) (QOISpecification.QOI_OP_INDEX_TAG | index)};
        // return the wrapped byte of QOI_OP_INDEX_TAG and index as demanded per the specification
    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_DIFF schema
     * @param diff (byte[]) - The difference between 2 pixels
     * @throws AssertionError if diff doesn't respect the constraints or diff's length is not 3
     * (See the handout for the constraints)
     * @return (byte[]) - Encoding of the given difference
     */
    public static byte[] qoiOpDiff(byte[] diff){
        /*
        * This function checks if dr, dg and db are within the required range
        * Once this is done it aligns the diff tag with dr, dg and db on 2 bits each
        * -2 = 00
        * -1 = 01
        * 0 = 10
        * 1 = 11
        * following this logic it's similar to x = byte(x+2), x has an offset of 2
         */
        assert diff != null;
        assert diff.length == 3;
        assert diff[0] >= -3 && diff[0] <= 2;
        assert diff[1] >= -3 && diff[1] <= 2;
        assert diff[2] >= -3 && diff[2] <= 2;
        return new byte[]{(byte) (QOISpecification.QOI_OP_DIFF_TAG | (diff[0]+2)<<4  | (diff[1]+2) << 2 | (diff[2]+2))};
        // return the wrapped byte of QOI_OP_DIFF_TAG and the diff as demanded per the specification
    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_LUMA schema
     * @param diff (byte[]) - The difference between 2 pixels
     * @throws AssertionError if diff doesn't respect the constraints
     * or diff's length is not 3
     * (See the handout for the constraints)
     * @return (byte[]) - Encoding of the given difference
     */
    public static byte[] qoiOpLuma(byte[] diff){
        /*
        * This function checks if dg, dr-dg, db-dg is within the required range
        * Once this is done it aligns the diff tag with dr-dg, dg and db-dg on 2 bits each
         */
        assert diff != null;
        assert diff.length==3;
        assert diff[1] > -33 && diff[1] < 32;
        assert diff[0]-diff[1] > -9 && diff[0]-diff[1] < 8;
        assert diff[2]-diff[1] > -9 && diff[2]-diff[1] < 8;
        return new byte[]{
                (byte) (QOISpecification.QOI_OP_LUMA_TAG | (diff[1]+32)),
                (byte) (((diff[0]-diff[1]+8) << 4) | diff[2]-diff[1]+8)
        };
        // return the wrapped byte of QOI_OP_LUMA_TAG and the diff as demanded per the specification
    }

    /**
     * Encode the number of similar pixels using the QOI_OP_RUN schema
     * @param count (byte) - Number of similar pixels
     * @throws AssertionError if count is not between 0 (exclusive) and 63 (exclusive)
     * @return (byte[]) - Encoding of count
     */
    public static byte[] qoiOpRun(byte count){
        /*
        the variable count is the number of pixels that are the same as the previous one
        it has an offset of 1 because the current pixel is not counted
         */
        assert (0 < count && count < 63);
        return new byte[]{
                (byte) (QOISpecification.QOI_OP_RUN_TAG | count-1)
        };
        // return the wrapped byte of QOI_OP_RUN_TAG and count as demanded per the specification
    }

    // ==================================================================================
    // ============================== GLOBAL ENCODING METHODS  ==========================
    // ==================================================================================

    /**
     * Encode the given image using the "Quite Ok Image" Protocol
     * (See handout for more information about the "Quite Ok Image" protocol)
     * @param image (byte[][]) - Formatted image to encode
     * @return (byte[]) - "Quite Ok Image" representation of the image
     */
    public static byte[] encodeData(byte[][] image){
        //Assertions
        assert image != null;
        for (byte[] row : image) {
            assert row != null;
            assert row.length == 4;
        }
        //Initialization
        byte[][] hashtable = new byte[64][4];
        int count = 0;
        byte[] previous;
        byte[] current;
        ArrayList<byte[]> data = new ArrayList<>();
        //Encoding
        for (int i = 0; i< image.length; i++) {
            //If we are on the first pixel, we use QOISPecification.START_PIXEL as the previous pixel
            if (i==0) {
                previous = QOISpecification.START_PIXEL;
            }
            else {previous = image[i-1];}
            current = image[i];
            //Pixel is the same as the 0<n<63 previous one
            if (ArrayUtils.equals(previous, current)){ // If the current pixel is the same as the previous one it will iterate in the loop until it finds a different pixel incrementing count by one everytime
                count++;
                if (count == 62 || i == image.length-1){ // If the count is 62 or if we are on the last pixel we encode the run
                    data.add(QOIEncoder.qoiOpRun((byte) count));
                    count = 0;
                }
                continue;
            } else {
                if (count > 0){ // If the count is greater than 0 and the current pixel is different from the previous one we encode the run
                    data.add(QOIEncoder.qoiOpRun((byte) (count)));
                    count = 0;
                }
            }
            //Hashing table
            if (ArrayUtils.equals(hashtable[QOISpecification.hash(current)], current)) { // If the current pixel is in the hashtable we encode it using the index schema
                data.add(QOIEncoder.qoiOpIndex(QOISpecification.hash(current)));
                continue;
            }
            else {
                hashtable[QOISpecification.hash(current)] = current; // If the current pixel is not in the hashtable we add it to the hashtable
            }
            //This part of the code is for readability
            byte dr = (byte) (current[0] - previous[0]);
            byte dg = (byte) (current[1] - previous[1]);
            byte db = (byte) (current[2] - previous[2]);
            if (current[3] == previous[3]) {
                //Diff
                if ((dr >= -2 && dr <= 1) && (dg >= -2 && dg <= 1) && (db >= -2 && db <= 1)) { //Condition check on the diff schema
                    data.add(QOIEncoder.qoiOpDiff(ArrayUtils.calculateDelta(ArrayUtils.extract(current, 0, 3), ArrayUtils.extract(previous, 0, 3)))); //Encode the diff
                    continue;
                }
                //Luma
                else if ((dg < 32 && dg > -33)
                        && ((byte) (dr - dg) < 8) && ((byte) (dr - dg) > -9)
                        && ((byte) (db - dg) < 8) && ((byte) (db - dg) > -9)) { //Condition check on the luma schema
                    data.add(QOIEncoder.qoiOpLuma(ArrayUtils.calculateDelta(ArrayUtils.extract(current, 0, 3), ArrayUtils.extract(previous, 0, 3)))); //Encode the luma
                    continue;
                }
                //RGB
                else { //If no optimization schema is possible and the alpha value of both pixels is the same we encode the pixel using the RGB schema
                    data.add(QOIEncoder.qoiOpRGB(current));
                    continue;
                }
            }
            //RGBA
            //If no optimization schema is possible and the alpha value of both pixels is different we encode the pixel using the RGBA schema
            data.add(QOIEncoder.qoiOpRGBA(current));
        }
        byte[][] encoded = new byte[data.size()][];
        for (int i=0; i<data.size(); i++) {
            encoded[i] = data.get(i);
        }
        return ArrayUtils.concat(encoded);
    }

    /**
     * Creates the representation in memory of the "Quite Ok Image" file.
     * @apiNote THE FILE IS NOT CREATED YET, THIS IS JUST ITS REPRESENTATION.
     * TO CREATE THE FILE, YOU'LL NEED TO CALL Helper::write
     * @param image (Helper.Image) - Image to encode
     * @return (byte[]) - Binary representation of the "Quite Ok File" of the image
     * @throws AssertionError if the image is null
     */
    public static byte[] qoiFile(Helper.Image image){ // This method creates the representation of the QOI file
        assert image != null;
        byte[] header = QOIEncoder.qoiHeader(image);
        byte[] data = QOIEncoder.encodeData(ArrayUtils.imageToChannels(image.data())); // We encode the data of the image (pixels are beforehand converted to channels)
        return ArrayUtils.concat(header, data, QOISpecification.QOI_EOF); // We concatenate the header, the data and the EOF
    }
}