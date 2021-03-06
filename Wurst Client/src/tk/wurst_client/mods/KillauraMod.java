/*
 * Copyright � 2014 - 2015 Alexander01998 and contributors
 * All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import org.darkstorm.minecraft.gui.component.BoundedRangeComponent.ValueDisplay;
import tk.wurst_client.WurstClient;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.navigator.settings.SliderSetting;
import tk.wurst_client.utils.EntityUtils;

@Info(category = Category.COMBAT,
        description = "Automatically attacks everything in your range.",
        name = "Killaura")
public class KillauraMod extends Mod implements UpdateListener {
    public float normalSpeed = 20F;
    public float normalRange = 5F;
    public float yesCheatSpeed = 12F;
    public float yesCheatRange = 4.25F;
    public int fov = 360;
    public float realSpeed;
    public float realRange;

    @Override
    public void initSettings() {
        settings.add(new SliderSetting("Speed", normalSpeed, 2, 20, 0.1, ValueDisplay.DECIMAL));
        settings.add(new SliderSetting("Range", normalRange, 1, 6, 0.05, ValueDisplay.DECIMAL));
        settings.add(new SliderSetting("FOV", fov, 30, 360, 10, ValueDisplay.DEGREES));
    }

    @Override
    public NavigatorItem[] getSeeAlso() {
        WurstClient wurst = WurstClient.INSTANCE;
        //TODO THIS WILL BE AFFECTED BY THE SPECIAL FEATURES API MIGRATION
        return new NavigatorItem[]{wurst.specialFeatures.targetFeature,
                wurst.mods.getModByClass(KillauraLegitMod.class), wurst.mods.getModByClass(MultiAuraMod.class),
                wurst.mods.getModByClass(ClickAuraMod.class), wurst.mods.getModByClass(TriggerBotMod.class),
                wurst.mods.getModByClass(CriticalsMod.class)};
    }

    @Override
    public void updateSliders() {
        normalSpeed = (float) ((SliderSetting) settings.get(0)).getValue();
        yesCheatSpeed = Math.min(normalSpeed, 12F);
        normalRange = (float) ((SliderSetting) settings.get(1)).getValue();
        yesCheatRange = Math.min(normalRange, 4.25F);
        fov = (int) ((SliderSetting) settings.get(2)).getValue();
    }

    @Override
    public void onEnable() {
        WurstClient.INSTANCE.mods.disableModsByClass(KillauraLegitMod.class, MultiAuraMod.class, ClickAuraMod.class,
                TriggerBotMod.class);
        WurstClient.INSTANCE.events.add(UpdateListener.class, this);
    }

    @Override
    public void onUpdate() {
        if (WurstClient.INSTANCE.mods.getModByClass(YesCheatMod.class).isActive()) {
            realSpeed = yesCheatSpeed;
            realRange = yesCheatRange;
        } else {
            realSpeed = normalSpeed;
            realRange = normalRange;
        }
        updateMS();
        EntityLivingBase en = EntityUtils.getClosestEntity(true);
        if (hasTimePassedS(realSpeed) && en != null) {
            if (Minecraft.getMinecraft().thePlayer.getDistanceToEntity(en) <= realRange) {
                if (WurstClient.INSTANCE.mods.getModByClass(AutoSwordMod.class).isActive()) AutoSwordMod.setSlot();
                CriticalsMod.doCritical();
                WurstClient.INSTANCE.mods.getModByClass(BlockHitMod.class).doBlock();
                EntityUtils.faceEntityPacket(en);
                Minecraft.getMinecraft().thePlayer.swingItem();
                Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().thePlayer, en);
                updateLastMS();
            }
        }
    }

    @Override
    public void onDisable() {
        WurstClient.INSTANCE.events.remove(UpdateListener.class, this);
    }
}
