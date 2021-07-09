package com.xunda.mo.staticdata;

import android.content.Context;
import android.text.format.DateFormat;

import com.xunda.mo.utils.HashUtil;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.ServiceException;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;

import java.io.File;
import java.util.Date;

public class ALiUploadManager {
//    private OSSClient ossClient = null;
    private ObsClient ossClient = null;
    private static ALiUploadManager instance = null;

    public static ALiUploadManager getInstance() {
        if (instance == null) {
            synchronized (ALiUploadManager.class) {
                if (instance == null) {
                    instance = new ALiUploadManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化阿里云SDK
     *
     * @param context
     */
    public void init(Context context) {

        // 创建ObsClient实例
        ObsClient obsClient = new ObsClient(AppConstant.ak, AppConstant.sk, AppConstant.endPoint);
        obsClient.putObject("bucketname", "objectname", new File("localfile")); // localfile为待上传的本地文件路径，需要指定到具体的文件名

        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(AppConstant.endPoint);
//        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(AppConstant.ACCESS_ID, AppConstant.ACCESS_KEY);
//        ClientConfiguration conf = new ClientConfiguration();
//        conf.setConnectionTimeout(15 * 1000);               // 连接超时，默认15秒
//        conf.setSocketTimeout(15 * 1000);                   // socket超时，默认15秒
//        conf.setMaxConcurrentRequest(8);                    // 最大并发请求数，默认5个
//        conf.setMaxErrorRetry(3);                           // 失败后最大重试次数，默认2次
        // oss为全局变量，OSS_ENDPOINT是一个OSS区域地址
//        ossClient = new OSSClient(context, AppConstant.ACCESS_ENDPOINT, credentialProvider, conf);
    }

    /**
     * 上传图片到阿里云
     *
     * @param filePath 本地图片地址
     */


//    public OSSAsyncTask uploadFile(String filePath, final ALiCallBack callBack) {
//        // 构造上传请求
//        final String key = getObjectPortraitKey(filePath);
////        ILog.e("key:" + key);
//        UploadFileRequest request = new UploadFileRequest(bucketName, filePath);
////        PutObjectRequest put = new PutObjectRequest(bucketName, key, filePath);
//        // 异步上传时可以设置进度回调
//        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
//            @Override
//            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                if (callBack != null) {
//                    callBack.process(currentSize, totalSize);
//                }
//            }
//        });
//
//        final OSSAsyncTask task = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
//            @Override
//            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                if (callBack != null) {
//                    //获取可访问的url
//                    String url = ossClient.presignPublicObjectURL(AppConstant.ACCESS_BUCKET_NAME, key);
//                    callBack.onSuccess(request, result, url);
//                }
//            }
//
//            @Override
//            public void onFailure(PutObjectRequest request, ObsException clientExcepion, ServiceException serviceException) {
//                // 请求异常
//                if (clientExcepion != null) {
//                    // 本地异常如网络异常等
//                    clientExcepion.printStackTrace();
//                }
//                if (serviceException != null) {
//                    // 服务异常
////                    ILog.e("ErrorCode" + serviceException.getErrorCode());
////                    ILog.e("RequestId" + serviceException.getRequestId());
////                    ILog.e("HostId" + serviceException.getHostId());
////                    ILog.e("RawMessage" + serviceException.getRawMessage());
//                }
//                if (callBack != null) {
//                    callBack.onError(request, clientExcepion, serviceException);
//                }
//            }
//        });
//        return task;
//    }

    //格式: portrait/201805/sfdsgfsdvsdfdsfs.jpg
    private static String getObjectPortraitKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg", dateString, fileMd5);
    }

    /**
     * 获取时间
     *
     * @return 时间戳 例如:201805
     */
    private static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    public interface ALiCallBack {

        /**
         * 上传成功
         *
         * @param request
         * @param result
         */
        void onSuccess(PutObjectRequest request, PutObjectResult result, String url);

        /**
         * 上传失败
         *
         * @param request
         * @param clientExcepion  ObsException
         * @param serviceException
         */

        void onError(PutObjectRequest request, ObsException clientExcepion, ServiceException serviceException);

        /**
         * 上传进度
         *
         * @param currentSize 当前进度
         * @param totalSize   总进度
         */
        void process(long currentSize, long totalSize);

    }
}

