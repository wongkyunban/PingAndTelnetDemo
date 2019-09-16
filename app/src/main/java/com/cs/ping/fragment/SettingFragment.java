package com.cs.ping.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cs.ping.PingApplication;
import com.cs.ping.R;
import com.cs.ping.SharedKey;
import com.cs.ping.dialog.EditDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {


    private EditDialog mEditDialog;
    private TextView mTvDefaultAddr;
    private EditText mEditPackageSize;
    private EditText mEditBox;
    private EditText mEditPackageInterval;
    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        init(view);

        return view;
    }

    private void init(View view){
        mTvDefaultAddr = view.findViewById(R.id.tv_default_addr_);
        mEditPackageSize = view.findViewById(R.id.et_package_size);
        int packageSize = PingApplication.getShared().getInt(SharedKey.PING_PACKAGE_SIZE,64);
        mEditPackageSize.setText(String.valueOf(packageSize));
        final String defaultAddr =PingApplication.getShared().getString(SharedKey.PING_DEFAULT_ADDRESS,"www.baidu.com");
        mTvDefaultAddr.setText(defaultAddr);
        mEditPackageInterval = view.findViewById(R.id.et_package_interval);
        int packageInterval = PingApplication.getShared().getInt(SharedKey.PING_PACKAGE_INTERVAL,1);
        mEditPackageInterval.setText(String.valueOf(packageInterval));


        view.findViewById(R.id.tv_default_addr_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() != null) {
                    mEditDialog = new EditDialog(getActivity());
                    mEditDialog.setTitle("添加地址");
                    mEditDialog.setSubTitle("请输入默认地址");
                    mEditDialog.setEditHint("");
                    mEditDialog.setOnEditDialogClickListener(new EditDialog.OnEditDialogClickListener() {
                        @Override
                        public void onNegativeClick(View view) {

                        }

                        @Override
                        public void onPositiveClick(View view, String content) {
                            // TODO 加上地址验证更好
                            if(!TextUtils.isEmpty(content)){
                                PingApplication.getShared().edit().putString(SharedKey.PING_DEFAULT_ADDRESS,content).apply();
                                mTvDefaultAddr.setText(content);

                            }

                        }
                    });
                    mEditDialog.showDialog();
                }
            }
        });
        mEditBox = view.findViewById(R.id.et_execute_count);
        int defaultCount =PingApplication.getShared().getInt(SharedKey.PING_COUNT,4);

        mEditBox.setText(String.valueOf(defaultCount));
        mEditBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String number = editable.toString();
                if(!TextUtils.isEmpty(number) && !"0".equals(number)) {
                    PingApplication.getShared().edit().putInt(SharedKey.PING_COUNT, Integer.valueOf(number)).apply();
                }

            }
        });

        mEditPackageSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String number = editable.toString();
                if(!TextUtils.isEmpty(number) && !"0".equals(number)) {
                    PingApplication.getShared().edit().putInt(SharedKey.PING_PACKAGE_SIZE, Integer.valueOf(number)).apply();
                }
            }
        });

        mEditPackageInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String number = editable.toString();
                if(!TextUtils.isEmpty(number) && !"0".equals(number)) {
                    PingApplication.getShared().edit().putInt(SharedKey.PING_PACKAGE_INTERVAL, Integer.valueOf(number)).apply();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mEditDialog != null){
            mEditDialog.dismiss();
        }
    }
}
