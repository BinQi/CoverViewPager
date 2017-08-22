package wbq.android.view.viewpager.coverviewpager.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import wbq.android.view.viewpager.PagerAdapter;
import wbq.android.view.viewpager.coverviewpager.CoverViewPager;


/**
 * Created by wubinqi on 17-8-9.
 */
public class DemoActivity extends Activity {
    CoverViewPager pager;
    PagerAdapter pagerAdapter;
    SparseArray<View> mCacheViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        pager = (CoverViewPager) findViewById(R.id.pager);
        pagerAdapter = new DemoPagerAdapter();
        pager.setAdapter(pagerAdapter, CoverViewPager.Type.LOOP_CACHE_ALL);
        pager.setMinAlpha(0.1f);
        pager.setDirection(false);
        mCacheViews = new SparseArray<>();
    }

    private class DemoPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.e("wbq", "destroyItem=" + position);
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.e("wbq", "instantiateItem=" + position);
            View view = null;
            switch (position) {
                case 0:
                    view = getLayoutInflater().inflate(R.layout.welcome_fragment01, null);
                    break;
                case 1:
                    view = getLayoutInflater().inflate(R.layout.welcome_fragment02, null);
                    break;
                case 2:
                    view = getLayoutInflater().inflate(R.layout.welcome_fragment03, null);
                    break;
            }
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
