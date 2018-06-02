/*
 * Copyright 2017 MovingBlocks
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
 */
package org.terasology.las.dialog.action;

import com.google.common.collect.ImmutableMap;
import org.terasology.persistence.typeHandling.*;

import java.util.Map;

@RegisterTypeHandler
public class TeleportPlayerActionTypeHandler extends SimpleTypeHandler<TeleportPlayerAction> {

    public TeleportPlayerActionTypeHandler() {

    }

    @Override
    public PersistedData serialize(TeleportPlayerAction action, SerializationContext context) {
        Map<String, PersistedData> data = ImmutableMap.of(
                "type", context.create(action.getClass().getSimpleName())
                //"targetPosition", context.create(action.getTargetPosition().toString())
        );

        return context.create(data);
    }

    @Override
    public TeleportPlayerAction deserialize(PersistedData data, DeserializationContext context) {
        PersistedDataMap root = data.getAsValueMap();
        //String position = root.get("targetPostition").getAsString();
        return new TeleportPlayerAction();
    }
}
