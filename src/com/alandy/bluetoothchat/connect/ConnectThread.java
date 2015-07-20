package com.alandy.bluetoothchat.connect;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectThread extends Thread {

	private final BluetoothDevice mmDevice;
	private final BluetoothSocket mmSocket;
	private BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler;
	private ConnectedThread mConnectedThread;

	public ConnectThread(BluetoothDevice device, BluetoothAdapter adapter,
			Handler mUIHandler) {
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket tmp = null;
		mmDevice = device;
		mBluetoothAdapter = adapter;
		mHandler = mUIHandler;
		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server code
			tmp = device.createRfcommSocketToServiceRecord(UUID
					.fromString(Constant.CONNECTTION_UUID));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mmSocket = tmp;
	}
	
	public void run(){
		// Cancel discovery because it will slow down the connection
		mBluetoothAdapter.cancelDiscovery();
		try {
			// Connect the device through the socket. This will block
            // until it succeeds or throws an exception
			mmSocket.connect();
		} catch (IOException e) {
			mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR, e));
			// Unable to connect; close the socket and get out
			try {
				mmSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
		// Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
	}
	

	private void manageConnectedSocket(BluetoothSocket mmSocket2) {
		mHandler.sendEmptyMessage(Constant.MSG_CONNECTED_TO_SERVER);
        mConnectedThread = new ConnectedThread(mmSocket, mHandler);
        mConnectedThread.start();
	}

	/**
	 * Will cancel an in-progress connection, and close the socket
	 */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendData(byte[] data) {
        if( mConnectedThread!=null){
            mConnectedThread.write(data);
        }
    }
}
