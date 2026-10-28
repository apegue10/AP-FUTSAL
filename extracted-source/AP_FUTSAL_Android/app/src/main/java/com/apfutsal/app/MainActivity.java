package com.apfutsal.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.graphics.Color;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import android.print.PrintManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.content.Context;
import android.os.Build;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private WebView webView;
    private FrameLayout root;
    private View printOverlay;
    private WebView reportWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(16, 24, 39));
        getWindow().setNavigationBarColor(Color.rgb(11, 18, 32));
        root = new FrameLayout(this);
        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(11, 18, 32));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setLoadsImagesAutomatically(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new PrintBridge(), "AndroidPrint");
        root.addView(webView, new FrameLayout.LayoutParams(-1, -1));
        setContentView(root);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private class PrintBridge {
        @JavascriptInterface
        public void printReport(String html) {
            runOnUiThread(() -> showPrintPreview(html));
        }
    }

    private void showPrintPreview(String html) {
        if (printOverlay != null) root.removeView(printOverlay);
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setBackgroundColor(Color.WHITE);

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setPadding(dp(12), dp(8), dp(12), dp(8));
        toolbar.setBackgroundColor(Color.rgb(16, 24, 39));

        Button backButton = new Button(this);
        backButton.setText("← Volver a AP FUTSAL");
        backButton.setTextColor(Color.WHITE);
        backButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(38, 54, 75)));
        backButton.setOnClickListener(v -> closePrintPreview());
        toolbar.addView(backButton, new LinearLayout.LayoutParams(0, -2, 1f));

        Button saveButton = new Button(this);
        saveButton.setText("Guardar PDF");
        saveButton.setTextColor(Color.WHITE);
        saveButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(25, 105, 210)));
        saveButton.setOnClickListener(v -> startPrintJob());
        LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(-2, -2);
        saveParams.leftMargin = dp(8);
        toolbar.addView(saveButton, saveParams);

        TextView hint = new TextView(this);
        hint.setText("Vista previa · Puedes volver sin guardar");
        hint.setTextColor(Color.rgb(60, 60, 60));
        hint.setPadding(dp(12), dp(8), dp(12), dp(8));
        hint.setBackgroundColor(Color.rgb(245, 247, 250));

        reportWebView = new WebView(this);
        reportWebView.setBackgroundColor(Color.WHITE);
        reportWebView.getSettings().setJavaScriptEnabled(false);
        reportWebView.getSettings().setAllowFileAccess(true);
        reportWebView.setWebViewClient(new WebViewClient());
        reportWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);

        screen.addView(toolbar, new LinearLayout.LayoutParams(-1, -2));
        screen.addView(hint, new LinearLayout.LayoutParams(-1, -2));
        screen.addView(reportWebView, new LinearLayout.LayoutParams(-1, 0, 1f));
        printOverlay = screen;
        root.addView(printOverlay, new FrameLayout.LayoutParams(-1, -1));
    }

    private void startPrintJob() {
        if (reportWebView == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        if (printManager == null) return;
        PrintDocumentAdapter adapter = reportWebView.createPrintDocumentAdapter("AP_FUTSAL_estadisticas");
        PrintAttributes attributes = new PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(new PrintAttributes.Resolution("apfutsal", "AP FUTSAL", 300, 300))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build();
        printManager.print("AP FUTSAL - Estadísticas", adapter, attributes);
    }

    private void closePrintPreview() {
        if (printOverlay != null) {
            root.removeView(printOverlay);
            printOverlay = null;
        }
        if (reportWebView != null) {
            reportWebView.destroy();
            reportWebView = null;
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onBackPressed() {
        if (printOverlay != null) {
            closePrintPreview();
        } else if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
