package jp.co.intec.demoapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    /** BLE 機器検索のタイムアウト(ミリ秒) */
    private static final long SCAN_PERIOD = 10000;
    private static final String TAG = "MainActivity";
    private static final String BEACONUUID = "48534442-4C45-4144-80C0-180000000000";
    private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
    private Map<String, Object> map = new HashMap<String, Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //レイアウトで指定したWebViewのIDを指定する。
        WebView myWebView = (WebView) findViewById(R.id.webView1);

        //リンクをタップしたときに標準ブラウザを起動させない
        myWebView.setWebViewClient(new WebViewClient());

        //最初にgoogleのページを表示する。
        myWebView.loadUrl("https://www-team1.hackathon.i-lop.net/");

        //jacascriptを許可する
        myWebView.getSettings().setJavaScriptEnabled(true);

        final Handler mHandler = new Handler(); //

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // スキャン開始
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        final Runnable r = new Runnable() {

            @Override
            public void run() {
                // タイムアウトは今回は未実装　postDelaydで実装できるはず
               // Log.d(TAG, "タイムアウト");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                if (dataList.get(0).get("UUID").equals(BEACONUUID)) { // 指定したUUIDが含まれていた場合
                    //サーバにdata送信
                    Cilent task = new Cilent();
                    task.execute(dataList.get(0));
                }
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.post(r);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d(TAG, "receive!!!");
            getScanData(scanRecord);
            map.put("name",device.getName());
            map.put("address",device.getAddress());
            dataList.add(map);
            Log.d(TAG, "device name:"+device.getName() );
            Log.d(TAG, "device address:"+device.getAddress() );
        }

    };

    private void getScanData( byte[] scanRecord ){
        if(scanRecord.length > 30)
        {
            if((scanRecord[5] == (byte)0x4c) && (scanRecord[6] == (byte)0x00) &&
                    (scanRecord[7] == (byte)0x02) && (scanRecord[8] == (byte)0x15))
            {
                String uuid = Integer.toHexString(scanRecord[9] & 0xff)
                        + Integer.toHexString(scanRecord[10] & 0xff)
                        + Integer.toHexString(scanRecord[11] & 0xff)
                        + Integer.toHexString(scanRecord[12] & 0xff)
                        + "-"
                        + Integer.toHexString(scanRecord[13] & 0xff)
                        + Integer.toHexString(scanRecord[14] & 0xff)
                        + "-"
                        + Integer.toHexString(scanRecord[15] & 0xff)
                        + Integer.toHexString(scanRecord[16] & 0xff)
                        + "-"
                        + Integer.toHexString(scanRecord[17] & 0xff)
                        + Integer.toHexString(scanRecord[18] & 0xff)
                        + "-"
                        + Integer.toHexString(scanRecord[19] & 0xff)
                        + Integer.toHexString(scanRecord[20] & 0xff)
                        + Integer.toHexString(scanRecord[21] & 0xff)
                        + Integer.toHexString(scanRecord[22] & 0xff)
                        + Integer.toHexString(scanRecord[23] & 0xff)
                        + Integer.toHexString(scanRecord[24] & 0xff);

                String major = Integer.toHexString(scanRecord[25] & 0xff) + Integer.toHexString(scanRecord[26] & 0xff);
                String minor = Integer.toHexString(scanRecord[27] & 0xff) + Integer.toHexString(scanRecord[28] & 0xff);

                Log.d(TAG, "UUID:"+uuid );
                Log.d(TAG, "major:"+major );
                Log.d(TAG, "minor:"+minor );
                if(uuid.equals(BEACONUUID)){
                    map.put("UUID",uuid);
                }else{
                    map.put("UUID","NoMatch");
                    dataList.add(map);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Google Play Servicesのインストールチェック
        // GCMの場合、GoogleApiClientで妥当なチェックできないため非推奨メソッドを利用
        int gpsResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (gpsResult != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(gpsResult)) {
                Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(gpsResult, this, 0);
                if (errorDialog != null) {
                    ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(errorDialog);
                    errorDialogFragment.show(getFragmentManager(), "");
                }
            } else {
                Toast.makeText(this, "Google Play Services が利用不可です", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void sendData(){

    }
}