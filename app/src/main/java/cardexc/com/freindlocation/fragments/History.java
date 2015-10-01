package cardexc.com.freindlocation.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.service.events.HistoryListUpdate;
import cardexc.com.freindlocation.service.events.ServiceEventsInterface;
import cardexc.com.freindlocation.sqlite.ContactProvider;
import cardexc.com.freindlocation.sqlite.HistoryProvider;
import cardexc.com.freindlocation.sqlite.LocationContract;
import de.greenrobot.event.EventBus;

public class History extends Fragment {

    ListView history_list;
    EventBus eventBus;
    private AdapterView.OnItemClickListener onItemClickListener;

    public static History newInstance() {

        History fragment = new History();
        return fragment;

    }

    private void updateHistory() {

        Cursor historyCursor = HistoryProvider.getInstance().getHistoryCursor();
        HistoryCursorAdapter historyCursorAdapter = new HistoryCursorAdapter(Constants.getApplicationContext(), historyCursor, 0);

        history_list.setAdapter(historyCursorAdapter);
        history_list.setOnItemClickListener(onItemClickListener);

    }

    private OnFragmentInteractionListener mListener;

    public History() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        history_list = (ListView) view.findViewById(R.id.history_list);

        createListeners();

        updateHistory();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
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

        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        EventBus.getDefault().unregister(this);
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Cursor cursor, Boolean isDevice, Boolean isHistory);
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

            ContactProvider.setImageToView(history_contact_image_from, null);

            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_CONTACTID));
            ContactProvider.setImageToView(history_contact_image_to, contactId);

            ///////////////////////
        }

        private void fillInImage(Cursor cursor, ImageView history_image_req_type) {
            String request_type = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.HistoryEntry.COLUMN_REQ_TYPE));
            switch (request_type) {
                case (LocationContract.HistoryEntry.REQ_TYPE_IN):  {
                    history_image_req_type.setImageResource(R.drawable.a_in);
                    break;
                }
                case (LocationContract.HistoryEntry.REQ_TYPE_OUT):  {
                    history_image_req_type.setImageResource(R.drawable.a_out);
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

    private void createListeners() {

        onItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                mListener.onFragmentInteraction(cursor, false, true);
            }
        };

    }

    public void onEvent(ServiceEventsInterface event) {

        if (event instanceof HistoryListUpdate) {
            updateHistory();
        }

    }
}

