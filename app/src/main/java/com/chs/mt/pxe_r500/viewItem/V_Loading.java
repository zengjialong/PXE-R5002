package com.chs.mt.pxe_r500.viewItem;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chs.mt.pxe_r500.R;
import com.chs.mt.pxe_r500.util.img.MyAnimatorUpdateListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;


/**
 * Created by Administrator on 2017/8/7.
 */

public class V_Loading extends RelativeLayout {
    public TextView TV_Title;
    public SimpleDraweeView BtnImg;
    public RelativeLayout LY;
    private ObjectAnimator anim;//Animator ObjectAnimator
    private MyAnimatorUpdateListener listener;
    private Context mContext;
    //监听函数
    private SetOnClickListener mSetOnClickListener = null;
    public void OnSetOnClickListener(SetOnClickListener listener) {
        this.mSetOnClickListener = listener;
    }

    public interface SetOnClickListener{
        void onClickListener(int val, int type, boolean boolClick);
    }

    //结构函数
    public V_Loading(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    public V_Loading(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public V_Loading(Context context) {
        this(context, null);
    }

    private void init(Context context) {
        mContext = context;
        //加载布局文件，与setContentView()效果一样
        LayoutInflater.from(context).inflate(R.layout.chs_viewitem_loading, this);
        LY = (RelativeLayout) findViewById(R.id.id_ly);
        TV_Title    = (TextView) findViewById(R.id.id_title);

        BtnImg = (SimpleDraweeView)findViewById(R.id.my_image);
        RoundingParams roundingParams = new RoundingParams();
        roundingParams.setCornersRadius((context.getResources().getDimensionPixelOffset(R.dimen.Music_Control_Bar_ImgSize_radius)));
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder.build();
        hierarchy.setRoundingParams(roundingParams);
        BtnImg.setHierarchy(hierarchy);

        LinearInterpolator lin = new LinearInterpolator();
        anim = ObjectAnimator.ofFloat(BtnImg, "rotation", 0f, 360f);
        anim.setDuration(1800);
        anim.setInterpolator(lin);
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setRepeatCount(-1);
        listener = new MyAnimatorUpdateListener(anim);
        anim.addUpdateListener(listener);
        anim.start();

        clickEvent();
    }

    public void Start(){
        BtnImg.setVisibility(VISIBLE);
        TV_Title.setText(mContext.getResources().getString(R.string.scanning));
        anim.start();
    }
    public void Stop(){
        BtnImg.setVisibility(GONE);
        TV_Title.setText(mContext.getResources().getString(R.string.spp_le_button_scan));
        anim.cancel();
    }
    /*
    响应事件
     */
    private void clickEvent(){

    }

}
