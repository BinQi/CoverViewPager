package wbq.android.view.viewpager.coverviewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import wbq.android.view.ViewHelper;
import wbq.android.view.viewpager.PagerAdapter;
import wbq.android.view.viewpager.ViewPager;

import static wbq.android.view.viewpager.coverviewpager.CoverViewPager.Type.NOT_LOOP;

/**
 * @author wubinqi
 */
public class CoverViewPager extends ViewPager {

    /**
     * @author wubinqi
     */
    public enum Type {
        /** not an infinite viewpager */
        NOT_LOOP,
        /** infinite viewpager and just cache boundary views */
        LOOP_CACHE_BOUNDARY,
        /** infinite viewpager and cache all views. but this require at least 3 items */
        LOOP_CACHE_ALL
    }
    private static final Type DEFAULT_BOUNDARY_CASHING = NOT_LOOP;

    private OnPageChangeListener mOuterPageChangeListener;
    private PageTransformer mOuterPageTransformer;
    private CoverPagerAdapter mAdapter;
    private float mDurAlpha = 1.0f;

    /**
     * helper function which may be used when implementing FragmentPagerAdapter
     *   
     * @param position
     * @param count
     * @return (position-1)%count
     */
    public static int toRealPosition( int position, int count ){
        position = position-1;
        if( position < 0 ){
            position += count;
        }else{
            position = position%count;
        }
        return position;
    }
    
    public void setAdapter(PagerAdapter adapter, CoverViewPager.Type type) {
        type = null == type ? DEFAULT_BOUNDARY_CASHING : type;
        mAdapter = new CoverPagerAdapter(adapter);
        mAdapter.setType(type);
        super.setAdapter(mAdapter);
        setCurrentItem(0, false);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        CoverViewPager.Type type = mAdapter != null ? mAdapter.getType() : DEFAULT_BOUNDARY_CASHING;
        type = null == type ? DEFAULT_BOUNDARY_CASHING : type;
        mAdapter = new CoverPagerAdapter(adapter);
        mAdapter.setType(type);
        super.setAdapter(mAdapter);
        setCurrentItem(0, false);
    }

    @Override
    public PagerAdapter getAdapter() {
        return mAdapter != null ? mAdapter.getRealAdapter() : mAdapter;
    }

    @Override
    public int getCurrentItem() {
        return mAdapter != null ? mAdapter.toRealPosition(super.getCurrentItem()) : 0;
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        int realItem = mAdapter.toInnerPosition(item);
        super.setCurrentItem(realItem, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        if (getCurrentItem() != item) {
            setCurrentItem(item, true);
        }
    }

    public void setMinAlpha(float minAlpha) {
        mDurAlpha = 1f - minAlpha;
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOuterPageChangeListener = listener;
    };

    @Override
    public void setPageTransformer(boolean reverseDrawingOrder, PageTransformer transformer) {
        mOuterPageTransformer = transformer;
        super.setPageTransformer(reverseDrawingOrder, mCoverFadePageTransformer);
    }

    public CoverViewPager(Context context) {
        super(context);
        init();
    }

    public CoverViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        super.setOnPageChangeListener(onPageChangeListener);
        super.setPageTransformer(false, mCoverFadePageTransformer);
    }

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private float mPreviousOffset = -1;
        private float mPreviousPosition = -1;

        @Override
        public void onPageSelected(int position) {
            int realPosition = mAdapter.toRealPosition(position);

            if (mPreviousPosition != realPosition) {
                mPreviousPosition = realPosition;
                if (mOuterPageChangeListener != null) {
                    mOuterPageChangeListener.onPageSelected(realPosition);
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {
            int realPosition = position;
            if (mAdapter != null) {
                realPosition = mAdapter.toRealPosition(position);

                if (positionOffset == 0
                        && mPreviousOffset == 0
                        && (position == 0 || position == mAdapter.getCount() - 1)) {
                    setCurrentItem(realPosition, false);
                }
            }

            mPreviousOffset = positionOffset;
            if (mOuterPageChangeListener != null) {
                if (realPosition != mAdapter.getRealCount() - 1) {
                    mOuterPageChangeListener.onPageScrolled(realPosition,
                            positionOffset, positionOffsetPixels);
                } else {
                    if (positionOffset > .5) {
                        mOuterPageChangeListener.onPageScrolled(0, 0, 0);
                    } else {
                        mOuterPageChangeListener.onPageScrolled(realPosition,
                                0, 0);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mAdapter != null) {
                int position = CoverViewPager.super.getCurrentItem();
                int realPosition = mAdapter.toRealPosition(position);
                if (state == ViewPager.SCROLL_STATE_IDLE
                        && (position == 0 || position == mAdapter.getCount() - 1)) {
                    setCurrentItem(realPosition, false);
                }
            }
            if (mOuterPageChangeListener != null) {
                mOuterPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    private PageTransformer mCoverFadePageTransformer = new PageTransformer() {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            View backgroundView = page;
            if(-1 < position && position <= 0) {
                ViewHelper.setTranslationX(backgroundView, pageWidth * -position);
                ViewHelper.setAlpha(backgroundView, 1.0f + mDurAlpha * position);
            } else {
                ViewHelper.setTranslationX(backgroundView, 0);
                ViewHelper.setAlpha(backgroundView, 1.0f);
            }
            if (mOuterPageTransformer != null) {
                mOuterPageTransformer.transformPage(page, position);
            }
        }
    };
}