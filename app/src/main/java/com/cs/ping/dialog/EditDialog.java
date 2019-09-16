package com.cs.ping.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.cs.ping.R;

public class EditDialog extends Dialog {
    private OnEditDialogClickListener onEditDialogClickListener;
    private String title="标题";
    private String subTitle="子标题";
    private String editHint = "请输入内容";
    private TextView mTitle;
    private TextView mSubTitle;
    private EditText mEt;


    public EditDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.edit_dialog,null);
        setContentView(view);
        mTitle = view.findViewById(R.id.tv_title);
        mSubTitle = view.findViewById(R.id.tv_hint);
        mEt = view.findViewById(R.id.et_content);
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onEditDialogClickListener != null){
                    onEditDialogClickListener.onNegativeClick(view);
                }
                dismiss();
            }
        });
        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onEditDialogClickListener != null){
                    String content = mEt.getText().toString().trim();
                    onEditDialogClickListener.onPositiveClick(view,content);
                }
                dismiss();
            }
        });


    }

    public void setOnEditDialogClickListener(OnEditDialogClickListener onEditDialogClickListener) {
        this.onEditDialogClickListener = onEditDialogClickListener;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setEditHint(String editHint) {
        this.editHint = editHint;
    }


    public interface OnEditDialogClickListener{
        void onNegativeClick(View view);
        void onPositiveClick(View view,String content);
    }

    public void showDialog(){
        mTitle.setText(title);
        mSubTitle.setText(subTitle);
        mEt.setHint(editHint);

        show();
    }

}
