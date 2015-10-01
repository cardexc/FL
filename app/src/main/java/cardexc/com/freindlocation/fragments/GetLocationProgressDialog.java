package cardexc.com.freindlocation.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import cardexc.com.freindlocation.R;

public class GetLocationProgressDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Bundle arguments = getArguments();

        View view = inflater.inflate(R.layout.fragment_getlocation_progress_dialog, null);

        TextView title = (TextView) view.findViewById(R.id.fragment_progressdialog_title);
        title.setText(arguments.getString("title"));

        return view;

    }




}
