package com.zachklipp.captivate.test.state_machine;

import java.util.ArrayList;
import java.util.List;

import com.zachklipp.captivate.state_machine.StateMachine;
import com.zachklipp.captivate.state_machine.StateMachineStorage.StorageBackend;

public class MockStorageBackend implements StorageBackend
{
  private List<StateMachine> mSavedMachines = new ArrayList<StateMachine>();
  private long mCanLoadCallCount = 0;
  private boolean mCanLoad = false;
  private long mLoadCallCount = 0;
  private StateMachine mStateMachineToLoad;
  private long mCreateCallCount = 0;
  private StateMachine mStateMachineToCreate;

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
    return mCanLoad;
  }
  
  public long getCanLoadCallCount()
  {
    return mCanLoadCallCount;
  }
  
  public void setCanLoad(boolean canLoad)
  {
    mCanLoad = canLoad;
  }

  @Override
  public StateMachine load()
  {
    mLoadCallCount++;
    return mStateMachineToLoad;
  }
  
  public long getLoadCallCount()
  {
    return mLoadCallCount;
  }
  
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
