package tools.hardware;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class HardwareInfoUtil {
	public static void main(String[] args) {
		System.out.println(getMacAddr());

	}

	/**
     * 获取IP
     */
    public static String getIp() {
        String ip = "";
        try {
            InetAddress in = InetAddress.getLocalHost();
            ip = in.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }
    /**
     * 获取mac地址
     */
    public static String getMacAddr() {
        String macAddr = "";
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
            NetworkInterface dir = NetworkInterface.getByInetAddress(addr);
            byte[] dirMac = dir.getHardwareAddress();

            int count = 0;
            for (int b : dirMac) {
                if (b < 0)
                    b = 256 + b;
                if (b == 0) {
                    macAddr = macAddr.concat("00");
                }
                if (b > 0) {

                    int a = b / 16;
                    if (a == 10)
                        macAddr = macAddr.concat("A");
                    else if (a == 11)
                        macAddr = macAddr.concat("B");
                    else if (a == 12)
                        macAddr = macAddr.concat("C");
                    else if (a == 13)
                        macAddr = macAddr.concat("D");
                    else if (a == 14)
                        macAddr = macAddr.concat("E");
                    else if (a == 15)
                        macAddr = macAddr.concat("F");
                    else
                        macAddr = macAddr.concat(String.valueOf(a));
                    a = (b % 16);
                    if (a == 10)
                        macAddr = macAddr.concat("A");
                    else if (a == 11)
                        macAddr = macAddr.concat("B");
                    else if (a == 12)
                        macAddr = macAddr.concat("C");
                    else if (a == 13)
                        macAddr = macAddr.concat("D");
                    else if (a == 14)
                        macAddr = macAddr.concat("E");
                    else if (a == 15)
                        macAddr = macAddr.concat("F");
                    else
                        macAddr = macAddr.concat(String.valueOf(a));
                }
                if (count < dirMac.length - 1)
                    macAddr = macAddr.concat("-");
                count++;
            }

        } catch (UnknownHostException e) {
            macAddr = e.getMessage();
        } catch (SocketException e) {
            macAddr = e.getMessage();
        }
        return macAddr;
    }
}
