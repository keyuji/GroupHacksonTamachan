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

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    /** BLE 機器検索のタイムアウト(ミリ秒) */
    private static final long SCAN_PERIOD = 10000;
    private static final String TAG = "MainActivity";

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

        Handler mHandler = new Handler(); //

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // スキャン開始
        mBluetoothAdapter.startLeScan(mLeScanCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // タイムアウト
                Log.d(TAG, "タイムアウト");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }, SCAN_PERIOD);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d(TAG, "receive!!!");
            getScanData(scanRecord);
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
}