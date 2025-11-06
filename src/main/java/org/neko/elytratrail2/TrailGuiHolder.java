package org.neko.elytratrail2;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Marker interface to identify the main trail selection GUI.
 */
public class TrailGuiHolder implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        return null; // Unused, just need the type check
    }
}
