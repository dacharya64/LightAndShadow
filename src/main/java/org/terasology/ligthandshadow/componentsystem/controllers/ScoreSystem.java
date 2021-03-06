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
package org.terasology.ligthandshadow.componentsystem.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.Event;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.ligthandshadow.componentsystem.LASUtils;
import org.terasology.ligthandshadow.componentsystem.components.BlackFlagComponent;
import org.terasology.ligthandshadow.componentsystem.components.HasFlagComponent;
import org.terasology.ligthandshadow.componentsystem.components.LASTeamComponent;
import org.terasology.ligthandshadow.componentsystem.components.RedFlagComponent;
import org.terasology.ligthandshadow.componentsystem.components.WinConditionCheckOnActivateComponent;
import org.terasology.ligthandshadow.componentsystem.events.ScoreUpdateFromServerEvent;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.math.geom.Vector3i;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.ControlWidget;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.items.BlockItemComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
@Share(ScoreSystem.class)
public class ScoreSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(ScoreSystem.class);

    @In
    private InventoryManager inventoryManager;
    @In
    private NUIManager nuiManager;
    @In
    private EntityManager entityManager;
    @In
    private BlockManager blockManager;
    @In
    private WorldProvider worldProvider;

    private int redScore = 0;
    private int blackScore = 0;
    private Vector3i basePosition = null;
    private String flag = "";

    @Override
    public void postBegin() {
        // Sets score screen bindings
        ControlWidget scoreScreen = nuiManager.getHUD().getHUDElement("LightAndShadow:ScoreHud");
        UILabel blackScoreArea = scoreScreen.find("blackScoreArea", UILabel.class);
        blackScoreArea.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                return String.valueOf(blackScore);
            }
        });
        UILabel redScoreArea = scoreScreen.find("redScoreArea", UILabel.class);
        redScoreArea.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                return String.valueOf(redScore);
            }
        });
    }

    @Override
    public void initialise() {
        // Displays score UI on game start
        nuiManager.getHUD().addHUDElement("ScoreHud");
    }

    @ReceiveEvent(components = {WinConditionCheckOnActivateComponent.class, LASTeamComponent.class})
    public void onActivate(ActivateEvent event, EntityRef entity) {
        checkAndResetGameOnScore(event, entity);
    }

    private void checkAndResetGameOnScore(ActivateEvent event, EntityRef entity) {
        LASTeamComponent baseTeamComponent = entity.getComponent(LASTeamComponent.class);
        EntityRef player = event.getInstigator();
        if (player.hasComponent(HasFlagComponent.class)) {
            if (player.getComponent(LASTeamComponent.class).team.equals(LASUtils.RED_TEAM)) {
                flag = LASUtils.BLACK_FLAG_URI;
            }
            if (player.getComponent(LASTeamComponent.class).team.equals(LASUtils.BLACK_TEAM)) {
                flag = LASUtils.RED_FLAG_URI;
            }
            EntityRef heldFlag = getHeldFlag(player);
            if (checkIfTeamScores(baseTeamComponent, heldFlag)) {
                incrementScore(baseTeamComponent);
                if (redScore < LASUtils.GOAL_SCORE && blackScore < LASUtils.GOAL_SCORE) {
                    resetRound(baseTeamComponent, heldFlag);
                } else {
                    resetLevel(player, baseTeamComponent, heldFlag);
                }
            }
        }
    }

    private EntityRef getHeldFlag(EntityRef player) {
        int inventorySize = inventoryManager.getNumSlots(player);
        for (int slotNumber = 0; slotNumber <= inventorySize; slotNumber++) {
            EntityRef inventorySlot = inventoryManager.getItemInSlot(player, slotNumber);
            if (inventorySlot.hasComponent(BlockItemComponent.class)) {
                if (inventorySlot.getComponent(BlockItemComponent.class).blockFamily.getURI().toString().equals(flag)) {
                    return inventorySlot;
                }
            }
        }
        return EntityRef.NULL;
    }

    private boolean checkIfTeamScores(LASTeamComponent baseTeamComponent, EntityRef heldItem) {
        // Check to see if player has other team's flag
        if (baseTeamComponent.team.equals(LASUtils.RED_TEAM) && heldItem.hasComponent(BlackFlagComponent.class)) {
            return true;
        }
        if (baseTeamComponent.team.equals(LASUtils.BLACK_TEAM) && heldItem.hasComponent(RedFlagComponent.class)) {
            return true;
        }
        return false;
    }

    private void incrementScore(LASTeamComponent baseTeamComponent) {
        if (baseTeamComponent.team.equals(LASUtils.RED_TEAM)) {
            redScore++;
            // Send event to clients to update their Score UI
            sendEventToClients(new ScoreUpdateFromServerEvent(LASUtils.RED_TEAM, redScore));
            return;
        }
        if (baseTeamComponent.team.equals(LASUtils.BLACK_TEAM)) {
            blackScore++;
            // Send event to clients to update their Score UI
            sendEventToClients(new ScoreUpdateFromServerEvent(LASUtils.BLACK_TEAM, blackScore));
            return;
        }
    }

    private void resetRound(LASTeamComponent baseTeamComponent, EntityRef heldItem) {
        Iterable<EntityRef> playersWithFlag = entityManager.getEntitiesWith(HasFlagComponent.class);
        for (EntityRef playerWithFlag : playersWithFlag) {
            movePlayerFlagToBase(playerWithFlag, baseTeamComponent, heldItem);
        }
    }

    // TODO: Handle level reset
    private void resetLevel(EntityRef player, LASTeamComponent baseTeamComponent, EntityRef heldItem) {
    }

    private void movePlayerFlagToBase(EntityRef player, LASTeamComponent baseTeamComponent, EntityRef heldItem) {
        if (baseTeamComponent.team.equals(LASUtils.RED_TEAM)) {
            basePosition = LASUtils.CENTER_BLACK_BASE_POSITION;
            flag = LASUtils.BLACK_FLAG_URI;
        }
        if (baseTeamComponent.team.equals(LASUtils.BLACK_TEAM)) {
            basePosition = LASUtils.CENTER_RED_BASE_POSITION;
            flag = LASUtils.RED_FLAG_URI;
        }
        inventoryManager.removeItem(player, player, heldItem, true);
        worldProvider.setBlock(new Vector3i(basePosition.x, basePosition.y + 1, basePosition.z), blockManager.getBlock(flag));
    }

    private void sendEventToClients(Event event) {
        if (entityManager.getCountOfEntitiesWith(ClientComponent.class) != 0) {
            Iterable<EntityRef> clients = entityManager.getEntitiesWith(ClientComponent.class);
            for (EntityRef client : clients) {
                client.send(event);
            }
        }
    }
}
