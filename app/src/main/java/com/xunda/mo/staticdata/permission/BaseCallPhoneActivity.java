package com.xunda.mo.staticdata.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.xunda.mo.R;
import com.xunda.mo.dialog.PermissionApplyDialog;
import com.xunda.mo.dialog.TwoButtonDialog;
import com.xunda.mo.hx.section.base.BaseActivity;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 带打电话的基类Activity
 */

public abstract class BaseCallPhoneActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    public static final String[] PERMS_CALL_PHONE = {Manifest.permission.CALL_PHONE};//拨打电话
    public static final int PERMISSION_CALL_PHONE_CODE = 114;
    private static final int APP_SETTINGS = 100;
    private String phone;

    /**
     * 拨打电话
     */
    protected void callPhone(String phone) {
        this.phone = phone;
        showCallDialog();
    }


    /**
     * 拨打电话dialog
     */
    private void showCallDialog() {
        TwoButtonDialog dialog = new TwoButtonDialog(this, phone, getString(R.string.Cancel), "呼叫",
                new TwoButtonDialog.ConfirmListener() {

                    @Override
                    public void onClickRight() {
                        checkCallPermission();
                    }

                    @Override
                    public void onClickLeft() {

                    }
                });
        dialog.show();

    }


    /**
     * 检查电话权限
     */
    private void checkCallPermission() {
        boolean result = PermissionManager.checkPermission(this, PERMS_CALL_PHONE);//检查拨打电话权限
        if (result) {
            startCall();
        } else {
            PermissionManager.requestPermission(this, getString(R.string.permission_call_phone_tip), PERMISSION_CALL_PHONE_CODE, PERMS_CALL_PHONE);
        }
    }


    /**
     * 开始呼叫
     */

    @AfterPermissionGranted(PERMISSION_CALL_PHONE_CODE)
    private void startCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phone);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        intent.setData(data);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 请求权限成功
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    /**
     * 请求权限失败
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        /**
         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            showPermissionApplyDialog(getString(R.string.permission_write_denied_call_phone), true);
        } else {
            switch (requestCode) {
                case PERMISSION_CALL_PHONE_CODE:
                    showPermissionApplyDialog(getString(R.string.permission_apply_call_phone_reason), false);
                    break;
            }
        }
    }


    /**
     * 被拒绝后的权限申请引导弹框
     */
    private void showPermissionApplyDialog(String reason_content, final boolean isNotAskAgain) {
        PermissionApplyDialog mPermissionApplyDialog = new PermissionApplyDialog(this, reason_content, isNotAskAgain, new PermissionApplyDialog.OnJumpToSettingListener() {
            @Override
            public void clickRightButton() {
                if (isNotAskAgain) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.fromParts("package", getPackageName(), null));
                    startActivityForResult(intent, APP_SETTINGS);
                } else {
                    checkCallPermission();
                }
            }
        });

        mPermissionApplyDialog.show();
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == APP_SETTINGS) {
            checkCallPermission();
        }
    }


}
