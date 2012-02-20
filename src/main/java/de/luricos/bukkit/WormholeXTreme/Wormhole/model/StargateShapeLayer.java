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
package de.luricos.bukkit.WormholeXTreme.Wormhole.model;

import de.luricos.bukkit.WormholeXTreme.Wormhole.utils.WXTLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class StargateShapeLayer.
 */
public class StargateShapeLayer {

    /** The block positions. */
    private ArrayList<Integer[]> layerBlockPositions = new ArrayList<Integer[]>();
    /** The sign position. */
    private int[] layerNameSignPosition = null;
    /** The exit position. */
    private int[] layerPlayerExitPosition = null;
    /** The minecart exit position. */
    private int[] layerMinecartExitPosition = null;
    /** The activation position. */
    private int[] layerActivationPosition = null;
    /** The iris activation position. */
    private int[] layerIrisActivationPosition = null;
    /** The dialer position. */
    private int[] layerDialSignPosition = null;
    /** Position of point that allows gate to be activated via redstone. */
    private int[] layerRedstoneDialActivationPosition = null;
    /** Position of point that allows gate to cycle sign targets via redstone. */
    private int[] layerRedstoneSignActivationPosition = null;
    /** The layer redstone activation position. */
    private int[] layerRedstoneGateActivatedPosition = null;
    /** The light_positions. */
    private ArrayList<ArrayList<Integer[]>> layerLightPositions = new ArrayList<ArrayList<Integer[]>>();
    /** The positions of woosh. First array is the order to activate them. Inner array is list of points */
    private ArrayList<ArrayList<Integer[]>> layerWooshPositions = new ArrayList<ArrayList<Integer[]>>();
    /** The water_positions. */
    private ArrayList<Integer[]> layerPortalPositions = new ArrayList<Integer[]>();

