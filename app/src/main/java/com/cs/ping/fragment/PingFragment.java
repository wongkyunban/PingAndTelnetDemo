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

import android.os.FileUtils;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cs.ping.PingApplication;
import com.cs.ping.R;
import com.cs.ping.SharedKey;
import com.cs.ping.adapter.PingAdapter;
import com.cs.ping.event.CloseFloatingButtonEvent;
import com.cs.ping.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 */
public class PingFragment extends Fragment {


    private RecyclerView mRecylerView;
    private PingAdapter pingAdapter;
    private PingHandler pingHandler;
    private EditText mEtInputNewIp;
    private ExecutorService executorService;
    private ImageButton mIbMenu;
    private Placeholder mCopyHolder;
    private Placeholder mSaveHolder;
    private Placeholder mClearHolder;
    private Group mMenuGroup;

    public PingFragment() {
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
        View view = inflater.inflate(R.layout.fragment_ping, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mRecylerView = view.findViewById(R.id.rv_cycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecylerView.setLayoutManager(layoutManager);
        pingAdapter = new PingAdapter();
        mRecylerView.setAdapter(pingAdapter);
        pingHandler = new PingHandler(this);

        mEtInputNewIp = view.findViewById(R.id.et_input_ip);
        String defaultAddr = PingApplication.getShared().getString(SharedKey.PING_DEFAULT_ADDRESS, "www.baidu.com");
        mEtInputNewIp.setText(defaultAddr);
        mCopyHolder = view.findViewById(R.id.pl_copy_holder);
        mSaveHolder = view.findViewById(R.id.pl_save_holder);
        mClearHolder = view.findViewById(R.id.pl_clear_holder);
        mMenuGroup = view.findViewById(R.id.g_menu_group);

        mIbMenu = view.findViewById(R.id.ib_menu);
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


        view.findViewById(R.id.cl_menu_containter).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int id = view.getId();
                if(R.id.ib_menu != id){
                    viewGone();
                }
                return false;
            }
        });



        view.findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewGone();
                pingAdapter.clear();
            }
        });

        view.findViewById(R.id.tv_copy).setOnClickListener(new View.OnClickListener() {
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
                    String data = pingAdapter.getStringList().toString();
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

        view.findViewById(R.id.tv_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkStoragePermission();
                String data = pingAdapter.getStringList().toString();
                String fileName = "ping_" + new Date().getTime() + ".txt";
                FileUtil fileUtil = new FileUtil();
                File file = fileUtil.append2File(fileUtil.getStoragePath() + "/CS_PING/", fileName, data);

                if (file.exists()) {
                    Toast.makeText(getActivity(), "保存成功！\n\n保存路径：" + fileUtil.getStoragePath(),
                            Toast.LENGTH_LONG).show();
                }
                viewGone();
            }
        });

        view.findViewById(R.id.btn_ping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pingAdapter.clear();
                String ip = mEtInputNewIp.getText().toString();
                int count = PingApplication.getShared().getInt(SharedKey.PING_COUNT, 4);
                int size = PingApplication.getShared().getInt(SharedKey.PING_PACKAGE_SIZE, 64);
                int interval = PingApplication.getShared().getInt(SharedKey.PING_PACKAGE_INTERVAL, 1);
                String countCmd = " -c " + count + " ";
                String sizeCmd = " -s " + size + " ";
                String timeCmd = " -i " + interval + " ";
                String ping = "ping" + countCmd + timeCmd + sizeCmd + ip;
                executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Thread(new PingTask(ping, pingHandler, interval)));

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
        mCopyHolder.setContentId(R.id.tv_copy);
        mSaveHolder.setContentId(R.id.tv_save);
        mClearHolder.setContentId(R.id.tv_clear);
    }

    private static class PingHandler extends Handler {
        private WeakReference<PingFragment> weakReference;

        public PingHandler(PingFragment fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 10:
                    String resultMsg = (String) msg.obj;
                    weakReference.get().pingAdapter.addString(resultMsg);
                    weakReference.get().mRecylerView.scrollToPosition(weakReference.get().pingAdapter.getItemCount() - 1);
                    break;
                default:
                    break;
            }
        }
    }

    // 创建ping任务
    private class PingTask implements Runnable {
        private String ping;
        private PingHandler pingHandler;
        private long delay;

        public PingTask(String ping, PingHandler pingHandler, long delay) {
            this.ping = ping;
            this.pingHandler = pingHandler;
            this.delay = delay;
        }

        @Override
        public void run() {
            Process process = null;
            BufferedReader successReader = null;
            BufferedReader errorReader = null;
            try {
                process = Runtime.getRuntime().exec(ping);
                // success
                successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                // error
                errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String lineStr;

                while ((lineStr = successReader.readLine()) != null) {

                    // receive
                    Message msg = pingHandler.obtainMessage();
                    msg.obj = lineStr + "\r\n";
                    msg.what = 10;
                    msg.sendToTarget();
                }
                while ((lineStr = errorReader.readLine()) != null) {

                    // receive
                    Message msg = pingHandler.obtainMessage();
                    msg.obj = lineStr + "\r\n";
                    msg.what = 10;
                    msg.sendToTarget();
                }
                Thread.sleep(delay * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {

                    if (successReader != null) {
                        successReader.close();
                    }
                    if (errorReader != null) {
                        errorReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (process != null) {
                    process.destroy();
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (pingHandler != null) {
            pingHandler.removeCallbacksAndMessages(null);
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
