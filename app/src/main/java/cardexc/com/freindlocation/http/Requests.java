package cardexc.com.freindlocation.http;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cardexc.com.freindlocation.data.AppController;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.data.Contact;
import cardexc.com.freindlocation.service.events.MessageContactListReceived;
import cardexc.com.freindlocation.sqlite.HistoryProvider;
import de.greenrobot.event.EventBus;

public class Requests {

    private static Context lastUsedContext;

    private static Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.i(Constants.TAG, "VOLLEY onResponse: " + response.toString());

            try {

                String request = (String) response.get("request");
                switch (request) {
                    case "getUserId": {
                        serverAnswerAnalyze_getUserId(response);
                        break;
                    }
                    case "addUser": {
                        serverAnswerAnalyze_addUser(response);
                        break;
                    }
                    case "getContacstList": {
                        serverAnswerAnalyze_getContacstList(response);
                        break;
                    }
                    case "getLocation": {
                        serverAnswerAnalyze_getLocation(response);
                        break;
                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private static Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            VolleyLog.d(Constants.TAG, "Error: " + error.getMessage());
            Log.i(Constants.TAG, "Error! onErrorResponse /" + error.getMessage());
        }
    };

    private static synchronized void setLastUsedContext(Context context) {
        lastUsedContext = context;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void getMySqlIdFromServer(Context context) {

        setLastUsedContext(context);

        if (Constants.getInstance(context).getPhonenum() == null || Constants.getInstance(context).getIMEI() == null) {
            Log.i(Constants.TAG, "getMySqlIdFromServer IMEI & PHONENUM == NULL");
            return;
        }

        String url = String.format(Constants.SERVHTTP_USERPHONEEXISTS, Constants.getInstance(context).getPhonenum(), Constants.getInstance(context).getIMEI());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

    private static void setUserPhoneToServer(final Context context) {

        if (Constants.getInstance(context).getPhonenum() == null || Constants.getInstance(context).getIMEI() == null) {
            return;
        }

        String url = String.format(Constants.SERVHTTP_USERPHONEADD, Constants.getInstance(context).getPhonenum(), Constants.getInstance(context).getIMEI());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);
    }

    public static void setLocationToServer(final Location location, final Context context) {

        setLastUsedContext(context);

        String url = String.format(Constants.SERVHTTP_SETLOCATION, Constants.getInstance(context).getMYSQLID(),
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

    public static void getContactListFromServer(Context context) {

        if (Constants.getInstance(context).getPhonenum() == null || Constants.getInstance(context).getIMEI() == null) {
            Log.i(Constants.TAG, "getMySqlIdFromServer IMEI & PHONENUM == NULL");
            return;
        }

        String url = String.format(Constants.SERVHTTP_GETCONTACTLIST, Constants.getInstance(context).getMYSQLID());

        Log.i(Constants.TAG, "getContactListFromServer URL=" + url);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

    public static void getContactLocation(Context context, String phone, String IMEI, String UUID){

        setLastUsedContext(context);

        String url = String.format(Constants.SERVHTTP_GETCONTACTLOCATION,
                    Constants.getInstance(context).getMYSQLID(),
                    phone,
                    IMEI,
                    UUID);

        Log.i(Constants.TAG, "getContactLocation URL=" + url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static void serverAnswerAnalyze_getContacstList(JSONObject response) {

        String message = null;
        try {
            message = (String) response.get("message");
        } catch (JSONException e) {
            Log.i(Constants.TAG, "Error while parsing  serverAnswerAnalyze_getUserId" + e.getMessage());
        }

        switch (message) {
            case "error": {
                Log.i(Constants.TAG, "Request has returned the error: getContacstList  ");
            }
            case "empty": {

            }
            case "success": {

                ArrayList<Contact> contacts = new ArrayList<>();
                try {

                    int count = response.getInt("count");

                    for (int i = 0; i < count; i++) {

                        JSONObject record = response.getJSONObject(String.valueOf(i));

                        String id = record.getString("id");
                        String phonenum = record.getString("phonenum");
                        String IMEI = record.getString("IMEI");
                        Boolean approved = "1".equals(record.getString("approved")) ? true : false;

                        Contact contact = new Contact(id, phonenum, IMEI, approved);
                        contacts.add(contact);

                    }

                    EventBus.getDefault().post(new MessageContactListReceived(contacts));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }

    }

    private static void serverAnswerAnalyze_getUserId(JSONObject response) {

        String message = null;
        try {
            message = (String) response.get("message");
        } catch (JSONException e) {
            Log.i(Constants.TAG, "Error while parsing  serverAnswerAnalyze_getUserId" + e.getMessage());
        }

        if (message == null)
            return;

        switch (message) {
            case "empty": {
                setUserPhoneToServer(lastUsedContext);
                break;
            }
            case "error": {
                Log.i(Constants.TAG, "MYSQL / PHP Error");
                break;
            }
            default:
                Constants.getInstance(lastUsedContext).setMYSQLID(message);
        }

    }

    private static void serverAnswerAnalyze_addUser(JSONObject response) {

        String message = null;
        try {
            message = (String) response.get("message");
        } catch (JSONException e) {
            Log.i(Constants.TAG, "Error while parsing  serverAnswerAnalyze_addUser" + e.getMessage());
        }

        if (message == null)
            return;

        switch (message) {
            case "success": {
                getMySqlIdFromServer(lastUsedContext);
                break;
            }
            case "error": {
                Log.i(Constants.TAG, "MYSQL / PHP Error");
                break;
            }
        }
    }

    private static void serverAnswerAnalyze_getLocation(JSONObject response) {

        String message = null;
        try {
            message = (String) response.get("message");
        } catch (JSONException e) {
            Log.i(Constants.TAG, "Error while parsing serverAnswerAnalyze_getLocation" + e.getMessage());
        }

        if (message == null)
            return;

        switch (message) {
            case "success": {
                HistoryProvider.getInstance().updateLocationByRequestUUID(lastUsedContext, response);
                break;
            }
            case "error": {
                Log.i(Constants.TAG, "MYSQL / PHP Error");
                break;
            }
        }
    }

}


