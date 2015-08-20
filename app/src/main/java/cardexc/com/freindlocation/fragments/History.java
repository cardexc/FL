package cardexc.com.freindlocation.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.sqlite.ContactProvider;
import cardexc.com.freindlocation.sqlite.HistoryProvider;
import cardexc.com.freindlocation.sqlite.LocationContract;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link History.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link History#newInstance} factory method to
 * create an instance of this fragment.
 */
public class History extends Fragment {

    ListView history_list;

    public static History newInstance() {

        History fragment = new History();
        return fragment;

    }

    private void updateHistory() {

        Cursor historyCursor = HistoryProvider.getInstance().getHistoryCursor(getActivity());
        HistoryCursorAdapter historyCursorAdapter = new HistoryCursorAdapter(getActivity(), historyCursor, 0);

        history_list.setAdapter(historyCursorAdapter);

    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment History.
     */
    // TODO: Rename and change types and number of parameters
    public static History newInstance(String param1, String param2) {
        History fragment = new History();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public History() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        history_list = (ListView) view.findViewById(R.id.history_list);
        updateHistory();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    public class HistoryCursorAdapter extends android.support.v4.widget.CursorAdapter {
        public HistoryCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.historytab_row, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            ImageView history_contact_image_from = (ImageView) view.findViewById(R.id.history_contact_image_from);
            ImageView history_contact_image_to = (ImageView) view.findViewById(R.id.history_contact_image_to);
            ImageView history_image_req_type = (ImageView) view.findViewById(R.id.history_image_req_type);

            TextView history_label_from = (TextView) view.findViewById(R.id.history_label_from);
            TextView history_label_to   = (TextView) view.findViewById(R.id.history_label_to);
            TextView history_label_req_time_1   = (TextView) view.findViewById(R.id.history_label_req_time_1);
            TextView history_label_req_time_2   = (TextView) view.findViewById(R.id.history_label_req_time_2);

            ///////////////////////

            history_label_from.setText(getResources().getString(R.string.label_from));
            history_label_to.setText(cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_NAME)));

            ///////////////////////

            fillInImage(cursor, history_image_req_type);

            ///////////////////////
            String timeStr = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.HistoryEntry.COLUMN_REQUEST_TIME));
            if (!"".equals(timeStr) && !"null".equals(timeStr))
                fillInRequestedTime(history_label_req_time_1, history_label_req_time_2, timeStr);

            ///////////////////////

            ContactProvider.setImageToView(context, history_contact_image_from, null);

            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_CONTACTID));
            ContactProvider.setImageToView(context, history_contact_image_to, contactId);

            ///////////////////////
        }

        private void fillInImage(Cursor cursor, ImageView history_image_req_type) {
            String request_type = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.HistoryEntry.COLUMN_REQ_TYPE));
            switch (request_type) {
                case (LocationContract.HistoryEntry.REQ_TYPE_IN):  {
                    history_image_req_type.setImageResource(R.drawable.arrow_in);
                    break;
                }
                case (LocationContract.HistoryEntry.REQ_TYPE_OUT):  {
                    history_image_req_type.setImageResource(R.drawable.arrow_out);
                    break;
                }
            }
        }

        private void fillInRequestedTime(TextView history_label_req_time_1, TextView history_label_req_time_2, String timeStr) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(timeStr));

            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            day = day.length() == 1 ? "0" + day : day;

            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            month = month.length() == 1 ? "0" + month : month;

            String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            hour = hour.length() == 1 ? "0" + hour : hour;

            String minute = String.valueOf(calendar.get(Calendar.MINUTE));
            minute = minute.length() == 1 ? "0" + minute : minute;

            history_label_req_time_1.setText(day + "/" + month);
            history_label_req_time_2.setText(hour + ":" + minute);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////


}

