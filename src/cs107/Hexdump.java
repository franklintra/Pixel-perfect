package cs107;

/**
 * Utility class used to simulate the Unix command "hexdump"
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @since 1.0
 */
public final class Hexdump {

    // ============================================================================================
    // ================================== HEXDUMP API =============================================
    // ============================================================================================

    /**
     * Print the content of the array in a hexadecimal form
     * in the Terminal
     * @param binary (byte[]) - Array to print
     */
    public static void hexdump(byte[] binary){
        hexdump(binary, 0);
    }

    /**
     * Print the content of the array in a hexadecimal form starting from
     * a specific index
     * @param binary (byte[]) - Array to print
     * @param start_address (int) - Index from which we start printing
     */
    public static void hexdump(byte[] binary, int start_address){
        hexdump(binary, start_address, binary.length - 1);
    }

    /**
     * Print the content of an array starting from a specific index
     * and finishing in another index
     * @param binary (byte[]) - Array to print
     * @param start_address (int) - Index from which we start printing
     * @param end_address (int) - Index from which we stop printing
     * @throws AssertionError if the array is null or the addresses are
     * outside the possible bound
     */
    public static void hexdump(byte[] binary, int start_address, int end_address){
        assert binary != null : "(hexdump) You've used a null array, cannot dump the null array";
        assert  0 <= start_address && start_address <= end_address;
        assert end_address < binary.length;
        var mod_length = (end_address - start_address + 1) % 10;
        var div_length = (end_address - start_address + 1) / 10;
        System.out.println("==========================================================================================");
        for(var i = 0; i < div_length; i++)
            System.out.print(dump_10bytes(start_address + i * 10, binary));
        System.out.print(switch (mod_length){
            case 1 -> dump_1byte(start_address + div_length * 10, binary);
            case 2 -> dump_2bytes(start_address + div_length * 10, binary);
            case 3 -> dump_3bytes(start_address + div_length * 10, binary);
            case 4 -> dump_4bytes(start_address + div_length * 10, binary);
            case 5 -> dump_5bytes(start_address + div_length * 10, binary);
            case 6 -> dump_6bytes(start_address + div_length * 10, binary);
            case 7 -> dump_7bytes(start_address + div_length * 10, binary);
            case 8 -> dump_8bytes(start_address + div_length * 10, binary);
            case 9 -> dump_9bytes(start_address + div_length * 10, binary);
            default -> ""; // Dead code
        });
        System.out.println("==========================================================================================");
    }

    // ============================================================================================

    // Hide default constructor
    private Hexdump(){}

    private static String dump_1byte(int addr, byte[] b){
        var fmt = "%06X : %02X | %c |%n";
        return String.format(fmt, addr, b[addr], display_char(b[addr]));
    }
    private static String dump_2bytes(int addr, byte[] b){
        var fmt = "%06X : %02X %02X | %c%c |%n";
        return String.format(fmt, addr, b[addr], b[addr + 1],
                display_char(b[addr]),
                display_char(b[addr + 1]));
    }
    private static String dump_3bytes(int addr, byte[] b){
        var fmt = "%06X : %02X %02X %02X | %c%c%c |%n";
        return String.format(fmt, addr, b[addr], b[addr+1], b[addr+2],
                display_char(b[addr]),
                display_char(b[addr+1]),
                display_char(b[addr+2]));
    }

    private static String dump_4bytes(int addr, byte[] b){
        var fmt = "%06X : %02X %02X %02X %02X | %c%c%c%c |%n";
        return String.format(fmt, addr, b[addr], b[addr+1],b[addr+2], b[addr+3],
                display_char(b[addr]),
                display_char(b[addr+1]),
                display_char(b[addr+2]),
                display_char(b[addr+3]));
    }

    private static String dump_5bytes(int addr, byte[] b){
        var fmt = "%06X : %02X %02X %02X %02X %02X | %c%c%c%c%c |%n";
        return String.format(fmt, addr, b[addr], b[addr+1],b[addr+2], b[addr+3],b[addr+4],
                display_char(b[addr]),
                display_char(b[addr+1]),
                display_char(b[addr+2]),
                display_char(b[addr+3]),
                display_char(b[addr+4]));
    }

    private static String dump_6bytes(int addr, byte[] b){
        var fmt = "%06X : %02X %02X %02X %02X %02X %02X  | %c%c%c%c%c%c |%n";
        return String.format(fmt, addr, b[addr], b[addr+1],b[addr+2], b[addr+3],b[addr+4], b[addr+5],
                display_char(b[addr]),
                display_char(b[addr+1]),
                display_char(b[addr+2]),
                display_char(b[addr+3]),
                display_char(b[addr+4]),
                display_char(b[addr+5]));
    }

    private static String dump_7bytes(int addr, byte[] b){
        var fmt = "%06X : %02X %02X %02X %02X %02X %02X %02X  | %c%c%c%c%c%c%c |%n";
        return String.format(fmt, addr, b[addr], b[addr+1],b[addr+2], b[addr+3],b[addr+4], b[addr+5],b[addr+6],
                display_char(b[addr]),
                display_char(b[addr+1]),
                display_char(b[addr+2]),
                display_char(b[addr+3]),
                display_char(b[addr+4]),
                display_char(b[addr+5]),
                display_char(b[addr+6]));
    }

    private static String dump_8bytes(int addr, byte[] b){
        var fmt = "%06X : %02X %02X %02X %02X %02X %02X %02X %02X  | %c%c%c%c%c%c%c%c |%n";
        return String.format(fmt, addr, b[addr], b[addr+1],b[addr+2], b[addr+3],b[addr+4], b[addr+5],b[addr+6], b[addr+7],
                display_char(b[addr]),
                display_char(b[addr+1]),
                display_char(b[addr+2]),
                display_char(b[addr+3]),
                display_char(b[addr+4]),
                display_char(b[addr+5]),
                display_char(b[addr+6]),
                display_char(b[addr+7]));
    }

    private static String dump_9bytes(int addr, byte[] b){
        var fmt = "%06X : %02X %02X %02X %02X %02X %02X %02X %02X %02X | %c%c%c%c%c%c%c%c%c |%n";
        return String.format(fmt, addr, b[addr], b[addr+1],b[addr+2], b[addr+3],b[addr+4], b[addr+5],b[addr+6],
                b[addr+7], b[addr+8],
                display_char(b[addr]),
                display_char(b[addr+1]),
                display_char(b[addr+2]),
                display_char(b[addr+3]),
                display_char(b[addr+4]),
                display_char(b[addr+5]),
                display_char(b[addr+6]),
                display_char(b[addr+7]),
                display_char(b[addr+8]));
    }

    private static String dump_10bytes(int addr, byte[] b){
        var fmt = "%06X : %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X | %c%c%c%c%c%c%c%c%c%c |%n";
        return String.format(fmt, addr, b[addr], b[addr+1],b[addr+2], b[addr+3],b[addr+4], b[addr+5],b[addr+6],
                b[addr+7], b[addr+8],b[addr+9],
                display_char(b[addr]),
                display_char(b[addr+1]),
                display_char(b[addr+2]),
                display_char(b[addr+3]),
                display_char(b[addr+4]),
                display_char(b[addr+5]),
                display_char(b[addr+6]),
                display_char(b[addr+7]),
                display_char(b[addr+8]),
                display_char(b[addr+9]));
    }


    private static char display_char(byte c){
        return Character.isISOControl(c) ? '.' : (char) c;
    }

}