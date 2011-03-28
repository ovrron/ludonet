package com.cliserver.test.comm;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.util.Log;

public class IPAddressHelper {
	private static final String TAG = "-IPAddress-:";

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			Log.e(TAG, e.toString(), e);
		}
		return null;
	}

	public static InetAddress getLocalIpAddressInet() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress;
					}
				}
			}
		} catch (SocketException e) {
			Log.e(TAG, e.toString(), e);
		}
		return null;
	}

	public static int ipToInt(final String addr) {
		final String[] addressBytes = addr.split("\\.");

		int ip = 0;
		for (int i = 0; i < 4; i++) {
			ip <<= 8;
			ip |= Integer.parseInt(addressBytes[i]);
		}
		return ip;
	}
	
	public static byte[] ipToByteArray(final String addr) {
		final String[] addressBytes = addr.split("\\.");
		byte inets[] = new byte[4];

		for (int i = 0; i < 4; i++) {
			inets[i] = (byte)Integer.parseInt(addressBytes[i]);
		}
		return inets;
	}
}
