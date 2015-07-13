package cardexc.com.freindlocation.http;

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

public class Requests {


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

        if (Constants.getPhonenum() == null || Constants.getIMEI() == null) {
            Log.i(Constants.TAG, "getMySqlIdFromServer IMEI & PHONENUM == NULL");
            return;
        }

        String url = String.format(Constants.SERVHTTP_USERPHONEEXISTS, Constants.getPhonenum(), Constants.getIMEI());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {

        };


        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

    private static void setUserPhoneToServer() {

        if (Constants.getPhonenum() == null || Constants.getIMEI() == null) {
            return;
        }

        String url = Constants.SERVHTTP_USERPHONEADD;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param1", Constants.getPhonenum());
                params.put("param2", Constants.getIMEI());

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);
    }

    public static void setLocationToServer(final Location location) {

        String url = String.format(Constants.SERVHTTP_SETLOCATION, Constants.getMYSQLID(),
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                responseListener, errorListener) {
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, Constants.TAG);

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                Constants.setMYSQLID(message);
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
}