    /**
     * Instantiates a new stargate shape layer.
     *
     * @param layerLines
     *            the layer lines
     * @param height
     *            the height
     * @param width
     *            the width
     */
    protected StargateShapeLayer(final String[] layerLines, final int height, final int width) {
        @SuppressWarnings("unused")
		int numBlocks = 0;

        // 1. scan all lines for lines beginning with [  - that is the height of the gate
        for (int i = 0; i < layerLines.length; i++) {
            if (Pattern.compile("\\[(.+?)\\]") == null) {
                WXTLogger.prettyLog(Level.CONFIG, false, "Stargate Sign Position: \"" + Arrays.toString(getLayerNameSignPosition()) + "\"");
            }
            final Matcher m = Pattern.compile("\\[(.+?)\\]").matcher(layerLines[i]);
            int j = 0;
            while (m.find()) {
                final Integer[] point = {0, (height - 1 - i), (width - 1 - j)};

                for (final String mod : m.group(1).split(":")) {
                    if (mod.equalsIgnoreCase("S")) {
                        numBlocks++;
                        getLayerBlockPositions().add(point);
                    } else if (mod.equalsIgnoreCase("P")) {
                        getLayerPortalPositions().add(point);
                    } else if (mod.equalsIgnoreCase("N") || mod.equalsIgnoreCase("EP") || mod.equalsIgnoreCase("EM") || mod.equalsIgnoreCase("A") || mod.equalsIgnoreCase("D") || mod.equalsIgnoreCase("IA") || mod.equalsIgnoreCase("RA") || mod.equalsIgnoreCase("RD") || mod.equalsIgnoreCase("RS")) {
                        final int[] pointI = new int[3];
                        for (int k = 0; k < 3; k++) {
                            pointI[k] = point[k];
                        }

                        if (mod.equalsIgnoreCase("N")) {
                            setLayerNameSignPosition(pointI);
                        }
                        if (mod.equalsIgnoreCase("EP")) {
                            setLayerPlayerExitPosition(pointI);
                        }
                        if (mod.equalsIgnoreCase("EM")) {
                            setLayerMinecartExitPosition(pointI);
                        }
                        if (mod.equalsIgnoreCase("A")) {
                            setLayerActivationPosition(pointI);
                        }
                        if (mod.equalsIgnoreCase("D")) {
                            setLayerDialSignPosition(pointI);
                        }
                        if (mod.equalsIgnoreCase("IA")) {
                            setLayerIrisActivationPosition(pointI);
                        }
                        if (mod.equalsIgnoreCase("RA")) {
                            setLayerRedstoneGateActivatedPosition(pointI);
                        }
                        if (mod.equalsIgnoreCase("RD")) {
                            setLayerRedstoneDialActivationPosition(pointI);
                        }
                        if (mod.equalsIgnoreCase("RS")) {
                            setLayerRedstoneSignActivationPosition(pointI);
                        }
                    } else if (mod.contains("L") || mod.contains("l")) {
                        final int light_iteration = mod.contains("#") ? Integer.parseInt(mod.split("#")[1]) : 1;

                        while (getLayerLightPositions().size() <= light_iteration) {
                            getLayerLightPositions().add(null);
                        }

                        if (getLayerLightPositions().get(light_iteration) == null) {
                            final ArrayList<Integer[]> new_it = new ArrayList<Integer[]>();
                            getLayerLightPositions().set(light_iteration, new_it);
                        }

                        getLayerLightPositions().get(light_iteration).add(point);
                        WXTLogger.prettyLog(Level.CONFIG, false, "Light Material Position (Order:" + light_iteration + " Position:" + Arrays.toString(point) + ")");
                    } else if (mod.contains("W") || mod.contains("w")) {
                        final int w_iteration = mod.contains("#") ? Integer.parseInt(mod.split("#")[1]) : 1;

                        while (getLayerWooshPositions().size() <= w_iteration) {
                            getLayerWooshPositions().add(null);
                        }

                        if (getLayerWooshPositions().get(w_iteration) == null) {
                            final ArrayList<Integer[]> new_it = new ArrayList<Integer[]>();
                            getLayerWooshPositions().set(w_iteration, new_it);
                        }

                        getLayerWooshPositions().get(w_iteration).add(point);
                        WXTLogger.prettyLog(Level.CONFIG, false, "Woosh Position (Order:" + w_iteration + " Position:" + Arrays.toString(point) + ")");
                    }
                }
                j++;
            }
        }
        //TODO: debug printout for the materials the gate uses.
        //TODO: debug printout for the redstone_activated
        WXTLogger.prettyLog(Level.CONFIG, false, "Stargate Sign Position: \"" + Arrays.toString(getLayerNameSignPosition()) + "\"");
        WXTLogger.prettyLog(Level.CONFIG, false, "Stargate Player Exit Position: \"" + Arrays.toString(getLayerPlayerExitPosition()) + "\"");
        WXTLogger.prettyLog(Level.CONFIG, false, "Stargate Minecart Exit Position: \"" + Arrays.toString(getLayerMinecartExitPosition()) + "\"");
        WXTLogger.prettyLog(Level.CONFIG, false, "Stargate Activation Position: \"" + Arrays.toString(getLayerActivationPosition()) + "\"");
        WXTLogger.prettyLog(Level.CONFIG, false, "Stargate Iris Activation Position: \"" + Arrays.toString(getLayerIrisActivationPosition()) + "\"");
        WXTLogger.prettyLog(Level.CONFIG, false, "Stargate Dial Sign Position: \"" + Arrays.toString(getLayerDialSignPosition()) + "\"");
        WXTLogger.prettyLog(Level.CONFIG, false, "Stargate Redstone Dial Activation Position: \"" + Arrays.toString(getLayerRedstoneDialActivationPosition()) + "\"");
        WXTLogger.prettyLog(Level.CONFIG, false, "Stargate Redstone Sign Activation Position: \"" + Arrays.toString(getLayerRedstoneSignActivationPosition()) + "\"");
        WXTLogger.prettyLog(Level.CONFIG, false, "Stargate Redstone Gate Activated Position: \"" + Arrays.toString(getLayerRedstoneGateActivatedPosition()) + "\"");
        //WXTLogger.prettyLog(Level.CONFIG, false, "Stargate Portal Positions: \"" + Arrays.deepToString((int[][])this.waterPositions) + "\"");

        // WXTLogger.prettyLog(Level.CONFIG, false, "Light Material Positions: \"" + lightPositions.toString() + "\"");
        //WXTLogger.prettyLog(Level.CONFIG, false, "Stargate Material Positions: \"" + Arrays.deepToString((int[][])this.stargatePositions) + "\"");
    }

