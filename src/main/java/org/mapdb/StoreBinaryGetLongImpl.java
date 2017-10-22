package org.mapdb;

import java.io.IOException;

import static org.mapdb.tree.IndexTreeListJava.dirOffsetFromLong;
import static org.mapdb.tree.IndexTreeListJava.treePos;


public class StoreBinaryGetLongImpl implements StoreBinaryGetLong {

    private int dirShift;
    private int level;
    private long index;

    public StoreBinaryGetLongImpl(int dirShift, int level, long index){
        this.dirShift = dirShift;
        this.level = level;
        this.index = index;
    }

    @Override
    public long get(DataInput2 input, int size) throws IOException {
        long bitmap1 = input.readLong();
        long bitmap2 = input.readLong();

        //index
        int dirPos = dirOffsetFromLong(bitmap1, bitmap2, treePos(dirShift, level, index));
        if(dirPos<0){
            //not set
            return 0L;
        }

        //second value is index, it is delta packed and can not be skipped, reenable binaryGet once its supported

        //skip until offset
        //input.unpackLongSkip(dirPos-2);
        long oldIndex=0;
        for(int i=0; i<(dirPos-2)/2;i++){
            input.unpackLong();
            oldIndex += input.unpackLong();
        }

        long recid1 = input.unpackLong();
        if(recid1 ==0)
            return 0L; //TODO this should not be here, if tree collapse exist

        oldIndex += input.unpackLong()-1;

        if (oldIndex == index) {
            //found it, return value (recid)
            return recid1;
        }else  if (oldIndex != -1) {
            // there is wrong index stored here, given index is not found
            return 0L;
        }

        return -recid1; //continue
    }
}
