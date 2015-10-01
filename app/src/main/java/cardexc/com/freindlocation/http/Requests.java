package cardexc.com.freindlocation.http;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cardexc.com.freindlocation.data.AppController;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.service.events.MessageContactListReceived;
import cardexc.com.freindlocation.sqlite.ContactProvider;
import cardexc.com.freindlocation.sqlite.HistoryProvider;
import de.greenrobot.event.EventBus;

public class Requests {

    private static Long mysql_id_time_checked = null;

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
                    case "addContact": {
                        serverAnswerAnalyze_addContact(response);
                        break;
                    }
                    case "deleteContact": {
                        serverAnswerAnalyze_deleteContact(response);
                        break;
                    }
                    case "getContactsList": {
                        serverAnswerAnalyze_getContactList(response);
                        break;
                    }
                    case "getLocation": {
                        serverAnswerAnalyze_getLocation(response);
                        break;
                    }
                    case "getHistoryRequests": {
                        serverAnswerAnalyze_getHistoryRequests(response);
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void getMySqlIdFromServer() {

        if (mysql_id_time_checked == null) {
            mysql_id_time_checked = System.currentTimeMillis();
        }else{

            long current_time = System.currentTimeMillis();
            if (current_time - mysql_id_time_checked < 5000)
                return;
        }

        if (Constants.getInstance().getPhonenum() == null || Constants.getInstance().getIMEI() == null) {
            Log.i(Constants.TAG, "getMySqlIdFromServer IMEI & PHONENUM == NULL");
            return;
        }

        String url = String.format(Constants.SERVHTTP_USERPHONEEXISTS, Constants.getInstance().getPhonenum(), Constants.getInstance().getIMEI());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

    private static void setUserPhoneToServer() {

        if (Constants.getInstance().getPhonenum() == null || Constants.getInstance().getIMEI() == null) {
            return;
        }

        String url = String.format(Constants.SERVHTTP_USERPHONEADD, Constants.getInstance().getPhonenum(), Constants.getInstance().getIMEI());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);
    }

    public static void putContactToServer(String contactPhone) {

        if (Constants.getInstance().getMYSQLID() == null) {
            return;
        }

        String url = String.format(Constants.SERVHTTP_ADDCONTACT, Constants.getInstance().getMYSQLID(), contactPhone);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

    public static void deleteContactFromServer(String contactPhone) {

        if (Constants.getInstance().getMYSQLID() == null) {
            return;
        }

        String url = String.format(Constants.SERVHTTP_DELETECONTACT, Constants.getInstance().getMYSQLID(), contactPhone, Constants.getInstance().getIMEI());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

    public static void setLocationToServer(final Location location, final Context context) {

        String url = String.format(Constants.SERVHTTP_SETLOCATION, Constants.getInstance().getMYSQLID(),
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

    public static void getContactListFromServer() {

        if (Constants.getInstance().getMYSQLID() == null) {
            Log.i(Constants.TAG, "getMySqlIdFromServer MYSQL_ID == NULL. Sending request...");
            getMySqlIdFromServer();
            return;
        }

        String url = String.format(Constants.SERVHTTP_GETCONTACTLIST, Constants.getInstance().getMYSQLID());

        //Log.i(Constants.TAG, "getContactListFromServer URL=" + url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

    public static void getContactLocation(String target_phone, String UUID){

        if (Constants.getInstance().getMYSQLID() == null) {
            Log.i(Constants.TAG, "getMySqlIdFromServer MYSQL_ID == NULL");
            return;
        }

        String url = String.format(Constants.SERVHTTP_GETCONTACTLOCATION,
                Constants.getInstance().getMYSQLID(),
                Constants.getInstance().getIMEI(),
                target_phone,
                UUID,
                String.valueOf(System.currentTimeMillis()));

        Log.i(Constants.TAG, "getContactLocation URL=" + url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

    public static void getHistoryRequests(){

        if (Constants.getInstance().getMYSQLID() == null) {
            Log.i(Constants.TAG, "getMySqlIdFromServer MYSQL_ID == NULL");
            return;
        }

        String url = String.format(Constants.SERVHTTP_GETHISTORY,
                Constants.getInstance().getMYSQLID(),
                Constants.getInstance().getIMEI());

        Log.i(Constants.TAG, "getHistoryRequests URL=" + url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

    public static void historyRequestsReceived_updateByUUID(String uuid) {

        if (Constants.getInstance().getMYSQLID() == null) {
            Log.i(Constants.TAG, "getMySqlIdFromServer MYSQL_ID == NULL");
            return;
        }

        String url = String.format(Constants.SERVHTTP_SETHISTORY_UPDATED, uuid);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static void serverAnswerAnalyze_getContactList(JSONObject response) {

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

                Map<String, Boolean> users = new HashMap<String, Boolean>();

                try {

                    int count = response.getInt("count");

                    for (int i = 0; i < count; i++) {

                        JSONObject record = response.getJSONObject(String.valueOf(i));

                        String phonenum  = record.getString("phonenum");
                        Boolean approved = !"null".equals(record.getString("id"));

                        users.put(phonenum,approved);

                    }

                    EventBus.getDefault().post(new MessageContactListReceived(users));

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
                setUserPhoneToServer();
                break;
            }
            case "error": {
                Log.i(Constants.TAG, "MYSQL / PHP Error");
                break;
            }
            default:
                Constants.getInstance().setMYSQLID(message);
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
                getMySqlIdFromServer();
                break;
            }
            case "error": {
                Log.i(Constants.TAG, "MYSQL / PHP Error");
                break;
            }
        }
    }

    private static void serverAnswerAnalyze_addContact(JSONObject response) {

        String message = null;
        try {
            message = (String) response.get("message");
        } catch (JSONException e) {
            Log.i(Constants.TAG, "Error while parsing  serverAnswerAnalyze_addContact" + e.getMessage());
        }

        if (message == null)
            return;

        switch (message) {
            case "success": {

                String contactPhone = null;
                try {
                    contactPhone = (String) response.get("contactPhone");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (contactPhone != null)
                    ContactProvider.getInstance().ContactsToUpdateOnServer_delete(contactPhone);

                break;
            }
            case "error": {
                Log.i(Constants.TAG, "MYSQL / PHP Error");
                break;
            }
        }

    }

    private static void serverAnswerAnalyze_deleteContact(JSONObject response) {

        String message = null;
        try {
            message = (String) response.get("message");
        } catch (JSONException e) {
            Log.i(Constants.TAG, "Error while parsing  serverAnswerAnalyze_deleteContact" + e.getMessage());
        }

        if (message == null)
            return;

        switch (message) {
            case "success": {

                String contactPhone = null;
                try {
                    contactPhone = (String) response.get("contactPhone");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (contactPhone != null)
                    ContactProvider.getInstance().ContactsToDeleteOnServer_delete(contactPhone);
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
                HistoryProvider.getInstance().updateLocationByRequestedUUID(response);
                break;
            }
            case "error": {
                Log.i(Constants.TAG, "MYSQL / PHP Error");
                break;
            }
        }
    }

    private static void serverAnswerAnalyze_getHistoryRequests(JSONObject response) {

        String message = null;
        try {
            message = (String) response.get("message");
        } catch (JSONException e) {
            Log.i(Constants.TAG, "Error while parsing serverAnswerAnalyze_getHistoryRequests" + e.getMessage());
        }

        if (message == null)
            return;

        switch (message) {
            case "success": {
                HistoryProvider.getInstance().historyRequests_Analyze(response);
                break;
            }
            case "error": {
                Log.i(Constants.TAG, "MYSQL / PHP Error");
                break;
            }
        }
    }

}


