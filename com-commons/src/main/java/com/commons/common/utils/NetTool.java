package com.commons.common.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public final class NetTool {
    private final static InetAddressComparator ADDRESS_COMPARATOR = new InetAddressComparator();
    public final static int MAC_ADDRESS_LENGTH = 8;
    private final static Logger logger = LoggerFactory.getLogger(NetTool.class);
    private final static byte[] NOT_FOUND = {-1};
    private static final Inet4Address LOCALHOST4;

    private final static String LOCAL_ADDRESS;
    @Getter
    private final static String DC;

    static {
        byte[] LOCALHOST4_BYTES = {127, 0, 0, 1};
        // Create IPv4 loopback address.
        Inet4Address localhost4 = null;
        try {
            localhost4 = (Inet4Address) InetAddress.getByAddress("localhost", LOCALHOST4_BYTES);
        } catch (Exception e) {
            // We should not get here as long as the length of the address is correct.
        }
        LOCALHOST4 = localhost4;
        LOCAL_ADDRESS = bestAvailableIp();
        DC = parseDcByIp(LOCAL_ADDRESS).name();
    }

    private NetTool() {

    }

    public static String getSnowflakeWorker() {
        if (!DockerTool.isDocker()) {
            return LOCAL_ADDRESS;
        }
        return DockerTool.getHostAddress() + ":" + DockerTool.getDockerMappedPort(8080);
    }

    public static String getLocalAddress() {
        return LOCAL_ADDRESS;
    }

    public static List<InetAddress> getAllByName(String host) throws UnknownHostException {
        List<InetAddress> ipAddresses = Arrays.asList(InetAddress.getAllByName(host));
        if (ipAddresses.size() > 1) {
            ipAddresses.sort(ADDRESS_COMPARATOR);
        }
        return ipAddresses;
    }

    private static String loadLocalIp() {
        String localAddress = null;// 本地IP，如果没有配置外网IP则返回它
        String netAddress = null;// 外网IP
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(':') == -1) {// 外网IP
                        netAddress = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(':') == -1) {// 内网IP
                        localAddress = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            logger.error("failed to get local ip address!", e);
        }
        return StringTool.isNotBlank(netAddress) ? netAddress : localAddress;
    }

    /**
     * 获取本机IP地址,如果没有配置外网地址,则使用内网地址;否则使用外网地址.
     */
    private static String bestAvailableIp() {
        if (DockerTool.isDocker()) {
            return DockerTool.getHostAddress();
        }
        return loadLocalIp();
    }

    public static byte[] bestAvailableMac() {
        // Find the best MAC address available.
        byte[] bestMacAddr = NOT_FOUND;
        InetAddress bestInetAddr = LOCALHOST4;

        // Retrieve the list of available network interfaces.
        Map<NetworkInterface, InetAddress> ifaces = new LinkedHashMap<>();
        try {
            for (Enumeration<NetworkInterface> i = NetworkInterface.getNetworkInterfaces(); i
                    .hasMoreElements(); ) {
                NetworkInterface iface = i.nextElement();
                // Use the interface with proper INET addresses only.
                Enumeration<InetAddress> addrs = iface.getInetAddresses();
                if (addrs.hasMoreElements()) {
                    InetAddress a = addrs.nextElement();
                    if (!a.isLoopbackAddress()) {
                        ifaces.put(iface, a);
                    }
                }
            }
        } catch (SocketException e) {
            logger.warn("Failed to retrieve the list of available network interfaces", e);
        }

        for (Map.Entry<NetworkInterface, InetAddress> entry : ifaces.entrySet()) {
            NetworkInterface iface = entry.getKey();
            InetAddress inetAddr = entry.getValue();
            if (iface.isVirtual()) {
                continue;
            }

            byte[] macAddr;
            try {
                macAddr = iface.getHardwareAddress();
            } catch (SocketException e) {
                logger.debug("Failed to get the hardware address of a network interface: {}", iface,
                        e);
                continue;
            }

            boolean replace = false;
            int res = compareAddresses(bestMacAddr, macAddr);
            if (res < 0) {
                // Found a better MAC address.
                replace = true;
            } else if (res == 0) {
                // Two MAC addresses are of pretty much same quality.
                res = compareAddresses(bestInetAddr, inetAddr);
                if (res < 0) {
                    // Found a MAC address with better INET address.
                    replace = true;
                } else if (res == 0) {
                    // Cannot tell the difference.  Choose the longer one.
                    if (bestMacAddr.length < macAddr.length) {
                        replace = true;
                    }
                }
            }

            if (replace) {
                bestMacAddr = macAddr;
                bestInetAddr = inetAddr;
            }
        }

        if (bestMacAddr == NOT_FOUND) {
            return null;
        }

        switch (bestMacAddr.length) {
            case 6: // EUI-48 - convert to EUI-64
                byte[] newAddr = new byte[MAC_ADDRESS_LENGTH];
                System.arraycopy(bestMacAddr, 0, newAddr, 0, 3);
                newAddr[3] = (byte) 0xFF;
                newAddr[4] = (byte) 0xFE;
                System.arraycopy(bestMacAddr, 3, newAddr, 5, 3);
                bestMacAddr = newAddr;
                break;
            default: // Unknown
                bestMacAddr = Arrays.copyOf(bestMacAddr, MAC_ADDRESS_LENGTH);
        }

        return bestMacAddr;
    }

    private static int compareAddresses(byte[] current, byte[] candidate) {
        if (candidate == null) {
            return 1;
        }

        // Must be EUI-48 or longer.
        if (candidate.length < 6) {
            return 1;
        }

        // Must not be filled with only 0 and 1.
        boolean onlyZeroAndOne = true;
        for (byte b : candidate) {
            if (b != 0 && b != 1) {
                onlyZeroAndOne = false;
                break;
            }
        }

        if (onlyZeroAndOne) {
            return 1;
        }

        // Must not be a multicast address
        if ((candidate[0] & 1) != 0) {
            return 1;
        }

        // Prefer globally unique address.
        if ((current[0] & 2) == 0) {
            if ((candidate[0] & 2) == 0) {
                // Both current and candidate are globally unique addresses.
                return 0;
            } else {
                // Only current is globally unique.
                return 1;
            }
        } else {
            if ((candidate[0] & 2) == 0) {
                // Only candidate is globally unique.
                return -1;
            } else {
                // Both current and candidate are non-unique.
                return 0;
            }
        }
    }

    private static int compareAddresses(InetAddress current, InetAddress candidate) {
        return scoreAddress(current) - scoreAddress(candidate);
    }


    private static int scoreAddress(InetAddress addr) {
        if (addr.isAnyLocalAddress() || addr.isLoopbackAddress()) {
            return 0;
        }
        if (addr.isMulticastAddress()) {
            return 1;
        }
        if (addr.isLinkLocalAddress()) {
            return 2;
        }
        if (addr.isSiteLocalAddress()) {
            return 3;
        }

        return 4;
    }

    public enum DcEnum {
        unknown,    //未知
        bjdx,       //北京电信（4、7）
        bjdx11,     //北京电信（11）
        sjhl,       //世纪互联
        jy,         //济阳
        shgq,       //上海桂桥
        shjj,       //上海金京
        bjdxt,      //北京电信通
        bjlt,       //北京联通（5）
        zjy,        //中经云
        bdwg,       //百度万国
        zyx,        //中云信
        whlk        //武汉临空洪
    }

    public static DcEnum parseDcByIp(String ip) {
        if (ip == null) {
            return DcEnum.unknown;
        }
        if (ip.matches("^10\\.10\\..*")) {
            return DcEnum.bjdx;
        } else if (ip.matches("^10\\.110\\..*")) {
            return DcEnum.bjdx11;
        } else if (ip.matches("^10\\.77\\..*")) {
            return DcEnum.sjhl;
        } else if (ip.matches("^10\\.153\\..*")) {
            return DcEnum.jy;
        } else if (ip.matches("^10\\.221\\..*")) {
            return DcEnum.shgq;
        } else if (ip.matches("^10\\.121\\..*")) {
            return DcEnum.shjj;
        } else if (ip.matches("^10\\.15\\..*")) {
            return DcEnum.bjdxt;
        } else if (ip.matches("^10\\.13\\..*")) {
            return DcEnum.bjlt;
        } else if (ip.matches("^10\\.49\\..*")) {
            return DcEnum.zjy;
        } else if (ip.matches("^10\\.39\\..*")) {
            return DcEnum.bdwg;
        } else if (ip.matches("^10\\.62\\..*")) {
            return DcEnum.zyx;
        } else if (ip.matches("^10\\.52\\..*")) {
            return DcEnum.zjy;
        } else if (ip.matches("^10\\.41\\..*")) {
            return DcEnum.bdwg;
        } else if (ip.matches("^10\\.130\\..*")) {
            return DcEnum.whlk;
        } else {
            return DcEnum.unknown;
        }
    }

    static class InetAddressComparator implements Comparator<InetAddress> {

        @Override
        public int compare(InetAddress addr1, InetAddress addr2) {
            if (addr1 == null) {
                return addr2 == null ? 0 : -1;
            } else if (addr2 == null) {
                return 1;
            }

            byte[] addrBytes1 = addr1.getAddress();
            byte[] addBytes2 = addr2.getAddress();

            // general ordering: ipv4 before ipv6
            if (addrBytes1.length < addBytes2.length) {
                return -1;
            }
            if (addrBytes1.length > addBytes2.length) {
                return 1;
            }

            // we have 2 ips of the same type, so we have to compare each byte
            for (int i = 0; i < addrBytes1.length; i++) {
                int b1 = unsignedByteToInt(addrBytes1[i]);
                int b2 = unsignedByteToInt(addBytes2[i]);
                if (b1 == b2) {
                    continue;
                }
                if (b1 < b2) {
                    return -1;
                } else {
                    return 1;
                }
            }

            return 0;
        }

        private int unsignedByteToInt(byte b) {
            return (int) b & 0xFF;
        }
    }

    public static String intToIpv4String(int i) {
        return new StringBuilder(15).append((i >> 24) & 0xff).append('.').append((i >> 16) & 0xff)
                .append('.')
                .append((i >> 8) & 0xff).append('.').append(i & 0xff).toString();
    }

    /**
     * Ipv4 String 转换到int
     */
    public static int ipv4StringToInt(String ipv4Str) {
        byte[] byteAddress = ip4StringToBytes(ipv4Str);
        if (byteAddress == null) {
            return 0;
        } else {
            return toInt(byteAddress);
        }
    }

    public static int toInt(byte[] bytes) {
        if (bytes == null || bytes.length < Integer.BYTES) {
            throw new IllegalArgumentException(
                    String.format("array too small: %s < %s", bytes.length, Integer.BYTES));
        }
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3]
                & 0xFF);
    }

    /**
     * Ipv4 String 转换到byte[]
     */
    private static byte[] ip4StringToBytes(String ipv4Str) {
        if (ipv4Str == null) {
            return null;
        }

        List<String> it = StringTool.split(ipv4Str, '.', 4);
        if (it.size() != 4) {
            return null;
        }

        byte[] byteAddress = new byte[4];
        for (int i = 0; i < 4; i++) {
            int tempInt = Integer.parseInt(it.get(i));
            if (tempInt > 255 || tempInt < 0) {
                return null;
            }
            byteAddress[i] = (byte) tempInt;
        }
        return byteAddress;
    }
}
