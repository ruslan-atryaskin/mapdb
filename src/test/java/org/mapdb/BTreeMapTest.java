/*
 * (C) Copyright 2015 Mikhail Vorontsov ( http://java-performance.info/ ) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *      Mikhail Vorontsov
 */

package org.mapdb;

import junit.framework.TestCase;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class BTreeMapTest extends TestCase
{

    private final int SIZE = 10000;
    private static final String NOT_PRESENT = null;

    protected DBConcurrentMap<Integer, String> makeMap()
    {
        return DBMaker.memoryDB().make().treeMap("map", Serializer.INTEGER, Serializer.STRING).counterEnable().createOrOpen();
    }

    /**
     * Add keys 0-SIZE to the map
     */
    public void testPut()
    {
        final DBConcurrentMap<Integer, String> map = makeMap();
        for ( int i = 0; i < SIZE; ++i )
        {
            assertEquals( NOT_PRESENT, map.put( i, String.valueOf( i ) ) );

            assertEquals( i + 1, map.size() );
            assertEquals( String.valueOf( i ), map.get( i ) );
        }
        //now check the final state
        assertEquals( SIZE, map.size() );
        for ( int i = 0; i < SIZE; ++i )
            assertEquals(  String.valueOf( i ), map.get( i ));
    }

    /**
     * Add a series of negative keys to the map
     */
    public void testPutNegative()
    {
        final DBConcurrentMap<Integer, String> map = makeMap();
        for ( int i = 0; i < SIZE; ++i )
        {
            map.put( -i, String.valueOf( -i ));
            assertEquals( i + 1, map.size() );
            assertEquals(  String.valueOf( -i ), map.get( -i ));
        }
        //now check the final state
        assertEquals(SIZE, map.size());
        for ( int i = 0; i < SIZE; ++i )
            assertEquals(  String.valueOf( -i ), map.get( -i ) );
    }

    /**
     * Add a set of keys to the map. Then add it again to test update operations.
     */
    public void testPutThenUpdate()
    {
        final DBConcurrentMap<Integer, String> map = makeMap();
        for ( int i = 0; i < SIZE; ++i )
        {
            assertEquals( NOT_PRESENT, map.put( i, String.valueOf( i ) ) );
            assertEquals( i + 1, map.size() );
            assertEquals( String.valueOf( i ), map.get( i ) );
        }
        //now check the initial state
        assertEquals( SIZE, map.size() );
        for ( int i = 0; i < SIZE; ++i )
            assertEquals( String.valueOf( i ), map.get( i ) );

        //now try to update all keys
        for ( int i = 0; i < SIZE; ++i )
        {
            assertEquals( String.valueOf( i ), map.put( i, String.valueOf( i + 1 ) ) );
            assertEquals( SIZE, map.size() );
            assertEquals( String.valueOf( i + 1 ), map.get( i ) );
        }
        //and check the final state
        for ( int i = 0; i < SIZE; ++i )
            assertEquals( String.valueOf( i + 1 ), map.get( i ) );
    }

    /**
     * Add random keys to the map. We use random seeds for the random generator (each test run is unique), so we log
     * the seeds used to initialize Random-s.
     */
    public void testPutRandom()
    {
        final int seed = ThreadLocalRandom.current().nextInt();
        final Random r = new Random( seed );
        final Set<Integer> set = new LinkedHashSet<>( SIZE );
        final int[] vals = new int[ SIZE ];
        while ( set.size() < SIZE )
            set.add( r.nextInt() );
        int i = 0;
        for ( final Integer v : set )
            vals[ i++ ] = v;
        final DBConcurrentMap<Integer, String> map = makeMap();
        for ( i = 0; i < vals.length; ++i )
        {
            assertEquals( NOT_PRESENT, map.put( vals[i], String.valueOf( vals[ i ] )  ) );
            assertEquals( i + 1, map.size());
            assertEquals( String.valueOf( vals[ i ] ), map.get( vals[ i ] ));
        }
        //now check the final state
        assertEquals( SIZE, map.size() );
        for ( i = 0; i < vals.length; ++i )
            assertEquals( String.valueOf( vals[ i ] ), map.get( vals[ i ] ) );
    }

    /**
     * Interleaved put and remove operations - we remove half of added entries
     */
    public void testRemove()
    {
        final DBConcurrentMap<Integer, String> map = makeMap();
        int addCnt = 0, removeCnt = 0;
        for ( int i = 0; i < SIZE; ++i )
        {
            assertEquals( NOT_PRESENT, map.put( addCnt, String.valueOf( addCnt ) ) );
            assertEquals( i + 1, map.size() );
            addCnt++;

            assertEquals( NOT_PRESENT, map.put( addCnt, String.valueOf( addCnt ) ) );
            assertEquals( i + 2, map.size() ); //map grows by one element on each iteration
            addCnt++;

            assertEquals( String.valueOf( removeCnt ), map.remove(removeCnt));
            removeCnt++;

            assertEquals( i + 1, map.size()); //map grows by one element on each iteration
        }

        assertEquals( SIZE, map.size() );
        for ( int i = removeCnt; i < addCnt; ++i )
            assertEquals( String.valueOf( i ), map.get( i ) );
    }

    public void testRandomRemove()
    {
        final Random r = new Random( 1 );
        final String[] values = new String[ SIZE ];
        Set<Integer> ks = new LinkedHashSet<>( SIZE );
        while ( ks.size() < SIZE )
            ks.add( r.nextInt() );
        final Integer[] keys = ks.toArray( new Integer[ SIZE ] );
        ks = null;

        assertEquals( SIZE, keys.length );

        for ( int i = 0; i < SIZE; ++i )
            values[ i ] = String.valueOf( r.nextInt() );

        DBConcurrentMap<Integer, String> m = makeMap();
        int add = 0, remove = 0;
        while ( add < SIZE )
        {
            assertEquals( NOT_PRESENT, m.put( keys[ add ], String.valueOf( values[ add ] ) ) );
            ++add;
            assertEquals( NOT_PRESENT, m.put( keys[ add ], String.valueOf( values[ add ] ) ) );
            ++add;

            assertEquals( String.valueOf( values[ remove ] ), m.remove( keys[ remove ] ) );
            remove++;

            assertEquals( remove, m.size() );
        }

        assertEquals( SIZE / 2, m.size() );

        for ( int i = 0; i < SIZE / 2; ++i )
            assertEquals( NOT_PRESENT, m.get( keys[ i ] ) );
        for ( int i = SIZE / 2; i < SIZE; ++i )
            assertEquals( String.valueOf( values[ i ] ), m.get( keys[ i ] ) );
    }



}