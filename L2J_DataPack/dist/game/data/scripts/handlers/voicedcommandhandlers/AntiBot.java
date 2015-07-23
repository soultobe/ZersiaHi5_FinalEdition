/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package handlers.voicedcommandhandlers;

import java.util.concurrent.ScheduledFuture;

import com.l2jserver.Config;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

//import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Zersia
 */

public final class AntiBot implements IVoicedCommandHandler
{
	private final String[] COMMANDS = new String[]
	{
		"antibot",
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (command.equals("antibot") && activeChar.getVariables().hasVariable("BOT_KICK_TASK"))
		{
			// Eliminar efectos:
			activeChar.getVariables().getObject("BOT_KICK_TASK", ScheduledFuture.class).cancel(true);
			activeChar.getVariables().remove("BOT_KICK_TASK");
			
			// Mensaje:
			activeChar.sendMessage(Config.ANTIBOT_MSG);
			return true;
		}
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}