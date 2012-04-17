package com.zachklipp.captivate.captive_portal;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class PortalInfo implements Parcelable
{
  public static final Parcelable.Creator<PortalInfo> CREATOR = new Parcelable.Creator<PortalInfo>()
  {
    public PortalInfo createFromParcel(Parcel in) {
      return new PortalInfo(in);
    }
  
    public PortalInfo[] newArray(int size) {
      return new PortalInfo[size];
    }
  };
  
  private Uri mPortalUri;
  
  public PortalInfo(Uri portalUri)
  {
    mPortalUri = portalUri;
  }
  
  private PortalInfo(Parcel in)
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
    return String.format("PortalInfo@%s", mPortalUri);
  }
}
