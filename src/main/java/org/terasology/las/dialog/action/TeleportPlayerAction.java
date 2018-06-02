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

import org.terasology.dialogs.action.PlayerAction;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;

public class TeleportPlayerAction implements PlayerAction {
   // private Vector3f targetPosition;

    @In
    private LocalPlayer localPlayer;

    @Override
    public void execute(EntityRef charEntity, EntityRef talkTo) {

        EntityRef controller = charEntity.getComponent(CharacterComponent.class).controller; // the client
        ClientComponent clientComponent = controller.getComponent(ClientComponent.class);
        EntityRef playerEntity = clientComponent.character;
        //EntityRef playerEntity = localPlayer.getCharacterEntity();
        LocationComponent playerLocation = playerEntity.getComponent(LocationComponent.class);
        playerLocation.setLocalPosition(new Vector3f(29, 10, 0));

//
//        //handle which base player should go to
//        if (playerEntity.getComponent(LASTeam.class).team.equals("red")) {
//            targetPosition = new Vector3f(29, 10, 0);
//            playerLocation.setLocalPosition(targetPosition);
//        }
//        if (playerEntity.getComponent(LASTeam.class).team.equals("black")) {
//            targetPosition = new Vector3f(-29, 10, 0);
//            playerLocation.setLocalPosition(targetPosition);
//        }
    }
    //public Vector3f getTargetPosition() {
        //return targetPosition;
    //}
}
