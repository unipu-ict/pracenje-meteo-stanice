/*
 * Created by Mikaela on 01/06/2018.
 */

package projektv2.iot.example.com.mobapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.URISyntaxException;

import cloud.artik.model.Acknowledgement;
import cloud.artik.model.ActionOut;
import cloud.artik.model.MessageOut;
import cloud.artik.model.WebSocketError;
import cloud.artik.websocket.ArtikCloudWebSocketCallback;
import cloud.artik.websocket.FirehoseWebSocket;

public class ArtikCloudSession {
    private static final String TAG = ArtikCloudSession.class.getSimpleName();
    private final static String DEVICE_ID = "9f167ec1e53f4e3e91ad3ab6222d50e0";
    private final static String DEVICE_TOKEN = "d1add190c69e418288ba3f5849a38e41";
    private final static String DEVICE_NAME = "Android Things board - Monitoring System";

    private static ArtikCloudSession ourInstance = new ArtikCloudSession();
    private static Context ourContext;


    public String vrijednosti;
    public String vrijednostTlak;
    public String vrijednostTemp;

    public static float getPress() {
        return press;
    }

    public static void setPress(float press) {
        ArtikCloudSession.press = press;
    }

    private static float press;
    private static float temp;

    public final static String WEBSOCKET_LIVE_ONOPEN =
            "cloud.artik.example.iot.WEBSOCKET_LIVE_ONOPEN";
    public final static String WEBSOCKET_LIVE_ONMSG =
            "cloud.artik.example.iot.WEBSOCKET_LIVE_ONMSG";
    public final static String WEBSOCKET_LIVE_ONCLOSE =
            "cloud.artik.example.iot.WEBSOCKET_LIVE_ONCLOSE";
    public final static String WEBSOCKET_LIVE_ONERROR =
            "cloud.artik.example.iot.WEBSOCKET_LIVE_ONERROR";
    public final static String SDID = "sdid";
    public final static String DEVICE_DATA = "data";
    public final static String TIMESTEP = "ts";


    private FirehoseWebSocket mFirehoseWS = null; //  end point: /live

    public static ArtikCloudSession getInstance() {
        return ourInstance;
    }

    private ArtikCloudSession() {
        // Do nothing
    }

    public void setContext(Context context) {ourContext = context;}

    public String getDeviceID() {return DEVICE_ID;}
    public String getDeviceName() {return DEVICE_NAME;}

    public void connectFirehoseWS() {
        createFirehoseWebsocket();
        try {
            mFirehoseWS.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes a websocket /live connection
     */
    public void disconnectFirehoseWS() {
        if (mFirehoseWS != null) {
            try {
                mFirehoseWS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mFirehoseWS = null;
    }

    private void createFirehoseWebsocket() {
        try {
            mFirehoseWS = new FirehoseWebSocket(DEVICE_TOKEN, DEVICE_ID, null, null, null, new ArtikCloudWebSocketCallback() {
                @Override
                public void onOpen(int i, String s) {
                    Log.d(TAG, "FirehoseWebSocket: onOpen()");
                    final Intent intent = new Intent(WEBSOCKET_LIVE_ONOPEN);
                    LocalBroadcastManager.getInstance(ourContext).sendBroadcast(intent);
                }

                @Override
                public void onMessage(MessageOut messageOut) {
                    Log.d(TAG, "FirehoseWebSocket: onMessage(" + messageOut.toString() + ")");
                    final Intent intent = new Intent(WEBSOCKET_LIVE_ONMSG);
                    intent.putExtra(SDID, messageOut.getSdid());
                    intent.putExtra(DEVICE_DATA, messageOut.getData().toString());
                    intent.putExtra(TIMESTEP, messageOut.getTs().toString());
                    LocalBroadcastManager.getInstance(ourContext).sendBroadcast(intent);

                    vrijednosti = messageOut.getData().toString();
                    Log.d(TAG, "vrijednosti :" + vrijednosti);
                    String[] parts = vrijednosti.split(",");
                    vrijednostTlak = parts[0];
                    vrijednostTemp = parts[1];
                    Log.d(TAG, "Tlak: " + vrijednostTlak);
                    Log.d(TAG, "Temp: " + vrijednostTemp);
                    String parts2[] = vrijednostTlak.split("=");
                    setPress(Float.valueOf(parts2[1]));
                    Log.d(TAG, "Press" + getPress());

                    String parts3[] = vrijednostTemp.split("=");
                    parts3[1] = parts3[1].replace("}", "");
                    //temp= Float.valueOf(parts3[1]);
                    setTemp(Float.valueOf(parts3[1]));
                    Log.d(TAG, "Temp" + temp);
                }

                @Override
                public void onAction(ActionOut actionOut) {
                }

                @Override
                public void onAck(Acknowledgement acknowledgement) {
                    Log.d(TAG, "FirehoseWebSocket::onAck: ");
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    final Intent intent = new Intent(WEBSOCKET_LIVE_ONCLOSE);
                    intent.putExtra("error", "mFirehoseWS is closed. code: " + code + "; reason: " + reason);
                    LocalBroadcastManager.getInstance(ourContext).sendBroadcast(intent);
                }

                @Override
                public void onError(WebSocketError ex) {
                    final Intent intent = new Intent(WEBSOCKET_LIVE_ONERROR);
                    intent.putExtra("error", "mFirehoseWS error: " + ex.getMessage());
                    LocalBroadcastManager.getInstance(ourContext).sendBroadcast(intent);
                }

                @Override
                public void onPing(long timestamp) {
                    Log.d(TAG, "FirehoseWebSocket::onPing: " + timestamp);
                }
            });
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void setTemp(float temp) {
        ArtikCloudSession.temp = temp;
    }

    public static float getTemp() {
        return temp;
    }
}
