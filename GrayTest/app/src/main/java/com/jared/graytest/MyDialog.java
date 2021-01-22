package com.jared.graytest;

import android.app.Dialog;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

public class MyDialog extends Dialog {

    public MyDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder  {

        private View mLayout;
        private TextView mTvContent;
        private TextView mTvCancel;
        private TextView mTvPositive;
        private MyDialog mDialog;
        private OnConfirmClickListener mSureClickListener;
        private OnCancelClickListener mCancelClickListener;

        public Builder(Context context) {
            this.mDialog = new MyDialog(context, R.style.Theme_AppCompat_Dialog);
            LayoutInflater inflater =
                    (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //加载布局文件
            this.mLayout = inflater.inflate(R.layout.dialog_sure_cancel, null, false);

            setViewGray(this.mLayout);

            //添加布局文件到 Dialog
            this.mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            this.mTvContent = mLayout.findViewById(R.id.tv_content);
            this.mTvCancel = mLayout.findViewById(R.id.tv_cancel);
            this.mTvPositive = mLayout.findViewById(R.id.tv_positive);
        }

        public void setViewGray(View view) {
            Paint paint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0f);
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            view.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
        }

        /**
         * 设置 Dialog 内容
         * @param content 内容
         * @return
         */
        public Builder setContent(@NonNull String content) {
            mTvContent.setText(content);
            return this;
        }

        /**
         * 确定回调
         * @param onClickListener
         * @return
         */
        public Builder comfirm(OnConfirmClickListener onClickListener){
            this.mSureClickListener = onClickListener;
            return this;
        }

        public Builder cancel(OnCancelClickListener onClickListener){
            this.mCancelClickListener = onClickListener;
            return this;
        }

        public MyDialog create() {
            mTvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCancelClickListener != null) {
                        mCancelClickListener.cancel();
                    }
                    mDialog.dismiss();
                }
            });

            mTvPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSureClickListener != null) {
                        mSureClickListener.confirm();
                    }
                }
            });
            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);                //用户可以点击后退键关闭 Dialog
            mDialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
            return mDialog;
        }

        public MyDialog show() {
            if (mDialog != null) {
                mDialog.show();
            }
            return mDialog;
        }
    }

    public interface OnConfirmClickListener {
        void confirm();
    }

    public interface OnCancelClickListener {
        void cancel();
    }
}