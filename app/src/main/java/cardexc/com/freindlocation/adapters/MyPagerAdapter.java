package cardexc.com.freindlocation.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.fragments.Devices;
import cardexc.com.freindlocation.fragments.History;
import cardexc.com.freindlocation.fragments.Map;

public class MyPagerAdapter extends FragmentStatePagerAdapter { //FragmentPagerAdapter {

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
                return Map.newInstance();
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
