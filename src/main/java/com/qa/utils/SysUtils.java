package com.qa.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class SysUtils {

    private static Logger LOG = LoggerFactory.getLogger(SysUtils.class);

    private static Properties properties;

    static {
        properties = System.getProperties();
    }

    private static String getOperatingSystem() {
        return properties.getProperty("os.name").toLowerCase();
    }

    public static String getLineSeparator() {
        return properties.getProperty("line.separator");
    }

    public static String getFileSeparator() {
        return properties.getProperty("file.separator");
    }

    public static void sleep(long msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepWithMessage(long msec, String message) {
        try {
            LOG.info(message);
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(double value) {
        try {
            Thread.sleep(Math.round(value));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isMacOS() {
        return getOperatingSystem().contains("mac");
    }

    public static boolean isLinux() {
        return getOperatingSystem().contains("linux");
    }

    public static boolean isWindows() {
        return getOperatingSystem().contains("windows");
    }

    public static String getLocalIP() {
        String ipAddress = isLinux() ? getLinuxIP(Arrays.asList("ens18", "enp3s0")) : getNotLinuxIP();
        assert ipAddress != null : "IP Address cannot be 'null'";
        LOG.info("IP address of current machine is: " + ipAddress);
        return ipAddress;
    }

    private static String getLinuxIP(List<String> networkNames) {
        for (String name : networkNames) {
            try {
                NetworkInterface networkInterface = NetworkInterface.getByName(name);
                Enumeration<InetAddress> inetAddress = networkInterface.getInetAddresses();
                while (inetAddress.hasMoreElements()) {
                    InetAddress currentAddress = inetAddress.nextElement();
                    if (currentAddress instanceof Inet4Address) {
                        return currentAddress.getHostAddress();
                    }
                }
            } catch (SocketException | NullPointerException ignored) {
            }
        }
        return null;
    }

    private static String getNotLinuxIP() {
        String ipAddress = null;
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ipAddress;
    }
}
