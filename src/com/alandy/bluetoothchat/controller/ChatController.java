package com.alandy.bluetoothchat.controller;

import java.io.UnsupportedEncodingException;

import com.alandy.bluetoothchat.connect.AcceptThread;
import com.alandy.bluetoothchat.connect.ConnectThread;
import com.alandy.bluetoothchat.connect.ProtocolHandler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.widget.TextView;

/**
 * @author Fangjun
 *
 */
public class ChatController {
	private ConnectThread mConnectThread;
	private AcceptThread mAcceptThread;
	private ChatProtocol mProtocol = new ChatProtocol();
	
	private class ChatProtocol implements ProtocolHandler<String>{

		private static final String CHARSET_NAME = "utf-8";

		@Override
		public byte[] encodePackage(String data) {
			if (data == null) {
				return new byte[0];
			}else {
				try {
					return data.getBytes(CHARSET_NAME);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return new byte[0];
				}
			}
		}

		@Override
		public String decodePackage(byte[] netData) {
			if (netData == null) {
				return "";
			}else {
				try {
	                return new String(netData, CHARSET_NAME);
	            } catch (UnsupportedEncodingException e) {
	                e.printStackTrace();
	                return "";
	            }
			}
		}
		
		
	}

	// The singleton of ChatController
	private static class ChatControlHolder {
		private static ChatController mInstance = new ChatController();
	}

	public static ChatController getInstance() {
		return ChatControlHolder.mInstance;
	}

	/**
     * Send message
     * @param msg
     */
    public void sendMessage(String msg) {
        byte[] data = mProtocol.encodePackage(msg);
        if(mConnectThread != null) {
            mConnectThread.sendData(data);
        }
        else if( mAcceptThread != null) {
            mAcceptThread.sendData(data);
        }
    }


    /**
     * Network data decoding
     * @param data
     * @return
     */
    public String decodeMessage(byte[] data) {
        return  mProtocol.decodePackage(data);
    }
    
	// Stop chatting
	public void stopChat() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
		}else if (mAcceptThread != null) {
			mAcceptThread.cancel();
		}
	}

	/**
	 * Connect with service and chatting
	 * @param device
	 * @param adapter
	 * @param mUIHandler
	 */
	public void startChatWidth(BluetoothDevice device,
			BluetoothAdapter adapter, Handler mUIHandler) {
		mConnectThread = new ConnectThread(device, adapter, mUIHandler);
		mConnectThread.start();
	}

	/**
	 * Waiting for the client to connect
	 * @param adapter
	 * @param mUIHandler
	 */
	public void waitingForFriends(BluetoothAdapter adapter, Handler mUIHandler) {
		mAcceptThread = new AcceptThread(adapter, mUIHandler);
		mAcceptThread.start();
	}

	
}
