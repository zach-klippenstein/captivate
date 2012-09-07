package com.zachklipp.captivate.captive_portal;

import com.zachklipp.captivate.service.PortalDetectorService;
import com.zachklipp.captivate.util.BitmapHelper;
import com.zachklipp.captivate.util.BitmapHelper.ImageLoadListener;
import com.zachklipp.captivate.util.FaviconHelper;
import com.zachklipp.captivate.util.StringHelper;

import android.content.Intent;

public class PortalInfo
{
  private final String mPortalUrl;
  private final String mFaviconUrl;
  
  public PortalInfo()
  {
    mPortalUrl = "";
    mFaviconUrl = "";
  }
  
  public PortalInfo(String portalUrl)
  {
    mPortalUrl = portalUrl;
    mFaviconUrl = FaviconHelper.createFaviconUrl(mPortalUrl);
  }
  
  public PortalInfo(Intent intent)
  {
    mPortalUrl = StringHelper.stringOrEmpty(intent.getStringExtra(PortalDetectorService.EXTRA_PORTAL_URL));
    
    String faviconUrl = intent.getStringExtra(PortalDetectorService.EXTRA_FAVICON_URL);
    if (faviconUrl == null)
    {
      faviconUrl = FaviconHelper.createFaviconUrl(mPortalUrl);
    }
    mFaviconUrl = faviconUrl;
  }
  
  public String toString()
  {
    return String.format("PortalInfo@%s", mPortalUrl);
  }
  
  public void saveToIntent(Intent intent)
  {
    intent.putExtra(PortalDetectorService.EXTRA_PORTAL_URL, mPortalUrl);
    intent.putExtra(PortalDetectorService.EXTRA_FAVICON_URL, mFaviconUrl);
  }
  
  public String getPortalUrl()
  {
    return mPortalUrl;
  }
  
  public String getFaviconUrl()
  {
    return mFaviconUrl;
  }
  
  public void getFavicon(ImageLoadListener callback)
  {
    BitmapHelper.loadImage(mFaviconUrl, callback);
  }
}
