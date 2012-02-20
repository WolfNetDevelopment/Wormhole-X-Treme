/*
 * Wormhole X-Treme Plugin for Bukkit
 * Copyright (C) 2011 Lycano <https://github.com/lycano/Wormhole-X-Treme/>
 *
 * Copyright (C) 2011 Ben Echols
 *                    Dean Bailey
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.luricos.bukkit.WormholeXTreme.Wormhole.listeners;

import de.luricos.bukkit.WormholeXTreme.Wormhole.model.Stargate;
import de.luricos.bukkit.WormholeXTreme.Wormhole.model.StargateManager;
import de.luricos.bukkit.WormholeXTreme.Wormhole.utils.WXTLogger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.logging.Level;

/**
 * The listener interface for receiving wormholeXTremeRedstone events.
 * The class that is interested in processing a wormholeXTremeRedstone
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addWormholeXTremeRedstoneListener<code> method. When
 * the wormholeXTremeRedstone event occurs, that object's appropriate
 * method is invoked.
 *
 * @see WormholeXTremeRedstoneEvent
 */
public class WormholeXTremeRedstoneListener implements Listener {

    /**
     * Checks if current is new.
     *
     * @param oldCurrent
     *            the old current
     * @param newCurrent
     *            the new current
     * @return true, if is current new
     */
    private static boolean isCurrentNew(final int oldCurrent, final int newCurrent) {
        return ((oldCurrent == 0) && (newCurrent > 0)) || ((oldCurrent > 0) && (newCurrent == 0));
    }

    /**
     * Checks if current is on.
     *
     * @param oldCurrent
     *            the old current
     * @param newCurrent
     *            the new current
     * @return true, if is current on
     */
    private static boolean isCurrentOn(final int oldCurrent, final int newCurrent) {
        return (newCurrent > 0) && (oldCurrent == 0);
    }

    /* (non-Javadoc)
     * @see org.bukkit.event.block.BlockListener#onBlockRedstoneChange(org.bukkit.event.block.BlockRedstoneEvent)
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockRedstoneChange(final BlockRedstoneEvent event) {
        final Block block = event.getBlock();

        if (StargateManager.isBlockInGate(block)) {
            WXTLogger.prettyLog(Level.FINEST, false, "Caught redstone event on block: " + block.toString() + " oldCurrent: " + event.getOldCurrent() + " newCurrent: " + event.getNewCurrent());

            final Stargate stargate = StargateManager.getGateFromBlock(event.getBlock());
            if (
                    (stargate.isGateSignPowered()) && (stargate.isGateRedstonePowered()) &&
                    (block.getType().equals(Material.REDSTONE_WIRE)) && (isCurrentNew(event.getOldCurrent(), event.getNewCurrent())) &&
                    (!stargate.isGateActive())
                ) {

                if ((stargate.getGateRedstoneSignActivationBlock() != null) && block.equals(stargate.getGateRedstoneSignActivationBlock()) && isCurrentOn(event.getOldCurrent(), event.getNewCurrent())) {
                    stargate.tryClickTeleportSign(stargate.getGateDialSignBlock(), Action.PHYSICAL);
                    WXTLogger.prettyLog(Level.FINE, false, "Caught redstone sign event on gate: " + stargate.getGateName() + " block: " + block.toString());
                } else if ((stargate.getGateRedstoneDialActivationBlock() != null) && block.equals(stargate.getGateRedstoneDialActivationBlock()) && isCurrentOn(event.getOldCurrent(), event.getNewCurrent())) {
                    if (stargate.isGateActive() && (stargate.getGateTarget() != null)) {
                        stargate.shutdownStargate(true);
                        WXTLogger.prettyLog(Level.FINE, false, "Caught redstone shutdown event on gate: " + stargate.getGateName() + " block: " + block.toString());
                    }

                    if (!stargate.isGateActive() && (stargate.getGateDialSignTarget() != null) && !stargate.isGateRecentlyActive()) {
                        stargate.dialStargate(stargate.getGateDialSignTarget(), false);
                        WXTLogger.prettyLog(Level.FINE, false, "Caught redstone dial event on gate: " + stargate.getGateName() + " block: " + block.toString());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockFromToEvent(BlockFromToEvent event) {
        WXTLogger.prettyLog(Level.FINE, false, "We got a BlockFromToEvent here: " + event.getToBlock());
    }
}
