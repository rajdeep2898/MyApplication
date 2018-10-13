package com.example.myapp2.browser;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //ImageView superImageView;
    ProgressBar superProgressBar;
    WebView superWebView;
    LinearLayout superLinearLayout;
    SwipeRefreshLayout superSwipeLayout;
    EditText superEditText;
    Button superButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        superLinearLayout=findViewById(R.id.myLinearLayout);
        //superImageView = findViewById(R.id.myImageView);
        superProgressBar = findViewById(R.id.myProgressBar);
        superWebView = findViewById(R.id.myWebView);
        superSwipeLayout=findViewById(R.id.mySwipeLayout);
        superEditText=findViewById(R.id.editText);
        superButton=findViewById(R.id.button);

        superProgressBar.setMax(100);

        superWebView.loadUrl("https://www.google.com");
        superWebView.getSettings().setJavaScriptEnabled(true);
        superWebView.getSettings().setSupportZoom(true);
        superWebView.getSettings().setBuiltInZoomControls(false);
        superWebView.getSettings().setLoadWithOverviewMode(true);
        superWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        superWebView.setBackgroundColor(Color.WHITE);
        superWebView.setWebViewClient(new WebViewClient(){

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                superLinearLayout.setVisibility(view.VISIBLE);
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                superLinearLayout.setVisibility(view.GONE);
                superSwipeLayout.setRefreshing(false);
                super.onPageFinished(view, url);
            }
        });

        superWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                superProgressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }

           /* @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                superImageView.setImageBitmap(icon);

            }*/
        });

        superWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                DownloadManager.Request myRequest=new DownloadManager.Request(Uri.parse(url));
                myRequest.allowScanningByMediaScanner();
                myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager myManager=(DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                myManager.enqueue(myRequest);

                Toast.makeText(MainActivity.this,"Your file is downloading...",Toast.LENGTH_SHORT).show();

            }
        });

        superButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                superWebView.loadUrl("https://"+ superEditText.getText().toString());
                superEditText.setText("");

            }
        });



        superSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                superWebView.reload();

            }
        });


    }

    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    @Override
    public void onStart() {
        super.onStart();

        superSwipeLayout.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener =
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (superWebView.getScrollY() == 0)
                            superSwipeLayout.setEnabled(true);
                        else
                            superSwipeLayout.setEnabled(false);

                    }
                });
    }

    @Override
    public void onStop() {
        superSwipeLayout.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
        super.onStop();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenuInflater = getMenuInflater();
        myMenuInflater.inflate(R.menu.menu_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_back:
                onBackPressed();
                break;

            case R.id.menu_forward:
                GoForward();
                break;

            case R.id.menu_home:
                superWebView.loadUrl("https://www.google.com");

                break;

        }
        return true;
    }

    private void GoForward() {
        if (superWebView.canGoForward()) {
            superWebView.goForward();
        } else {
            Toast.makeText(this, "Can't go further!", Toast.LENGTH_SHORT).show();
        }
    }



    public void onBackPressed() {
        if (superWebView.canGoBack()) {
            superWebView.goBack();
        } else {
            finish();
        }
    }


}
