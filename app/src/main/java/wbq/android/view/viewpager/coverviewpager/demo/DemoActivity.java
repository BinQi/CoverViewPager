package wbq.android.view.viewpager.coverviewpager.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import wbq.android.view.ViewHelper;
import wbq.android.view.viewpager.coverviewpager.LoopViewPager;


/**
 * Created by wubinqi on 17-8-9.
 */

public class DemoActivity extends Activity {
    LoopViewPager pager;
    PagerAdapter pagerAdapter;
    final int NUM_PAGES = 3;
    SparseArray<View> mCacheViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        pager = (LoopViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter();
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(false, new CoverFadePageTransformer());
        mCacheViews = new SparseArray<>();
    }

    private class ScreenSlidePagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.e("wbq", "destroyItem=" + position);
            if (null == mCacheViews.get(position)) {
                container.removeView((View) object);
                mCacheViews.put(position, (View) object);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mCacheViews.get(position);
//            mCacheViews.remove(position);
            if (null == view) {
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
                Log.e("wbq", "instantiateItem=" + position);
            } else {
                Log.i("wbq", "instantiateItem=" + position);
            }
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            container.addView(view);
            view.setVisibility(View.VISIBLE);
            return view;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        /**
         * Determines whether a page View is associated with a specific key object
         * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
         * required for a PagerAdapter to function properly.
         *
         * @param view   Page View to check for association with <code>object</code>
         * @param object Object to check for association with <code>view</code>
         * @return true if <code>view</code> is associated with the key object <code>object</code>
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    public class CoverFadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
//            Log.d("wbq", "transformPage=" + position + " p=" + page.getTag());
            int pageWidth = page.getWidth();

            View backgroundView = page.findViewById(R.id.welcome_fragment);
            if(backgroundView != null) {
                if(-1 < position && position <= 0) {
                    ViewHelper.setTranslationX(backgroundView, pageWidth * -position);
                    ViewHelper.setAlpha(backgroundView, 1.0f - Math.abs(position));
                } else {
                    ViewHelper.setTranslationX(backgroundView, 0);
                    ViewHelper.setAlpha(backgroundView, 1.0f);
                }
            }
        }
    }
}
