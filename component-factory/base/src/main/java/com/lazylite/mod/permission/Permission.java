package com.lazylite.mod.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.fragment.app.Fragment;

import com.lazylite.mod.permission.core.Callback;
import com.lazylite.mod.permission.core.IamUI;
import com.lazylite.mod.permission.core.ManagerPermissions;

import java.util.List;

/**
 * android系统权限管理工具。
 * <p/>
 * 用法：
 * <ul>
 * <li>在Fragment的三个生命周期方法中调用：</strong><br/>
 * 1.{@link Fragment#onRequestPermissionsResult(int, String[], int[])}中调用{@link #onRequestPermissionResult(Fragment, int, String[], int[])}；<br/>
 * 2.{@link Fragment#onActivityResult(int, int, Intent)}中调用{@link #onActivityResult(Fragment, int, int, Intent)}；<br/>
 * 3.{@link Fragment#onDestroy()}中调用{@link #onFragmentDestroy(Fragment)}；<br/>
 *
 * <li><strong>在Activity的三个生命周期方法中调用：</strong><br/>
 * 1.{@link Activity#onRequestPermissionsResult(int, String[], int[])}中调用{@link #onRequestPermissionResult(Activity, int, String[], int[])}；<br/>
 * 2.{@link Activity#onActivityResult(int, int, Intent)}中调用{@link #onActivityResult(Activity, int, int, Intent)}；<br/>
 * 3.{@link Activity#onDestroy()}中调用{@link #onActivityDestroy(Activity)}；<br/>
 * </ul>
 * <p/>
 * 然后在需要申请权限的地方调用{@link #requestPermissions(Activity, int, String[], Callback)}等四个重载方法即可。
 * <p/>
 *
 * 下列权限使用必须动态申请，但是目前来看，只需要获取一组中的一个权限，那么android系统会给你整个组的权限（无需申请）：
 * <ul>
 * <li><strong>group:android.permission-group.CONTACTS</strong><br/>
 * permission:android.permission.WRITE_CONTACTS<br/>
 * permission:android.permission.GET_ACCOUNTS<br/>
 * permission:android.permission.READ_CONTACTS<br/>
 *
 * <li><strong>group:android.permission-group.PHONE</strong><br/>
 * permission:android.permission.READ_CALL_LOG<br/>
 * permission:android.permission.READ_PHONE_STATE<br/>
 * permission:android.permission.CALL_PHONE<br/>
 * permission:android.permission.WRITE_CALL_LOG<br/>
 * permission:android.permission.USE_SIP<br/>
 * permission:android.permission.PROCESS_OUTGOING_CALLS<br/>
 * permission:com.android.voicemail.permission.ADD_VOICEMAIL
 *
 * <li><strong>group:android.permission-group.CALENDAR</strong><br/>
 * permission:android.permission.READ_CALENDAR<br/>
 * permission:android.permission.WRITE_CALENDAR
 *
 * <li><strong>group:android.permission-group.CAMERA</strong><br/>
 * permission:android.permission.CAMERA
 *
 * <li><strong>group:android.permission-group.SENSORS</strong><br/>
 * permission:android.permission.BODY_SENSORS
 *
 * <li><strong>group:android.permission-group.LOCATION</strong><br/>
 * permission:android.permission.ACCESS_FINE_LOCATION<br/>
 * permission:android.permission.ACCESS_COARSE_LOCATION
 *
 * <li><strong>group:android.permission-group.STORAGE</strong><br/>
 * permission:android.permission.READ_EXTERNAL_STORAGE<br/>
 * permission:android.permission.WRITE_EXTERNAL_STORAGE
 *
 * <li><strong>group:android.permission-group.MICROPHONE</strong><br/>
 * permission:android.permission.RECORD_AUDIO
 *
 * <li><strong>group:android.permission-group.SMS</strong><br/>
 * permission:android.permission.READ_SMS<br/>
 * permission:android.permission.RECEIVE_WAP_PUSH<br/>
 * permission:android.permission.RECEIVE_MMS<br/>
 * permission:android.permission.RECEIVE_SMS<br/>
 * permission:android.permission.SEND_SMS<br/>
 * permission:android.permission.READ_CELL_BROADCASTS
 * </ul>
 * <p/>
 * Created by lizhaofei on 2018/3/12 15:14
 */
public class Permission {
    public static final int REQUEST_PERMISSION_SETTING = 65535;//打开设置页必须用这个 作为 RequestCode （不能大于65536，也不能为负数）

    public static void requestPermissions(Activity activity, String[] permissions, Callback callback, IamUI iamUI) {
        sRequestPermissions.requestPermissions(activity, 1, permissions, callback, iamUI);
    }

