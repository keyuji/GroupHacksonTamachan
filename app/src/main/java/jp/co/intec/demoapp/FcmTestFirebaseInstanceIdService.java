package jp.co.intec.demoapp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * プッシュ通知に使用する登録トークンの生成、更新をハンドルするサービスです。
 * <p/>
 * Created by Shirai on 2016/08/05.
 */
public class FcmTestFirebaseInstanceIdService extends FirebaseInstanceIdService {

    /**
     * ログ出力用
     */
    private static final String TAG = "FCM";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        // InstanceIDトークンを取得
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Refreshed token: " + refreshedToken);
        System.out.println("***********Refreshed token*************: " + refreshedToken);
    }

}