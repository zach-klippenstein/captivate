package com.zachklipp.captivate.captive_portal;

import com.zachklipp.captivate.service.PortalDetectorService;

import android.content.Intent;
import android.net.Uri;

public class PortalInfo
{
  private String mPortalUrl;
  
  public PortalInfo()
  {
    initialize(null);
  }
  
  public PortalInfo(String portalUrl)
  {
    initialize(portalUrl);
  }
  
  public PortalInfo(Intent intent)
  {
    initialize(intent.getStringExtra(PortalDetectorService.EXTRA_PORTAL_URL));
  }
  
  private void initialize(String portalUrl)
  {
    mPortalUrl = portalUrl == null ? "" : portalUrl;
  }
  
  public Intent getShowPortalIntent()
  {
    Intent showPortalIntent = new Intent(Intent.ACTION_VIEW);
    showPortalIntent.setData(Uri.parse(mPortalUrl));
    
    return showPortalIntent;
  }
  
  public String toString()
  {
    return String.format("PortalInfo@%s", mPortalUrl);
  }
  
  public void saveToIntent(Intent intent)
  {
    intent.putExtra(PortalDetectorService.EXTRA_PORTAL_URL, mPortalUrl.toString());
  }
  
  public String getPortalUrl()
  {
    return mPortalUrl;
  }
}
