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
package handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Admin game point commands.
 * @author Pandragon
 */
public class AdminGamePoints implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_add_game_points",
		"admin_count_game_points",
		"admin_gamepoints",
		"admin_set_game_points",
		"admin_subtract_game_points"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_add_game_points"))
		{
			try
			{
				if ((activeChar.getTarget() != null) && activeChar.getTarget().isPlayer())
				{
					String val = command.substring(22);
					if (!addGamePoints(activeChar, val))
					{
						activeChar.sendMessage("사용법: //add_game_points 숫자 입력");
					}
				}
				else
				{
					activeChar.sendMessage("타겟이 없습니다.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("사용법: //add_game_points 숫자 입력");
			}
		}
		else if (command.equals("admin_count_game_points"))
		{
			if ((activeChar.getTarget() != null) && activeChar.getTarget().isPlayer())
			{
				L2PcInstance target = (L2PcInstance) activeChar.getTarget();
				activeChar.sendMessage(target.getName() + "님의 총 아이템 몰 포인트는 " + target.getGamePoints() + "포인트입니다.");
			}
			else
			{
				activeChar.sendMessage("타겟이 없습니다.");
			}
		}
		else if (command.equals("admin_gamepoints"))
		{
			openGamePointsMenu(activeChar);
		}
		else if (command.startsWith("admin_set_game_points"))
		{
			try
			{
				if ((activeChar.getTarget() != null) && activeChar.getTarget().isPlayer())
				{
					String val = command.substring(22);
					if (!setGamePoints(activeChar, val))
					{
						activeChar.sendMessage("사용법: //set_game_points 숫자 입력");
					}
				}
				else
				{
					activeChar.sendMessage("타겟이 없습니다.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of missing parameter
				activeChar.sendMessage("사용법: //set_game_points 숫자 입력");
			}
		}
		else if (command.startsWith("admin_subtract_game_points"))
		{
			try
			{
				if ((activeChar.getTarget() != null) && activeChar.getTarget().isPlayer())
				{
					String val = command.substring(27);
					if (!subtractGamePoints(activeChar, val))
					{
						activeChar.sendMessage("사용법: //subtract_game_points 숫자 입력");
					}
				}
				else
				{
					activeChar.sendMessage("타겟이 없습니다.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of missing parameter
				activeChar.sendMessage("사용법: //subtract_game_points 숫자 입력");
			}
		}
		return true;
	}
	
	private void openGamePointsMenu(L2PcInstance activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setFile(activeChar.getHtmlPrefix(), "data/html/admin/game_points.htm");
		activeChar.sendPacket(html);
	}
	
	private boolean addGamePoints(L2PcInstance admin, String val)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target.isPlayer())
		{
			player = (L2PcInstance) target;
		}
		else
		{
			admin.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		
		final Long points = Long.valueOf(val);
		if (points < 1)
		{
			admin.sendMessage("입력이 바르지 않습니다.");
			return false;
		}
		
		final long currentPoints = player.getGamePoints();
		if (currentPoints < 1)
		{
			player.setGamePoints(points);
		}
		else
		{
			player.setGamePoints(currentPoints + points);
		}
		
		player.sendMessage("GM이 " + player.getName() + "님에게 아이템 몰 포인트 " + points + "포인트를 주었습니다.");
		player.sendMessage("아이템 몰 포인트 " + points + "포인트를 획득하였습니다.");
		admin.sendMessage(player.getName() + "님에게 아이템 몰 포인트 " + points + "포인트를 주었습니다.");
		admin.sendMessage(player.getName() + "님의 총 아이템 몰 포인트는 " + player.getGamePoints() + "포인트입니다.");
		return true;
	}
	
	private boolean setGamePoints(L2PcInstance admin, String val)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target.isPlayer())
		{
			player = (L2PcInstance) target;
		}
		else
		{
			admin.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		
		final Long points = Long.valueOf(val);
		if (points < 0)
		{
			admin.sendMessage("입력이 바르지 않습니다.");
			return false;
		}
		
		player.setGamePoints(points);
		player.sendMessage("GM이 " + player.getName() + "님의 아이템 몰 포인트를 " + points + "포인트로 변경하였습니다.");
		player.sendMessage("아이템 몰 포인트가 " + points + "포인트로 변경되었습니다.");
		admin.sendMessage(player.getName() + "님의 아이템 몰 포인트를 " + points + "포인트로 변경하였습니다.");
		admin.sendMessage(player.getName() + "님의 총 아이템 몰 포인트는 " + points + "포인트입니다.");
		return true;
	}
	
	private boolean subtractGamePoints(L2PcInstance admin, String val)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target.isPlayer())
		{
			player = (L2PcInstance) target;
		}
		else
		{
			admin.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		
		final Long points = Long.valueOf(val);
		if (points < 1)
		{
			admin.sendMessage("입력이 바르지 않습니다.");
			return false;
		}
		
		final long currentPoints = player.getGamePoints();
		if (currentPoints <= points)
		{
			player.setGamePoints(0);
			player.sendMessage("GM이 " + player.getName() + "님의 아이템 몰 포인트 " + currentPoints + "포인트를 회수하였습니다.");
			player.sendMessage("아이템 몰 포인트 " + currentPoints + "포인트가 감소되었습니다.");
			admin.sendMessage(player.getName() + "님의 아이템 몰 포인트 " + currentPoints + "포인트를 회수하였습니다.");
		}
		else
		{
			player.setGamePoints(currentPoints - points);
			player.sendMessage("GM이 " + player.getName() + "님의 아이템 몰 포인트 " + points + "포인트를 회수하였습니다.");
			player.sendMessage("아이템 몰 포인트 " + points + "포인트가 감소되었습니다.");
			admin.sendMessage(player.getName() + "님의 아이템 몰 포인트 " + points + "포인트를 회수하였습니다.");
		}
		admin.sendMessage(player.getName() + "님의 총 아이템 몰 포인트는 " + player.getGamePoints() + "포인트입니다.");
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}