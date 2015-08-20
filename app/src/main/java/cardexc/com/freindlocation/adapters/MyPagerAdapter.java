package cardexc.com.freindlocation.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.fragments.Devices;
import cardexc.com.freindlocation.fragments.History;

public class MyPagerAdapter extends FragmentPagerAdapter {

    CharSequence Titles[];

    public MyPagerAdapter(FragmentManager fm, CharSequence[] mTitles) {
        super(fm);

        this.Titles = mTitles;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return Devices.newInstance("sa", "as");
            case 1:
                return History.newInstance();
            case 2:
                return History.newInstance();

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return Constants.TABS_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }
}
