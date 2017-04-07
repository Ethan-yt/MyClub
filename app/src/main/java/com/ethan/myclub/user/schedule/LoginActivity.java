package com.ethan.myclub.user.schedule;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ethan.myclub.R;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.user.schedule.model.Course;
import com.ethan.myclub.user.schedule.model.CourseTime;
import com.ethan.myclub.user.schedule.model.Schedule;
import com.ethan.myclub.user.schedule.service.ScheduleService;
import com.ethan.myclub.util.Utils;
import com.ethan.myclub.main.BaseActivity;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends BaseActivity {


    private static final String TAG = "LoginActivity";

    public static final String BASE_URL = "http://202.195.144.163/";
    private EditText mPwView, mIdView;
    private TextInputLayout mIdWrapper, mPwWrapper;

    private ScheduleService mScheduleService;


    final private ArrayList<Schedule> schedules = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_login);

        getToolbarWrapper()
                .setTitle("导入课程")
                .showBackIcon()
                .show();

        mPwView = (EditText) findViewById(R.id.pw);
        mIdView = (EditText) findViewById(R.id.id);
        mIdWrapper = (TextInputLayout) findViewById(R.id.idWrapper);
        mPwWrapper = (TextInputLayout) findViewById(R.id.pwWrapper);

        mPwView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {
                    doOnLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doOnLogin();
            }
        });


        CookieJar cookieJar = new CookieJar() {
            private List<Cookie> cookieStore = new ArrayList<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore = cookies;
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                return cookieStore;
            }
        };

        OkHttpClient client = new OkHttpClient
                .Builder()
                .followRedirects(false)
                //设置拦截器，添加headers
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Referer", "http://202.195.144.163")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .cookieJar(cookieJar)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                //.addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        mScheduleService = retrofit.create(ScheduleService.class);

    }

    private void doOnLogin() {

        mIdWrapper.setErrorEnabled(false);
        mPwWrapper.setErrorEnabled(false);

        final String username = mIdView.getText().toString();
        final String password = mPwView.getText().toString();
        if (TextUtils.isEmpty(username)) {
            mIdWrapper.setError("请输入学号");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPwWrapper.setError("请输入密码");
            return;
        }

        Login(username, password);

    }


    private void Login(final String username, final String password) {

        mScheduleService.getViewState()
                .subscribeOn(Schedulers.io())
                //将Html解析，获取ViewState
                .map(new Function<ResponseBody, String>() {

                    @Override
                    public String apply(ResponseBody responseBody) throws Exception {
                        String html = responseBody.string();
                        Document doc = Jsoup.parse(html);
                        Elements content = doc.getElementsByAttributeValue("name", "__VIEWSTATE");
                        if (content.size() != 1)
                            throw new Exception("获取不到ViewState");
                        //viewState = URLEncoder.encode(viewState, "UTF-8");
                        return content.get(0).attr("value");
                    }

                })
                //.delay(1, TimeUnit.SECONDS)
                //利用获取到的ViewState登录，返回null，cookie自动保存到cookieJar中
                .flatMap(new Function<String, Observable<String>>() {
                             @Override
                             public Observable<String> apply(String viewState) throws Exception {
                                 return mScheduleService.login(
                                         viewState,
                                         username,
                                         password,
                                         "%D1%A7%C9%FA",
                                         "")
                                         .map(new Function<ResponseBody, String>() {
                                             @Override
                                             public String apply(ResponseBody responseBody) throws Exception {
                                                 String html = responseBody.string();
                                                 //获取错误信息
//                                                 if (html.contains("请不要重复刷新！")) {
//                                                     throw new Exception("访问过快");
//                                                 }

                                                 Pattern pattern = Pattern.compile("alert\\('(.*?)'\\);");
                                                 Matcher matcher = pattern.matcher(html);
                                                 if (matcher.find())
                                                     throw new Exception(matcher.group(1));

                                                 throw new Exception("未知错误");
                                             }
                                         });
                             }
                         }
                )
                .onErrorResumeNext(new Function<Throwable, Observable<String>>() {
                    @Override
                    public Observable<String> apply(Throwable throwable) throws Exception {
                        if (throwable instanceof HttpException) {
                            HttpException exception = (HttpException) throwable;
                            if (exception.code() == 302) {
                                Response response = exception.response();
                                String html = response.errorBody().string();
                                if (html.contains("Object moved to <a href='/jndx/xs_main.aspx?xh=")) {
                                    return Observable.just("success");
                                }
                            }
                        }
                        return Observable.error(throwable);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                showWaitingDialog("登录中", "正在登录...", d);
                            }

                            @Override
                            public void onNext(String s) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                //OnError 登录失败
                                dismissDialog();
                                showSnackbar(e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                //OnNext 登录成功
                                dismissDialog();
                                getSchedule(username);
                            }
                        });
    }

    private class ScheduleParam {
        private final String year;
        private final int term;

        ScheduleParam(String year, int term) {
            this.year = year;
            this.term = term;
        }
    }

    private void getSchedule(final String username) {


        final String[] viewState = new String[1];


        mScheduleService.getCurrentSchedule(username)
                .subscribeOn(Schedulers.io())
//                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                //将Html解析，获取当前学期课表以及必要参数
                .flatMap(new Function<ResponseBody, Observable<ScheduleParam>>() {

                    @Override
                    public Observable<ScheduleParam> apply(ResponseBody responseBody) throws Exception {
                        String html = responseBody.string();

                        Document doc = Jsoup.parse(html);
                        Elements viewStateContent = doc.getElementsByAttributeValue("name", "__VIEWSTATE");
                        if (viewStateContent.size() != 1)
                            throw new Exception("获取不到ViewState");
                        viewState[0] = viewStateContent.get(0).attr("value");


                        //获取学年列表
                        List<String> years = new ArrayList<>();
                        Elements yearContent = doc.getElementById("xnd")
                                .getElementsByTag("option");
                        years.clear();
                        for (Element year : yearContent) {
                            years.add(year.text());
                        }

                        //获取课程表

                        Schedule currentSchedule = parseSchedule(doc);
                        schedules.clear();
                        schedules.add(currentSchedule);

                        //生成所有学年和学期的笛卡尔积，减去当前学年学期

                        List<ScheduleParam> scheduleIDs = new ArrayList<>();
                        for (String year : years) {
                            for (int term = 1; term <= 2; term++) {
                                if (year.equals(currentSchedule.getYear()))
                                    if (term >= Integer.parseInt(currentSchedule.getTerm()))
                                        continue;
                                scheduleIDs.add(new ScheduleParam(year, term));
                            }
                        }

                        mProgressDialog.dismiss();

                        showProgressDialog("下载中", "正在下载课程表...");

                        mProgressDialog.setMax(scheduleIDs.size());

                        return Observable.fromIterable(scheduleIDs);
                    }

                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<ScheduleParam, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> apply(ScheduleParam parameter) throws Exception {

                        return mScheduleService.getOtherSchedule(
                                username,
                                parameter.year,
                                String.valueOf(parameter.term),
                                viewState[0],
                                "xqd",
                                "")
//                                .delay(1, TimeUnit.SECONDS)
                                .subscribeOn(Schedulers.io());//这行很重要，为每个课表单独创建一个线程，实现同步下载。
                    }
                })
                .map(new Function<ResponseBody, Schedule>() {
                    @Override
                    public Schedule apply(ResponseBody responseBody) throws Exception {

                        String html = responseBody.string();

                        Document doc = Jsoup.parse(html);

                        return parseSchedule(doc);
                    }
                })
                .filter(new Predicate<Schedule>() {
                    @Override
                    public boolean test(Schedule schedule) throws Exception {
                        return schedule.getCourses().size() > 0;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<Schedule>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                showWaitingDialog("下载中", "正在下载课程表...", d);
                            }

                            @Override
                            public void onNext(Schedule schedule) {
                                //OnNext 获取到一个课程表
                                schedules.add(schedule);
                                mProgressDialog.incrementProgressBy(1);
                            }

                            @Override
                            public void onError(Throwable e) {
                                //OnError 获取失败
                                dismissDialog();
                                showSnackbar(e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                dismissDialog();
                                showSnackbar("课表下载完成！");
                                uploadToServer();
                            }
                        }
                );


    }

    private void uploadToServer() {
        ApiHelper.getProxy(this)
                .updateSchedule(schedules)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showWaitingDialog("上传中", "正在上传课程表...", d);
                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissDialog();
                        showSnackbar("上传失败：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                        showNumberPicker();
                    }
                });


    }

    private void showNumberPicker() {

        final SchedulePickerView v = new SchedulePickerView(this);
        v.setSchedules(schedules);

        new AlertDialog.Builder(this)
                .setTitle("请选择当前的学年学期")
                .setView(v)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(LoginActivity.this, ScheduleActivity.class);
                        intent.putParcelableArrayListExtra("Schedules", schedules);
                        intent.putExtra("Year", v.getYear());
                        intent.putExtra("Term", v.getTerm());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();

    }

    private Schedule parseSchedule(Element doc) throws Exception {

        //获取当前学年
        Elements yearContent = doc.getElementById("xnd")
                .getElementsByTag("option");

        String currentYear = "";
        for (Element year : yearContent) {
            if (year.attr("selected").equals("selected"))
                currentYear = year.text();
        }

        //获取当前学期
        Elements termContent = doc.getElementById("xqd")
                .getElementsByTag("option");

        String currentTerm = "";
        for (Element term : termContent) {
            if (term.attr("selected").equals("selected"))
                currentTerm = term.text();
        }


        Map<String, Course> scheduleMap = new HashMap<>();

        Elements rows = doc.getElementById("Table1").getElementsByTag("tr");
        if (rows.size() < 14)
            throw new Exception("课表结构出错");
        Random random = new Random();

        for (int i = 2; i < 14; i++) {
            Element row = rows.get(i);
            Elements cells = row.getElementsByTag("td");

            for (Element cell : cells) {
                String[] info = cell.text().split(" ");
                if (info.length % 5 != 0)
                    continue;

                for (int j = 0; j < (info.length / 5); j++) {
                    Course course = scheduleMap.get(info[0]);
                    if (course == null) {
                        int color = 0x88;
                        color <<= 8;
                        color |= random.nextInt(255);
                        color <<= 8;
                        color |= random.nextInt(255);
                        color <<= 8;
                        color |= random.nextInt(255);
                        course = new Course.Builder()
                                .name(info[j * 5 + 0])
                                .teacher(info[j * 5 + 3])
                                .type(info[j * 5 + 1])
                                .color(color)
                                .build();

                        scheduleMap.put(info[j * 5 + 0], course);
                    }

                    Pattern pattern = Pattern.compile("周(.)第(.*?)节\\{第(\\d*?)-(\\d*?)周(\\|(单|双)周)?\\}");
                    Matcher matcher = pattern.matcher(info[j * 5 + 2]);
                    if (matcher.find() && matcher.groupCount() == 6) {
                        String time = matcher.group(2);
                        String[] split = time.split(",");

                        int timeBegin = Integer.parseInt(split[0]);
                        int timeEnd = Integer.parseInt(split[split.length - 1]);

                        int weekFlag = 3;
                        if (matcher.group(6) != null)
                            weekFlag = matcher.group(6).equals("单") ? 1 : 2;

                        course.getTime().add(new CourseTime.Builder()
                                .day(Utils.Str2Int(matcher.group(1)))
                                .timeBegin(timeBegin)
                                .timeEnd(timeEnd)
                                .weekBegin(Integer.parseInt(matcher.group(3)))
                                .weekEnd(Integer.parseInt(matcher.group(4)))
                                .weekFlag(weekFlag)
                                .location(info[j * 5 + 4])
                                .build());
                    }
                }


            }
        }
        return new Schedule.Builder()
                .term(currentTerm)
                .year(currentYear)
                .courses(new ArrayList<>(scheduleMap.values()))
                .build();
    }

    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LoginActivity.class);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);

    }
}
