package com.orientechnologies.orient.core.storage.impl.local.paginated.wal.po.cellbtree.singlevalue.v1.bucket;

import com.orientechnologies.common.directmemory.OByteBufferPool;
import com.orientechnologies.common.directmemory.OPointer;
import com.orientechnologies.orient.core.storage.cache.OCacheEntry;
import com.orientechnologies.orient.core.storage.cache.OCacheEntryImpl;
import com.orientechnologies.orient.core.storage.cache.OCachePointer;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.po.PageOperationRecord;
import com.orientechnologies.orient.core.storage.index.sbtree.singlevalue.v1.CellBTreeBucketSingleValueV1;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class CellBTreeBucketSingleValueV1SwitchBucketTypePOTest {
  @Test
  public void testRedo() {
    final int pageSize = 64 * 1024;
    final OByteBufferPool byteBufferPool = new OByteBufferPool(pageSize);
    try {
      final OPointer pointer = byteBufferPool.acquireDirect(false);
      final OCachePointer cachePointer = new OCachePointer(pointer, byteBufferPool, 0, 0);
      final OCacheEntry entry = new OCacheEntryImpl(0, 0, cachePointer);

      CellBTreeBucketSingleValueV1 bucket = new CellBTreeBucketSingleValueV1(entry);
      bucket.init(true);

      entry.clearPageOperations();

      final OPointer restoredPointer = byteBufferPool.acquireDirect(false);
      final OCachePointer restoredCachePointer =
          new OCachePointer(restoredPointer, byteBufferPool, 0, 0);
      final OCacheEntry restoredCacheEntry = new OCacheEntryImpl(0, 0, restoredCachePointer);

      final ByteBuffer originalBuffer = cachePointer.getBufferDuplicate();
      final ByteBuffer restoredBuffer = restoredCachePointer.getBufferDuplicate();

      Assert.assertNotNull(originalBuffer);
      Assert.assertNotNull(restoredBuffer);

      restoredBuffer.put(originalBuffer);

      bucket.switchBucketType();

      final List<PageOperationRecord> operations = entry.getPageOperations();
      Assert.assertEquals(1, operations.size());

      Assert.assertTrue(
          operations.get(0) instanceof CellBTreeBucketSingleValueV1SwitchBucketTypePO);

      final CellBTreeBucketSingleValueV1SwitchBucketTypePO pageOperation =
          (CellBTreeBucketSingleValueV1SwitchBucketTypePO) operations.get(0);

      CellBTreeBucketSingleValueV1<Byte> restoredBucket =
          new CellBTreeBucketSingleValueV1<>(restoredCacheEntry);

      Assert.assertTrue(restoredBucket.isLeaf());

      pageOperation.redo(restoredCacheEntry);

      Assert.assertFalse(restoredBucket.isLeaf());

      byteBufferPool.release(pointer);
      byteBufferPool.release(restoredPointer);
    } finally {
      byteBufferPool.clear();
    }
  }

  @Test
  public void testUndo() {
    final int pageSize = 64 * 1024;

    final OByteBufferPool byteBufferPool = new OByteBufferPool(pageSize);
    try {
      final OPointer pointer = byteBufferPool.acquireDirect(false);
      final OCachePointer cachePointer = new OCachePointer(pointer, byteBufferPool, 0, 0);
      final OCacheEntry entry = new OCacheEntryImpl(0, 0, cachePointer);

      CellBTreeBucketSingleValueV1 bucket = new CellBTreeBucketSingleValueV1(entry);
      bucket.init(true);

      entry.clearPageOperations();

      bucket.switchBucketType();

      final List<PageOperationRecord> operations = entry.getPageOperations();
      Assert.assertEquals(1, operations.size());

      Assert.assertTrue(
          operations.get(0) instanceof CellBTreeBucketSingleValueV1SwitchBucketTypePO);

      final CellBTreeBucketSingleValueV1SwitchBucketTypePO pageOperation =
          (CellBTreeBucketSingleValueV1SwitchBucketTypePO) operations.get(0);

      final CellBTreeBucketSingleValueV1<Byte> restoredBucket =
          new CellBTreeBucketSingleValueV1<>(entry);

      Assert.assertFalse(restoredBucket.isLeaf());

      pageOperation.undo(entry);

      Assert.assertTrue(restoredBucket.isLeaf());

      byteBufferPool.release(pointer);
    } finally {
      byteBufferPool.clear();
    }
  }
}
