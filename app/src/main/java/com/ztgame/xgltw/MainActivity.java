package com.ztgame.xgltw;

import android.app.AlertDialog;
import android.content.DialogInterface;
import com.tommy.ios.map05.UnityPlayerNativeActivity;
import com.tencent.bugly.crashreport.CrashReport;
import com.youan.voicechat.vcinterface.VoiceChatInterface;
import com.youan.voicechat.callback.PlayCallBackListener;
import com.youan.voicechat.callback.SendCallBackListener;
import com.youan.voicechat.callback.TimeOutCallBackListsener;
import com.youan.voicechat.callback.VolumeCallBackListener;
import com.youan.voicechat.contants.StatusCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import java.util.ArrayList;
import android.Manifest;
import com.baplay.core.tools.BaplayLogUtil;
import com.baplay.payPageClose.PayPageCloseListener;
import com.baplay.platform.login.comm.bean.LoginParameters;
import com.baplay.platform.login.comm.callback.OnBaplayLoginListener;
import com.baplay.platform.login.comm.utils.BaplayLoginHelper;
import com.baplay.tc.entrance.BaplayPlatform;
import com.baplay.tc.identification.ChannelType;
import com.unity3d.player.UnityPlayer;

public class MainActivity extends UnityPlayerNativeActivity  implements PayPageCloseListener {

    private static final int CODE_FOR_RECORD_AUDIO = 0;
    private String _accid;
    private String _zoneID;

    private long voiceID;
    private String isSendAll;
    private int voiceTime;
    private String textStr;
    private Boolean isChanging = false;
    private ArrayList<String> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        BaplayLogUtil.enableDebug(true);
        BaplayLogUtil.enableInfo(true);
        BaplayPlatform.getInstance().init(this);
        CrashReport.initCrashReport(this, "900051837", true);

        //設定支付頁關閉監聽
        BaplayPlatform.getInstance().setPayPageCloseListener(MainActivity.this, MainActivity.this);
        BaplayPlatform.getInstance().baplaySetIdentification(this, ChannelType.Efun_Google);
        BaplayLogUtil.enableDebug(true);
        BaplayLogUtil.enableInfo(true);
        VoiceChatInterface.initWithGameId(this, 10018, 100, 0);
        VoiceChatInterface.setLongRecordTime(10);
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

    public void onStart()
    {
        super.onStart();
        BaplayPlatform.getInstance().baplayOnStart(this);
    }

    public void onStop()
    {
        super.onStop();
        BaplayPlatform.getInstance().baplayOnStop(this);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        BaplayPlatform.getInstance().baplayOnDestroy(this);
    }

    public void onResume()
    {
        super.onResume();
        BaplayPlatform.getInstance().baplayOnResume(this);
    }

    public void onPause()
    {
        super.onResume();
        BaplayPlatform.getInstance().baplayOnPause(this);
    }

