/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.yahoo.dataplatform.mds.blockindex;

import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.util.Set;

import jp.co.yahoo.dataplatform.mds.constants.PrimitiveByteLength;

import jp.co.yahoo.dataplatform.mds.spread.column.filter.IFilter;
import jp.co.yahoo.dataplatform.mds.spread.column.filter.NumberFilter;
import jp.co.yahoo.dataplatform.mds.spread.column.filter.NumberRangeFilter;

public class IntegerRangeBlockIndex implements IBlockIndex{

  private int min;
  private int max;

  public IntegerRangeBlockIndex(){
    min = Integer.MAX_VALUE;
    max = Integer.MIN_VALUE;
  }

  public IntegerRangeBlockIndex( final int min , final int max ){
    this.min = min;
    this.max = max;
  }

  @Override
  public BlockIndexType getBlockIndexType(){
    return BlockIndexType.RANGE_INTEGER;
  }

  @Override
  public boolean merge( final IBlockIndex blockIndex ){
    if( ! ( blockIndex instanceof IntegerRangeBlockIndex ) ){
      return false;
    }
    IntegerRangeBlockIndex numberBlockIndex = (IntegerRangeBlockIndex)blockIndex;
    if( numberBlockIndex.getMin() < min ){
      min = numberBlockIndex.getMin();
    }
    if( max < numberBlockIndex.getMax() ){
      max = numberBlockIndex.getMax();
    }
    return true;
  }

  @Override
  public int getBinarySize(){
    return PrimitiveByteLength.INT_LENGTH * 2;
  }

  @Override
  public byte[] toBinary(){
    byte[] result = new byte[getBinarySize()];
    ByteBuffer wrapBuffer = ByteBuffer.wrap( result );
    wrapBuffer.putInt( min );
    wrapBuffer.putInt( max );
    return result;
  }

  @Override
  public void setFromBinary( final byte[] buffer , final int start , final int length ){
    ByteBuffer wrapBuffer = ByteBuffer.wrap( buffer );
    min = wrapBuffer.getInt();
    max = wrapBuffer.getInt();
  }

  @Override
  public boolean canBlockSkip( final IFilter filter ){
    switch( filter.getFilterType() ){
      case NUMBER:
        NumberFilter numberFilter = (NumberFilter)filter;
        int setNumber;
        try{
          setNumber = numberFilter.getNumberObject().getInt();
        }catch( NumberFormatException|IOException e ){
          return false;
        }
        switch( numberFilter.getNumberFilterType() ){
          case EQUAL:
            if( setNumber < min || max < setNumber  ){
              return true;
            }
            return false;
          case LT:
            if( setNumber <= min ){
              return true;
            }
            return false;
          case LE:
            if( setNumber < min ){
              return true;
            }
            return false;
          case GT:
            if( max <= setNumber ){
              return true;
            }
            return false;
          case GE:
            if( max < setNumber ){
              return true;
            }
            return false;
          default:
            return false;
        }
      case NUMBER_RANGE:
        NumberRangeFilter numberRangeFilter = (NumberRangeFilter)filter;
        int setMin;
        int setMax;
        try{
          setMin = numberRangeFilter.getMinObject().getInt();
          setMax = numberRangeFilter.getMaxObject().getInt();
        }catch( NumberFormatException|IOException e ){
          return false;
        }
        boolean minHasEquals = numberRangeFilter.isMinHasEquals();
        boolean maxHasEquals = numberRangeFilter.isMaxHasEquals();
        boolean invert = numberRangeFilter.isInvert();
        if( minHasEquals && maxHasEquals ){
          if( ( min < setMin && setMax < max ) != invert ){
            return true;
          }
          return false;
        }
        else if( minHasEquals ){
          if( ( min < setMin && setMax <= max ) != invert ){
            return true;
          }
          return false;
        }
        else if( maxHasEquals ){
          if( ( min <= setMin && setMax < max ) != invert ){
            return true;
          }
          return false;
        }
        else{
          if( ( min <= setMin && setMax <= max ) != invert ){
            return true;
          }
          return false;
        }
      default:
        return false;
    }
  }

  @Override
  public IBlockIndex getNewInstance(){
    return new IntegerRangeBlockIndex();
  }

  public int getMin(){
    return min;
  }

  public int getMax(){
    return max;
  }

}
