package entertainment.apc.com.myapplication;

import android.os.Handler;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 对okhttp各种请求的封装,所有的接口回调信息经过处理都已经传递到了主线程中去了
 */

public class OkHttpUtils {
    /**
     * 设置默认的超时时间是10秒
     */
    private static final int DEFAULT_TIMEOUT=10*1000*1000;

    private  static OkHttpClient mOkHttpClient=new OkHttpClient();

    private static OkHttpUtils mOkHttpUtils;

    private Handler mhandler;

    private static final MediaType JSON_TYPE=MediaType.parse("application/json; charset=utf-8");//JSON
    private static final MediaType MEDIA_OBJECT_STREAM=MediaType.parse("application/octet-stream");//上传文件
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");//图片类型
    /**
     * 用来返回一个okhttp请求对象
     * @return
     */
    public static OkHttpUtils getInstance(){
        if(mOkHttpUtils == null){
            synchronized (OkHttpUtils.class){
                if(mOkHttpUtils == null){
                    mOkHttpUtils=new OkHttpUtils();
                }
            }
        }
        return mOkHttpUtils;
    }

    /**
     * 创建私有的构造函数，防止外部对该类进行实例化
     */
    private OkHttpUtils(){
        //设置超时时间
        mOkHttpClient.setConnectTimeout(DEFAULT_TIMEOUT, TimeUnit.MICROSECONDS);
        mOkHttpClient.setReadTimeout(DEFAULT_TIMEOUT, TimeUnit.MICROSECONDS);
        mOkHttpClient.setWriteTimeout(DEFAULT_TIMEOUT, TimeUnit.MICROSECONDS);
    }

