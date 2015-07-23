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
						activeChar.sendMessage("����: //add_game_points ���� �Է�");
					}
				}
				else
				{
					activeChar.sendMessage("Ÿ���� �����ϴ�.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("����: //add_game_points ���� �Է�");
			}
		}
		else if (command.equals("admin_count_game_points"))
		{
			if ((activeChar.getTarget() != null) && activeChar.getTarget().isPlayer())
			{
				L2PcInstance target = (L2PcInstance) activeChar.getTarget();
				activeChar.sendMessage(target.getName() + "���� �� ������ �� ����Ʈ�� " + target.getGamePoints() + "����Ʈ�Դϴ�.");
			}
			else
			{
				activeChar.sendMessage("Ÿ���� �����ϴ�.");
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
						activeChar.sendMessage("����: //set_game_points ���� �Է�");
					}
				}
				else
				{
					activeChar.sendMessage("Ÿ���� �����ϴ�.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of missing parameter
				activeChar.sendMessage("����: //set_game_points ���� �Է�");
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
						activeChar.sendMessage("����: //subtract_game_points ���� �Է�");
					}
				}
				else
				{
					activeChar.sendMessage("Ÿ���� �����ϴ�.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of missing parameter
				activeChar.sendMessage("����: //subtract_game_points ���� �Է�");
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
			admin.sendMessage("�Է��� �ٸ��� �ʽ��ϴ�.");
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
		
		player.sendMessage("GM�� " + player.getName() + "�Կ��� ������ �� ����Ʈ " + points + "����Ʈ�� �־����ϴ�.");
		player.sendMessage("������ �� ����Ʈ " + points + "����Ʈ�� ȹ���Ͽ����ϴ�.");
		admin.sendMessage(player.getName() + "�Կ��� ������ �� ����Ʈ " + points + "����Ʈ�� �־����ϴ�.");
		admin.sendMessage(player.getName() + "���� �� ������ �� ����Ʈ�� " + player.getGamePoints() + "����Ʈ�Դϴ�.");
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
			admin.sendMessage("�Է��� �ٸ��� �ʽ��ϴ�.");
			return false;
		}
		
		player.setGamePoints(points);
		player.sendMessage("GM�� " + player.getName() + "���� ������ �� ����Ʈ�� " + points + "����Ʈ�� �����Ͽ����ϴ�.");
		player.sendMessage("������ �� ����Ʈ�� " + points + "����Ʈ�� ����Ǿ����ϴ�.");
		admin.sendMessage(player.getName() + "���� ������ �� ����Ʈ�� " + points + "����Ʈ�� �����Ͽ����ϴ�.");
		admin.sendMessage(player.getName() + "���� �� ������ �� ����Ʈ�� " + points + "����Ʈ�Դϴ�.");
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
			admin.sendMessage("�Է��� �ٸ��� �ʽ��ϴ�.");
			return false;
		}
		
		final long currentPoints = player.getGamePoints();
		if (currentPoints <= points)
		{
			player.setGamePoints(0);
			player.sendMessage("GM�� " + player.getName() + "���� ������ �� ����Ʈ " + currentPoints + "����Ʈ�� ȸ���Ͽ����ϴ�.");
			player.sendMessage("������ �� ����Ʈ " + currentPoints + "����Ʈ�� ���ҵǾ����ϴ�.");
			admin.sendMessage(player.getName() + "���� ������ �� ����Ʈ " + currentPoints + "����Ʈ�� ȸ���Ͽ����ϴ�.");
		}
		else
		{
			player.setGamePoints(currentPoints - points);
			player.sendMessage("GM�� " + player.getName() + "���� ������ �� ����Ʈ " + points + "����Ʈ�� ȸ���Ͽ����ϴ�.");
			player.sendMessage("������ �� ����Ʈ " + points + "����Ʈ�� ���ҵǾ����ϴ�.");
			admin.sendMessage(player.getName() + "���� ������ �� ����Ʈ " + points + "����Ʈ�� ȸ���Ͽ����ϴ�.");
		}
		admin.sendMessage(player.getName() + "���� �� ������ �� ����Ʈ�� " + player.getGamePoints() + "����Ʈ�Դϴ�.");
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}