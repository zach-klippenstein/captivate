package com.zachklipp.captivate.state_machine;

import com.zachklipp.captivate.util.Log;
import com.zachklipp.captivate.util.Observable;
import com.zachklipp.captivate.util.Observer;

public class StateMachineStorage implements Observer<TransitionEvent>
{
  public static interface StorageBackend
  {
    void save(StateMachine machine);
    boolean canLoad();
    StateMachine load();
    StateMachine create();
  }
  
  private boolean mAutoSave = true;
  private StorageBackend mBackend;
  private StateMachine mStateMachine;
  
  public StateMachineStorage(StorageBackend backend)
  {
    assert(backend != null);
    
    mBackend = backend;
  }
  
  public StateMachine loadOrCreate()
  {
    if (mStateMachine != null)
    {
      mStateMachine.deleteObserver(this);
      mStateMachine = null;
    }
    
    if (!tryLoad())
    {
      mStateMachine = mBackend.create();
    }
    
    assert(mStateMachine != null);
    
    if (mAutoSave)
    {
      mStateMachine.addObserver(this);
    }
    
    return mStateMachine;
  }
  
  public void save(StateMachine stateMachine)
  {
    assert(stateMachine != null);
    
    Log.d(String.format("Saving state machine in state %s",
        stateMachine.getCurrentState().getName()));
    
    mBackend.save(stateMachine);
  }

  @Override
  public void update(Observable<TransitionEvent> observable, TransitionEvent event)
  {
    if (mAutoSave && mStateMachine == observable)
    {
      save(mStateMachine);
    }
  }
  
  private boolean tryLoad()
  {
    if (mBackend.canLoad())
    {
      Log.d("Loading state machine");
      
      mStateMachine = mBackend.load();
      return true;
    }
    
    return false;
  }
}
