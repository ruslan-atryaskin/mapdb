package org.mapdb.serializer;

import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.util.Comparator;

/**
 * Extension of {@link Serializer} to serialize group of objects together.
 * <p/>
 * This is mostly used in BTreeMap where BTree node stores multiple values in single array.
 * Some data types can be optimized when stored together. For example multiple {@code java.util.Date}s can be
 * represented by primitive {@code long[]} with much improved memory usage.
 * GroupSerializer is than used to access Nth item from {@code long[]} and convert it from/to object.
 * <p/>
 * GroupSerializer might also compress serialized data.
 * For example sorted data can be efficiently compressed with delta compression.
 */
public abstract class GroupSerializer<A> extends Serializer<A> {

    public A valueArrayBinaryGet(DataInput2 input, int keysLen, int pos) throws IOException {
        Object keys = valueArrayDeserialize(input, keysLen);
        return valueArrayGet(keys, pos);
//        A a=null;
//        while(pos-- >= 0){
//            a = deserialize(input, -1);
//        }
//        return a;
    }



    public int valueArrayBinarySearch(A key, DataInput2 input, int keysLen, Comparator comparator) throws IOException {
        Object keys = valueArrayDeserialize(input, keysLen);
        return valueArraySearch(keys, key, comparator);
//        for(int pos=0; pos<keysLen; pos++){
//            A from = deserialize(input, -1);
//            int comp = compare(key, from);
//            if(comp==0)
//                return pos;
//            if(comp<0)
//                return -(pos+1);
//        }
//        return -(keysLen+1);
    }


    public abstract int valueArraySearch(Object keys, A key);

    public abstract int valueArraySearch(Object keys, A key, Comparator comparator);

    public abstract void valueArraySerialize(DataOutput2 out, Object vals) throws IOException;

    public abstract Object valueArrayDeserialize(DataInput2 in, int size) throws IOException;

    public abstract A valueArrayGet(Object vals, int pos);

    public abstract int valueArraySize(Object vals);

    public abstract Object valueArrayEmpty();

    public abstract Object valueArrayPut(Object vals, int pos, A newValue);


    public abstract Object valueArrayUpdateVal(Object vals, int pos, A newValue);

    public abstract Object valueArrayFromArray(Object[] objects);

    public abstract Object valueArrayCopyOfRange(Object vals, int from, int to);

    public abstract Object valueArrayDeleteValue(Object vals, int pos);

    public Object[] valueArrayToArray(Object vals){
        Object[] ret = new Object[valueArraySize(vals)];
        for(int i=0;i<ret.length;i++){
            ret[i] = valueArrayGet(vals,i);
        }
        return ret;
    }


    /** returns value+1, or null if there is no bigger value. */
    public A nextValue(A value){
        throw new UnsupportedOperationException("Next Value not supported");
    }

}
