package com.chs.mt.pxe_r500.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.chs.mt.pxe_r500.R;

import java.util.Timer;
import java.util.TimerTask;

public class SlideUnlockView extends View {

	/**
	 * 滑块当前的状态
	 */
	public int currentState;
	/**
	 * 未解锁
	 */
	public static final int STATE_LOCK = 1;
	/**
	 * 解锁
	 */
	public static final int STATE_UNLOCK = 2;
	/**
	 * 正在拖拽
	 */
	public static final int STATE_MOVING = 3;

	private static final String TAG = "SlideUnlockView";
	/**
	 * 滑动解锁的背景图片
	 */
	private Bitmap slideUnlockBackground;
	/**
	 * 滑块的图片
	 */
	private Bitmap slideUnlockBlock;
	/**
	 * 滑动解锁背景的宽度
	 */
	private int blockBackgoundWidth;
	/**
	 * 滑块宽高
	 */
	private int blockWidth;
	private int blockHeight;

    private int mViewHeight = 0;
    private int mViewWidth = 0;
	/**
	 * 手指在滑块的x，y值
	 */
	private float x;
	private float y;
	/**
	 * 手指在按下时，是否按到了滑块上
	 */
	private boolean downOnBlock;

	private int PicIndex = 0;
	/**
	 * 通过handler来控制滑块在未解锁的时候，平缓的滑动到左端
	 */
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				// 如果x还大于0，就人为的设置缓慢移动到最左端，每次移动距离设置为背景宽的/100
				if (x > 0) {
					x = x - blockBackgoundWidth * 1.0f / 100;
					// 刷新界面
					postInvalidate();
					// 设置继续移动
					handler.sendEmptyMessageDelayed(0, 10);
				} else {
					handler.removeCallbacksAndMessages(null);
					currentState = STATE_LOCK;
					Log.i(TAG, "state---lock.....");
				}
			}else  if (msg.what == 3) {
                // 直接移除，定时器停止
                handler.removeMessages(3);
                setThumb();
            }
		};
	};
    Timer timerColorMode = new Timer();
    TimerTask taskColorMode = new TimerTask() {

        @Override
        public void run() {
            Message msgil = Message.obtain();
            msgil.what = 3;
            handler.sendMessage(msgil);

        }
    };
	/**
	 * 解锁的监听
	 */
	private OnUnLockListener onUnLockListener;

	/**
	 * 自定义View的构造方法
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SlideUnlockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 默认滑动解锁为未解锁状态
		currentState = STATE_LOCK;

		// 取出自定义属性中当前状态
		// 如果解锁状态是true，说明已经解锁
		/**
		 * 当取出自定义属性的背景时，设置背景
		 */
		setSlideUnlockBackground();
		/**
		 * 当取出自定义属性的滑块时，设置滑块的图片
		 */
		setSlideUnlockBlock();
		/**
		 * 执行onDraw方法，进行界面绘制
		 */
		postInvalidate();
        timerColorMode.schedule(taskColorMode, 500, 500);
	}

	public SlideUnlockView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideUnlockView(Context context) {
		this(context, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 在一开始的使用将背景图绘制出来
		canvas.drawBitmap(slideUnlockBackground, 0, 0, null);
		/**
		 * 判断当前状态
		 */
		switch (currentState) {
		// 如果是未解锁，就将滑块绘制到最左端
		case STATE_LOCK:
			canvas.drawBitmap(slideUnlockBlock, 0, 0, null);
			break;
		// 已解锁，计算出
		case STATE_UNLOCK:
			int unlockX = blockBackgoundWidth - blockWidth;
			canvas.drawBitmap(slideUnlockBlock, unlockX, 0, null);
			break;
		case STATE_MOVING:
			if (x < 0) {
				x = 0;
			} else if (x > blockBackgoundWidth - blockWidth) {
				x = blockBackgoundWidth - blockWidth;
			}
			float nx=x-blockWidth/2;
            if (nx < 0) {
                nx = 0;
            }
			canvas.drawBitmap(slideUnlockBlock,nx , 0, null);
			break;
		default:
			break;
		}
	}

	public void setSlideUnlockBackground() {
		Log.i(TAG, "setSlideUnlockBackground.....");
		slideUnlockBackground = BitmapFactory.decodeResource(getResources(),
				R.drawable.chs_vivid_bg);
		// 获取背景图的宽和高
		blockBackgoundWidth = mViewWidth;

	}

	public void setSlideUnlockBlock() {
		Log.i(TAG, "setSlideUnlockBlock.....");
		slideUnlockBlock = BitmapFactory.decodeResource(getResources(),
                R.drawable.lock_thumb_normal1);
		// 获取滑块的宽和高
		blockWidth = slideUnlockBlock.getWidth();
		blockHeight = slideUnlockBlock.getHeight();
	}

	private void setThumb(){
//        Log.i(TAG, "state---setThumb.....1");

        ++PicIndex;
        if(PicIndex>=3){
            PicIndex = 0;
        }
        if(PicIndex == 0){
            slideUnlockBlock = BitmapFactory.decodeResource(getResources(),
                    R.drawable.lock_thumb_normal1);
        }else if(PicIndex == 1){
            slideUnlockBlock = BitmapFactory.decodeResource(getResources(),
                    R.drawable.lock_thumb_normal2);
        }else if(PicIndex == 2){
            slideUnlockBlock = BitmapFactory.decodeResource(getResources(),
                    R.drawable.lock_thumb_normal3);
        }
        invalidate();
    }



	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        mViewWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//        Log.i(TAG, "onMeauser.....");
        blockBackgoundWidth = mViewWidth;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		// 当手指按下的时候，判断手指按下的位置是否在滑块上边
		case MotionEvent.ACTION_DOWN:

			if (currentState != STATE_MOVING) {
				// 判断一下，如果当前正在移动，则不执行触摸操作
				// 获取相对于背景的左上角的x，y值
				x = event.getX();
				y = event.getY();
				// 先计算出滑块的中心点的x，y坐标
				float blockCenterX = blockWidth * 1.0f / 2;
				float blockCenterY = blockHeight * 1.0f / 2;
				downOnBlock = isDownOnBlock(blockCenterX, x, blockCenterY, y);
//				Log.i(TAG, "down......................");
				// 调用onDraw方法
				postInvalidate();

			}
			break;
		case MotionEvent.ACTION_MOVE:
			// 如果手指确定按在滑块上，就视为开始拖拽滑块
			if (downOnBlock) {
				// 获取相对于背景的左上角的x，y值
				x = event.getX();
				y = event.getY();
				currentState = STATE_MOVING;
//				Log.i(TAG, "move......................");
				// 调用onDraw方法
				postInvalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (currentState == STATE_MOVING) {
				// 当手指抬起的时候，应该是让滑块归位的
				// 说明未解锁
				if (x < blockBackgoundWidth - blockWidth) {
					handler.sendEmptyMessageDelayed(0, 10);
					// 通过回调设置已解锁
					onUnLockListener.setUnLocked(false);
				} else {
					currentState = STATE_UNLOCK;
					// 通过回调设置未解锁
					onUnLockListener.setUnLocked(true);
				}
				downOnBlock = false;
				// 调用onDraw方法
				postInvalidate();

			}
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * 计算手指是否是落在了滑块上(默认是按照滑块在未解锁的初始位置来计算的)
	 */
	public boolean isDownOnBlock(float x1, float x2, float y1, float y2) {
		float sqrt = (float) Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2)
				+ Math.abs(y1 - y2) * Math.abs(y1 - y2));
		if (sqrt <= blockWidth / 2) {
			return true;
		}
		return false;
	}

	/**
	 * 设置解锁监听
	 * 
	 * @param onUnLockListener
	 */
	public void setOnUnLockListener(OnUnLockListener onUnLockListener) {
		this.onUnLockListener = onUnLockListener;
	}

	// 定义一个解锁的监听
	public interface OnUnLockListener {
		public void setUnLocked(boolean lock);
	}

	/**
	 * 重置一下滑动索的状态，保证下次能够正常使用
	 */
	public void reset() {
		currentState = STATE_LOCK;
		postInvalidate();
	}
	
	//判断手指是否在滑块的背景区域移动
	public boolean isOnBackground(int x,int y){
		if(x<=mViewWidth&&y<=mViewHeight){
			return true;
		}
		return false;
	}
}
