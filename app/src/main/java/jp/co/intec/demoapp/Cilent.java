package jp.co.intec.demoapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by team1 on 2017/10/21.
 */

public class Cilent extends AsyncTask<Map<String, Object>, Void, String> {

    @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // doInBackground前処理
        }

        @Override
        protected String doInBackground(Map<String, Object>... map) {
            HttpURLConnection con = null;
            URL url = null;
            String urlSt = "https://tomcat-team1.hackathon.i-lop.net/demo/sv/PushPublish";
            FcmTestFirebaseInstanceIdService token = new FcmTestFirebaseInstanceIdService();


            try {
                // URLの作成
                url = new URL(urlSt);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setInstanceFollowRedirects(false);
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                // 接続
                con.connect();

                // POSTデータ送信処理
                OutputStream out = null;
                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("UUID", map[0].get("UUID"));
                    jsonParam.put("name", map[0].get("name"));
                    jsonParam.put("address", map[0].get("address"));
                    jsonParam.put("token", new FcmTestFirebaseInstanceIdService().token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    out = con.getOutputStream();
                    out.write(jsonParam.toString().getBytes("UTF-8"));
                    out.flush();
                } catch (IOException e) {
                    // POST送信エラー
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }

                InputStream in = con.getInputStream();
                byte bodyByte[] = new byte[1024];
                in.read(bodyByte);

                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                System.out.println(sb.toString());
                in.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // doInBackground後処理
        }

    }


