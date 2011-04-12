package com.ronny.ludo.helper;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.util.Log;

// se http://www.krvarma.com/posts/android/getting-ip-address-of-the-device-in-android/

public class IPAddressHelper
{
	private static final String TAG = "-IPAddress-:";
	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
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
}
