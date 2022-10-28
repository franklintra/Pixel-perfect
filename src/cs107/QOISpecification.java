package cs107;

/**
 * Utility class to describe the "Quite Ok Image" Specification
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.2
 * @since 1.0
 */
public final class QOISpecification {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOISpecification(){}

    // ============================================================================================
    // ================================== CHANNEL INDEXING ========================================
    // ============================================================================================

    public static final int r = 0;

    public static final int g = 1;

    public static final int b = 2;

    public static final int a = 3;


    // ==================================================================================
    // =============================== CHANNELS TAGS ====================================
    // ==================================================================================

    /**
     * "RGB CHANNEL" TAG
     */
    public static final byte RGB  = 3;

    /**
     * "RGBA CHANNEL" TAG
     */
    public static final byte RGBA = 4;

    // ==================================================================================
    // =============================== COLOR SPACE TAGS =================================
    // ==================================================================================

    /**
     * "sRGB COLOR SPACE" TAG
     */
    public static final byte sRGB = 0;

    /**
     * "ALL COLOR SPACE TAG"
     */
    public static final byte ALL  = 1;

    // ==================================================================================
    // =========================== "Quite Ok Image" Header ==============================
    // ==================================================================================

    /**
     * Magic Number of a "Quite Ok Image" file
     */
    public static final byte[] QOI_MAGIC = new byte[]{'q', 'o', 'i', 'f'};

    /**
     * Size of a "Quite Ok Image" header
     */
    public static final int HEADER_SIZE = QOI_MAGIC.length + 4 + 4 + 1 + 1;

    // ==================================================================================
    // ======================== "Quite Ok Image" Start Pixel ============================
    // ==================================================================================

    /**
     * First pixel to be stored as "previous pixel" when encoding and decoding
     */
    public static final byte[] START_PIXEL = new byte[]{0, 0, 0, (byte) 255};

    // ==================================================================================
    // ============================ "Quite Ok Image" EOF ================================
    // ==================================================================================

    /**
     * "End Of File" of a "Quite Ok Image" file
     */
    public static final byte[] QOI_EOF = new byte[]{0, 0, 0, 0, 0, 0, 0, 1};

    // ==================================================================================
    // ============================ "Quite Ok Image" Tags ===============================
    // ==================================================================================

    /**
     * "QOI_OP_RGB" TAG
     */
    public static final byte QOI_OP_RGB_TAG   = (byte) 0b11_11_11_10;

    /**
     * "QOI_OP_RGBA" TAG
     */
    public static final byte QOI_OP_RGBA_TAG  = (byte) 0b11_11_11_11;

    /**
     * "QOI_OP_INDEX" TAG
     */
    public static final byte QOI_OP_INDEX_TAG = (byte) 0b00_00_00_00;

    /**
     * "QOI_OP_DIFF" TAG
     */
    public static final byte QOI_OP_DIFF_TAG  = (byte) 0b01_00_00_00;

    /**
     * "QOI_OP_LUMA" TAG
     */
    public static final byte QOI_OP_LUMA_TAG  = (byte) 0b10_00_00_00;

    /**
     * "QOI_OP_RUN" TAG
     */
    public static final byte QOI_OP_RUN_TAG   = (byte) 0b11_00_00_00;

    // ==================================================================================
    // ======================== "Quite Ok Image" Hash Function ==========================
    // ==================================================================================

    /**
     * Hash a given pixel using the hash function specific to "Quite Ok Image" format
     * @apiNote index = (r * 3 + g * 5 + b * 7 + a * 11) % 64
     * @param pixel (byte[]) - Pixel to hash
     * @return (int) - hash of the pixel
     */
    public static byte hash(byte[] pixel){
        assert pixel.length == 4;
        var tmp = (pixel[r] * 3 + pixel[g] * 5 + pixel[b] * 7 + pixel[a] * 11) % 64;
        return (byte) (tmp < 0 ? tmp + 64 : tmp);
    }

}