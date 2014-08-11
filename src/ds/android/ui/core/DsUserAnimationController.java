package ds.android.ui.core;


public abstract class DsUserAnimationController {
	/** 下拉进程或收起进程达到此阈值时，切换到自动动画 */
	public static final float DRAG_PROCESS_THRESHOLD = 0.2f;

	/** 当前动画的动作，无动作，拖动，自动向下或自动向上 */
	public static final int ACTION_NONE = 0;
	public static final int ACTION_DRAG = 1;
	public static final int ACTION_MOVING_DOWN = 2;
	public static final int ACTION_MOVING_UP = 3;

	/** 当前动画的状态，收起或展开 */
	public static final int STATE_FOLD = 4;
	public static final int STATE_EXPAND = 5;

	/** 当前动画的动作 */
	private int mAnimationActionType;
	/** 当前动画的状态，区别于mIsExpand，mIsExpand用于排版布局，mAnimationStateType用于动画控制 */
	private int mAnimationStateType;

	/**
	 * 动画共有8种情况： 1. 收起状态 2. 展开状态 3. 收起状态用户拖动 4. 展开状态用户拖动 5. 收起状态自动向上 6. 展开状态自动向上
	 * 7. 收起状态自动向下 8. 展开状态自动向下
	 */
	
	public DsUserAnimationController() {
		mAnimationActionType = ACTION_NONE;
		mAnimationStateType = STATE_FOLD;
	}

	public int getAnimationActionType() {
		return mAnimationActionType;
	}

	public void setAnimationActionType(int aAnimationActionType) {
		this.mAnimationActionType = aAnimationActionType;
	}

	public int getAnimationStateType() {
		return mAnimationStateType;
	}

	public void setAnimationStateType(int aAnimationStateType) {
		this.mAnimationStateType = aAnimationStateType;
	}

	public void startShowAnimation() {
//		DsLog.i("public void startShowAnimation()");
//		DsLog.i("mAnimationStateType:" + mAnimationStateType + "|mAnimationActionType:"
//				+ mAnimationActionType);
		/** 情况1、3、4时，可以执行该方法 */
		final boolean flag = (mAnimationStateType == STATE_FOLD && mAnimationActionType == ACTION_NONE)
				|| mAnimationActionType == ACTION_DRAG;
		if (!flag) {
			return;
		}
		
		mAnimationActionType = ACTION_MOVING_DOWN;

		startShowAnimationInner(0);
	}

	public void startDismissAnimation() {
//		DsLog.i("startDismissAnimation(float aRawY)");
//		DsLog.i("mAnimationStateType:" + mAnimationStateType + "|mAnimationActionType:"
//				+ mAnimationActionType);
		/** 情况2、3、4时，可以执行该方法 */
		final boolean flag = (mAnimationStateType == STATE_EXPAND && mAnimationActionType == ACTION_NONE)
				|| mAnimationActionType == ACTION_DRAG;
		if (!flag) {
			return;
		}
		
		
		mAnimationActionType = ACTION_MOVING_UP;

		startDismissAnimationInner(1);
	}

	public void startShowAnimation(float aProcess) {
//		DsLog.i("startShowAnimation(float aRawY)");
//		DsLog.i("mAnimationStateType:" + mAnimationStateType + "|mAnimationActionType:"
//				+ mAnimationActionType);
		/** 情况1、3、4时，可以执行该方法 */
		final boolean flag = (mAnimationStateType == STATE_FOLD && mAnimationActionType == ACTION_NONE)
				|| mAnimationActionType == ACTION_DRAG;
		if (!flag) {
			return;
		}
		
		mAnimationActionType = ACTION_MOVING_DOWN;

		startShowAnimationInner(aProcess);
	}