    /**
     * Gets the layer activation position.
     *
     * @return the layer activation position
     */
    public int[] getLayerActivationPosition() {
        return layerActivationPosition != null
                ? layerActivationPosition.clone()
                : new int[]{};
    }

    /**
     * Gets the layer block positions.
     *
     * @return the layer block positions
     */
    public ArrayList<Integer[]> getLayerBlockPositions() {
        return layerBlockPositions;
    }

    /**
     * Gets the layer dialer position.
     *
     * @return the layer dialer position
     */
    public int[] getLayerDialSignPosition() {
        return layerDialSignPosition != null
                ? layerDialSignPosition.clone()
                : new int[]{};
    }

    /**
     * Gets the layer iris activation position.
     *
     * @return the layer iris activation position
     */
    public int[] getLayerIrisActivationPosition() {
        return layerIrisActivationPosition != null
                ? layerIrisActivationPosition.clone()
                : new int[]{};
    }

    /**
     * Gets the layer light positions.
     *
     * @return the layer light positions
     */
    public ArrayList<ArrayList<Integer[]>> getLayerLightPositions() {
        return layerLightPositions;
    }

    /**
     * Gets the layer minecart exit position.
     *
     * @return the layer minecart exit position
     */
    public int[] getLayerMinecartExitPosition() {
        return layerMinecartExitPosition != null
                ? layerMinecartExitPosition.clone()
                : new int[]{};
    }

    /**
     * Gets the layer sign position.
     *
     * @return the layer sign position
     */
    public int[] getLayerNameSignPosition() {
        return layerNameSignPosition != null
                ? layerNameSignPosition.clone()
                : new int[]{};
    }

    /**
     * Gets the layer enter position.
     *
     * @return the layer enter position
     */
    public int[] getLayerPlayerExitPosition() {
        return layerPlayerExitPosition != null
                ? layerPlayerExitPosition.clone()
                : new int[]{};
    }

    /**
     * Gets the layer portal positions.
     *
     * @return the layer portal positions
     */
    public ArrayList<Integer[]> getLayerPortalPositions() {
        return layerPortalPositions;
    }

    /**
     * Gets the layer redstone activation position.
     *
     * @return the layer redstone activation position
     */
    public int[] getLayerRedstoneDialActivationPosition() {
        return layerRedstoneDialActivationPosition != null
                ? layerRedstoneDialActivationPosition.clone()
                : new int[]{};
    }

    /**
     * Gets the layer redstone activation position.
     *
     * @return the layer redstone activation position
     */
    public int[] getLayerRedstoneGateActivatedPosition() {
        return layerRedstoneGateActivatedPosition != null
                ? layerRedstoneGateActivatedPosition.clone()
                : new int[]{};
    }

    /**
     * Gets the layer redstone dialer activation position.
     *
     * @return the layer redstone dialer activation position
     */
    public int[] getLayerRedstoneSignActivationPosition() {
        return layerRedstoneSignActivationPosition != null
                ? layerRedstoneSignActivationPosition.clone()
                : new int[]{};
    }

    /**
     * Gets the layer woosh positions.
     *
     * @return the layer woosh positions
     */
    public ArrayList<ArrayList<Integer[]>> getLayerWooshPositions() {
        return layerWooshPositions;
    }

