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
        assert image.color_space() == QOISpecification.sRGB || image.color_space() == QOISpecification.ALL;
        assert image.channels() == QOISpecification.RGB || image.channels() == QOISpecification.RGBA;
        assert image != null;
        var header = new byte[4];
        header = ArrayUtils.concat(QOISpecification.QOI_MAGIC, ArrayUtils.fromInt(image.data()[0].length), ArrayUtils.fromInt(image.data().length), new byte[]{image.channels()}, new byte[]{(image.color_space())});

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
                (byte) ((diff[0]-diff[1]+8 << 4) | diff[2]-diff[1]+8)
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
        byte[][] hashing = new byte[64][4];
        int count = 0;
        byte[] previous;
        byte[] current;
        ArrayList<byte[]> data = new ArrayList<>();
        //data.add(QOISpecification.START_PIXEL);
        //data.add(0, QOIEncoder.qoiOpRGBA(pixels[0]));
        for (int i = 0; i< image.length; i++) {
            if (i==0) {
                previous = QOISpecification.START_PIXEL;
            }
            else {previous = image[i-1];}
            current = image[i];
            //Pixel is the same as the 0<n<63 previous one
            //Todo: doesn't work
            if (ArrayUtils.equals(previous, current)){
                count++;
                if (count == 62 || i == image.length-1){
                    data.add(QOIEncoder.qoiOpRun((byte) count));
                    count = 0;
                    continue;
                } else {
                    continue;
                }
            } else {
                if (count > 0){
                    data.add(QOIEncoder.qoiOpRun((byte) (count)));
                    //System.out.println("Run of " + count + " pixels");
                    count = 0;
                }
            }
            //Hashing table works
            if (ArrayUtils.equals(hashing[QOISpecification.hash(current)], current)) {
                data.add(QOIEncoder.qoiOpIndex(QOISpecification.hash(current)));
                continue;
            }
            else {
                hashing[QOISpecification.hash(current)] = current;
            }
            //Diff works
            if (current[3]==previous[3] && checkDelta(current, previous, -3, 2)) {
                //System.out.println("Diff"+Arrays.toString(QOIEncoder.qoiOpDiff(calculateDelta(ArrayUtils.extract(current, 0, 3), ArrayUtils.extract(previous, 0, 3)))));
                data.add(QOIEncoder.qoiOpDiff(calculateDelta(ArrayUtils.extract(current, 0, 3), ArrayUtils.extract(previous, 0, 3))));
                continue;
            }
            //Luma works
            if (current[3]==previous[3] && checkDelta(ArrayUtils.wrap(current[1]) , ArrayUtils.wrap(previous[1]), -33, 32) && checkDelta(new byte[]{(byte) (current[0]-previous[0]), (byte) (current[2]-previous[2])}, new byte[]{(byte) (current[1] - previous[1]), (byte) (current[1]-previous[1])}, -9, 8)) {
                data.add(QOIEncoder.qoiOpLuma(calculateDelta(ArrayUtils.extract(current, 0, 3), ArrayUtils.extract(previous, 0, 3))));
                //System.out.println("Luma"+ Arrays.toString(data.get(data.size() - 1)));
                continue;
            }
            //RGB works
            if (current[3] == previous[3]) {
                //System.out.println("RGB"+Arrays.toString(QOIEncoder.qoiOpRGB(current)));
                data.add(QOIEncoder.qoiOpRGB(current));
                continue;
            }
            //RGBA works
            //System.out.println("RGBA"+Arrays.toString(QOIEncoder.qoiOpRGBA(current)));
            data.add(QOIEncoder.qoiOpRGBA(current));
        }
        byte[][] encoded = new byte[data.size()][];
        for (int i=0; i<data.size(); i++) {
            encoded[i] = data.get(i);
        }
        //Hexdump.hexdump(ArrayUtils.concat(encoded));
        return ArrayUtils.concat(encoded);
    }

    public static boolean checkDelta(byte[] a, byte[] b, int lower, int upper){
        for (int i=0; i<a.length; i++){
            if (a[i]-b[i] <= lower || a[i]-b[i] >= upper){
                return false;
            }
        }
        return true;
    }

    public static byte[] calculateDelta(byte[] a, byte[] b){
        byte[] delta = new byte[a.length];
        for (int i=0; i<a.length; i++){
            delta[i] = (byte) (a[i]-b[i]);
        }
        return delta;
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
        assert image != null;
        byte[] header = QOIEncoder.qoiHeader(image);
        byte[] data = QOIEncoder.encodeData(ArrayUtils.imageToChannels(image.data()));
        //Hexdump.hexdump(ArrayUtils.concat(header, data, QOISpecification.QOI_EOF));
        Helper.write("image.qoi", ArrayUtils.concat(header, data, QOISpecification.QOI_EOF));
        //Hexdump.hexdump(ArrayUtils.concat(header, data, QOISpecification.QOI_EOF));
        //Hexdump.hexdump(data);
        return ArrayUtils.concat(header, data, QOISpecification.QOI_EOF);
    }
}