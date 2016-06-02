package phoenix.datasource.davincicode.util;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.GZIPOutputStream;

import com.alibaba.fastjson.JSON;

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
				if (!address.isSiteLocalAddress() && !address.isLoopbackAddress()
						&& address.getHostAddress().indexOf(":") == -1 && isInnerNet(address.getHostAddress())) {
					netIP = address.getHostAddress();
					finded = true;
					break;
				} else if (address.isSiteLocalAddress() && !address.isLoopbackAddress()
						&& address.getHostAddress().indexOf(":") == -1 && isInnerNet(address.getHostAddress())) {
					localIP = address.getHostAddress();
				}
			}
		}
		return (netIP != null && !"".equals(netIP)) ? netIP : localIP;
	}

	public static boolean isInnerNet(String v) {
		if (v.startsWith("10.") || v.startsWith("172.") || v.startsWith("192.168"))
			return true;
		return false;
	}

	public static String toString(InputStream input, String encoding) throws IOException {
		return (null == encoding) ? toString(new InputStreamReader(input))
				: toString(new InputStreamReader(input, encoding));
	}

	public static String toString(Reader reader) throws IOException {
		CharArrayWriter sw = new CharArrayWriter();
		copy(reader, sw);
		return sw.toString();
	}

	public static long copy(Reader input, Writer output) throws IOException {
		char[] buffer = new char[1 << 12];
		long count = 0;
		for (int n = 0; (n = input.read(buffer)) >= 0;) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static HttpResult httpGet(String url) {
		return httpGet(url, "utf-8");
	}

	public static HttpResult httpGet(String url, String encoding) {
		int trytimes = 3;
		while (trytimes > 0) {
			HttpURLConnection connection = null;
			try {
				URL u = new URL(url);
				connection = (HttpURLConnection) u.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(100);
				connection.connect();
				int respCode = connection.getResponseCode();
				String resp = null;
				if (HttpURLConnection.HTTP_OK == respCode) {
					resp = toString(connection.getInputStream(), encoding);
				} else {
					resp = toString(connection.getErrorStream(), encoding);
				}
				return new HttpResult(HttpURLConnection.HTTP_OK == respCode, resp);
			} catch (Exception e) {
				trytimes--;
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}
		return new HttpResult(false, "Try my best,but failed![" + url + "]");
	}

	public static HttpResult httpPost(String url, byte[] params) {
		return httpPost(url, "utf-8", null, params);
	}

	public static HttpResult httpPost(String url, String params) {
		return httpPost(url, "utf-8", params, null);
	}

	public static HttpResult httpPost(String url, String encoding, String paramsString, byte[] params) {
		int trytimes = 3;
		while (trytimes > 0) {
			HttpURLConnection connection = null;
			try {
				URL u = new URL(url);
				connection = (HttpURLConnection) u.openConnection();
				connection.setRequestMethod("POST");
				connection.setConnectTimeout(100);
				connection.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				if (paramsString == null) {
					wr.write(params);
				} else {
					wr.writeBytes(paramsString);
				}
				wr.flush();
				wr.close();
				int respCode = connection.getResponseCode();
				String resp = null;
				if (HttpURLConnection.HTTP_OK == respCode) {
					resp = toString(connection.getInputStream(), encoding);
				} else {
					resp = toString(connection.getErrorStream(), encoding);
				}
				return new HttpResult(HttpURLConnection.HTTP_OK == respCode, resp);
			} catch (Exception e) {
				trytimes--;
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}
		return new HttpResult(false, "Try my best,but failed![" + url + "]");
	}

	public static class HttpResult {
		final public boolean success;
		final public String content;

		public HttpResult(boolean success, String content) {
			this.success = success;
			this.content = content;
		}
	}

	public static String buildParams(String base, String key, String value) {
		base = ((base != null && base.length() > 0) ? (base + "&") : "");
		return base + key + "=" + value;
	}

	public static <K> byte[] compress(K k) throws IOException {
		byte[] jsonData = JSON.toJSONBytes(k);
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(1024);
		GZIPOutputStream gzipOutput = new GZIPOutputStream(byteOutput, 1024);
		gzipOutput.write(jsonData);
		gzipOutput.close();
		return byteOutput.toByteArray();
	}
}
