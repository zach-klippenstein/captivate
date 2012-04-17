package com.zachklipp.captivate.state_machine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.zachklipp.captivate.captive_portal.PortalDetector;
import com.zachklipp.captivate.state_machine.StateMachineStorage.StorageBackend;

public class PortalStateMachineStorageBackend implements StorageBackend
{
  private static final String PREFERENCES_NAME = "PortalStateMachine";
  private static final String STATE_KEY = "state";
  
  private static final String LOG_TAG = "captivate";
  
  private PortalDetector mDetector;
  private SharedPreferences mPreferences;
  
  public PortalStateMachineStorageBackend(Context context, PortalDetector detector)
  {
    assert(context != null);
    assert(detector != null);
    
    mPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    mDetector = detector;
  }

  @Override
  public void save(StateMachine machine)
  {
    Editor editor = mPreferences.edit();
    
    editor.putString(STATE_KEY, machine.getCurrentState().getName());
    
    editor.commit();
  }

  @Override
  public boolean canLoad()
  {
    return mPreferences.contains(STATE_KEY);
  }

  @Override
  public StateMachine load()
  {
    String stateName = mPreferences.getString(STATE_KEY, PortalStateMachine.State.UNKNOWN.getName());
    
    Log.d(LOG_TAG, String.format("Attempting to load state machine from state %s", stateName));
    
    return new PortalStateMachine(mDetector, stateName);
  }
  
  @Override
  public StateMachine create()
  {
    return new PortalStateMachine(mDetector);
  }
  
  public void clear()
  {
    Editor editor = mPreferences.edit();
    
    editor.remove(STATE_KEY);
    
    editor.commit();
  }

}