    /** {@link ManagerPermissions#requestPermissions(Object, int, String[], Callback, IamUI)} */
    public static void requestPermissions(Activity activity, int requestCode, String[] permissions,
                                          Callback callback, IamUI iamUI) {
        sRequestPermissions.requestPermissions(activity, requestCode, permissions, callback, iamUI);
    }

    /** {@link ManagerPermissions#requestPermissions(Object, int, String[], Callback, IamUI)} */
    public static void requestPermissions(Fragment fragment, int requestCode, String[] permissions, Callback callback, IamUI iamUI) {
        //fixme lzf 目前采用Fragment请求有一个问题：Activity中有多个Fragment(A,B,C)，使用C Fragment来请求，但是结果回调会回调成最先添加的那个Fragment(A)，所以这里先把Fragment请求全部转换成其所在的Activity请求
        //sRequestPermissions.requestPermissions(fragment, requestCode, permissions, callback, iamUI);
        if(null == fragment || null == fragment.getActivity()){
            return;
        }
        sRequestPermissions.requestPermissions(fragment.getActivity(), requestCode, permissions, callback, iamUI);
    }

    /** {@link ManagerPermissions#requestPermissions(Object, int, String[], Callback, IamUI)} */
    public static void requestPermissions(Fragment fragment, String[] permissions, Callback callback, IamUI iamUI) {
        requestPermissions(fragment, 1, permissions,callback,iamUI);
    }

    /** {@link ManagerPermissions#requestPermissions(Object, int, String[], Callback, IamUI)} */
    public static void requestPermissions(Activity activity, int requestCode, String[] permissions, Callback callback) {
        sRequestPermissions.requestPermissions(activity, requestCode, permissions, callback, null);
    }

    /** {@link ManagerPermissions#requestPermissions(Object, int, String[], Callback, IamUI)} */
    public static void requestPermissions(Activity activity, String[] permissions, Callback callback) {
        sRequestPermissions.requestPermissions(activity, 1, permissions, callback, null);
    }

    /** {@link ManagerPermissions#requestPermissions(Object, int, String[], Callback, IamUI)} */
    public static void requestPermissions(Fragment fragment, int requestCode, String[] permissions,
            Callback callback) {
        sRequestPermissions.requestPermissions(fragment, requestCode, permissions, callback, null);
    }

    /** {@link ManagerPermissions#requestPermissions(Object, int, String[], Callback, IamUI)} */
    public static void requestPermissions(Fragment fragment, String[] permissions, Callback callback) {
        sRequestPermissions.requestPermissions(fragment, 1, permissions, callback, null);
    }

    /** 检测是否已经拥有了权限，只要有一项没有拥有就返回false */
    public static boolean checkSelfPermission(Context context, String[] permissions) {
        List<String> denied = sRequestPermissions.checkSelfPermission(context, permissions);
        return null == denied || denied.size() == 0;
    }

    /** 只能在权限申请的结果中调用，其它地方调用不保证结果正确 */
    public static boolean checkDeniedAndNoShow(Activity activity, String permission) {
        return sRequestPermissions.checkDeniedAndNoShow(activity, permission);
    }

    public static void onRequestPermissionResult(Fragment fragment, int requestCode,
                                                 String[] permissions, int[] grantResults) {
        sRequestPermissions.onRequestPermissionsResult(fragment, requestCode, permissions,
                grantResults);
    }

    public static void onRequestPermissionResult(Activity activity, int requestCode,
                                                 String[] permissions, int[] grantResults) {
        sRequestPermissions.onRequestPermissionsResult(activity, requestCode, permissions,
                grantResults);
    }

    public static void onActivityDestroy(Activity activity) {
        sRequestPermissions.onActivityOrFragmentDestroy(activity);
    }

    public static void onFragmentDestroy(Fragment fragment) {
        sRequestPermissions.onActivityOrFragmentDestroy(fragment);
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode,
                                        Intent data) {
        sRequestPermissions.onActivityResult(activity, requestCode, resultCode, data);
    }

    public static void onActivityResult(Fragment fragment, int requestCode, int resultCode,
            Intent data) {
        sRequestPermissions.onActivityResult(fragment, requestCode, resultCode, data);
    }

    private static final ManagerPermissions sRequestPermissions = findRequestPermission();

    private static ManagerPermissions findRequestPermission() {
        ManagerPermissions result = new ManagerPermissions();
        ManagerPermissions.PlatformAdapter adapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            adapter = new MAdapter(result);
        } else {
            adapter = new BMAdapter(result);
            //adapter = new EmptyAdapter();
        }
        result.setPlatformAdapter(adapter);
        return result;
    }
}
