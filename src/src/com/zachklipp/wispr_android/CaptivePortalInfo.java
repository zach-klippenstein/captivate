package com.zachklipp.wispr_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class CaptivePortalInfo implements Parcelable
{
  public static final Parcelable.Creator<CaptivePortalInfo> CREATOR = new Parcelable.Creator<CaptivePortalInfo>()
  {
    public CaptivePortalInfo createFromParcel(Parcel in) {
      return new CaptivePortalInfo(in);
    }
  
    public CaptivePortalInfo[] newArray(int size) {
      return new CaptivePortalInfo[size];
    }
  };
  
  private Uri mPortalUri;
  
  public CaptivePortalInfo(Uri portalUri)
  {
    mPortalUri = portalUri;
  }
  
  private CaptivePortalInfo(Parcel in)
  {
    mPortalUri = Uri.CREATOR.createFromParcel(in);
  }
  
  public Intent getShowPortalIntent()
  {
    Intent showPortalIntent = new Intent(Intent.ACTION_VIEW);
    showPortalIntent.setData(mPortalUri);
    
    return showPortalIntent;
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags)
  {
    Uri.writeToParcel(out, mPortalUri);
  }
  
  public String toString()
  {
    return String.format("CaptivePortalInfo@%s", mPortalUri);
  }
}
