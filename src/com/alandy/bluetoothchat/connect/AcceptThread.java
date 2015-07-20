package com.alandy.bluetoothchat.connect;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class AcceptThread extends Thread {
	private static final String NAME = "BlueToothClass";
	private BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler;
	private BluetoothServerSocket mmServerSocket;
	private ConnectedThread mConnectedThread;

	public AcceptThread(BluetoothAdapter adapter, Handler mUIHandler) {
		// Use a temporary object that is later assigned to mmServerSocket,
		// because mmServerSocket is final
		mBluetoothAdapter = 
				adapter;
		mHandler = mUIHandler;
		BluetoothServerSocket tmp = null;
		try {
			// MY_UUID is the app's UUID string, also used by the client code
			tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME,
					UUID.fromString(Constant.CONNECTTION_UUID));
		} catch (IOException e) {
			e.printStackTrace();
		}
		mmServerSocket = tmp;
	}

	@Override
	public void run() {
		BluetoothSocket socket = null;
		// Keep listening until exception occurs or a socket is returned
		while (true) {
			try {
				mHandler.sendEmptyMessage(Constant.MSG_START_LISTENING);
				socket = mmServerSocket.accept();
			} catch (IOException e) {
				mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR,e));
				break;
			}
			// If a connection was accepted
			if (socket != null) {
				// Do work to manage the connection (in a separate thread)
				manageConnectedSocket(socket);
				try {
					mmServerSocket.close();
					mHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	private void manageConnectedSocket(BluetoothSocket socket) {
		//Only support one handle  connection at the same time
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
		}
		mHandler.sendEmptyMessage(Constant.MSG_GOT_A_CLINET);
		mConnectedThread = new ConnectedThread(socket, mHandler);
		mConnectedThread.start();
	}

	/**
	 * Will cancel the listening socket, and cause the thread to finish
	 */
	public void cancel() {
		try {
			mHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
			mmServerSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendData(byte[] data) {
        if( mConnectedThread!=null){
            mConnectedThread.write(data);
        }
    }
}