	public void startDismissAnimation(float aProcess) {
//		DsLog.i("startDismissAnimation(float aRawY)");
//		DsLog.i("mAnimationStateType:" + mAnimationStateType + "|mAnimationActionType:"
//				+ mAnimationActionType);
		/** 情况2、3、4时，可以执行该方法 */
		final boolean flag = (mAnimationStateType == STATE_EXPAND && mAnimationActionType == ACTION_NONE)
				|| mAnimationActionType == ACTION_DRAG;
		if (!flag) {
			return;
		}
		
		
		mAnimationActionType = ACTION_MOVING_UP;

		startDismissAnimationInner(aProcess);

	}

	public void simulateShowAnimation(float aProcess) {
//		DsLog.i("public void simulateShowAnimation(float aRawY)");
//		DsLog.i("mAnimationStateType:" + mAnimationStateType + "|mAnimationActionType:"
//				+ mAnimationActionType);
		/** 情况1、3时，可以执行该方法 */
		final boolean flag = mAnimationStateType == STATE_FOLD
				&& (mAnimationActionType == ACTION_NONE || mAnimationActionType == ACTION_DRAG);
		if (!flag) {
			return;
		}
		
		mAnimationActionType = ACTION_DRAG;

		simulateShowAnimationInner(aProcess);
		

	}

	public void simulateDismissAnimation(float aProcess) {
//		DsLog.i("public void simulateDismissAnimation(float aRawY) ");
//		DsLog.i("mAnimationStateType:" + mAnimationStateType + "|mAnimationActionType:"
//				+ mAnimationActionType);
		/** 情况2、4时，可以执行该方法 */
		final boolean flag = mAnimationStateType == STATE_EXPAND
				&& (mAnimationActionType == ACTION_NONE || mAnimationActionType == ACTION_DRAG);
		if (!flag) {
			return;
		}
		
		
		mAnimationActionType = ACTION_DRAG;

		simulateDismissAnimationInner(aProcess);

	}

	public void showWithoutAnimation() {
//		DsLog.i("showWithoutAnimation()");
//		DsLog.i("mAnimationStateType:" + mAnimationStateType + "|mAnimationActionType:"
//				+ mAnimationActionType);
		/** 情况1时，可以执行该方法 */
		final boolean flag = (mAnimationStateType == STATE_FOLD && mAnimationActionType == ACTION_NONE);
		if (!flag) {
			return;
		}
		
		mAnimationStateType = STATE_EXPAND;
		mAnimationActionType = ACTION_NONE;

		showWithoutAnimationInner();
	}

	public void dismissWithoutAnimation() {
//		DsLog.i("dismissWithoutAnimation()");
//		DsLog.i("mAnimationStateType:" + mAnimationStateType + "|mAnimationActionType:"
//				+ mAnimationActionType);
		/** 情况2时，可以执行该方法 */
		final boolean flag = (mAnimationStateType == STATE_EXPAND && mAnimationActionType == ACTION_NONE);
		if (!flag) {
			return;
		}
		
		mAnimationStateType = STATE_FOLD;
		mAnimationActionType = ACTION_NONE;

		dismissWithoutAnimationInner();
	}

	public void onShowAnimationFinished() {
//		DsLog.i("onShowAnimationFinished()");
		
		mAnimationStateType = STATE_EXPAND;
		mAnimationActionType = ACTION_NONE;

		onShowAnimationFinishedInner();
	}

	public void onDismissAnimationFinished() {
//		DsLog.i("onDismissAnimationFinished() ");
		
		mAnimationStateType = STATE_FOLD;
		mAnimationActionType = ACTION_NONE;

		onDismissAnimationFinishedInner();
	}

	/** 以下方法子类通过重载，用于控制动画进程 */
	protected abstract void startShowAnimationInner(float aProcess);

	protected abstract void startDismissAnimationInner(float aProcess);

	protected abstract void simulateShowAnimationInner(float aProcess);

	protected abstract void simulateDismissAnimationInner(float aProcess);

	protected abstract void showWithoutAnimationInner();

	protected abstract void dismissWithoutAnimationInner();

	protected abstract void onShowAnimationFinishedInner();

	protected abstract void onDismissAnimationFinishedInner();
}
