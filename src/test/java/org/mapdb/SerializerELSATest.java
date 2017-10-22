package org.mapdb;

import junit.framework.TestCase;
import org.mapdb.serializer.SerializerFSTLZ4;

public class SerializerELSATest extends TestCase {

    public void test() {
        DBConcurrentMap<SerializableTestObject, SerializableTestObject> map = DBMaker.memoryDB().make().hashMap("map", Serializer.ELSA, Serializer.ELSA).counterEnable().createOrOpen();
        for (int i = 0; i < 1000000; i++) {
            SerializableTestObject obj = new SerializableTestObject();
            map.put(obj, obj);
            assertEquals(obj, map.get(obj));
        }
    }
}
