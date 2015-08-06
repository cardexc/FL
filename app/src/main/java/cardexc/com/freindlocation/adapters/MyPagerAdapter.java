package cardexc.com.freindlocation.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.fragments.Devices;
import cardexc.com.freindlocation.fragments.History;

public class MyPagerAdapter extends FragmentPagerAdapter {
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return Devices.newInstance("sa", "as");
            case 1:
                return History.newInstance();
            case 2:
                return new Devices();

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }
}
