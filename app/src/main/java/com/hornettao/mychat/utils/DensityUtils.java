package com.hornettao.mychat.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * 常用单位转换的辅助类
 * 
 * @author zhy
 * 
 */
public class DensityUtils
{
	private DensityUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 *转换标准尺寸dp
	 *
	 * @param context
	 * @param value
	 * @return
	 */
	public static int getDP(Context context, float value) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
				, value, context.getResources().getDisplayMetrics());
	}

	/**
	 *转换标准尺寸px
	 *
	 * @param context
	 * @param value
	 * @return
	 */
	public static int getPX(Context context, float value) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX
				, value, context.getResources().getDisplayMetrics());
	}

	/**
	 *转换标准尺寸dp
	 *
	 * @param context
	 * @param value
	 * @return
	 */
	public static int getSP(Context context, float value) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
				, value, context.getResources().getDisplayMetrics());
	}

	/**
	 * dp转px
	 * 
	 * @param context
	 * @param dpVal
	 * @return
	 */
	public static int dp2px(Context context, float dpVal)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, context.getResources().getDisplayMetrics());
	}

	/**
	 * sp转px
	 * 
	 * @param context
	 * @param spVal
	 * @return
	 */
	public static int sp2px(Context context, float spVal)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVal, context.getResources().getDisplayMetrics());
	}

	/**
	 * px转dp
	 * 
	 * @param context
	 * @param pxVal
	 * @return
	 */
	public static float px2dp(Context context, float pxVal)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (pxVal / scale);
	}

	/**
	 * px转sp
	 * 
	 * @param context
	 * @param pxVal
	 * @return
	 */
	public static float px2sp(Context context, float pxVal)
	{
		return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
	}

}
