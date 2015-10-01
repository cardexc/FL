package cardexc.com.freindlocation.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;

import java.util.ArrayList;
import java.util.List;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.data.Constants;

public class FakeLocationActivityFragment extends Fragment

        implements IabHelper.OnIabSetupFinishedListener {

    IabHelper mHelper;

    private Boolean mIsPremium = false;

    private LinearLayout fakelocation_layout_notactivated;
    private LinearLayout fakelocation_layout_activated;
    private LinearLayout fakelocation_layout_NoInternetConnection;
    private Button fake_location_button_activate;
    private Switch fake_Location_switch;
    private ProgressBar fakelocation_progressBar;
    private Button fakelocation_setLocationButton;

    private Boolean isNetworkConnected;
    private Boolean isFakeLocationEnabled = false;


    public FakeLocationActivityFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);

        if (Constants.isNetworkConnected()) {

            isNetworkConnected = true;

            String base64EncodedPublicKey = getResources().getString(R.string.application_key);
            mHelper = new IabHelper(Constants.getApplicationContext(), base64EncodedPublicKey);

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        Log.i(Constants.TAG, "Problem setting up In-app Billing: " + result);
                    }

                    Log.i(Constants.TAG, "Hooray, IAB is fully set up! ");
                    getProductsList();
                }
            });

        } else {
            isNetworkConnected = false;
            updateVisibility();
        }

        fillSettings();

    }

    private void findViews(View view) {

        fakelocation_layout_notactivated = (LinearLayout) view.findViewById(R.id.fakelocation_layout_notactivated);
        fakelocation_layout_activated = (LinearLayout) view.findViewById(R.id.fakelocation_layout_activated);
        fakelocation_layout_NoInternetConnection = (LinearLayout) view.findViewById(R.id.fakelocation_layout_NoInternetConnection);
        fake_location_button_activate = (Button) view.findViewById(R.id.fake_location_button_activate);
        fake_Location_switch = (Switch) view.findViewById(R.id.fake_Location_switch);
        fakelocation_progressBar = (ProgressBar) view.findViewById(R.id.fakelocation_progressBar);

        fakelocation_setLocationButton = (Button) view.findViewById(R.id.fake_Location_setButton);

        fake_location_button_activate.setOnClickListener(onActivateClick());

        fake_Location_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFakeLocationEnabled = fake_Location_switch.isChecked();
                writeSettings_isFakeEnabled();
            }
        });

        fakelocation_setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });


    }

    @NonNull
    private View.OnClickListener onActivateClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mHelper != null)
                    mHelper.flagEndAsync();

                    mHelper.launchPurchaseFlow(getActivity(), Constants.SUBSCRIPTION_CODE, Constants.SUBSCRIPTION_REQUEST_CODE,
                        new IabHelper.OnIabPurchaseFinishedListener() {
                            @Override
                            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                                if (result.isFailure()) {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                                getResources().getString(R.string.fake_Location_purchasing_error) + " \n" + result,
                                                Toast.LENGTH_SHORT).show();
                                    return;
                                }else if (info.getSku().equals(Constants.SUBSCRIPTION_CODE)) {
                                    getProductsList();
                                }
                            }
                        }, null);

            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mHelper != null) mHelper.dispose();
        mHelper = null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fake_location, container, false);


    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        Toast.makeText(Constants.getApplicationContext(), "Hpppppppppppppiy", Toast.LENGTH_SHORT).show();
    }

    public void getProductsList() {

        if (mHelper != null)
            mHelper.flagEndAsync();

        List<String> SKUlist = new ArrayList<>();
        SKUlist.add(Constants.SUBSCRIPTION_CODE);

        Log.v(Constants.TAG, "onQueryInventoryFinished start Query...");

        mHelper.queryInventoryAsync(true, SKUlist, new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {

                if (inv != null) {
                    mIsPremium = inv.hasPurchase(Constants.SUBSCRIPTION_CODE);
                    fillSettings();
                    writeSettings_isFakeEnabled();
                    updateVisibility();
                }

            }
        });

    }

    private void updateVisibility() {

        fakelocation_progressBar.setVisibility(View.GONE);

        if (mIsPremium) {
            fakelocation_layout_notactivated.setVisibility(View.GONE);
            fakelocation_layout_activated.setVisibility(View.VISIBLE);
            fakelocation_layout_NoInternetConnection.setVisibility(View.GONE);
        } else if (isNetworkConnected) {
            fakelocation_layout_notactivated.setVisibility(View.VISIBLE);
            fakelocation_layout_activated.setVisibility(View.GONE);
            fakelocation_layout_NoInternetConnection.setVisibility(View.GONE);
        }else{
            fakelocation_layout_activated.setVisibility(View.GONE);
            fakelocation_layout_notactivated.setVisibility(View.GONE);
            fakelocation_layout_NoInternetConnection.setVisibility(View.VISIBLE);
        }

        fake_Location_switch.setChecked(isFakeLocationEnabled);

    }

    private void writeSettings_isFakeEnabled() {

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("FakeLocation", Context.MODE_PRIVATE).edit();
        editor.putBoolean("isFakeLocationEnabled", mIsPremium);
        editor.commit();

    }

    private void fillSettings() {

        if (mIsPremium) {

            SharedPreferences sp = getActivity().getSharedPreferences("FakeLocation", Context.MODE_PRIVATE);

            isFakeLocationEnabled = sp.getBoolean("isFakeLocationEnabled", false);

        }


    }
}
