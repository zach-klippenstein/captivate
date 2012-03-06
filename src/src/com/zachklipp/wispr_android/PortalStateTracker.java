package com.zachklipp.wispr_android;

public class PortalStateTracker
{
  private int mState = CaptivePortalDetectorService.STATE_UNKNOWN;
  private CaptivePortalInfo mPortal;
  private PortalStateChangedHandler mStateChangedHandler;
  
  public static interface PortalStateChangedHandler
  {
    public void onStateChanged(int newState, CaptivePortalInfo portalInfo);
  }
  
  public void setStateChangedHandler(PortalStateChangedHandler handler)
  {
    mStateChangedHandler = handler;
  }
  
  public void setPortalInfo(CaptivePortalInfo portal)
  {
    mPortal = portal;
    transitionToState(CaptivePortalDetectorService.STATE_NEEDS_SIGNIN);
  }
  
  public void clearPortalInfo()
  {
    mPortal = null;
    transitionToState(CaptivePortalDetectorService.STATE_NO_PORTAL);
  }
  
  public int getPortalState()
  {
    return mState;
  }
  
  public CaptivePortalInfo getPortalInfo()
  {
    return mPortal;
  }
  
  private void transitionToState(int newState)
  {
    boolean stateChanged = false;
    
    if (newState == CaptivePortalDetectorService.STATE_NO_PORTAL)
    {
      switch (mState)
      {
        case CaptivePortalDetectorService.STATE_UNKNOWN:
        case CaptivePortalDetectorService.STATE_NEEDS_SIGNIN:
        case CaptivePortalDetectorService.STATE_SIGNING_IN:
          mState = CaptivePortalDetectorService.STATE_SIGNED_IN;
          stateChanged = true;
      }
    }
    else if (newState != mState)
    {
      mState = newState;
      stateChanged = true;
    }
    
    if (stateChanged && mStateChangedHandler != null)
    {
      mStateChangedHandler.onStateChanged(mState, mPortal);
    }
  }
}