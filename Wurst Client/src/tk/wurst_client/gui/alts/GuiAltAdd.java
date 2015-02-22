/*
 * Copyright � 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.alts;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import tk.wurst_client.Client;
import tk.wurst_client.alts.Alt;
import tk.wurst_client.alts.LoginManager;

public class GuiAltAdd extends AltEditorScreen
{
	public GuiAltAdd(GuiScreen par1GuiScreen)
	{
		super(par1GuiScreen);
	}
	
	@Override
	protected String getDoneButtonText()
	{
		return "Add";
	}

	@Override
	protected void onDoneButtonClick(GuiButton button)
	{
		if(passwordBox.getText().length() == 0)
		{// Cracked
			GuiAltList.alts.add(new Alt(emailBox.getText()));
			displayText = "";
		}else
		{// Premium
			displayText = LoginManager.check(emailBox.getText(), passwordBox.getText());
			if(displayText.equals(""))
				GuiAltList.alts.add(new Alt(emailBox.getText(), passwordBox.getText()));
		}
		if(displayText.equals(""))
		{
			GuiAltList.sortAlts();
			Client.wurst.fileManager.saveAlts();
			mc.displayGuiScreen(prevMenu);
		}else
			errorTimer = 8;
	}

	@Override
	protected String getTitle()
	{
		return "Add an Alt";
	}
}
