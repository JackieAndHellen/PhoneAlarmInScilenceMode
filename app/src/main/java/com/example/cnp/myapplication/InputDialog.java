/**
 * @ProjectName: LeChange
 * @Copyright: DahuaTech All Right Reserved.
 * @address: http://www.dahuatech.com/
 * @date: 2016年8月12日 上午10:18:13
 * @Description: 本内容仅限于大华集团内部使用，禁止转发.
 */
package com.example.cnp.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * <p>
 * </p>
 *
 * @author chen_nianpo 2016年8月12日 上午10:18:13
 * @version V2.0
 */
@SuppressLint("ValidFragment")
public class InputDialog extends DialogFragment {
    private DialogClickListener mDialogClickListener;

    private TextView mCancleTv, mConfirmTv, mMusicPathTv;

    private EditText mPhoneNumberEt, mNickNameEt;
    private Handler mHandler;
    private int mTitle;

    @SuppressLint("ValidFragment")
    public InputDialog(DialogClickListener clickListener) {
        super();
        mDialogClickListener = clickListener;
    }


    /**
     * 创建一个新的实例VideoEncryptInputDialog.
     */
    public InputDialog() {
        super();
    }

    private UserItem item;

    public void setInitData(final UserItem item, int position) {
        this.item = item;
        mPosition = position;
        if(mPosition ==  -1){
            return;
        }
        if(mHandler == null){
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mMusicPathTv != null)mMusicPathTv.setText(item != null ? item.musicPath : "");
                if(mNickNameEt != null) {
                    mNickNameEt.removeTextChangedListener(mTw);
                    mNickNameEt.setText(item != null ? item.userName : "",TextView.BufferType.EDITABLE);
                    mNickNameEt.addTextChangedListener(mTw);

                }
                if(mPhoneNumberEt != null){
                    mPhoneNumberEt.removeTextChangedListener(mTw);

                    mPhoneNumberEt.setText(item != null ? item.phoneNumber : "",TextView.BufferType.EDITABLE);
                    mPhoneNumberEt.addTextChangedListener(mTw);
                    mPhoneNumberEt.setSelection(mPhoneNumberEt.getText().toString().length()-1);

                }
                mConfirmTv.setEnabled(mPhoneNumberEt.getText().toString().length() == 11 && mNickNameEt.getText().toString().length() > 0);
                mConfirmTv.setTextColor(getResources().getColor(mConfirmTv.isEnabled()?android.R.color.black:android.R.color.darker_gray));

            }
        },100);

    }

    private int mPosition = -1;

    public int getPosition() {
        return mPosition;
    }


    public interface DialogClickListener {

        void cancel();

        void confirm();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style.checks_dialog);
    }

    @Override
    public void onResume() {
        super.onResume();

        translationUp();
    }

    private void translationUp() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            WindowManager.LayoutParams params = null;
            try {
                params = getDialog().getWindow().getAttributes();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (params != null) {
                DisplayMetrics pM = getResources().getDisplayMetrics();
                params.y = (int) -(100 * pM.density / 3.0f);

                int configure = getResources().getConfiguration().orientation;
                if (configure == Configuration.ORIENTATION_PORTRAIT) {
                    params.width = pM.widthPixels;
                } else if (configure == Configuration.ORIENTATION_LANDSCAPE) {
                    params.width = pM.widthPixels;
                } else {
                    params.width = pM.widthPixels;
                }


                getDialog().getWindow().setAttributes(params);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * android.support.v4.app.Fragment#onConfigurationChanged(android.content.res.Configuration)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        translationUp();
        super.onConfigurationChanged(newConfig);
    }
        private TextWatcher mTw = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("test", "onTextChanged: ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                mConfirmTv.setEnabled(mPhoneNumberEt.getText().toString().length() == 11 && mNickNameEt.getText().toString().length() > 0);
                mConfirmTv.setTextColor(getResources().getColor(mConfirmTv.isEnabled()?android.R.color.black:android.R.color.darker_gray));
            }
        };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_check_add_phone_number, container, false);

        if (mTitle != 0) {
            TextView title_tv = (TextView) view.findViewById(R.id.title);
            title_tv.setText(getResources().getString(mTitle));
        }

        mCancleTv = (TextView) view.findViewById(R.id.tv_cancel);
        mConfirmTv = (TextView) view.findViewById(R.id.tv_confirm);
        mPhoneNumberEt = (EditText) view.findViewById(R.id.et_user_input);

        mNickNameEt = (EditText) view.findViewById(R.id.et_user_nick);
        mMusicPathTv = (TextView) view.findViewById(R.id.tv_music_path);
        if (item != null) {
            mMusicPathTv.setText(item.musicPath);
            mNickNameEt.setText(item.userName,TextView.BufferType.EDITABLE);
            mPhoneNumberEt.setText(item.phoneNumber,TextView.BufferType.EDITABLE);
        }

        mMusicPathTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("audio/x-mpeg");
                i.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(i, 101);
            }
        });
        mPhoneNumberEt.addTextChangedListener(mTw);

        mNickNameEt.addTextChangedListener(mTw);
        mCancleTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPhoneNumberEt.setText("");
                mNickNameEt.setText("");
                hideSoftInputFromWindow();
                if (mDialogClickListener != null) {
                    mDialogClickListener.cancel();
                }
            }
        });

        mConfirmTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDialogClickListener != null) {
                    mDialogClickListener.confirm();
                }

            }
        });

        return view;
    }



    @Override
    public void onCancel(DialogInterface dialog) {
        clear();
        super.onCancel(dialog);
    }

    public String getNickName() {
        return mNickNameEt == null ? "" : mNickNameEt.getText().toString();
    }

    public String getPhoneNumber() {
        return mPhoneNumberEt == null ? "" : mPhoneNumberEt.getText().toString();
    }

    public void clear() {
        if (mPhoneNumberEt != null)
            mPhoneNumberEt.setText("");
        if (mNickNameEt != null)
            mNickNameEt.setText("");
    }


    @Override
    public void dismiss() {
        super.dismiss();
        clear();

    }

    public void hideSoftInputFromWindow() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mNickNameEt.getWindowToken(), 0);
    }

    public Uri getMusicUri() {
        return mMusicPathTv == null ? null : (Uri) mMusicPathTv.getTag();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            Uri url = data.getData();
            if (mMusicPathTv != null) {
                mMusicPathTv.setText("" + url.getPath());
                mMusicPathTv.setTag(url);
            }
        }
    }
}
