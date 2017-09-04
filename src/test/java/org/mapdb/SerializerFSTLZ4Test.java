package org.mapdb;

import junit.framework.TestCase;
import org.mapdb.serializer.SerializerFSTLZ4;

public class SerializerFSTLZ4Test extends TestCase {

    public void test() {
        SerializerFSTLZ4<SerializableTestObject> serializerFSTLZ4 = new SerializerFSTLZ4<>(165);
        DBConcurrentMap<String, SerializableTestObject> map = DBMaker.memoryDB().make().hashMap("map", Serializer.STRING, serializerFSTLZ4).createOrOpen();
        for (int i = 0; i < 1000000; i++) {
            SerializableTestObject obj = new SerializableTestObject();
            final String key = Long.toString(i, 36);
            map.put(key, obj);
            assertEquals(obj, map.get(key));
        }
    }
}
