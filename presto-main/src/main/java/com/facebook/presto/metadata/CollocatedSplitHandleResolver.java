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
package com.facebook.presto.metadata;

import com.facebook.presto.spi.ColumnHandle;
import com.facebook.presto.spi.ConnectorHandleResolver;
import com.facebook.presto.spi.Split;
import com.facebook.presto.spi.TableHandle;
import com.facebook.presto.split.CollocatedSplit;

public class CollocatedSplitHandleResolver
        implements ConnectorHandleResolver
{
    @Override
    public boolean canHandle(TableHandle tableHandle)
    {
        return false;
    }

    @Override
    public boolean canHandle(ColumnHandle columnHandle)
    {
        return false;
    }

    @Override
    public boolean canHandle(Split split)
    {
        return split instanceof CollocatedSplit;
    }

    @Override
    public Class<? extends TableHandle> getTableHandleClass()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<? extends ColumnHandle> getColumnHandleClass()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<? extends Split> getSplitClass()
    {
        return CollocatedSplit.class;
    }
}
