package com.zachklipp.captivate.test.state_machine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.zachklipp.captivate.captive_portal.PortalDetector;
import com.zachklipp.captivate.service.PortalDetectorService;
import com.zachklipp.captivate.state_machine.StateMachine;
import com.zachklipp.captivate.state_machine.StateMachineStorage.StorageBackend;

public class MockStorageBackend implements StorageBackend
{
  public static class Factory implements PortalDetectorService.StorageBackendFactory
  {
    public boolean mLoadFromSave = false;
    public StateMachine mMachineToCreate = null;
    
    @Override
    public StorageBackend create(Context context, PortalDetector detector)
    {
      MockStorageBackend backend = new MockStorageBackend();
      
      backend.setLoadFromSave(mLoadFromSave);
      backend.setStateMachineToCreate(mMachineToCreate);
      
      return backend;
    }
  };
  
  private List<StateMachine> mSavedMachines = new ArrayList<StateMachine>();
  private long mCanLoadCallCount = 0;
  private boolean mCanLoad = false;
  private long mLoadCallCount = 0;
  private StateMachine mStateMachineToLoad;
  private long mCreateCallCount = 0;
  private StateMachine mStateMachineToCreate;
  
  private boolean mLoadFromSave = false;
  
  public void setLoadFromSave(boolean loadFromSave)
  {
    mLoadFromSave = loadFromSave;
  }

  @Override
  public void save(StateMachine machine)
  {
    mSavedMachines.add(machine);
  }
  
  public StateMachine[] getSavedMachines()
  {
    StateMachine[] machines = new StateMachine[mSavedMachines.size()];
    return mSavedMachines.toArray(machines);
  }

  @Override
  public boolean canLoad()
  {
    mCanLoadCallCount++;
    
    if (mLoadFromSave)
      return mSavedMachines.size() > 0;
    else
      return mCanLoad;
  }
  
  public long getCanLoadCallCount()
  {
    return mCanLoadCallCount;
  }
  
  /*
   * Has no effect if mLoadFromSave is true.
   */
  public void setCanLoad(boolean canLoad)
  {
    mCanLoad = canLoad;
  }

  @Override
  public StateMachine load()
  {
    mLoadCallCount++;
    
    if (mLoadFromSave)
      return mSavedMachines.get(mSavedMachines.size() - 1);
    else
      return mStateMachineToLoad;
  }
  
  public long getLoadCallCount()
  {
    return mLoadCallCount;
  }

  /*
   * Has no effect if mLoadFromSave is true.
   */
  public void setStateMachineToLoad(StateMachine machine)
  {
    mStateMachineToLoad = machine;
  }

  @Override
  public StateMachine create()
  {
    mCreateCallCount++;
    return mStateMachineToCreate;
  }
  
  public long getCreateCallCount()
  {
    return mCreateCallCount;
  }
  
  public void setStateMachineToCreate(StateMachine machine)
  {
    mStateMachineToCreate = machine;
  }

}
