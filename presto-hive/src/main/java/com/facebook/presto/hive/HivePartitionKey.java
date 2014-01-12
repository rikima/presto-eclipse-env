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
package com.facebook.presto.hive;

import com.facebook.presto.spi.ColumnType;
import com.facebook.presto.spi.PartitionKey;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class HivePartitionKey
        implements PartitionKey
{
    private final String name;
    private final HiveType hiveType;
    private final String value;

    @JsonCreator
    public HivePartitionKey(
            @JsonProperty("name") String name,
            @JsonProperty("hiveType") HiveType hiveType,
            @JsonProperty("value") String value)
    {
        checkNotNull(name, "name is null");
        checkNotNull(hiveType, "hiveType is null");
        checkNotNull(value, "value is null");

        this.name = name;
        this.hiveType = hiveType;
        this.value = value;
    }

    @JsonProperty
    @Override
    public String getName()
    {
        return name;
    }

    @JsonProperty
    public HiveType getHiveType()
    {
        return hiveType;
    }

    @Override
    public ColumnType getType()
    {
        return hiveType.getNativeType();
    }

    @JsonProperty
    @Override
    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("hiveType", hiveType)
                .add("value", value)
                .toString();
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(name, hiveType, value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HivePartitionKey other = (HivePartitionKey) obj;
        return Objects.equal(this.name, other.name) &&
                Objects.equal(this.hiveType, other.hiveType) &&
                Objects.equal(this.value, other.value);
    }

    public static Function<HivePartitionKey, String> nameGetter()
    {
        return new Function<HivePartitionKey, String>()
        {
            @Override
            public String apply(HivePartitionKey input)
            {
                return input.getName();
            }
        };
    }
}
