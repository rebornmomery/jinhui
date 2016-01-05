package com.android.shortvideo.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 视图相关操作的工具类
 * 
 * @author ls
 *
 */
public class ViewUtil {
    /**
     * 禁止下拉（�?配魅族手机）
     * @param view
     */
    public static void disableScrollMode(View view) {
        try {
            Method method = AbsListView.class.getMethod("setOverScrollMode",
                    int.class);
            @SuppressWarnings("rawtypes")
            Class viewCls = view.getClass();
            int OVER_SCROLL_NEVER = (Integer) viewCls.getField(
                    "OVER_SCROLL_NEVER").get(view);
            method.invoke(view, OVER_SCROLL_NEVER);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    /**
	 * 
	 * @Description: 动�?设置ListView/ grideview的高�?
	 * @param view
	 * @return void
	 * @date 2015-2-6 上午8:48:28
	 * @update (date)
	 */
    public static void setListViewHeight(ListView listView) {    
        
        // 获取ListView对应的Adapter    
        
        ListAdapter listAdapter = listView.getAdapter();    
        
        if (listAdapter == null) {    
            return;    
        }    
        int totalHeight = 0;    
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目    
            View listItem = listAdapter.getView(i, null, listView);    
            listItem.measure(0, 0); // 计算子项View 的宽�?   
            totalHeight += listItem.getMeasuredHeight(); // 统计�?��子项的�?高度    
        }    
        
        ViewGroup.LayoutParams params = listView.getLayoutParams();    
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));    
        listView.setLayoutParams(params);    
    }    
}
