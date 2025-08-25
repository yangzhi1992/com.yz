package com.commons.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public final class RandomGenerator {

    private final static Logger logger = LoggerFactory.getLogger(RandomGenerator.class);
    private static final AtomicInteger NEXT_SEQUENCE = new AtomicInteger();
    private static final int MACHINE_ID_LEN = 8;


    private static final byte[] MACHINE_ID;
    private static final int PROCESS_ID_LEN = 4;
    private static final int MAX_PROCESS_ID = 4194304;
    private static final int PROCESS_ID;
    private static final int SEQUENCE_LEN = 4;
    private static final int TIMESTAMP_LEN = 8;
    private static final int RANDOM_LEN = 4;


    private static final String[] BYTE2HEX_PAD = new String[256];

    static {
        PROCESS_ID = defaultProcessId();
        MACHINE_ID = defaultMachineId();

        // Generate the lookup table that converts a byte into a 2-digit hexadecimal integer.
        int i;
        for (i = 0; i < 10; i++) {
            BYTE2HEX_PAD[i] = "0" + i;
        }
        for (; i < 16; i++) {
            char c = (char) ('a' + i - 10);
            BYTE2HEX_PAD[i] = "0" + c;
        }
        for (; i < BYTE2HEX_PAD.length; i++) {
            String str = Integer.toHexString(i);
            BYTE2HEX_PAD[i] = str;
        }
    }

    private static byte[] defaultMachineId() {
        byte[] bestMacAddr = NetTool.bestAvailableMac();
        if (bestMacAddr == null) {
            bestMacAddr = new byte[NetTool.MAC_ADDRESS_LENGTH];
            ThreadLocalRandom.current().nextBytes(bestMacAddr);
            logger.warn(
                    "Failed to find a usable hardware address from the network interfaces; using random bytes: {}",
                    formatAddress(bestMacAddr));
        }
        return bestMacAddr;
    }

    private static int defaultProcessId() {
        String value;
        RuntimeMXBean getRuntimeMXBean = ManagementFactory.getRuntimeMXBean();
        value = getRuntimeMXBean.getName();

        int atIndex = value.indexOf('@');
        if (atIndex >= 0) {
            value = value.substring(0, atIndex);
        }

        int pid;
        try {
            pid = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // value did not contain an integer.
            pid = -1;
        }

        if (pid < 0 || pid > MAX_PROCESS_ID) {
            pid = ThreadLocalRandom.current().nextInt(MAX_PROCESS_ID + 1);
            logger.warn("Failed to find the current process ID from '{}'; using a random value: {}",
                    new Object[]{value, pid});
        }

        return pid;
    }

    public static String next() {
        byte[] data =
                new byte[MACHINE_ID_LEN + PROCESS_ID_LEN + SEQUENCE_LEN + TIMESTAMP_LEN
                        + RANDOM_LEN];
        int i = 0;

        // machineId
        System.arraycopy(MACHINE_ID, 0, data, i, MACHINE_ID_LEN);
        i += MACHINE_ID_LEN;

        // processId
        i = writeInt(data, i, PROCESS_ID);

        // sequence
        i = writeInt(data, i, NEXT_SEQUENCE.getAndIncrement());

        // timestamp (kind of)
        i = writeLong(data, i, Long.reverse(System.nanoTime()) ^ System.currentTimeMillis());

        // random
        int random = ThreadLocalRandom.current().nextInt();
        i = writeInt(data, i, random);

        assert i == data.length;
        return HexUtil.hexDump(data, MACHINE_ID_LEN + PROCESS_ID_LEN + SEQUENCE_LEN + TIMESTAMP_LEN,
                RANDOM_LEN);
    }

    private static int writeInt(byte[] data, int i, int value) {
        data[i++] = (byte) (value >>> 24);
        data[i++] = (byte) (value >>> 16);
        data[i++] = (byte) (value >>> 8);
        data[i++] = (byte) value;
        return i;
    }

    private static int writeLong(byte[] data, int i, long value) {
        data[i++] = (byte) (value >>> 56);
        data[i++] = (byte) (value >>> 48);
        data[i++] = (byte) (value >>> 40);
        data[i++] = (byte) (value >>> 32);
        data[i++] = (byte) (value >>> 24);
        data[i++] = (byte) (value >>> 16);
        data[i++] = (byte) (value >>> 8);
        data[i++] = (byte) value;
        return i;
    }


    public static String formatAddress(byte[] addr) {
        StringBuilder buf = new StringBuilder(24);
        for (byte b : addr) {
            buf.append(String.format("%02x:", b & 0xff));
        }
        return buf.substring(0, buf.length() - 1);
    }

    private static final class HexUtil {

        private static final char[] BYTE2CHAR = new char[256];
        private static final char[] HEXDUMP_TABLE = new char[256 * 4];
        private static final String[] HEXPADDING = new String[16];
        private static final String[] HEXDUMP_ROWPREFIXES = new String[65536 >>> 4];
        private static final String[] BYTE2HEX = new String[256];
        private static final String[] BYTEPADDING = new String[16];
        private static final String NEWLINE = System.getProperty("line.separator");

        static {
            final char[] DIGITS = "0123456789abcdef".toCharArray();
            for (int i = 0; i < 256; i++) {
                HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 0x0F];
                HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 0x0F];
            }

            int i;

            // Generate the lookup table for hex dump paddings
            for (i = 0; i < HEXPADDING.length; i++) {
                int padding = HEXPADDING.length - i;
                StringBuilder buf = new StringBuilder(padding * 3);
                for (int j = 0; j < padding; j++) {
                    buf.append("   ");
                }
                HEXPADDING[i] = buf.toString();
            }

            // Generate the lookup table for the start-offset header in each row (up to 64KiB).
            for (i = 0; i < HEXDUMP_ROWPREFIXES.length; i++) {
                StringBuilder buf = new StringBuilder(12);
                buf.append(NEWLINE);
                buf.append(Long.toHexString(i << 4 & 0xFFFFFFFFL | 0x100000000L));
                buf.setCharAt(buf.length() - 9, '|');
                buf.append('|');
                HEXDUMP_ROWPREFIXES[i] = buf.toString();
            }

            // Generate the lookup table for byte-to-hex-dump conversion
            for (i = 0; i < BYTE2HEX.length; i++) {
                BYTE2HEX[i] = ' ' + byteToHexStringPadded(i);
            }

            // Generate the lookup table for byte dump paddings
            for (i = 0; i < BYTEPADDING.length; i++) {
                int padding = BYTEPADDING.length - i;
                StringBuilder buf = new StringBuilder(padding);
                for (int j = 0; j < padding; j++) {
                    buf.append(' ');
                }
                BYTEPADDING[i] = buf.toString();
            }

            // Generate the lookup table for byte-to-char conversion
            for (i = 0; i < BYTE2CHAR.length; i++) {
                if (i <= 0x1f || i >= 0x7f) {
                    BYTE2CHAR[i] = '.';
                } else {
                    BYTE2CHAR[i] = (char) i;
                }
            }
        }

        public static String byteToHexStringPadded(int value) {
            return BYTE2HEX_PAD[value & 0xff];
        }


        private static String hexDump(byte[] array, int fromIndex, int length) {
            if (length < 0) {
                throw new IllegalArgumentException("length: " + length);
            }
            if (length == 0) {
                return "";
            }

            int endIndex = fromIndex + length;
            char[] buf = new char[length << 1];

            int srcIdx = fromIndex;
            int dstIdx = 0;
            for (; srcIdx < endIndex; srcIdx++, dstIdx += 2) {
                System.arraycopy(HEXDUMP_TABLE, (array[srcIdx] & 0xFF) << 1, buf, dstIdx, 2);
            }

            return new String(buf);
        }

    }
}
