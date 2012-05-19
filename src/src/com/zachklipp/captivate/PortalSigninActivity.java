package com.zachklipp.captivate;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.zachklipp.captivate.captive_portal.PortalInfo;
import com.zachklipp.captivate.service.PortalDetectorService;
import com.zachklipp.captivate.state_machine.PortalStateMachine.State;
import com.zachklipp.captivate.util.Log;
import com.zachklipp.captivate.util.SafeIntentSender;
import com.zachklipp.captivate.util.SafeIntentSender.OnNoReceiverListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class PortalSigninActivity extends SherlockFragmentActivity
{
  private static final String LOG_TAG = "PortalSigninActivity";
  
  public static Intent getStartIntent(Context context, PortalInfo portal)
  {
    Intent intent = new Intent(context, PortalSigninActivity.class);
    intent.putExtra(PortalDetectorService.EXTRA_PORTAL_URL, portal.getPortalUrl());
    
    return intent;
  }
  
  private PortalInfo mPortalInfo;
  private WebView mWebView;
  private ProgressBar mProgressBar;
  private SafeIntentSender mOpenInBrowserSender;
  
  private PortalStateChangedReceiver mPortalStateChangedReceiver;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    mPortalStateChangedReceiver = new PortalStateChangedReceiver();
    registerReceiver(mPortalStateChangedReceiver,
        mPortalStateChangedReceiver.INTENT_FILTER);
    
    setContentView(R.layout.portal_signin_layout);
    getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    
    mPortalInfo = new PortalInfo(getIntent());
    if (BuildConfig.DEBUG && mPortalInfo.getPortalUrl().length() == 0)
    {
      mPortalInfo = new PortalInfo("http://www.google.com");
    }
    
    mWebView = (WebView) findViewById(R.id.webview);
    mProgressBar = (ProgressBar) findViewById(R.id.progress);
    
    mOpenInBrowserSender = new SafeIntentSender(this);
    mOpenInBrowserSender.setNoReceiverHandler(new OnNoReceiverListener()
    {
      @Override
      public void onNoReceiver(Intent primary)
      {
        showNoBrowserDialog();
      }
    });
    
    initializeWebView();
    
    String portalUrl = mPortalInfo.getPortalUrl();
    
    Log.d(LOG_TAG, "Loading portal url: " + portalUrl);
    
    mWebView.loadUrl(portalUrl);
    
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    Log.i(LOG_TAG, "Screen density: " +  metrics.densityDpi);
  }
  
  private void initializeWebView()
  {
    WebSettings settings = mWebView.getSettings();
    
    settings.setJavaScriptEnabled(true);
    settings.setBuiltInZoomControls(true);
    settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
    
    // Desktop-sized webpages (e.g. Google) seem to load better when this is set,
    // and it seems to be required for double-tap zooming.
    settings.setUseWideViewPort(true);
    
    mWebView.setWebViewClient(new WebViewClient());
    mWebView.setWebChromeClient(new WebChromeClient());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.portal_signin_menu, menu);
    
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.menu_open_browser:
        showPortalInBrowser();
        return true;
        
      case R.id.menu_refresh:
        mWebView.reload();
        return true;
        
      case R.id.menu_settings:
        PreferenceActivity.showPreferences(this);
        return true;
        
      default:
        return super.onOptionsItemSelected(item);
    }
  }
  
  @Override
  public void onResume()
  {
    super.onResume();
    
    mWebView.resumeTimers();
  }
  
  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState)
  {
    super.onRestoreInstanceState(savedInstanceState);
    
    mWebView.restoreState(savedInstanceState);
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
    
    mWebView.saveState(outState);
  }
  
  @Override
  public void onPause()
  {
    super.onPause();
    
    mWebView.pauseTimers();
  }
  
  @Override
  public void onDestroy()
  {
    super.onDestroy();
    
    mWebView.destroy();
    
    unregisterReceiver(mPortalStateChangedReceiver);
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack())
    {
      mWebView.goBack();
      return true;
    }
    
    return super.onKeyDown(keyCode, event);
  }
  
  private void showPortalInBrowser()
  {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(mPortalInfo.getPortalUrl()));
    
    mOpenInBrowserSender.startActivity(intent);
  }

  private class WebViewClient extends android.webkit.WebViewClient
  {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
      view.loadUrl(url);
      
      return true;
    }
    
    @Override
    public void onPageFinished(WebView view, String url)
    {
      PortalDetectorService.startService(PortalSigninActivity.this);
    }
  }
  
  private void showNoBrowserDialog()
  {
    DialogFragment fragment = new NoBrowserAlertDialog();
    fragment.show(getSupportFragmentManager(), "dialog");
  }
  
  private class WebChromeClient extends android.webkit.WebChromeClient
  {
    @Override
    public void onProgressChanged(WebView view, int newProgress)
    {
      mProgressBar.setProgress(newProgress);
      
      mProgressBar.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
    }
    
    @Override
    public void onReceivedTitle(WebView view, String title)
    {
      setTitle(title);
    }
  }
  
  private class PortalStateChangedReceiver extends BroadcastReceiver
  {
    public final IntentFilter INTENT_FILTER = new IntentFilter(
        PortalDetectorService.ACTION_PORTAL_STATE_CHANGED);
    
    @Override
    public void onReceive(Context context, Intent intent)
    {
      Log.d(LOG_TAG, "Received broadcast intent");
      
      if (intent.getAction().equals(PortalDetectorService.ACTION_PORTAL_STATE_CHANGED))
      {
        if (!isBlocked(intent))
        {
          finish();
        }
      }
    }

    // TODO this should deserialize strings to States, then use isBlocked().
    private boolean isBlocked(Intent intent)
    {
      String newState = intent.getStringExtra(PortalDetectorService.EXTRA_PORTAL_STATE);

      return !(State.NO_PORTAL.equals(newState) || State.SIGNED_IN.equals(newState));
    }
  }
  
  private class NoBrowserAlertDialog extends DialogFragment
  {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
      return new AlertDialog.Builder(getActivity())
        .setMessage(getString(
            R.string.no_browser_message))
        .setNeutralButton(R.string.ok, null)
        .create();
    }
  }
}
