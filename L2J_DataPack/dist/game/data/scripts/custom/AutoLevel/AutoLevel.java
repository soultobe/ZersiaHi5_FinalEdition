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

package custom.AutoLevel;

import ai.npc.AbstractNpcAI;

import com.l2jserver.Config;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;

/**
 * Simple Anti Bot script for Auto Level.
 * @author Zersia
 */

public final class AutoLevel extends AbstractNpcAI
{
	// Chance for show htm after kill monster:
	private static final int CHANCE = Config.ANTIBOT_LEVEL;
	// Time for write the command in chat: (minutes)
	private static final int TIME = Config.ANTIBOT_INPUTTIME;
	private int monKillCount = 0;
	
	private AutoLevel()
	{
		super(AutoLevel.class.getSimpleName(), "custom");
		for (L2NpcTemplate npc : NpcData.getInstance().getAllNpcOfClassType("L2Monster"))
		{
			addKillId(npc.getId());
		}
	}
	
	private final class BotKickTask implements Runnable
	{
		private final L2PcInstance _player;
		
		public BotKickTask(L2PcInstance player)
		{
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_player != null)
			{
				_player.logout(true);
			}
		}
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (Config.ANTIBOT_ENABLE == true)
		{
			monKillCount++;
			if (monKillCount >= CHANCE)
			{
				// Effect :
				if (!killer.getVariables().hasVariable("BOT_KICK_TASK"))
				{
					monKillCount = 0;
					killer.getVariables().set("BOT_KICK_TASK", ThreadPoolManager.getInstance().scheduleAi(new BotKickTask(killer), TIME * 1000 * 60));
				}
				return "index.htm";
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new AutoLevel();
	}
}