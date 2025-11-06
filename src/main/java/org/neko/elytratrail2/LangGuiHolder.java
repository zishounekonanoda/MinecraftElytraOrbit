package org.neko.elytratrail2;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Marker interface to identify the language selection GUI.
 */
public class LangGuiHolder implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        return null; // Unused, just need the type check
    }
}
