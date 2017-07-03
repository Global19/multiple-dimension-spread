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
package jp.co.yahoo.dataplatform.mds.example.spread.expand;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

import jp.co.yahoo.dataplatform.schema.parser.JacksonMessageReader;

import jp.co.yahoo.dataplatform.mds.spread.Spread;
import jp.co.yahoo.dataplatform.mds.spread.expand.ExpandNode;

public final class UseExpandSpread1{

  private UseExpandSpread1(){}

  public int run() throws IOException{

    System.out.println( String.format( "Load data from json file." ) );
    System.out.println( String.format( "JSON dump." ) );
    InputStream in = this.getClass().getClassLoader().getResource( "sample_expand_json.txt" ).openStream();
    BufferedReader br = new BufferedReader( new InputStreamReader( in ) );
    while( br.ready() ){
      System.out.println( br.readLine() );
    }

    in = this.getClass().getClassLoader().getResource( "sample_expand_json.txt" ).openStream();
    br = new BufferedReader( new InputStreamReader( in ) );
    
    Spread spread = new Spread();

    JacksonMessageReader jacksonReader = new JacksonMessageReader();

    System.out.println( String.format( "Create empty Spread." ) );
    System.out.println( spread.toString() );
    System.out.println( String.format( "Load data from json document." ) );
    while( br.ready() ){
      spread.addParserRow( jacksonReader.create( br.readLine() ) );
    }

    System.out.println( String.format( "Spread dump." ) );
    System.out.println( spread.toString() );

    System.out.println( String.format( "Expand items column." ) );
    List<String> nodeNameList = new ArrayList<String>();
    List<String> linkNameList = new ArrayList<String>();
    nodeNameList.add( "items" );
    linkNameList.add( "expand_items" );
    ExpandNode expandNode = new ExpandNode( nodeNameList , linkNameList );

    Spread expandSpread = expandNode.get( spread );
    System.out.println( String.format( "Expand spread dump." ) );
    System.out.println( expandSpread.toString() );

    return 0;
  }

  public static void main( final String[] args ) throws IOException{
    new UseExpandSpread1().run();
  }

}