    /**
     * 通过get方式访问,同步的方式请求
     * @param url
     */
    public void okHttpByGetAync(final String url,final Handler handler,final ResponseListenerCallBack mCallback){
        try{
            Request request=new Request.Builder().url(url).build();
            Call call=mOkHttpClient.newCall(request);
            Response response=call.execute();
            if(response.isSuccessful()){
                if(response.code() == 200){
                    final String result=response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mCallback != null ){
                                mCallback.onSucceed(result);
                            }else{
                                throw new RuntimeException("the params of ResponseListenerCallBack is null");
                            }
                        }
                    });
                }else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mCallback != null ){
                                mCallback.onFailed("the result of request has problem");
                            }else{
                                throw new RuntimeException("the params of ResponseListenerCallBack is null");
                            }
                        }
                    });

                }
            }else{
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mCallback != null ){
                            mCallback.onFailed("request failed");
                        }else{
                            throw new RuntimeException("the params of ResponseListenerCallBack is null");
                        }
                    }
                });
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 通过get异步请求
     * @param url 访问的地址
     * @param handler 主线程传过来的handler
     * @param mCallback 自定义的结果处理监听
     */
    public void okHttpByGetEnque(final String url,final Handler handler,final ResponseListenerCallBack mCallback){
        try{
            Request request=new Request.Builder().url(url).build();
            Call call=mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(final Request request, final IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mCallback != null ){
                                mCallback.onFailed(e.getMessage());
                            }else{
                                throw new RuntimeException("the params of ResponseListenerCallBack is null");
                            }
                        }
                    });
                }

                @Override
                public void onResponse(final Response response) throws IOException {
                    if(response.code() == 200){
                        final String result=response.body().string();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mCallback != null ){
                                    mCallback.onSucceed(result);
                                }else{
                                    throw new RuntimeException("the params of ResponseListenerCallBack is null");
                                }
                            }
                        });
                    }else{
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mCallback != null ){
                                    mCallback.onFailed("the result of request has problem");
                                }else{
                                    throw new RuntimeException("the params of ResponseListenerCallBack is null");
                                }
                            }
                        });
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 通过post的方式提交json字符串(同步)
     * @param url
     * @param handler
     * @param mCallback
     * @param json
     */
    public void okHttpByPostAync(final String url,final Handler handler,final ResponseListenerCallBack mCallback,String json){
        RequestBody body=RequestBody.create(JSON_TYPE,json);
        Request request =new Request.Builder().post(body).url(url).build();
        Call call=mOkHttpClient.newCall(request);
        try {
            Response response=call.execute();
            if(response.isSuccessful()){
                if(response.code() == 200){
                    final String result=response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mCallback != null ){
                                mCallback.onSucceed(result);
                            }else{
                                throw new RuntimeException("the params of ResponseListenerCallBack is null");
                            }
                        }
                    });
                }else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mCallback != null ){
                                mCallback.onFailed("the result of request has problem");
                            }else{
                                throw new RuntimeException("the params of ResponseListenerCallBack is null");
                            }
                        }
                    });

                }
            }else{
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mCallback != null ){
                            mCallback.onFailed("request failed");
                        }else{
                            throw new RuntimeException("the params of ResponseListenerCallBack is null");
                        }
                    }
                });
            }
        }catch (final IOException e){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mCallback != null ){
                        mCallback.onFailed(e.getMessage());
                    }else{
                        throw new RuntimeException("the params of ResponseListenerCallBack is null");
                    }
                }
            });
        }
    }


    /**
     * 通过Post异步请求
     * @param url 访问的地址
     * @param handler 主线程传过来的handler
     * @param mCallback 自定义的结果处理监听
     * @param json 参数是json字符串
     */
    public void okHttpByPostEnque(final String url,final Handler handler,final ResponseListenerCallBack mCallback,String json){
        try{
            RequestBody body=RequestBody.create(JSON_TYPE,json);
            Request request =new Request.Builder().post(body).url(url).build();
            Call call=mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(final Request request, final IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mCallback != null ){
                                mCallback.onFailed(e.getMessage());
                            }else{
                                throw new RuntimeException("the params of ResponseListenerCallBack is null");
                            }
                        }
                    });
                }

                @Override
                public void onResponse(final Response response) throws IOException {
                    if(response.code() == 200){
                        final String result=response.body().string();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mCallback != null ){
                                    mCallback.onSucceed(result);
                                }else{
                                    throw new RuntimeException("the params of ResponseListenerCallBack is null");
                                }
                            }
                        });
                    }else{
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mCallback != null ){
                                    mCallback.onFailed("the result of request has problem");
                                }else{
                                    throw new RuntimeException("the params of ResponseListenerCallBack is null");
                                }
                            }
                        });
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 通过post方式提交键值对访问(此时支持的键值对都是字符串类型的)
     * @param url
     * @param handler
     * @param mCallback
     * @param map
     */
    public void okHttpByPost(final String url,final Handler handler,final ResponseListenerCallBack mCallback,Map<String,String> map){
        try{
            if(map != null){
                FormEncodingBuilder  formEncodingBuilder=new FormEncodingBuilder();
                Set<String> keySet = map.keySet();//先获取map集合的所有键的Set集合
                Iterator<String> it = keySet.iterator();//有了Set集合，就可以获取其迭代器。
                while(it.hasNext()){
                    String key = it.next();
                    String value = map.get(key);//有了键可以通过map集合的get方法获取其对应的值。
                    formEncodingBuilder.add(key,value);
                }
                RequestBody body=formEncodingBuilder.build();
                Request request=new Request.Builder().post(body).url(url).build();
                Call call=mOkHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(final Request request, final IOException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(mCallback != null ){
                                    mCallback.onFailed(e.getMessage());
                                }else{
                                    mCallback.onFailed("the params of ResponseListenerCallBack is null");
                                }

                            }
                        });
                    }

                    @Override
                    public void onResponse(final Response response) throws IOException {
                        if(response.code() == 200){
                            final String result=response.body().string();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(mCallback != null ){
                                        mCallback.onSucceed(result);
                                    }else{
                                        throw new RuntimeException("the params of ResponseListenerCallBack is null");
                                    }
                                }
                            });
                        }else{
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(mCallback != null ){
                                        mCallback.onFailed("the result of request has problem");
                                    }else{
                                        throw new RuntimeException("the params of ResponseListenerCallBack is null");
                                    }
                                }
                            });
                        }
                    }
                });
            }else{
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mCallback != null ){
                            mCallback.onFailed("the map is null");
                        }else{
                            throw new RuntimeException("the params of ResponseListenerCallBack is null");
                        }
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 通过post上传单个文件,不带其他参数(不是图片文件)
     * @param url
     * @param handler
     * @param mCallback
     * @param file
     */
    public void okHttpUploadFile(final String url,final Handler handler,final ResponseListenerCallBack mCallback,File file){
        RequestBody body = RequestBody.create(MEDIA_OBJECT_STREAM, file);
        Request request =new Request.Builder().post(body).url(url).build();
        Call call=mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mCallback != null){
                            mCallback.onFailed(e.getMessage());
                        }else{
                            throw new RuntimeException("the params of ResponseListenerCallBack is null");
                        }
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.code() == 200){
                    final String result=response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mCallback != null ){
                                mCallback.onSucceed(result);
                            }else{
                                throw new RuntimeException("the params of ResponseListenerCallBack is null");
                            }
                        }
                    });
                }else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mCallback != null ){
                                mCallback.onFailed("the result of request has problem");
                            }else{
                                throw new RuntimeException("the params of ResponseListenerCallBack is null");
                            }
                        }
                    });
                }
            }
        });
    }


    /**
     * 上传单张图片
     * @param url 访问的路径
     * @param handler 主线程消息处理
     * @param mCallback 回调
     * @param file 要上传的图片文件
     */
    public void okHttpUploadImageFile(final String url,final Handler handler,final ResponseListenerCallBack mCallback,File file){

    }

    /**
     * 上传多张图片(也可以添加文本表单信息上传)
     * @param url 访问的路径
     * @param handler 主线程消息处理
     * @param mCallback 回调
     * @param imgsUrl 图片的地址集合
     */
    public void okHttpUploadImagesFile(final String url,final Handler handler,final ResponseListenerCallBack mCallback,List<String> imgsUrl){
        MultipartBuilder mMultipartBuilder=new MultipartBuilder().type(MultipartBuilder.FORM);
        int length=imgsUrl.size();
        for(int i=0;i<length;i++){
            File imagefile=new File(imgsUrl.get(i));
            if(imagefile !=null){
                mMultipartBuilder.addFormDataPart("img",imagefile.getName(),RequestBody.create(MEDIA_TYPE_PNG,imagefile));
            }
        }
        RequestBody body=mMultipartBuilder.build();
        Request request=new Request.Builder().url(url).post(body).build();
        Call call=mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mCallback != null){
                            mCallback.onFailed(e.getMessage());
                        }else{
                            throw new RuntimeException("the params of ResponseListenerCallBack is null");
                        }
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.code() == 200){
                    final String result=response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mCallback != null ){
                                mCallback.onSucceed(result);
                            }else{
                                throw new RuntimeException("the params of ResponseListenerCallBack is null");
                            }
                        }
                    });
                }else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mCallback != null ){
                                mCallback.onFailed("the result of request has problem");
                            }else{
                                throw new RuntimeException("the params of ResponseListenerCallBack is null");
                            }
                        }
                    });
                }
            }
        });
    }
}
