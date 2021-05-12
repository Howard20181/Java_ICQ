package me.hwr.ICQ.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Java2_Ch4
 * 
 * @author HowardWu
 */
public class Server implements Runnable {

	private int Port;

	Server(int Port) {
		this.Port = Port;
	}

	@Override
	public void run() {
		System.out.println("Server: Port: " + Port);
		try {
			@SuppressWarnings("resource")
			ServerSocket ss = new ServerSocket(Port);
			System.out.println("Server: Wait for connect.");
			Socket st = ss.accept();
			OutputStream os = st.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			// call by UI
			@SuppressWarnings("unused")
			ServerWriter sw = new ServerWriter(dos);
			InputStream is = st.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			ServerReader sr = new ServerReader(dis);
			sr.start();
			System.out.println("Server: Success!");
			ServerUI.jButtonSend.setEnabled(true);
			ServerUI.jButtonStart.setEnabled(false);
			ServerUI.jTextAreaInput.setEditable(true);
			ServerUI.setConnected(true);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}

class ServerReader extends Thread {

	DataInputStream dis;
	DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
	DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);

	ServerReader(DataInputStream dis) {
		this.dis = dis;
	}

	@Override
	public void run() {
		try {
			while (!ServerUI.endTheWorld) {
				Date date = new Date();
				String time = dateFormat.format(date) + " " + timeFormat.format(date);
				String info = unCompress(dis.readUTF());

				if (ServerUI.endTheWorld || info.equals("ZW5kVGhlV29ybGQ=")) {
					System.out.println("Server Exit!");
					ServerUI.setConnected(false);
					ServerUI.setMessage(History.saveMessage(time + "\nThe other side is offline!\n"));
					break;
				}
				ServerUI.setMessage(History.saveMessage(time + "\nClient:\n" + info));
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

class History {

	static String message = "";

	static String saveMessage(String message) {
		History.message = History.message + message + "\n";
		return History.message;
	}
}

class ServerWriter {

	static DataOutputStream dos;
	static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
	static DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);

	ServerWriter(DataOutputStream dos) {
		ServerWriter.dos = dos;
	}

	static void exitServer() {
		System.out.println("Server Exit!");
		ServerWriter.inputMessage("ZW5kVGhlV29ybGQ=");
	}

	static void inputMessage(String message) {
		Date date = new Date();
		String time = dateFormat.format(date) + " " + timeFormat.format(date);
		try {
			dos.writeUTF(compress(History.saveMessage(time + "\nServer:\n" + message)));
			dos.flush();
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