package com.ztgame.xgltw;

import android.app.AlertDialog;
import android.content.DialogInterface;
import com.tommy.ios.map05.UnityPlayerNativeActivity;
import com.tencent.bugly.crashreport.CrashReport;
import android.os.Bundle;
import android.util.Log;

import com.baplay.core.tools.BaplayLogUtil;
import com.baplay.payPageClose.PayPageCloseListener;
import com.baplay.platform.login.comm.bean.LoginParameters;
import com.baplay.platform.login.comm.callback.OnBaplayLoginListener;
import com.baplay.platform.login.comm.utils.BaplayLoginHelper;
import com.baplay.tc.entrance.BaplayPlatform;
import com.baplay.tc.identification.ChannelType;
import com.unity3d.player.UnityPlayer;

public class MainActivity extends UnityPlayerNativeActivity  implements PayPageCloseListener {

    private String _accid;
    private String _zoneID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        BaplayLogUtil.enableDebug(true);
        BaplayLogUtil.enableInfo(true);
        BaplayPlatform.getInstance().init(this);
        CrashReport.initCrashReport(getApplicationContext(), "900029763", true);

        //設定支付頁關閉監聽
        BaplayPlatform.getInstance().setPayPageCloseListener(MainActivity.this, MainActivity.this);

        BaplayPlatform.getInstance().baplaySetIdentification(this, ChannelType.Efun_Google);
        //login();
    }
    public void checkActive()
    {
        UnityPlayer.UnitySendMessage("UI_LOGIN_FIRST(Clone)","RealLogin","");
    }
    public void initGALogin(String str)
    {
        String[] arr = str.split("\\|");
        _zoneID = arr[0];

        login();
    }
    private void login() {

        boolean isShowAnnouncement = true;// 是否显示公告，请在用户启动游戏的登录时传true；当次游戏内其他情况的登录传false(如切换服务器、账号)

        BaplayPlatform.getInstance().baplayLogin(this, isShowAnnouncement, new OnBaplayLoginListener() {
            @Override
            public void onFinishLoginProcess(LoginParameters params) {
                // 登陆成功
                if (BaplayLoginHelper.ReturnCode.RETURN_SUCCESS.equals(params.getCode()) || BaplayLoginHelper.ReturnCode.ALREADY_EXIST.equals(params.getCode())) {
                    String sign = params.getSign();
                    String userid = params.getUserId() + "";
                    String timps = params.getTimestamp() + "";
                    _accid = userid;
                    if (vervify(sign, userid, timps)) {// 厂商登录验证，验证成功才继续登入操作
//                        BaplayLogUtil.logI("登錄成功： " + params.getUserId() + "");
//                        Toast.makeText(MainActivity.this,"登錄成功", Toast.LENGTH_LONG).show();
//
//                        // 启动平台
//                        String serverCode = "1";//服务器code
//                        String roleId = "79963"; //角色id
//                        String level = "1";//角色等级;请在上传google的包传入实际角色等级，不上传google的包传入"";
//                        String appName = "六扇門";
//                        String roleName = "Tina";//角色名稱
//                        BaplayPlatform.getInstance().baplayCreateFloatView(MainActivity.this, userid, serverCode, roleId, level, appName, roleName);
                        //pay();
                        String str = "GL_TW"+"|57-"+userid+"|";
                        UnityPlayer.UnitySendMessage("UI_LOGIN_FIRST(Clone)","GALogin",str);
                        UnityPlayer.UnitySendMessage("UI_LOGIN_FIRST(Clone)","ShowNormalButton","");
                    }
                    // 重写返回事件
                } else if (BaplayLoginHelper.ReturnCode.LOGIN_BACK.equals(params.getCode())) {

                    Log.i("baplayLog", "按下返回键");
                    AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                    // ab.setIcon(R.drawable.com_facebook_button_check);
                    ab.setTitle("新古龍群俠傳");
                    ab.setCancelable(false);
                    ab.setMessage("你確定要退出新古龍群俠傳嗎?");
                    ab.setPositiveButton("確認", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            finish();
                        }
                    }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            paramDialogInterface.dismiss();
                            login();
                        }
                    });
                    ab.create().show();

                }

            }
        });
    }
    public void TDReg(String str)
    {

    }

    public void TDLogin(String str)
    {

    }
    public void loginOKZTGame(String str)
    {

    }
    /**
     * 账号登陆信息成功后需要厂商在服务器端进行加密验证，规则为：sign=md5(serverkey + userid +
     * timestamp)，使用此规则生成的sign与客户端返回的sign相同则可认定是真实玩家登入。
     */
    protected boolean vervify(String sign, String userid, String timps) {
        // 模拟厂商验证过程
        String str = MD5.md5("40A26A1FC43938E03E0C82F9854BFEAF"+userid+timps);
        str = str.toUpperCase();

        return str.equals(sign);

    }



    public void payMoney(String payStr) {
        String userid = _accid;// 登录成功得到的userid
        String creditid = payStr;// 角色id
        String serverCode = _zoneID;// 服务器code
        String roleLevel = "1";// 角色等级
        String roleName = "";// 角色名
        String remark = "";// 自定义数据串（选填）
        String roleId = payStr; // 角色id

        BaplayPlatform.getInstance().baplayGooglePlay(MainActivity.this, userid, creditid, serverCode, roleName, roleLevel, remark, roleId);
    }

    //通知支付頁面關閉, 用戶儲值程序完成或手動關閉儲值頁面
    @Override
    public void payPageClose() {
        Log.v("baplay","payPageClose~~~~~~~~~~~~~~~~~~~~~");
    }
}
