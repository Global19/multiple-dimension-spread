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
package jp.co.yahoo.dataplatform.mds.spread.column;

import java.io.IOException;

import org.apache.arrow.vector.Float8Vector;

import jp.co.yahoo.dataplatform.schema.objects.DoubleObj;
import jp.co.yahoo.dataplatform.schema.objects.PrimitiveObject;

import jp.co.yahoo.dataplatform.mds.inmemory.IMemoryAllocator;
import jp.co.yahoo.dataplatform.mds.spread.column.index.ICellIndex;
import jp.co.yahoo.dataplatform.mds.spread.column.filter.IFilter;
import jp.co.yahoo.dataplatform.mds.spread.expression.IExpressionIndex;

public class ArrowDoubleConnector implements IArrowPrimitiveConnector {

  private final String columnName;
  private final Float8Vector vector;

  public ArrowDoubleConnector( final String columnName , final Float8Vector vector ){
    this.columnName = columnName;
    this.vector = vector;
  }

  @Override
  public String getColumnName(){
    return columnName;
  }

  @Override
  public ColumnType getColumnType(){
    return ColumnType.DOUBLE;
  }

  @Override
  public void add( final ICell cell , final int index ){
    throw new UnsupportedOperationException( "This column is read only." );
  }

  @Override
  public ICell get( final int index , final ICell defaultCell ){
    if( vector.isNull( index ) ){
      return defaultCell;
    }
    return new PrimitiveCell( ColumnType.DOUBLE , new DoubleObj( vector.get( index ) ) );
  }

  @Override
  public int size(){
    return vector.getValueCount();
  }

  @Override
  public void clear(){
    vector.clear();
  }

  @Override
  public void setIndex( final ICellIndex index ){
    throw new UnsupportedOperationException( "This column is read only." );
  }

  @Override
  public boolean[] filter( final IFilter filter , final boolean[] filterArray ) throws IOException{
    throw new UnsupportedOperationException( "This column is read only." );
  }

  @Override
  public PrimitiveObject[] getPrimitiveObjectArray( final IExpressionIndex indexList , final int start , final int length ){
    PrimitiveObject[] result = new PrimitiveObject[length];
    for( int i = start ; i < ( start + length ) && i < size() ; i++ ){
      if( ! vector.isNull( i ) ){
        result[i - start] =  new DoubleObj( vector.get( i ) );
      }
    }
    return result;
  }

  @Override
  public void setPrimitiveObjectArray( final IExpressionIndex indexList , final int start , final int length , final IMemoryAllocator allocator ){
    for( int i = start ; i < ( start + length ) && i < size() ; i++ ){
      if( vector.isNull( i ) ){
        allocator.setNull( i - start );
      }
      else{
        try{
         allocator.setPrimitiveObject( i - start , new DoubleObj( vector.get( i ) ) );
        }catch( IOException e ){
          throw new RuntimeException( e );
        }
      }
    }
  }

}
