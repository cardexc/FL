package cardexc.com.freindlocation.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


public class FragmentActivityClass extends FragmentActivity {

    public  static FragmentManager fragmentManager;
    private static FragmentActivityClass mInstance;

    private FragmentActivityClass(){

    }

    public static FragmentActivityClass getInstance() {
        if (mInstance == null) {
            mInstance = new FragmentActivityClass();
        }

        return mInstance;
    }
    public FragmentManager getMySupportFragmentManager() {

        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }

        return fragmentManager;
    }



}
