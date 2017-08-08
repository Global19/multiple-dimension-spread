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
package jp.co.yahoo.dataplatform.mds.spread.column.filter;

public class LtStringCompareFilter implements IStringCompareFilter{

  private final String str;

  public LtStringCompareFilter( final String str ){
    this.str = str;
  }

  @Override
  public IStringComparator getStringComparator(){
    return new LtStringComparator( str );
  }

  @Override
  public StringCompareFilterType getStringCompareFilterType(){
    return StringCompareFilterType.LT;
  }

  @Override
  public FilterType getFilterType(){
    return FilterType.STRING_COMPARE;
  }

  private class LtStringComparator implements IStringComparator{

    private final String str;

    public LtStringComparator( final String str ){
      this.str = str;
    }

    @Override
    public boolean isFilterString( final String target ){
      return str.compareTo( target ) <= 0;
    }

    @Override
    public boolean isOutOfRange( final String min , final String max ){
      if( isFilterString( min ) && isFilterString( max ) ){
        return true;
      }
      return false;
    }

  }

}
