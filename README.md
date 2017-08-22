# CoverViewPager
Slide Coverd ViewPager on Android

## Preview
![](https://github.com/BinQi/CoverViewPager/blob/master/raw/demo.gif)

## Usage
```Java
setContentView(R.layout.activity_tutorial);
pager = (CoverViewPager) findViewById(R.id.pager);
pagerAdapter = new DemoPagerAdapter();
pager.setAdapter(pagerAdapter, CoverViewPager.Type.LOOP_CACHE_ALL);
pager.setMinAlpha(0.1f);
```

You can determine whether to be an infinite viewpager by sending different Type. You can also determine the minimum alpha value of the bottom view while sliding.

```Java
public enum Type {
    /** not an infinite viewpager */
    NOT_LOOP,
    /** infinite viewpager and just cache boundary views */
    LOOP_CACHE_BOUNDARY,
    /** infinite viewpager and cache all views. but this require at least 3 items */
    LOOP_CACHE_ALL
}
```
You can also use
```Java
pager = (CoverViewPager) findViewById(R.id.pager);
pager.setDirection(false)
```
to change the direction of behavior whil sliding.