    public void ShowFloatView(final String info)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.v("ShowFloatView", info);
                String[] arr = info.split("|");
                String serverCode = _zoneID;//服务器code
                String roleId = arr[0]; //角色id
                String level = arr[1];//角色等级;请在上传google的包传入实际角色等级，不上传google的包传入"";
                String appName = "新古龍群俠傳";
                String roleName = arr[2];//角色名稱
                BaplayPlatform.getInstance().baplayCreateFloatView(MainActivity.this, _accid, serverCode, roleId, level, appName, roleName);
            }
        });
    }

    public void HideFloatView()
    {
        Log.v("HideFloatView", "_accid---------" + _accid);
        BaplayPlatform.getInstance().hideFloatView(MainActivity.this);
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

    public void payMoney(String payStr)
    {
        String[] arr = payStr.split("|");
        String userid = _accid;// 登录成功得到的userid
        String creditid = arr[0];// 角色id
        String serverCode = _zoneID;// 服务器code
        String roleLevel = arr[1];// 角色等级
        String roleName = "";// 角色名
        String remark = "";// 自定义数据串（选填）
        String roleId = arr[0]; // 角色id

        BaplayPlatform.getInstance().baplayGooglePlay(MainActivity.this, userid, creditid, serverCode, roleName, roleLevel, remark, roleId);
    }

    //通知支付頁面關閉, 用戶儲值程序完成或手動關閉儲值頁面
    @Override
    public void payPageClose() {
        Log.v("baplay","payPageClose~~~~~~~~~~~~~~~~~~~~~");
    }


    //语音-------------------
    private boolean checkPermission(String permission)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            int targetSdkVersion = 23;
            try
            {
                final PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
                targetSdkVersion = info.applicationInfo.targetSdkVersion;
            } catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }
            if (targetSdkVersion >= Build.VERSION_CODES.M)
                return this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            else
                return PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED;
        }
        return true;
    }

    public boolean recordAudioIsPermission()
    {
        boolean recordAudioIsPermission = checkPermission(Manifest.permission.RECORD_AUDIO);
        if (recordAudioIsPermission == false)
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO }, CODE_FOR_RECORD_AUDIO);
        return recordAudioIsPermission;
    }

    public void beginRecord()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.v("RECORD_AUDIO","beginRecord------------");
                VoiceChatInterface.beginRecordingOnMoreThan10s(timeoutCallBack, volumeCallBack);
            }
        });
    }

    public void cancelRecord()
    {
        if (checkPermission(Manifest.permission.RECORD_AUDIO))
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.v("RECORD_AUDIO","cancel record-------");
                    VoiceChatInterface.cancelRecordingOnCompletion();
                }
            });
        }
    }
    //��ʱ�ص�
    TimeOutCallBackListsener timeoutCallBack = new TimeOutCallBackListsener() {
        @Override
        public void onTimeUp(int message) {
            if (StatusCode.RECORD_TIMEUP == message) {
                //VoiceChatLog.e("�ﵽ20��¼�������ʱ�䣬��������ֶ�����-�ɿ����ͽӿ�");
                //Toast.makeText(UnityPlayerNativeActivity.this, "�ﵽ10��¼�����ƣ�ϵͳ�Զ�����¼��", Toast.LENGTH_SHORT).show();
                //releaseSend();
                //��CALL UNITY����UNITY����ʱ���ִ��?�ٵ��÷���
                UnityPlayer.UnitySendMessage("VoiceObj", "RecordTimeOut","");
                sendRecord();
            }
        }
        @Override
        public void onTimeRemain(int time) {
            //VoiceChatLog.e("�������¼��ʱ������(20s)��ʣ:" + time + "��");
        }

        public void onStart()
        {

        }
    };

    //¼��������С�ص�
    VolumeCallBackListener volumeCallBack = new VolumeCallBackListener() {
        @Override
        public void onVolume(double sound)
        {
            //VoiceChatLog.e("¼��������С��" + sound);
            UnityPlayer.UnitySendMessage("VoiceObj", "VoiceVolume",String.valueOf((int)sound));
        }
    };
    //
    //
    //
    public void sendRecord()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.v("RECORD_AUDIO","java send record");
                VoiceChatInterface.sendAudioOnCompletion(sendCallBack, null);
            }
        });

    }
    //���ͻص�
    SendCallBackListener sendCallBack = new SendCallBackListener()
    {
        @Override
        public void onSend(int statusCode, final Bundle params)
        {
            if (statusCode == StatusCode.SEND_SUCCESS && null != params)
            {
                Log.v("tag","voice id:"+params.getLong("id"));
                //Toast.makeText(UnityPlayerNativeActivity.this, "���ͳɹ����õ������ļ�id=" + params.getLong("id"), Toast.LENGTH_SHORT).show();
                //eidtText.setText(String.valueOf(params.getLong("id")));
                //textView.setText("¼��ʱ��:" + params.getInt("duration") + "��");
                voiceID = params.getLong("id");
                voiceTime = params.getInt("duration");
                if(voiceTime == 0)
                    voiceTime = 10;

                String str = String.valueOf(voiceID)+"|"+""+"|"+voiceTime;
                Log.v("tag", "call unity "+str);
                UnityPlayer.UnitySendMessage("VoiceObj", "RecordFinish",str);
                //
                textStr = "";
                //VoiceChatInterface.convertVoice2Word(voiceID, mRecognizerListener);
            }
            else
            {
                Log.v("tag","����ʧ�ܣ����ӷ�����ʧ��");
                //Toast.makeText(UnityPlayerNativeActivity.this, "����ʧ�ܣ����ӷ�����ʧ��", Toast.LENGTH_SHORT).show();
            }
        }

        public void onShort()
        {

        }
    };

    public RecognizerListener mRecognizerListener = new RecognizerListener()
    {

        @Override
        public void onBeginOfSpeech() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onVolumeChanged(int arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onError(SpeechError arg0) {
            // TODO Auto-generated method stub
            //Toast.makeText(UnityPlayerNativeActivity.this, arg0.getPlainDescription(true), Toast.LENGTH_SHORT).show();
            isChanging = false;
            checkList();
        }

        @Override
        public void onResult(RecognizerResult result, boolean arg1)
        {
            textStr += VoiceChatInterface.getRecognizerStr(result);
            Log.v("tag", arg1+""+textStr);
            //convertTextView.setText(convertTextView.getText() + VoiceChatInterface.getRecognizerStr(result));
            //Toast.makeText(MainActivity.this, printResult(result), Toast.LENGTH_LONG).show();
            if(arg1 == true)
            {
                String str = String.valueOf(voiceID)+"|"+textStr+"|"+isSendAll;

                Log.v("tag", "changed to text: "+str);
                UnityPlayer.UnitySendMessage("VoiceObj", "ShowVoiceText",str);
            }
        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            // TODO Auto-generated method stub

        }

    };

    public void playAudio(final String id)
    {
        if (recordAudioIsPermission())
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if(id == "")
                        return;
                    Log.v("tag","java play audio:"+id);
                    VoiceChatInterface.playAudio(Long.parseLong(id), playCallBack);
                }
            });
        }
    }

    public void stopPlayAudio()
    {
        if (checkPermission(Manifest.permission.RECORD_AUDIO) == false)
            return;
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.v("tag","java stop play audio");
                VoiceChatInterface.stopPlayingSound();
            }
        });

    }
    //id������ID��1��0��� xxxx|1   xxxx|0
    //1 0 ����Ƿ��������ת�������Ժ�Ҫ�ڿͻ��˴����㲥��������
    //1���㲥 0��?�㲥
    public void voice2Text(String id)
    {
        return;
//		idList.add(id);
//		checkList();
    }

    private void checkList()
    {
        if(isChanging)
            return;

        if(idList.size() == 0)
            return;

        isChanging = true;

        String str = idList.get(0);
        String[] arr = str.split("|");
        voiceID =  Long.parseLong(arr[0]);
        isSendAll = arr[1];

        textStr = "";

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.v("tag","voice changing to text");
                VoiceChatInterface.convertVoice2Word(voiceID, mRecognizerListener);
            }
        });
    }

    //���Żص�
    PlayCallBackListener playCallBack = new PlayCallBackListener() {
        @Override
        public void onEvent(int statusCode) {
            switch (statusCode) {
                case StatusCode.DOWNLOAD_SUCCESS:
                    //Toast.makeText(UnityPlayerNativeActivity.this, "����¼���ɹ�", Toast.LENGTH_SHORT).show();
                    break;
                case StatusCode.DOWNLOAD_FAILED:
                    //Toast.makeText(UnityPlayerNativeActivity.this, "����¼��ʧ�ܣ�����������ʧ��", Toast.LENGTH_SHORT).show();
                    UnityPlayer.UnitySendMessage("VoiceObj", "PlayFinish","");
                    break;
            }
        }
        @Override
        public void onCompleted() {
            //Toast.makeText(UnityPlayerNativeActivity.this, "�������", Toast.LENGTH_SHORT).show();
            UnityPlayer.UnitySendMessage("VoiceObj", "PlayFinish","");
        }
    };
}
