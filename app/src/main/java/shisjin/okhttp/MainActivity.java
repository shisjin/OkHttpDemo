package shisjin.okhttp;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 1.同步请求
 * a.创建OkHttpClient对象
 * b.创建一个Request请求
 * c.发起请求
 * 2.异步请求
 */
public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv.setText(msg.obj.toString());
        }
    };
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
    }

    public void btnClick1(View view) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .build();
        final Call call = client.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //发起同步请求
                try {
                    Response response = call.execute();
                    String result = response.body().string();
                    Log.d("google.sang", "run: " + result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void btnClick2(View view) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url("http://www.baidu.com").build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            //请求成功时回调，该方法在子线程执行
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("google.sang", "onResponse: " + Thread.currentThread().getName());
                if (response.isSuccessful()) {
                    Headers headers = response.headers();
                    Set<String> names = headers.names();
                    Iterator<String> iterator = names.iterator();
                    while (iterator.hasNext()) {
                        String name = iterator.next();
                        Log.d("google.sang", "onResponse: "+headers.get(name));
                    }
                    String result = response.body().string();
                    Log.d("google.sang", "onResponse: " + result);
                }
            }
        });
    }

    public void btnClick3(View view) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url("http://192.168.152.2:8080/upload?username=zhangsan&password=123456&nickname=张三")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("google.sang", "onResponse: ");
            }
        });
    }

    /**
     * 通过IO 流向服务端传递数据
     * @param view
     */
    public void btnClick4(View view) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("username", "lisi");
            json.put("password", "111111");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mediaType,json.toString());
        Request request = new Request.Builder()
                .url("http://192.168.152.2:8080/upload")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    /**
     * 通过表单上传数据
     * @param view
     */
    public void btnClick5(View view) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        FormBody body = new FormBody.Builder().add("username", "王五").add("passwd", "222222").build();
        Request request = new Request.Builder()
                .url("http://192.168.152.2:8080/upload")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    public void btnClick6(View view) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        String s = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download";
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, new File(file, "154.jpg"));
        Request request = new Request.Builder()
                .url("http://192.168.152.2:8080/upload")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    public void btnClick7(View view) {
        OkHttpClient client = new OkHttpClient.Builder()
                //设置缓存位置
                .cache(new Cache(this.getExternalCacheDir(), 10 * 1024 * 1024))
                .build();
        CacheControl cc = new CacheControl.Builder().noCache().build();
        Request request = new Request.Builder()
                .url("http://www.tngou.net/api/food/classify").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Headers headers = response.headers();
                    Set<String> names = headers.names();
                    Iterator<String> iterator = names.iterator();
                    while (iterator.hasNext()) {
                        String name = iterator.next();
                        Log.d("google.sang", "onResponse: name:"+name+";value:"+headers.get(name));
                    }
                    Message msg = mHandler.obtainMessage();
                    msg.obj = response.body().string();
                    mHandler.sendMessage(msg);
                }
            }
        });
    }
}
