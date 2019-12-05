package javanesecoffee.com.blink.events;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class EventsPagerAdapter extends FragmentPagerAdapter {

    private String[] titles;
    public EventsPagerAdapter(FragmentManager fm, String[] titles ) {
        super(fm);
        this.titles = titles;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                EventListFragment exploreEventList = new EventListFragment();
                exploreEventList.setType(EventListTypes.EXPLORE);
                return exploreEventList;
            case 1:
                EventListFragment upcomingEventList = new EventListFragment();
                upcomingEventList.setType(EventListTypes.UPCOMING);
                return upcomingEventList;
            case 2:
                EventListFragment pastEventList = new EventListFragment();
                pastEventList.setType(EventListTypes.PAST_EVENTS);
                return pastEventList ;
            default:
                return new EventListFragment();
        }
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
