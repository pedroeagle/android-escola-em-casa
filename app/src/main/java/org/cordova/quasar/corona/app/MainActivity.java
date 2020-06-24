package org.cordova.quasar.corona.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.datami.smi.SdState;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);

            BottomNavigationView navigationView = (BottomNavigationView) findViewById(
                    R.id.navigation
            );
            navigationView.setSelectedItemId(R.id.classroom);

            TextView textView = (TextView) navigationView.findViewById(R.id.navigation)
                    .findViewById(R.id.largeLabel);
            textView.setTextSize(12);

            navigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.classroom:
                                    return true;
                                case R.id.wikipedia:
                                    startActivity(new Intent(
                                            getApplicationContext(),
                                            WikipediaActivity.class)
                                    );
                                    overridePendingTransition(0, 0);

                                    return true;
                                case R.id.about:
                                    startActivity(new Intent(
                                            getApplicationContext(),
                                            AboutActivity.class)
                                    );
                                    overridePendingTransition(0, 0);

                                    return true;
                            }
                            return true;
                        }
                    }
            );

            myWebView = (WebView) findViewById(R.id.web_view);
            myWebView.setWebViewClient(new MyWebViewClient());

            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            Locale.setDefault(new Locale("pt", "BR"));

            myWebView.loadUrl("https://classroom.google.com/?emr=0");
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();

            return true;
        }

        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
         BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String urlParameter) {
            String url = this.youtubeProtect(webView, urlParameter);
            try {
                if(url.startsWith("javascript"))
                    return false;

                if (url.startsWith("http") || url.startsWith("https"))
                {
                    if(MyApplication.sdState == SdState.SD_AVAILABLE)
                    {
                        URL urlEntrada = null;
                        urlEntrada = new URL(url);
                        List<String> urlsPermitidas = new ArrayList<String>(25);
                        urlsPermitidas.add("classroom.google.com");
                        urlsPermitidas.add("accounts.google.com");
                        urlsPermitidas.add("googledrive.com");
                        urlsPermitidas.add("drive.google.com");
                        urlsPermitidas.add("docs.google.com");
                        urlsPermitidas.add("c.docs.google.com");
                        urlsPermitidas.add("sheets.google.com");
                        urlsPermitidas.add("slides.google.com");
                        urlsPermitidas.add("takeout.google.com");
                        urlsPermitidas.add("gg.google.com");
                        urlsPermitidas.add("script.google.com");
                        urlsPermitidas.add("ssl.google-analytics.com");
                        urlsPermitidas.add("video.google.com");
                        urlsPermitidas.add("s.ytimg.com");
                        urlsPermitidas.add("apis.google.com");
                        urlsPermitidas.add("googleapis.com");
                        urlsPermitidas.add("googleusercontent.com");
                        urlsPermitidas.add("gstatic.com");
                        urlsPermitidas.add("gvt1.com");
                        urlsPermitidas.add("edu.google.com");
                        urlsPermitidas.add("accounts.youtube.com");
                        urlsPermitidas.add("myaccount.google.com");
                        urlsPermitidas.add("forms.gle");
                        urlsPermitidas.add("google.com");

                        //TODO: fazer um filtro inteligente de URLs
                        for (int i = 0; i <= urlsPermitidas.size() -1; i++) {
                            if(urlEntrada.getAuthority().contains(urlsPermitidas.get(i))) {
                                return false;
                            }
                        }

                        Log.d("ControleAcesso", "Acesso negado a " + url);

                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(
                                getApplicationContext(),
                                "Acesso negado.",
                                duration
                        );
                        toast.show();

                        return true;
                    }
                    else
                        return false;
                }

                if (url.startsWith("intent://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        PackageManager packageManager = webView.getContext().getPackageManager();

                        if (intent != null) {
                            webView.stopLoading();
                            ResolveInfo info = packageManager.resolveActivity(
                                    intent,
                                    PackageManager.MATCH_DEFAULT_ONLY
                            );

                            if (info != null) {
                                webView.getContext().startActivity(intent);
                            } else {
                                Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(
                                        Uri.parse("market://details?id=" + intent.getPackage()));

                                if (marketIntent.resolveActivity(packageManager) != null) {
                                    getApplicationContext().startActivity(marketIntent);

                                    return true;
                                }
                            }
                            return false;
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
            return true;
        }

        private String youtubeProtect(WebView view, String urlParameter) {
            final String regexYouTube = "^.*((youtu.be\\/)|(v\\/)|(\\/u\\/\\w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#&?]*).*";
            String url = "";
            WebBackForwardList mWebBackForwardList = view.copyBackForwardList();
            String historyUrl="";
            if (mWebBackForwardList.getCurrentIndex() > 0) {
                historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()).getUrl();
            }
            if(historyUrl.matches(regexYouTube)){
                return "";
            }
            if(urlParameter.matches(regexYouTube) && !urlParameter.matches("embed")) {
                Pattern compiledPattern = Pattern.compile(regexYouTube);
                Matcher matcher = compiledPattern.matcher(urlParameter);
                if (matcher.find()) {
                    url = "https://www.youtube-nocookie.com/embed/"+matcher.group(7)+"?rel=0";
                    view.loadUrl(url);

                    return "";
                } else {
                    url = urlParameter;
                }
            } else {
                url = urlParameter;
            }

            return url;
        }
    }
}

