package com.alandy.bluetoothchat.connect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ConnectedThread extends Thread {
	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;
	private Handler mHandler;

	public ConnectedThread(BluetoothSocket socket, Handler handler) {
		mmSocket = socket;
		mHandler = handler;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;
		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mmInStream = tmpIn;
		mmOutStream = tmpOut;
	}

	public void run() {
		// buffer store for the stream
		byte[] buffer = new byte[1024];
		// bytes returned from read()
		int bytes;
		// Keep listening to the InputStream until an exception occurs
		while (true) {
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);
				// Send the obtained bytes to the UI activity
				if (bytes > 0) {
					Message message = mHandler.obtainMessage(
							Constant.MSG_GOT_DATA, buffer);
					mHandler.sendMessage(message);
				}
				Log.d("GOTMSG", "message size" + bytes);
			} catch (IOException e) {
				mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR,
						e));
				break;
			}
		}
	}

	/* Call this from the main activity to send data to the remote device */
	public void write(byte[] bytes) {
		try {
			mmOutStream.write(bytes);
		} catch (IOException e) {
		}
	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
		}
	}
}
