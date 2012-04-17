package com.zachklipp.captivate.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetMap<E>
{
  private Map<E, Set<E>> mMatrix;
  
  public SetMap(E[][] matrix)
  {
    mMatrix = buildMatrixFrom2dArray(matrix);
  }
  
  public Set<E> get(E key)
  {
    return mMatrix.get(key);
  }
  
  private Map<E, Set<E>> buildMatrixFrom2dArray(E[][] array)
  {
    Set<E> toStates;
    Map<E, Set<E>> matrix = new HashMap<E, Set<E>>();
    
    for (E[] row : array)
    {
      assert(row.length > 0);
      
      toStates = buildRowFromArray(row);
      
      matrix.put(row[0], toStates);
    }
    
    return matrix;
  }
  
  private Set<E> buildRowFromArray(E[] array)
  {
    E current;
    Set<E> row = new HashSet<E>();
    
    for (int i = 0; i < array.length; i++)
    {
      current = array[i];
      assert(current != null);
      
      row.add(current);
    }
    
    return row;
  }
}
