package wbq.android.view.viewpager.coverviewpager;

import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import wbq.android.view.viewpager.PagerAdapter;
import wbq.android.view.viewpager.ViewPager;

/**
 * A PagerAdapter wrapper responsible for providing a proper page to
 * CoverViewPager
 * @author wubinqi
 */
final class CoverPagerAdapter extends PagerAdapter {

    private PagerAdapter mAdapter;

    private SparseArray<CacheItem> mCaches = null;

    private CoverViewPager.Type mType = null;

    void setType(CoverViewPager.Type type) {
        if (mType != null) {
            throw new IllegalStateException("This CoverPager's type has already been set, can not reset CoverPager's type!");
        }
        mType = type;
        switch (mType) {
            case LOOP_CACHE_BOUNDARY:
            case LOOP_CACHE_ALL:
                mCaches = new SparseArray<CacheItem>();
                break;
        }
    }

    CoverViewPager.Type getType() {
        return mType;
    }

    CoverPagerAdapter(PagerAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public void notifyDataSetChanged() {
        if (mCaches != null) {
            mCaches.clear();
        }
        super.notifyDataSetChanged();
    }

    int toRealPosition(int position) {
        switch (mType) {
            case NOT_LOOP:
                return position;
        }
        int realCount = getRealCount();
        if (realCount == 0)
            return 0;
        int realPosition = (position-1) % realCount;
        if (realPosition < 0)
            realPosition += realCount;

        return realPosition;
    }

    public int toInnerPosition(int realPosition) {
        switch (mType) {
            case NOT_LOOP:
                return realPosition;
        }
        int position = (realPosition + 1);
        return position;
    }

    private int getRealFirstPosition() {
        switch (mType) {
            case NOT_LOOP:
                return 0;
        }
        return 1;
    }

    private int getRealLastPosition() {
        return getRealFirstPosition() + getRealCount() - 1;
    }

    @Override
    public int getCount() {
        final int realCount = getRealCount();
        switch (mType) {
            case NOT_LOOP:
                return realCount;
            case LOOP_CACHE_ALL:
                if (realCount < 3) {
                    throw new IllegalStateException("LOOP_CACHE_ALL type require at least 3 items, current is " + realCount);
                }
                break;
        }
        return realCount + 2;
    }

    public int getRealCount() {
        return mAdapter.getCount();
    }

    public PagerAdapter getRealAdapter() {
        return mAdapter;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        int realPosition = (mAdapter instanceof FragmentPagerAdapter || mAdapter instanceof FragmentStatePagerAdapter)
//                ? position
//                : toRealPosition(position);
        int realPosition = toRealPosition(position);

        boolean needCache = false;
        switch (mType) {
            case LOOP_CACHE_ALL:
                CacheItem item = mCaches.get(realPosition);
                View view = item != null ? (View) item.mObject : null;
                if (view != null) {
                    ViewGroup parent = (ViewGroup) view.getParent();
                    if (parent != null) {
                        parent.removeView(view);
                    }
                    ViewGroup.LayoutParams lp = view.getLayoutParams();
                    if (lp instanceof ViewPager.LayoutParams) {
                        ViewPager.LayoutParams ll = (ViewPager.LayoutParams) lp;
                        ll.position = position;
                    }
                    container.addView(view);
                    return view;
                }
                needCache = true;
                break;
            case LOOP_CACHE_BOUNDARY:
                CacheItem item1 = mCaches.get(position);
                if (item1 != null) {
                    mCaches.remove(position);
                    return item1.mObject;
                }
                break;
        }
        Object obj = mAdapter.instantiateItem(container, realPosition);
        if (needCache) {
            mCaches.put(realPosition, new CacheItem(container, realPosition,
                    obj));
        }
        return obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        int realPosition = (mAdapter instanceof FragmentPagerAdapter || mAdapter instanceof FragmentStatePagerAdapter)
//                ? position
//                : toRealPosition(position);
        int realPosition = toRealPosition(position);

        switch (mType) {
            case LOOP_CACHE_ALL:
                CacheItem item = mCaches.get(realPosition);
                if (null == item) {
                    mCaches.put(realPosition, new CacheItem(container, realPosition,
                            object));
                    break;
                }
                return;
            case LOOP_CACHE_BOUNDARY:
                int realFirst = getRealFirstPosition();
                int realLast = getRealLastPosition();
                if (position == realFirst || position == realLast) {
                    mCaches.put(position, new CacheItem(container, realPosition,
                            object));
                    return;
                }
                break;
        }
        mAdapter.destroyItem(container, realPosition, object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        mAdapter.finishUpdate(container);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return mAdapter.isViewFromObject(view, object);
    }

    @Override
    public void restoreState(Parcelable bundle, ClassLoader classLoader) {
        mAdapter.restoreState(bundle, classLoader);
    }

    @Override
    public Parcelable saveState() {
        return mAdapter.saveState();
    }

    @Override
    public void startUpdate(ViewGroup container) {
        mAdapter.startUpdate(container);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mAdapter.setPrimaryItem(container, position, object);
    }

    /**
     * Container class for caching views
     * @author wubinqi
     */
    static class CacheItem {
        ViewGroup mContainer;
        int mPosition;
        Object mObject;

        public CacheItem(ViewGroup container, int position, Object object) {
            this.mContainer = container;
            this.mPosition = position;
            this.mObject = object;
        }
    }
}