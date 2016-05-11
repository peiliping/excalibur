package icesword.agent.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetTools {

    public static String getLocalIP() {
        String localIP = null;
        String netIP = null;
        Enumeration<NetworkInterface> nInterfaces = null;
        try {
            nInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
        }
        boolean finded = false;
        while (nInterfaces.hasMoreElements() && !finded) {
            Enumeration<InetAddress> inetAddress = nInterfaces.nextElement().getInetAddresses();
            while (inetAddress.hasMoreElements()) {
                InetAddress address = inetAddress.nextElement();
                if (!address.isSiteLocalAddress() && !address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
                    netIP = address.getHostAddress();
                    finded = true;
                    break;
                } else if (address.isSiteLocalAddress() && !address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
                    localIP = address.getHostAddress();
                }
            }
        }
        return (netIP != null && !"".equals(netIP)) ? netIP : localIP;
    }

}
