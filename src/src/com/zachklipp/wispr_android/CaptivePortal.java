package com.zachklipp.wispr_android;

import android.content.Intent;
import android.net.Uri;

public class CaptivePortal
{
  private Uri mPortalUri;
  
  public CaptivePortal(Uri portalUri)
  {
    mPortalUri = portalUri;
  }
  
  public Intent getShowPortalIntent()
  {
    Intent showPortalIntent = new Intent(Intent.ACTION_VIEW);
    showPortalIntent.setData(mPortalUri);
    
    return showPortalIntent;
  }
}
