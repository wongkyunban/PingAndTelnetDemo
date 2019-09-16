package com.cs.ping.fragment;


import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.constraintlayout.widget.Placeholder;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cs.ping.R;
import com.cs.ping.adapter.PingAdapter;
import com.cs.ping.adapter.TelnetAdapter;
import com.cs.ping.event.CloseFloatingButtonEvent;
import com.cs.ping.utils.FileUtil;

import org.apache.commons.net.telnet.TelnetClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 */
public class TelnetFragment extends Fragment {


    private EditText mEtIp;
    private EditText mEtPort;
    private Button mBtnTelnet;
    private TelnetHandler telnetHandler;
    private ExecutorService executorService;
    private RecyclerView mRecylerView;
    private TelnetAdapter telnetAdapter;
    private ImageButton mIbMenu;
    private Placeholder mCopyHolder;
    private Placeholder mSaveHolder;
    private Placeholder mClearHolder;
    private Group mMenuGroup;

    public TelnetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_telnet, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        mEtIp = view.findViewById(R.id.et_input_domain_ip);
        mEtPort = view.findViewById(R.id.et_port);
        mBtnTelnet = view.findViewById(R.id.btn_telnet);

        mRecylerView = view.findViewById(R.id.rv_telnet_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecylerView.setLayoutManager(layoutManager);
        telnetAdapter = new TelnetAdapter();
        mRecylerView.setAdapter(telnetAdapter);
        telnetHandler = new TelnetHandler(this);

        mBtnTelnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = mEtIp.getText().toString();
                telnetAdapter.clear();
                if (!TextUtils.isEmpty(ip)) {
                    int port = Integer.valueOf(mEtPort.getText().toString());
                    port = port == 0 ? 23 : port;
                    executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(new Thread(new TelnetTask(ip, port, telnetHandler)));
                } else {
                    Toast.makeText(getActivity(), "请输入IP或域名", Toast.LENGTH_LONG).show();
                }
            }
        });

        mCopyHolder = view.findViewById(R.id.pl_copy_holder_telnet);
        mSaveHolder = view.findViewById(R.id.pl_save_holder_telnet);
        mClearHolder = view.findViewById(R.id.pl_clear_holder_telnet);
        mMenuGroup = view.findViewById(R.id.g_menu_group_telnet);

        mIbMenu = view.findViewById(R.id.ib_menu_telnet);
        mIbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMenuGroup.getVisibility() == View.GONE) {
                    viewVisible();
                } else {
                    viewGone();

                }
            }
        });


        view.findViewById(R.id.cl_menu_telnet_containter).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int id = view.getId();
                if(R.id.ib_menu != id){
                    viewGone();
                }
                return false;
            }
        });



        view.findViewById(R.id.tv_clear_telnet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewGone();
                telnetAdapter.clear();
            }
        });

        view.findViewById(R.id.tv_copy_telnet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    // 获取系统剪贴板
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）,其他的还有
                    // newHtmlText、
                    // newIntent、
                    // newUri、
                    // newRawUri
                    String data = telnetAdapter.getStringList().toString();
                    ClipData clipData = ClipData.newPlainText(null, data);
                    // 把数据集设置（复制）到剪贴板
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clipData);
                        Toast.makeText(getActivity(), "复制成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "复制失败！", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });

        view.findViewById(R.id.tv_save_telnet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkStoragePermission();
                String data = telnetAdapter.getStringList().toString();
                String fileName = "telnet_" + new Date().getTime() + ".txt";
                FileUtil fileUtil = new FileUtil();
                File file = fileUtil.append2File(fileUtil.getStoragePath() + "/CS_TELNET/", fileName, data);

                if (file.exists()) {
                    Toast.makeText(getActivity(), "保存成功！\n\n保存路径：" + fileUtil.getStoragePath(),
                            Toast.LENGTH_LONG).show();
                }
                viewGone();
            }
        });
    }


    private void checkStoragePermission(){
        if(getActivity() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,1000);
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1000) {
            for (int per : grantResults) {
                if (per == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getActivity(), "权限申请失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    private void viewGone() {
        mMenuGroup.setVisibility(View.GONE);
        mCopyHolder.setContentId(0);
        mSaveHolder.setContentId(0);
        mClearHolder.setContentId(0);
    }

    private void viewVisible() {
        mMenuGroup.setVisibility(View.VISIBLE);
        mCopyHolder.setContentId(R.id.tv_copy_telnet);
        mSaveHolder.setContentId(R.id.tv_save_telnet);
        mClearHolder.setContentId(R.id.tv_clear_telnet);
    }
    private static class TelnetHandler extends Handler {
        private WeakReference<TelnetFragment> weakReference;

        public TelnetHandler(TelnetFragment fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 10:
                    String resultMsg = (String) msg.obj;
                    weakReference.get().telnetAdapter.addString(resultMsg);
                    weakReference.get().mRecylerView.scrollToPosition(weakReference.get().telnetAdapter.getItemCount() - 1);
                    break;
                default:
                    break;
            }
        }
    }

    // 创建telnet任务
    private class TelnetTask implements Runnable {
        private String ip;
        private int port;
        private TelnetHandler telnetHandler;

        public TelnetTask(String ip, int port, TelnetHandler telnetHandler) {
            this.ip = ip;
            this.port = port;
            this.telnetHandler = telnetHandler;
        }

        @Override
        public void run() {
            TelnetClient telnet = new TelnetClient();
            BufferedReader infoReader = null;

            try {
                if (!TextUtils.isEmpty(ip)) {
                    port = port == 0 ? 23 : port;
                    telnet.connect(ip, port);
                }
                infoReader = new BufferedReader(new InputStreamReader(telnet.getInputStream()));
                String lineStr;


                while ((lineStr = infoReader.readLine()) != null) {

                    // receive
                    Message msg = telnetHandler.obtainMessage();
                    msg.obj = lineStr;
                    msg.what = 10;
                    msg.sendToTarget();
                }


            } catch (IOException e) {
                e.printStackTrace();
                Message msg = telnetHandler.obtainMessage();
                msg.obj = e.getMessage();
                msg.what = 10;
                msg.sendToTarget();

            } finally {

                try {
                    if (infoReader != null) {
                        infoReader.close();
                    }

                    telnet.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (telnetHandler != null) {
            telnetHandler.removeCallbacksAndMessages(null);
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }
        super.onDestroy();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleData(CloseFloatingButtonEvent event){
        viewGone();
    }
}
