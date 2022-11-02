package cs107;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

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
        assert image.color_space() == QOISpecification.sRGB || image.color_space() == QOISpecification.ALL;
        assert image.channels() == QOISpecification.RGB || image.channels() == QOISpecification.RGBA;
        assert image != null;
        var header = new byte[4];
        header = ArrayUtils.concat(QOISpecification.QOI_MAGIC, ArrayUtils.fromInt(image.data()[0].length), ArrayUtils.fromInt(image.data().length), new byte[]{(byte) image.channels()}, new byte[]{(image.color_space())});

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
    }

    /**
     * Encode the index using the QOI_OP_INDEX schema
     * @param index (byte) - Index of the pixel
     * @throws AssertionError if the index is outside the range of all possible indices
     * @return (byte[]) - Encoding of the index using the QOI_OP_INDEX schema
     */
    public static byte[] qoiOpIndex(byte index){
        /* TODO: Check min and max index value*/
        assert index >= 0 && index <= 255;
        return new byte[]{(byte) (QOISpecification.QOI_OP_INDEX_TAG | index)};
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
        * following this logic it's similar to x = byte(x+2)
         */
        assert diff.length == 3;
        assert diff[0] >= -3 && diff[0] <= 2;
        assert diff[1] >= -3 && diff[1] <= 2;
        assert diff[2] >= -3 && diff[2] <= 2;
        //System.out.println(Integer.toBinaryString(QOISpecification.QOI_OP_DIFF_TAG));
        //System.out.println(Integer.toBinaryString((byte) (0b01000000 | ArrayUtils.dByte(diff[0])<<4  | ArrayUtils.dByte(diff[1]) << 2 | ArrayUtils.dByte(diff[2]))));
        return new byte[]{(byte) (QOISpecification.QOI_OP_DIFF_TAG | (diff[0]+2)<<4  | (diff[1]+2) << 2 | (diff[2]+2))};
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
        assert diff.length==3;
        assert diff[1] > -33 && diff[1] < 32;
        assert diff[0]-diff[1] > -9 && diff[0]-diff[1] < 8;
        assert diff[2]-diff[1] > -9 && diff[2]-diff[1] < 8;
        return new byte[]{
                (byte) (QOISpecification.QOI_OP_LUMA_TAG | (diff[1]+32)),
                (byte) ((diff[0]-diff[1]+8 >> 4) | diff[2]-diff[1]+8)
        };
    }

    /**
     * Encode the number of similar pixels using the QOI_OP_RUN schema
     * @param count (byte) - Number of similar pixels
     * @throws AssertionError if count is not between 0 (exclusive) and 63 (exclusive)
     * @return (byte[]) - Encoding of count
     */
    public static byte[] qoiOpRun(byte count){
        assert (0 < count && count < 63);
        return new byte[]{
                (byte) (QOISpecification.QOI_OP_RUN_TAG | count-1)
        };
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
        // Let's start by making the image a ring
        byte[] linearised = ArrayUtils.concat(image);
        ArrayList<Byte> data;
        int i = 0;
        do {
            //data.add(QOIEncoder.qoiOpRGBA(ArrayUtils.fromInt(linearised[i])));
            i++;
        } while (i<linearised.length);
        return new byte[]{};
    }

    /**
     * Creates the representation in memory of the "Quite Ok Image" file.
     * @apiNote THE FILE IS NOT CREATED YET, THIS IS JUST ITS REPRESENTATION.
     * TO CREATE THE FILE, YOU'LL NEED TO CALL Helper::write
     * @param image (Helper.Image) - Image to encode
     * @return (byte[]) - Binary representation of the "Quite Ok File" of the image
     * @throws AssertionError if the image is null
     */
    public static byte[] qoiFile(Helper.Image image){
        return Helper.fail("Not Implemented");
    }

}