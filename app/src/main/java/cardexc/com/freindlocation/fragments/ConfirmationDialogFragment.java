package cardexc.com.freindlocation.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import cardexc.com.freindlocation.R;

public class ConfirmationDialogFragment extends DialogFragment implements View.OnClickListener {

    private Button btn_cancel;
    private Button btn_ok;
    private TextView content;
    private TextView title;
    private OnConfirmationDialogFragmentClick mActivity;

    @Override

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (OnConfirmationDialogFragmentClick) activity;

    }

    public interface OnConfirmationDialogFragmentClick {
        void OnConfirmationDialogFragmentClick_OnClickYes();
        void OnConfirmationDialogFragmentClick_OnClickCancel();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Bundle arguments = getArguments();

        View view = inflater.inflate(R.layout.fragment_dialog_delete_confirmation, null);

        btn_cancel = (Button) view.findViewById(R.id.fragment_confirmationdialog_button_cancel);
        btn_ok = (Button) view.findViewById(R.id.fragment_confirmationdialog_button_ok);
        content = (TextView) view.findViewById(R.id.fragment_confirmationdialog_content);
        title = (TextView) view.findViewById(R.id.fragment_confirmationdialog_title);

        content.setText(arguments.getString("content"));
        title.setText(arguments.getString("title"));

        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);

        return view;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case (R.id.fragment_confirmationdialog_button_cancel):{
                dismiss();
                break;
            }
            case (R.id.fragment_confirmationdialog_button_ok):{

                mActivity.OnConfirmationDialogFragmentClick_OnClickYes();
                dismiss();
                break;

            }


        }

    }


}
