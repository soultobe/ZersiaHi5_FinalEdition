/* 
 * Copyright (C) 2004-2015 L2J DataPack 
 * 
 * This file is part of L2J DataPack. 
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * L2J DataPack is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>. 
 */
package handlers.bypasshandlers;

import com.l2jserver.Config;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

public class ChgSex implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"ChgSex"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (Config.CHGSEX_ENABLE == true)
		{
			int INITEM = Config.CHGSEX_ITEMID;
			int INITEMCOUNT = Config.CHGSEX_ITEMCOUNT;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(((L2Npc) target).getObjectId());
			L2ItemInstance InvenItem = activeChar.getInventory().getItemByItemId(INITEM);
			int InvenItemCnt = 0;
			if (InvenItem == null)
			{
				InvenItemCnt = 0;
			}
			else
			{
				InvenItemCnt = (int) InvenItem.getCount();
			}
			if (InvenItemCnt < INITEMCOUNT)
			{
				
				L2Item _item = ItemTable.getInstance().getTemplate(Config.CHGSEX_ITEMID);
				
				html.setFile(activeChar.getHtmlPrefix(), "data/html/default/chgsex-item.htm");
				html.replace("%itemname%", _item.getName());
				html.replace("%itemcount%", String.valueOf(INITEMCOUNT));
				activeChar.sendPacket(html);
				return false;
			}
			
			Race CharRace = activeChar.getRace();
			if (CharRace == Race.KAMAEL)
			{
				html.setFile(activeChar.getHtmlPrefix(), "data/html/default/chgsex-kamael.htm");
				activeChar.sendPacket(html);
				return false;
			}
			
			activeChar.destroyItemByItemId("ChgSex", INITEM, INITEMCOUNT, activeChar, true);
			activeChar.getAppearance().setSex(activeChar.getAppearance().getSex() ? false : true);
			activeChar.broadcastUserInfo();
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, 6673, 1, 1000, 0));
			html.setFile(activeChar.getHtmlPrefix(), "data/html/default/chgsex-ok.htm");
			activeChar.sendPacket(html);
		}
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}