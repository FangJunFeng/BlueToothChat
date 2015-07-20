package com.alandy.bluetoothchat;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alandy.bluetoothchat.connect.Constant;
import com.alandy.bluetoothchat.controller.BlueToothController;
import com.alandy.bluetoothchat.controller.ChatController;

public class MainActivity extends Activity {

	private static final int REQUEST_CODE = 0;
	private EditText mInputBox;
	private TextView mChatContent;
	private ListView mListView;
	private Button mSendBt;
	private View mChatPanel;
	private Toast mToast;
	private DeviceAdapter mAdapter;
	private List<BluetoothDevice> mDeviceList = new ArrayList<>();
	private ArrayList<BluetoothDevice> mBondedDeviceList = new ArrayList<>();
	private BlueToothController mController = new BlueToothController();
	private Handler mUIHandler = new MyHandler();
	private StringBuilder mChatText = new StringBuilder();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();
		registerBlueToothReceiver();
		// turn on BlueTooth
		mController.turnOnBlueTooth(this, REQUEST_CODE);
	}

	private void registerBlueToothReceiver() {
		IntentFilter filter = new IntentFilter();
		// start searching bluetooth device
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		// end of seach bluetooth device
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		// Looking for bluetooth device
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		// Device scanning mode change
		filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		// Binding state
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		registerReceiver(mReceiver, filter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				showToast("开始查找蓝牙设备");
				// Initialize the data list
				mDeviceList.clear();
				mAdapter.notifyDataSetChanged();
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				showToast("结束查找蓝牙设备");
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Find a bluetooth device and add
				mDeviceList.add(device);
				mAdapter.notifyDataSetChanged();
			} else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
				int scanMode = intent.getIntExtra(
						BluetoothAdapter.EXTRA_SCAN_MODE, 0);
				if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
					showToast("设备扫描模式改变中...");
				}
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice remoteDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (null == remoteDevice) {
					showToast("no device");
					return;
				}
				int status = intent.getIntExtra(
						BluetoothDevice.EXTRA_BOND_STATE, 0);
				if (status == BluetoothDevice.BOND_BONDED) {
					showToast("Bonded" + remoteDevice.getName());
				} else if (status == BluetoothDevice.BOND_BONDING) {
					showToast("Bonding" + remoteDevice.getName());
				} else if (status == BluetoothDevice.BOND_NONE) {
					showToast("Not bond" + remoteDevice.getName());
				}
			}
		}
	};

	private void initUI() {
		mInputBox = (EditText) findViewById(R.id.et_chat_edit);
		mChatContent = (TextView) findViewById(R.id.tv_chat_content);
		mChatPanel = findViewById(R.id.rl_chat_panel);

		mListView = (ListView) findViewById(R.id.lv_device_list);
		mAdapter = new DeviceAdapter(mDeviceList, this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(bindDeviceClick);

		mSendBt = (Button) findViewById(R.id.bt_sent);
		// 处理消息发送
		mSendBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String ext = mInputBox.getText().toString();
			}
		});
	}

	/**
	 * Bind BlueTooth Devices
	 */
	private OnItemClickListener bindDeviceClick = new OnItemClickListener() {
		@TargetApi(Build.VERSION_CODES.KITKAT)
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			BluetoothDevice device = mDeviceList.get(position);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				device.createBond();
			}
		}
	};

	/**
	 * Already Bind BlueTooth Devices
	 */
	private OnItemClickListener bindedDeviceClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			BluetoothDevice device = mBondedDeviceList.get(position);
			ChatController.getInstance().startChatWidth(device,
					mController.getAdapter(), mUIHandler);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		// Open BlueTooth Enable Visibly
		if (id == R.id.enable_visiblity) {
			mController.enableVisibly(this);
			// Find BlueToothDevice
		} else if (id == R.id.find_device) {
			mController.findDevice();
			mAdapter.refresh(mDeviceList);
			mListView.setOnItemClickListener(bindDeviceClick);
			// Already Bind BlueTooth Devices
		} else if (id == R.id.bonded_device) {
			mBondedDeviceList = mController.getBondedDeviceList();
			mAdapter.refresh(mBondedDeviceList);
			mListView.setOnItemClickListener(bindedDeviceClick);
		} else if (id == R.id.listening) {
			ChatController.getInstance().waitingForFriends(
					mController.getAdapter(), mUIHandler);
		} else if (id == R.id.stop_listening) {
			ChatController.getInstance().stopChat();
			exitChatMode();
		} else if (id == R.id.disconnect) {
			exitChatMode();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * If not allowed to open the bluetooth will close the application
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			if (requestCode != RESULT_OK) {
				if (resultCode == RESULT_OK) {
	                showToast("蓝牙已经打开");
	            }else{
	                showToast("蓝牙打开出错");
	                finish();
	            }
			}
		}
	}

	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.MSG_START_LISTENING:
				showToast("开始监听");
				break;
			case Constant.MSG_FINISH_LISTENING:
				showToast("结束监听");
				exitChatMode();
				break;
			case Constant.MSG_GOT_DATA:
				byte[] data = (byte[]) msg.obj;
				mChatText.append(ChatController.getInstance().decodeMessage(data)).append("\n");
				mChatContent.setText(mChatText.toString());
				break;
			case Constant.MSG_ERROR:
                exitChatMode();
                showToast("error: " + String.valueOf(msg.obj));
                break;
            case Constant.MSG_CONNECTED_TO_SERVER:
                enterChatMode();
                showToast("Connected to Server");
                break;
            case Constant.MSG_GOT_A_CLINET:
                enterChatMode();
                showToast("Got a Client");
                break;
			default:
				showToast("error!!!");
				break;
			}
		}
	}

	public void enterChatMode() {
        mListView.setVisibility(View.GONE);
        mChatPanel.setVisibility(View.VISIBLE);
    }
	
	public void exitChatMode() {
		mListView.setVisibility(View.VISIBLE);
		mChatPanel.setVisibility(View.GONE);
	}

	private void showToast(String text) {

		if (mToast == null) {
			mToast = Toast.makeText(this, text, 0);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("退出 BlueToothChat?")
				.setMessage("您想退出BlueToothChat吗？")
				.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).setNegativeButton("NO", null).show();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ChatController.getInstance().stopChat();
		unregisterReceiver(mReceiver);
	}
}
