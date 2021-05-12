package me.hwr.ICQ.Client;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Java2_Ch4
 * 
 * @author HowardWu
 */
public class Client {

	public static void startClient(String IP, int Port) {
		System.out.println("Client: " + IP + ":" + Port);
		Socket st = null;
		try {
			st = new Socket(IP, Port);
			InputStream is = st.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			ClientReader cr = new ClientReader(dis);
			cr.start();

			OutputStream os = st.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			@SuppressWarnings("unused")
			ClientWriter cw = new ClientWriter(dos); // call by UI

			ClientUI.jTextAreaInput.setEditable(true);
			ClientUI.jButtonSend.setEnabled(true);
			ClientUI.setConnected(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class ClientReader extends Thread {

	DataInputStream dis;

	ClientReader(DataInputStream dis) {
		this.dis = dis;
	}

	@Override
	public void run() {
		try {
			while (!ClientUI.endTheWorld) {
				String info = unCompress(dis.readUTF());

				if (info.substring(info.length() - 17, info.length() - 1).equals("ZW5kVGhlV29ybGQ=")) {
					ClientUI.setConnected(false);
					ClientUI.setMessage(info.substring(0, info.length() - 18) + " Offline now!\n");
					break;
				}
				ClientUI.setMessage(info);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static String unCompress(String compressedStr) {
		if (compressedStr == null) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = null;
		GZIPInputStream ginzip = null;
		byte[] compressed = null;
		String decompressed = null;
		try {
			compressed = Base64.getDecoder().decode(compressedStr);
			in = new ByteArrayInputStream(compressed);
			ginzip = new GZIPInputStream(in);

			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = ginzip.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ginzip != null) {
				try {
					ginzip.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			try {
				out.close();
			} catch (IOException e) {
			}
		}
		return decompressed;
	}
}

class ClientWriter {

	static DataOutputStream dos;

	ClientWriter(DataOutputStream dos) {
		ClientWriter.dos = dos;
	}

	static void inputMessage(String message) {
		try {
			dos.writeUTF(compress(message));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static String compress(String primStr) {
		if (primStr == null || primStr.length() == 0) {
			return primStr;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(primStr.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (gzip != null) {
				try {
					gzip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Base64.getEncoder().encodeToString(out.toByteArray());
	}
}