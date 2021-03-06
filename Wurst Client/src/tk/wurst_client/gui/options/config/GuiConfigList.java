/*
 * Copyright � 2014 - 2015 Alexander01998 and contributors
 * All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.options.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import tk.wurst_client.WurstClient;
import tk.wurst_client.api.Module;
import tk.wurst_client.api.ModuleConfiguration;
import tk.wurst_client.gui.GuiWurstSlot;
import tk.wurst_client.utils.F;
import tk.wurst_client.utils.ModuleUtils;

import java.util.ArrayList;
import java.util.Map;

public class GuiConfigList extends GuiWurstSlot {
    ArrayList<Map.Entry<String, ModuleConfiguration>> elements = new ArrayList<>();
    private int selectedSlot;
    private Minecraft mc;
    public GuiConfigList(Minecraft par1Minecraft, GuiScreen prevMenu) {
        super(par1Minecraft, prevMenu.width, prevMenu.height, 36, prevMenu.height - 56, 30);
        mc = par1Minecraft;
        WurstClient.INSTANCE.files.saveModuleConfigs();
        //Add entry for modules that have no entries but still accept configuration
        ModuleUtils.getAllModules().stream()
                .filter(module -> (!ModuleConfiguration.CONFIGURATION.containsKey(module.getClass().getName())) &&
                        module.isUsesConfig()).forEach(ModuleConfiguration::forModule);
        elements.addAll(ModuleConfiguration.CONFIGURATION.entrySet());
    }

    @Override
    protected boolean isSelected(int id) {
        return selectedSlot == id;
    }

    protected int getSelectedSlot() {
        return selectedSlot;
    }

    @Override
    protected int getSize() {
        return ModuleConfiguration.CONFIGURATION.size();
    }

    @Override
    protected void elementClicked(int var1, boolean var2, int var3, int var4) {
        selectedSlot = var1;
    }

    @Override
    protected void drawBackground() {
    }

    public Module moduleFromClassName(String name) throws ClassNotFoundException {
        ArrayList<Module> modules = new ArrayList<>();
        WurstClient.INSTANCE.mods.getAllMods().stream().filter(mod -> !modules.contains(mod)).forEach(modules::add);
        WurstClient.INSTANCE.commands.getAllCommands().stream().filter(cmd -> !modules.contains(cmd))
                .forEach(modules::add);
        ModuleUtils.miscModules.stream().filter(module -> !modules.contains(module)).forEach(modules::add);
        for (Module module : modules) {
            if (module.getClass().getName().equals(name)) {
                return module;
            }
        }
        return null;
    }

    @Override
    protected void drawSlot(int id, int x, int y, int var4, int var5, int var6) {
        Map.Entry<String, ModuleConfiguration> entry = elements.get(id);
        String name = entry.getKey();
        boolean unloaded = false;
        try {
            Module module = moduleFromClassName(name);
            if (module == null) throw new ClassNotFoundException();
            name = ModuleUtils.getModuleName(module);
        } catch (ClassNotFoundException e) {
            unloaded = true;
            System.out.println("[EvenWurse] Could not find module associated with config class: '" + name +
                    "', using class name!");
            e.printStackTrace();
        }
        mc.fontRendererObj.drawString("Module: " + name, x, y + 3, 10526880);
        String string = "Number of configuration entries: " + entry.getValue().getConfig().size();
        if (unloaded) {
            string = F.f("<RED>MODULE NOT LOADED</RED>");
        }
        mc.fontRendererObj.drawString(string, x, y + 15, 10526880);
    }
}
