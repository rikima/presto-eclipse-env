/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.tuple;

import com.facebook.presto.tuple.TupleInfo.Builder;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;
import org.testng.annotations.Test;

import static com.facebook.presto.tuple.TupleInfo.Type.BOOLEAN;
import static com.facebook.presto.tuple.TupleInfo.Type.DOUBLE;
import static com.facebook.presto.tuple.TupleInfo.Type.FIXED_INT_64;
import static com.facebook.presto.tuple.TupleInfo.Type.VARIABLE_BINARY;
import static com.facebook.presto.tuple.Tuples.NULL_LONG_TUPLE;
import static io.airlift.slice.SizeOf.SIZE_OF_BYTE;
import static io.airlift.slice.SizeOf.SIZE_OF_DOUBLE;
import static io.airlift.slice.SizeOf.SIZE_OF_INT;
import static io.airlift.slice.SizeOf.SIZE_OF_LONG;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TestTupleInfo
{
    @Test
    public void testSingleBooleanLength()
    {
        TupleInfo info = new TupleInfo(BOOLEAN);

        Tuple tuple = info.builder()
                .append(true)
                .build();

        assertEquals(tuple.getBoolean(0), true);
        assertEquals(tuple.size(), SIZE_OF_BYTE + SIZE_OF_BYTE);

        tuple = info.builder()
                .append(false)
                .build();

        assertFalse(tuple.isNull(0));
        assertEquals(tuple.getBoolean(0), false);
        assertEquals(tuple.size(), SIZE_OF_BYTE + SIZE_OF_BYTE);
    }

    /**
     * The following classes depend on this exact memory layout
     *
     * @see com.facebook.presto.block.uncompressed.UncompressedBooleanBlockCursor
     * @see com.facebook.presto.block.uncompressed.UncompressedBlock
     */
    @Test
    public void testOnlySingleBooleanMemoryLayout()
    {
        TupleInfo info = new TupleInfo(BOOLEAN);

        Tuple tuple = info.builder()
                .append(true)
                .build();

        Slice tupleSlice = tuple.getTupleSlice();
        assertEquals(tupleSlice.length(), SIZE_OF_BYTE + SIZE_OF_BYTE);
        // null bit set is in first byte
        assertEquals(tupleSlice.getByte(0), 0);
        assertEquals(tupleSlice.getByte(SIZE_OF_BYTE), 1);

        tuple = info.builder()
                .append(false)
                .build();

        tupleSlice = tuple.getTupleSlice();
        assertEquals(tupleSlice.length(), SIZE_OF_BYTE + SIZE_OF_BYTE);
        // null bit set is in first byte
        assertEquals(tupleSlice.getByte(0), 0);
        assertEquals(tupleSlice.getByte(SIZE_OF_BYTE), 0);
    }

    @Test
    public void testSingleBooleanLengthNull()
    {
        Tuple tuple = TupleInfo.SINGLE_BOOLEAN.builder()
                .appendNull()
                .build();

        assertTrue(tuple.isNull(0));
        // value of a null boolean is false
        assertEquals(tuple.getBoolean(0), false);
        assertEquals(tuple.size(), SIZE_OF_BYTE + SIZE_OF_BYTE);
    }

    /**
     * The following classes depend on this exact memory layout
     *
     * @see com.facebook.presto.block.uncompressed.UncompressedBooleanBlockCursor
     * @see com.facebook.presto.block.uncompressed.UncompressedBlock
     */
    @Test
    public void testSingleBooleanLengthNullMemoryLayout()
    {
        Tuple tuple = TupleInfo.SINGLE_BOOLEAN.builder()
                .appendNull()
                .build();

        Slice tupleSlice = tuple.getTupleSlice();
        assertEquals(tupleSlice.length(), SIZE_OF_BYTE + SIZE_OF_BYTE);
        // null bit set is in first byte
        assertEquals(tupleSlice.getByte(0), 0b0000_0001);
        // value of a null boolean is 0
        assertEquals(tupleSlice.getByte(SIZE_OF_BYTE), 0, 0);
    }

    @Test
    public void testSingleLongLength()
    {
        TupleInfo info = new TupleInfo(FIXED_INT_64);

        Tuple tuple = info.builder()
                .append(42)
                .build();

        assertEquals(tuple.getLong(0), 42L);
        assertEquals(tuple.size(), SIZE_OF_LONG + SIZE_OF_BYTE);
    }

    /**
     * The following classes depend on this exact memory layout
     *
     * @see com.facebook.presto.block.uncompressed.UncompressedLongBlockCursor
     * @see com.facebook.presto.block.uncompressed.UncompressedBlock
     */
    @Test
    public void testOnlySingleLongMemoryLayout()
    {
        TupleInfo info = new TupleInfo(FIXED_INT_64);

        Tuple tuple = info.builder()
                .append(42)
                .build();

        Slice tupleSlice = tuple.getTupleSlice();
        assertEquals(tupleSlice.length(), SIZE_OF_LONG + SIZE_OF_BYTE);
        // null bit set is in first byte
        assertEquals(tupleSlice.getByte(0), 0);
        assertEquals(tupleSlice.getLong(SIZE_OF_BYTE), 42L);
    }

    @Test
    public void testSingleLongLengthNull()
    {
        Tuple tuple = TupleInfo.SINGLE_LONG.builder()
                .appendNull()
                .build();

        assertTrue(tuple.isNull(0));
        // value of a null long is 0
        assertEquals(tuple.getLong(0), 0L);
        assertEquals(tuple.size(), SIZE_OF_LONG + SIZE_OF_BYTE);
    }

    @Test
    public void testAppendWithNull()
    {
        Builder builder = TupleInfo.SINGLE_LONG.builder();
        assertTrue(builder.append(NULL_LONG_TUPLE).build().isNull(0));
    }

    /**
     * The following classes depend on this exact memory layout
     *
     * @see com.facebook.presto.block.uncompressed.UncompressedSliceBlockCursor
     * @see com.facebook.presto.block.uncompressed.UncompressedBlock
     */
    @Test
    public void testSingleLongLengthNullMemoryLayout()
    {
        Tuple tuple = TupleInfo.SINGLE_LONG.builder()
                .appendNull()
                .build();

        Slice tupleSlice = tuple.getTupleSlice();
        assertEquals(tupleSlice.length(), SIZE_OF_LONG + SIZE_OF_BYTE);
        // null bit set is in first byte
        assertEquals(tupleSlice.getByte(0), 0b0000_0001);
        // value of a null long is 0
        assertEquals(tupleSlice.getLong(SIZE_OF_BYTE), 0L);
    }

    @Test
    public void testSingleDoubleLength()
    {
        TupleInfo info = new TupleInfo(DOUBLE);

        Tuple tuple = info.builder()
                .append(42.42)
                .build();

        assertEquals(tuple.getDouble(0), 42.42);
        assertEquals(tuple.size(), SIZE_OF_DOUBLE + SIZE_OF_BYTE);
    }

    /**
     * The following classes depend on this exact memory layout
     *
     * @see com.facebook.presto.block.uncompressed.UncompressedDoubleBlockCursor
     * @see com.facebook.presto.block.uncompressed.UncompressedBlock
     */
    @Test
    public void testOnlySingleDoubleMemoryLayout()
    {
        TupleInfo info = new TupleInfo(DOUBLE);

        Tuple tuple = info.builder()
                .append(42.42)
                .build();

        Slice tupleSlice = tuple.getTupleSlice();
        assertEquals(tupleSlice.length(), SIZE_OF_LONG + SIZE_OF_BYTE);
        // null bit set is in first byte
        assertEquals(tupleSlice.getByte(0), 0);
        assertEquals(tupleSlice.getDouble(SIZE_OF_BYTE), 42.42);
    }

    @Test
    public void testSingleDoubleLengthNull()
    {
        Tuple tuple = TupleInfo.SINGLE_DOUBLE.builder()
                .appendNull()
                .build();

        assertTrue(tuple.isNull(0));
        // value of a null double is 0
        assertEquals(tuple.getDouble(0), 0.0);
        assertEquals(tuple.size(), SIZE_OF_DOUBLE + SIZE_OF_BYTE);
    }

    /**
     * The following classes depend on this exact memory layout
     *
     * @see com.facebook.presto.block.uncompressed.UncompressedDoubleBlockCursor
     * @see com.facebook.presto.block.uncompressed.UncompressedBlock
     */
    @Test
    public void testSingleDoubleLengthNullMemoryLayout()
    {
        Tuple tuple = TupleInfo.SINGLE_DOUBLE.builder()
                .appendNull()
                .build();

        Slice tupleSlice = tuple.getTupleSlice();
        assertEquals(tupleSlice.length(), SIZE_OF_DOUBLE + SIZE_OF_BYTE);
        // null bit set is in first byte
        assertEquals(tupleSlice.getByte(0), 0b0000_0001);
        // value of a null double is 0
        assertEquals(tupleSlice.getDouble(SIZE_OF_BYTE), 0, 0);
    }

    @Test
    public void testSingleVariableLength()
    {
        Slice binary = Slices.wrappedBuffer(new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9});

        Tuple tuple = TupleInfo.SINGLE_VARBINARY.builder()
                .append(binary)
                .build();

        assertEquals(tuple.size(), binary.length() + SIZE_OF_INT + SIZE_OF_BYTE);
        assertEquals(tuple.getSlice(0), binary);
    }

    /**
     * The following classes depend on this exact memory layout
     *
     * @see com.facebook.presto.block.uncompressed.UncompressedSliceBlockCursor
     * @see com.facebook.presto.block.uncompressed.UncompressedBlock
     */
    @Test
    public void testSingleVariableLengthMemoryLayout()
    {
        Slice binary = Slices.wrappedBuffer(new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9});

        Tuple tuple = TupleInfo.SINGLE_VARBINARY.builder()
                .append(binary)
                .build();

        Slice tupleSlice = tuple.getTupleSlice();
        assertEquals(tupleSlice.length(), binary.length() + SIZE_OF_INT + SIZE_OF_BYTE);
        // null bit set is in first byte
        assertEquals(tupleSlice.getByte(0), 0);
        assertEquals(tupleSlice.getInt(SIZE_OF_BYTE), binary.length() + SIZE_OF_INT + SIZE_OF_BYTE);
        assertEquals(tupleSlice.slice(SIZE_OF_INT + SIZE_OF_BYTE, binary.length()), binary);
    }

    @Test
    public void testSingleVariableLengthNull()
    {
        Tuple tuple = TupleInfo.SINGLE_VARBINARY.builder()
                .appendNull()
                .build();

        assertTrue(tuple.isNull(0));
        assertEquals(tuple.getSlice(0), Slices.EMPTY_SLICE);
        assertEquals(tuple.size(), SIZE_OF_INT + SIZE_OF_BYTE);
    }

    /**
     * The following classes depend on this exact memory layout
     *
     * @see com.facebook.presto.block.uncompressed.UncompressedSliceBlockCursor
     * @see com.facebook.presto.block.uncompressed.UncompressedBlock
     */
    @Test
    public void testSingleVariableLengthNullMemoryLayout()
    {
        Tuple tuple = TupleInfo.SINGLE_VARBINARY.builder()
                .appendNull()
                .build();

        Slice tupleSlice = tuple.getTupleSlice();
        assertEquals(tupleSlice.length(), SIZE_OF_INT + SIZE_OF_BYTE);
        // null bit set is in first byte
        assertEquals(tupleSlice.getByte(0), 0b0000_0001);
        // the size of the tuple is stored as an int starting at the second byte
        assertEquals(tupleSlice.getInt(SIZE_OF_BYTE), SIZE_OF_INT + SIZE_OF_BYTE);
    }

    //
    // Tuples with multiple tuples with multiple types do not have a declared memory layout.
    //

    @Test
    public void testMultipleVariableLength()
    {
        TupleInfo info = new TupleInfo(VARIABLE_BINARY, VARIABLE_BINARY);

        Slice binary1 = Slices.wrappedBuffer(new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        Slice binary2 = Slices.wrappedBuffer(new byte[] {10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24});

        Tuple tuple = info.builder()
                .append(binary1)
                .append(binary2)
                .build();

        assertEquals(tuple.getSlice(0), binary1);
        assertEquals(tuple.getSlice(1), binary2);
        assertEquals(tuple.size(), binary1.length() + binary2.length() + SIZE_OF_INT + SIZE_OF_INT + SIZE_OF_BYTE);
    }

    @Test
    public void testMultipleFixedLength()
    {
        TupleInfo info = new TupleInfo(FIXED_INT_64, FIXED_INT_64);

        Tuple tuple = info.builder()
                .append(42)
                .append(67)
                .build();

        assertEquals(tuple.getLong(0), 42L);
        assertEquals(tuple.getLong(1), 67L);
        assertEquals(tuple.size(), SIZE_OF_LONG + SIZE_OF_LONG + SIZE_OF_BYTE);
    }

    @Test
    public void testMixed()
    {
        TupleInfo info = new TupleInfo(FIXED_INT_64, VARIABLE_BINARY, FIXED_INT_64, VARIABLE_BINARY, FIXED_INT_64, VARIABLE_BINARY);

        Slice binary1 = Slices.wrappedBuffer(new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        Slice binary2 = Slices.wrappedBuffer(new byte[] {10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24});
        Slice binary3 = Slices.wrappedBuffer(new byte[] {30, 31, 32, 33, 34, 35});

        Tuple tuple = info.builder()
                .append(42)
                .append(binary1)
                .append(67)
                .append(binary2)
                .append(90)
                .append(binary3)
                .build();

        assertEquals(tuple.getLong(0), 42L);
        assertEquals(tuple.getSlice(1), binary1);
        assertEquals(tuple.getLong(2), 67L);
        assertEquals(tuple.getSlice(3), binary2);
        assertEquals(tuple.getLong(4), 90L);
        assertEquals(tuple.getSlice(5), binary3);
    }

    @Test
    public void testSetNullAtNonZeroOffset()
            throws Exception
    {
        TupleInfo info = new TupleInfo(FIXED_INT_64);
        Slice slice = Slices.allocate(info.getFixedSize() * 2);

        info.setNull(slice, FIXED_INT_64.getSize(), 0);
        assertTrue(info.isNull(slice, FIXED_INT_64.getSize(), 0));
    }

    @Test
    public void testSetNonNullAtNonZeroOffset()
            throws Exception
    {
        TupleInfo info = new TupleInfo(FIXED_INT_64);
        Slice slice = Slices.allocate(info.getFixedSize() * 2);

        // initialize to nulls
        info.setNull(slice, 0, 0);
        assertTrue(info.isNull(slice, 0, 0));

        info.setNull(slice, info.getFixedSize(), 0);
        assertTrue(info.isNull(slice, info.getFixedSize(), 0));

        // now test setNotNull
        info.setNotNull(slice, info.getFixedSize(), 0);
        assertFalse(info.isNull(slice, info.getFixedSize(), 0));
    }
}
