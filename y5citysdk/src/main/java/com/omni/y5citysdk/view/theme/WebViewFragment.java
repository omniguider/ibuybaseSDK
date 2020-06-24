package com.omni.y5citysdk.view.theme;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.omni.y5citysdk.R;
import com.omni.y5citysdk.manager.AnimationFragmentManager;
import com.omni.y5citysdk.network.WebviewClientSSL;
import com.omni.y5citysdk.tool.Tools;

public class WebViewFragment extends Fragment {

    public static final String TAG = "fragment_tag_webview";
    private static final String ARG_KEY_TITLE = "arg_key_title";
    private static final String ARG_KEY_URL = "arg_key_url";

    private View mView;
    private WebView wv;
    private ProgressDialog mProgressDialog;
    private String title;
    private String url;

    public static WebViewFragment newInstance(String title, String url) {
        WebViewFragment fragment = new WebViewFragment();

        Bundle arg = new Bundle();
        arg.putString(ARG_KEY_TITLE, title);
        arg.putString(ARG_KEY_URL, url);
        fragment.setArguments(arg);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString(ARG_KEY_TITLE);
        url = getArguments().getString(ARG_KEY_URL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_webview, container, false);
//            mView.setPadding(0, Tools.STATUS_BAR, 0, 0);

            mView.findViewById(R.id.fragment_webview_action_bar_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });

            wv = mView.findViewById(R.id.fragment_webview_wv);

            ((TextView) mView.findViewById(R.id.fragment_webview_action_bar_title)).setText(title);
            loadWebView(url);

        }

        return mView;
    }

    private void loadWebView(String url) {
        WebViewClient wc = new WebviewClientSSL(getContext()) {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setMessage(getString(R.string.dialog_message_loading));
                }
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        };

        wv.setWebViewClient(wc);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        wv.getSettings().setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wv.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        wv.loadUrl(url);
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        AnimationFragmentManager.getInstance().addFragmentPage(getActivity(),
                R.id.activity_main_fl, fragment, tag);
    }

}
