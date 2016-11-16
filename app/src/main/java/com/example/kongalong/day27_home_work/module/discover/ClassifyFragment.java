package com.example.kongalong.day27_home_work.module.discover;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kongalong.day27_home_work.R;
import com.example.kongalong.day27_home_work.SampleImageLoad.SampleImageLoad;
import com.example.kongalong.day27_home_work.Uri.Uri;
import com.example.kongalong.day27_home_work.adapters.RecommendListAdapter;
import com.example.kongalong.day27_home_work.adapters.RecommendPagerAdapter1;
import com.example.kongalong.day27_home_work.adapters.RecommendPagerAdapter2;
import com.example.kongalong.day27_home_work.asynctasks.LoadBytesAsynctask;
import com.example.kongalong.day27_home_work.base.BaseFragment;
import com.example.kongalong.day27_home_work.model.RecommendBeans1;
import com.example.kongalong.day27_home_work.model.RecommendBeans2;
import com.example.kongalong.day27_home_work.model.RecommendBeans3;
import com.example.kongalong.day27_home_work.callbacks.OnBytesCallback;
import com.example.kongalong.day27_home_work.utils.InternetUtil;
import com.example.kongalong.day27_home_work.utils.JsonParseUtil;
import com.example.kongalong.day27_home_work.utils.SdCardUtils;
import com.example.kongalong.day27_home_work.widget.CustomIndicator;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClassifyFragment extends BaseFragment implements OnBytesCallback,Handler.Callback {


    private ListView mRecommendListView;
    private BaseAdapter mRecommendListAdapter;

    private ViewPager mListViewHeaderViewPager1;
    private ViewPager mListViewHeaderViewPager2;

    private ViewPager mListViewFooterViewPager;

    private PagerAdapter mHeaderPagerAdapter1;
    private PagerAdapter mHeaderPagerAdapter2;

    private PagerAdapter mFooterPagerAdapter;

    private PullToRefreshListView mPullToRefreshListView;

    private CustomIndicator mHeaderCustomIndicator1;
    private CustomIndicator mHeaderCustomIndicator2;

    private CustomIndicator mFooterCustomIndicator;

    private List<View> mHeaderPagerData1;
    private List<View> mHeaderPagerData2;

    private List<View> mFooterPagerData;

    private List<Object> mRecommendlistData;

    private SampleImageLoad mSampleImageLoad ;

    private int mCurrentPosition;
    private Handler mHandler;




    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recommend;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void initViewAndEvent(View ret) {

        mHandler = new Handler(this);

        mCurrentPosition = Integer.MAX_VALUE/2;

        mSampleImageLoad = new SampleImageLoad(getContext());


        initListView(ret);

        initHeaderFooterViewPager();

        initViewPagerListener();


    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initViewPagerListener() {

        mListViewHeaderViewPager1.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset,
                                               int positionOffsetPixels) {

                        mHeaderCustomIndicator1.setMoveX(position% mHeaderPagerData1.size(),
                                positionOffset);
                    }

                    @Override
                    public void onPageSelected(int position) {
                        mHandler.sendMessage(mHandler.obtainMessage(3,position,0,null));

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        switch(state){
                            case ViewPager.SCROLL_STATE_DRAGGING:
                                mHandler.sendEmptyMessage(2);
                                break;
                        }
                    }
                });

        mListViewHeaderViewPager2.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageScrolled(int position, float positionOffset,
                                               int positionOffsetPixels) {

                        mHeaderCustomIndicator2.setMoveX(position%3,positionOffset);
                    }
                    @Override
                    public void onPageSelected(int position) {
                    }
                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

    }


    private void initHeaderFooterViewPager() {

        //header1
        mHeaderPagerData1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.mipmap.ic_launcher);
            mHeaderPagerData1.add(imageView);
        }
        mHeaderCustomIndicator1.setCount(10);

        mHeaderPagerAdapter1 = new RecommendPagerAdapter1(mHeaderPagerData1);
        mListViewHeaderViewPager1.setAdapter(mHeaderPagerAdapter1);

        mListViewHeaderViewPager1.setCurrentItem(Integer.MAX_VALUE/2);

        //header2
        mHeaderPagerData2 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            View view = LayoutInflater.from(getActivity()).inflate
                    (R.layout.recommend_list_header2_layout,
                            mListViewHeaderViewPager2, false);

            mHeaderPagerData2.add(view);

        }

        mHeaderCustomIndicator2.setCount(3);
        mHeaderPagerAdapter2 = new RecommendPagerAdapter2(mHeaderPagerData2);
        mListViewHeaderViewPager2.setAdapter(mHeaderPagerAdapter2);


        //footer
        mFooterPagerData = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.mipmap.ic_launcher);
            mFooterPagerData.add(imageView);
        }
        mFooterCustomIndicator.setCount(3);

        mFooterPagerAdapter = new RecommendPagerAdapter2(mFooterPagerData);
        mListViewFooterViewPager.setAdapter(mFooterPagerAdapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initListView(View view) {

        //mRecommendListView = (ListView) view.findViewById(R.id.recommend_list);

        mPullToRefreshListView = (PullToRefreshListView) view.findViewById
                (R.id.pulltoreflesh);
        //从pullToReflesh获得listview
        mRecommendListView = mPullToRefreshListView.getRefreshableView();
        //头布局
        View header = LayoutInflater.from(getContext()).inflate
                (R.layout.recommend_list_header, mRecommendListView, false);
        //头布局中的viewpager1
        mListViewHeaderViewPager1 = (ViewPager) header
                .findViewById(R.id.recommend_list_header_viewpager1);
        //头布局中的viewpager2
        mListViewHeaderViewPager2 = (ViewPager) header
                .findViewById(R.id.recommend_list_header_viewpager2);

        //尾布局
        View footer = LayoutInflater.from(getContext()).inflate
                (R.layout.recommend_list_footer, mRecommendListView, false);

        //尾布局中的viewpager
        mListViewFooterViewPager = (ViewPager) footer.findViewById(R.id.recommend_list_footer_pager);



        //头布局中的viewpager指示器
        mHeaderCustomIndicator1 = (CustomIndicator) header.findViewById(R.id.indicator1);
        mHeaderCustomIndicator2 = (CustomIndicator) header.findViewById(R.id.indicator2);
        //尾布局中的viewpager指示器
        mFooterCustomIndicator = (CustomIndicator) footer.findViewById(R.id.indicator3);

        TextView emptyView = (TextView) view.findViewById(R.id.emptyView);

        //listView中的数据
        mRecommendlistData = new ArrayList<>();
        //添加头尾布局
        mRecommendListView.addHeaderView(header);
        mRecommendListView.addFooterView(footer);
        //设置适配器
        mRecommendListAdapter = new RecommendListAdapter(getContext(), mRecommendlistData);

        mRecommendListView.setEmptyView(emptyView);

        mRecommendListView.setAdapter(mRecommendListAdapter);


    }







    @Override
    protected void initData() {
        if(InternetUtil.isConnnected(getContext())){

            new LoadBytesAsynctask(this).execute(
                    Uri.discoverRecommend1,
                    Uri.discoverRecommend2,
                    Uri.discoverRecommend3);

        }else{
            String path = getContext().getExternalCacheDir().getAbsolutePath()+ File.separator;
            byte[] json1 = SdCardUtils.getBytesFromFile(path+"json1");
            byte[] json2 = SdCardUtils.getBytesFromFile(path+"json2");
            byte[] json3 = SdCardUtils.getBytesFromFile(path+"json3");


            List<byte[]> listBytes = new ArrayList<>();
            listBytes.add(json1);
            listBytes.add(json2);
            listBytes.add(json3);
            refleshData(listBytes);

        }

    }

    @Override
    protected void initSampleImageLoad() {

    }


    @Override
    public void bytesCallback(List<byte[]> listBytes) {

        SdCardUtils.saveFile(getContext().getExternalCacheDir().getAbsolutePath(),"json1",listBytes.get(0));
        SdCardUtils.saveFile(getContext().getExternalCacheDir().getAbsolutePath(),"json2",listBytes.get(1));
        SdCardUtils.saveFile(getContext().getExternalCacheDir().getAbsolutePath(),"json3",listBytes.get(2));

        refleshData(listBytes);


    }

    public void refleshData(List<byte[]> listBytes){

        String jsonStr1 = null;
        String jsonStr2 = null;
        String jsonStr3 = null;
        //数据为空直接返回
        if(listBytes.get(0)==null||listBytes.get(1)==null||listBytes.get(2)==null){
            return;
        }
        jsonStr1 = new String(listBytes.get(0));
        jsonStr2 = new String(listBytes.get(1));
        jsonStr3 = new String(listBytes.get(2));

        RecommendBeans1 recommendBeans1 = JsonParseUtil
                .parseJsonToRecommendBeans1(jsonStr1);
        RecommendBeans2 recommendBeans2 = JsonParseUtil
                .parseJsonToRecommendBeans2(jsonStr2);
        RecommendBeans3 recommendBeans3 = JsonParseUtil
                .parseJsonToRecommendBeans3(jsonStr3);

        //刷新头布局viewpager1数据
        refleshHeaderPagerData1(recommendBeans1);
        //刷新头布局viewpager2数据
        refleshHeaderPagerData2(recommendBeans1);
        //刷新尾布局viewpager数据
        refleshFooterPagerData(recommendBeans3);
        //刷新listView数据
        refleshRecommendListViewData(recommendBeans1,recommendBeans2);
    }

    private void refleshFooterPagerData(RecommendBeans3 recommendBeans3) {

        for (int i = 0; i < mFooterPagerData.size(); i++) {

            int tempI = i%recommendBeans3.getData().size();
            showImage(recommendBeans3.getData().get(tempI).getCover(),(ImageView)
                    mFooterPagerData.get(i));

        }

    }


    private void refleshRecommendListViewData(RecommendBeans1 recommendBeans1, RecommendBeans2 recommendBeans2) {

        List<RecommendBeans2.GuessBean.ListBean> guessList =
                recommendBeans2.getGuess().getList();

        List<RecommendBeans1.EditorRecommendAlbumsBean.ListBean> editRecommend =
                recommendBeans1.getEditorRecommendAlbums().getList();

        List<RecommendBeans2.HotRecommendsBean.ListBean> normalList =
                recommendBeans2.getHotRecommends().getList();

        List<RecommendBeans1.SpecialColumnBean.ListBean> speciaList =
                recommendBeans1.getSpecialColumn().getList();

        mRecommendlistData.add(0, guessList);
        mRecommendlistData.add(1, editRecommend);
        mRecommendlistData.add(2,speciaList);
        mRecommendlistData.addAll(normalList);

        mRecommendListAdapter.notifyDataSetChanged();


    }

    private void refleshHeaderPagerData2(RecommendBeans1 mRecommendBeans1) {

        List<RecommendBeans1.DiscoveryColumnsBean.ListBean> pagerDatabeans = mRecommendBeans1
                .getDiscoveryColumns().getList();


        int index = -1;
        for (int i = 0; i < mHeaderPagerData2.size(); i++) {

            View view = mHeaderPagerData2.get(i);

            ImageView imageView1 = (ImageView) view.findViewById(R.id.recommend_list_header2_image1);
            ImageView imageView2 = (ImageView) view.findViewById(R.id.recommend_list_header2_image2);
            ImageView imageView3 = (ImageView) view.findViewById(R.id.recommend_list_header2_image3);
            ImageView imageView4 = (ImageView) view.findViewById(R.id.recommend_list_header2_image4);

            TextView textView1 = (TextView) view.findViewById(R.id.recommend_list_header2_text1);
            TextView textView2 = (TextView) view.findViewById(R.id.recommend_list_header2_text2);
            TextView textView3 = (TextView) view.findViewById(R.id.recommend_list_header2_text3);
            TextView textView4 = (TextView) view.findViewById(R.id.recommend_list_header2_text4);

            LinearLayout linearLayout1 = (LinearLayout) view.findViewById(R.id.recommend_text_image1);
            LinearLayout linearLayout2 = (LinearLayout) view.findViewById(R.id.recommend_text_image2);
            LinearLayout linearLayout3 = (LinearLayout) view.findViewById(R.id.recommend_text_image3);
            LinearLayout linearLayout4 = (LinearLayout) view.findViewById(R.id.recommend_text_image4);


            if(index==pagerDatabeans.size()-1){
                linearLayout1.setVisibility(View.INVISIBLE);
                linearLayout2.setVisibility(View.INVISIBLE);
                linearLayout3.setVisibility(View.INVISIBLE);
                linearLayout4.setVisibility(View.INVISIBLE);
                break;
            }
            showImage(pagerDatabeans.get(++index).getCoverPath(),imageView1);
            textView1.setText(pagerDatabeans.get(index).getTitle());
            if(index==pagerDatabeans.size()-1){
                linearLayout2.setVisibility(View.INVISIBLE);
                linearLayout3.setVisibility(View.INVISIBLE);
                linearLayout4.setVisibility(View.INVISIBLE);
                break;
            }
            showImage(pagerDatabeans.get(++index).getCoverPath(), imageView2);
            textView2.setText(pagerDatabeans.get(index).getTitle());
            if(index==pagerDatabeans.size()-1){
                linearLayout3.setVisibility(View.INVISIBLE);
                linearLayout4.setVisibility(View.INVISIBLE);
                break;
            }
            showImage(pagerDatabeans.get(++index).getCoverPath(), imageView3);
            textView3.setText(pagerDatabeans.get(index).getTitle());
            if(index==pagerDatabeans.size()-1){
                linearLayout4.setVisibility(View.INVISIBLE);
                break;
            }
            showImage(pagerDatabeans.get(++index).getCoverPath(), imageView4);
            textView4.setText(pagerDatabeans.get(index).getTitle());
        }

    }


    private void refleshHeaderPagerData1(RecommendBeans1 mRecommendBeans1) {

        List<RecommendBeans1.FocusImagesBean.ListBean> pagerDatabeans = mRecommendBeans1
                .getFocusImages().getList();
        //根据数据动态生成相应数量额pager
        int value = pagerDatabeans.size() - mHeaderPagerData1.size();
        if(value>0){
            for (int i = 0; i < value; i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(R.mipmap.ic_launcher);
                mHeaderPagerData1.add(imageView);
            }
        }else if(value<0){
            for (int i = 0; i < Math.abs(value); i++) {
                mHeaderPagerData1.remove(mHeaderPagerData1.size()-1);
            }
        }

        mHeaderCustomIndicator1.setCount(mHeaderPagerData1.size());

        for (int i = 0; i < mHeaderPagerData1.size(); i++) {
            showImage(pagerDatabeans.get(i).getPic(),(ImageView) mHeaderPagerData1.get(i));
        }
        //mHeaderPagerAdapter1.notifyDataSetChanged();

    }


    @Override
    public boolean handleMessage(Message msg) {

        switch(msg.what) {
            case 1:
                if (mHandler.hasMessages(1)) {
                    mHandler.removeMessages(1);
                }
                mCurrentPosition++;
                mListViewHeaderViewPager1.setCurrentItem(mCurrentPosition);
                // mHandler.sendEmptyMessageDelayed(1, 2000);
                //  Log.d("flag", "handleMessage: " +mCurrentPosition);
                break;
            case 2:
                mHandler.removeMessages(1);
                break;
            case 3:
                mCurrentPosition = msg.arg1;
                mListViewHeaderViewPager1.setCurrentItem(mCurrentPosition);
                mHandler.sendEmptyMessageDelayed(1, 2000);
                break;
        }
        return true;
    }



    public void showImage(String path,ImageView imageView){

        mSampleImageLoad.initParams()
                .setUrl(path)
                .setImageSize(100,100)
                .setImageView(imageView)
                .attachToView();
    }

}