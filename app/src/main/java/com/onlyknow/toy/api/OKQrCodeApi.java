package com.onlyknow.toy.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.onlyknow.toy.R;
import com.onlyknow.toy.utils.OKBase64Util;
import com.onlyknow.toy.utils.OKNetUtil;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 * 生成二维码图片
 */
public class OKQrCodeApi {
    private Context context;
    private onCallBack mListener;
    private QrCodeTask mQrCodeTask;

    public OKQrCodeApi(Context cont) {
        this.context = cont;
    }

    public interface onCallBack {
        void generateQrCodeComplete(Bitmap bitmap);
    }

    public void requestGenerateQrCode(Bitmap bitmapTx, int mWidth, String msg, onCallBack listener) {
        this.mListener = listener;
        Params mParams = new Params();
        mParams.setBitmapTx(bitmapTx);
        mParams.setWidth(mWidth);
        mParams.setMsg(msg);
        cancelTask();
        mQrCodeTask = new QrCodeTask();
        mQrCodeTask.executeOnExecutor(OKNetUtil.exe, mParams);
    }

    public void cancelTask() {
        if (mQrCodeTask != null && mQrCodeTask.getStatus() == AsyncTask.Status.RUNNING) {
            mQrCodeTask.cancel(true);
        }
    }

    private class QrCodeTask extends AsyncTask<Params, Void, Bitmap> {
        private Bitmap mBitmapTx;
        private int mWidth;

        @Override
        protected Bitmap doInBackground(Params... param) {
            Params mParams = param[0];

            mBitmapTx = mParams.getBitmapTx();
            mWidth = mParams.getWidth();

            int foregroundColor = context.getResources().getColor(R.color.md_pink_200);
            int backgroundColor = context.getResources().getColor(R.color.md_white_1000);

            mBitmapTx = OKBase64Util.toRoundBitmap(mBitmapTx);

            return QRCodeEncoder.syncEncodeQRCode(mParams.getMsg(), mWidth, foregroundColor, backgroundColor, mBitmapTx);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isCancelled()) {
                return;
            }
            mListener.generateQrCodeComplete(bitmap);
        }
    }

    private class Params {
        Bitmap bitmapTx;
        int width;
        String msg;

        public Bitmap getBitmapTx() {
            return bitmapTx;
        }

        public void setBitmapTx(Bitmap bitmapTx) {
            this.bitmapTx = bitmapTx;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
