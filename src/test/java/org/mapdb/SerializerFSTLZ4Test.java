package org.mapdb;

import junit.framework.TestCase;
import org.mapdb.serializer.SerializerFSTLZ4;

public class SerializerFSTLZ4Test extends TestCase {

    public void test() {
        SerializerFSTLZ4<SerializableTestObject> serializerFSTLZ4 = new SerializerFSTLZ4<>(165);
        DBConcurrentMap<SerializableTestObject, SerializableTestObject> map = DBMaker.memoryDB().make().hashMap("map", serializerFSTLZ4, serializerFSTLZ4).counterEnable().createOrOpen();
        for (int i = 0; i < 1000000; i++) {
            SerializableTestObject obj = new SerializableTestObject();
            map.put(obj, obj);
            assertEquals(obj, map.get(obj));
        }
    }
}