    /**
     * Sets the layer activation position.
     *
     * @param layerActivationPosition
     *            the new layer activation position
     */
    public void setLayerActivationPosition(final int[] layerActivationPosition) {
        this.layerActivationPosition = layerActivationPosition.clone();
    }

    /**
     * Sets the layer block positions.
     *
     * @param layerBlockPositions
     *            the new layer block positions
     */
    public void setLayerBlockPositions(final ArrayList<Integer[]> layerBlockPositions) {
        this.layerBlockPositions = layerBlockPositions;
    }

    /**
     * Sets the layer dialer position.
     *
     * @param layerDialerPosition
     *            the new layer dialer position
     */
    public void setLayerDialSignPosition(final int[] layerDialSignPosition) {
        this.layerDialSignPosition = layerDialSignPosition.clone();
    }

    /**
     * Sets the layer iris activation position.
     *
     * @param layerIrisActivationPosition
     *            the new layer iris activation position
     */
    public void setLayerIrisActivationPosition(final int[] layerIrisActivationPosition) {
        this.layerIrisActivationPosition = layerIrisActivationPosition.clone();
    }

    /**
     * Sets the layer light positions.
     *
     * @param layerLightPositions
     *            the new layer light positions
     */
    public void setLayerLightPositions(final ArrayList<ArrayList<Integer[]>> layerLightPositions) {
        this.layerLightPositions = layerLightPositions;
    }

    /**
     * Sets the layer minecart exit position.
     *
     * @param layerMinecartExitPosition
     *            the new layer minecart exit position
     */
    public void setLayerMinecartExitPosition(final int[] layerMinecartExitPosition) {
        this.layerMinecartExitPosition = layerMinecartExitPosition.clone();
    }

    /**
     * Sets the layer sign position.
     *
     * @param layerSignPosition
     *            the new layer sign position
     */
    public void setLayerNameSignPosition(final int[] layerNameSignPosition) {
        this.layerNameSignPosition = layerNameSignPosition.clone();
    }

    /**
     * Sets the layer exit position.
     *
     * @param layerPlayerExitPosition
     *            the new layer player exit position
     */
    public void setLayerPlayerExitPosition(final int[] layerPlayerExitPosition) {
        this.layerPlayerExitPosition = layerPlayerExitPosition.clone();
    }

    /**
     * Sets the layer portal positions.
     *
     * @param layerPortalPositions
     *            the new layer portal positions
     */
    public void setLayerPortalPositions(final ArrayList<Integer[]> layerPortalPositions) {
        this.layerPortalPositions = layerPortalPositions;
    }

    /**
     * Sets the layer redstone activation position.
     *
     * @param layerRedstoneDialActivationPosition
     *            the new layer redstone dial activation position
     */
    public void setLayerRedstoneDialActivationPosition(final int[] layerRedstoneDialActivationPosition) {
        this.layerRedstoneDialActivationPosition = layerRedstoneDialActivationPosition.clone();
    }

    /**
     * Sets the layer redstone activation position.
     *
     * @param layerRedstoneActivationPosition
     *            the new layer redstone activation position
     */
    public void setLayerRedstoneGateActivatedPosition(final int[] layerRedstoneGateActivatedPosition) {
        this.layerRedstoneGateActivatedPosition = layerRedstoneGateActivatedPosition.clone();
    }

    /**
     * Sets the layer redstone dialer activation position.
     *
     * @param layerRedstoneSignActivationPosition
     *            the new layer redstone sign activation position
     */
    public void setLayerRedstoneSignActivationPosition(final int[] layerRedstoneSignActivationPosition) {
        this.layerRedstoneSignActivationPosition = layerRedstoneSignActivationPosition.clone();
    }

    /**
     * Sets the layer woosh positions.
     *
     * @param layerWooshPositions
     *            the new layer woosh positions
     */
    public void setLayerWooshPositions(final ArrayList<ArrayList<Integer[]>> layerWooshPositions) {
        this.layerWooshPositions = layerWooshPositions;
    }
}
