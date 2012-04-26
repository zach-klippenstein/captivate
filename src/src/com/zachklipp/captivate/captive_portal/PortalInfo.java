package com.zachklipp.captivate.captive_portal;

import com.zachklipp.captivate.service.PortalDetectorService;

import android.content.Intent;
import android.net.Uri;

public class PortalInfo
{
  private Uri mPortalUri;
  
  public PortalInfo(Uri portalUri)
  {
    mPortalUri = portalUri;
  }
  
  public PortalInfo(Intent intent)
  {
    String uri = intent.getStringExtra(PortalDetectorService.EXTRA_CAPTIVE_PORTAL_INFO);
    mPortalUri = uri == null ? Uri.EMPTY : Uri.parse(uri);
  }
  
  public Intent getShowPortalIntent()
  {
    Intent showPortalIntent = new Intent(Intent.ACTION_VIEW);
    showPortalIntent.setData(mPortalUri);
    
    return showPortalIntent;
  }
  
  public String toString()
  {
    return String.format("PortalInfo@%s", mPortalUri);
  }
  
  public void saveToIntent(Intent intent)
  {
    intent.putExtra(PortalDetectorService.EXTRA_CAPTIVE_PORTAL_INFO, mPortalUri.toString());
  }
}
