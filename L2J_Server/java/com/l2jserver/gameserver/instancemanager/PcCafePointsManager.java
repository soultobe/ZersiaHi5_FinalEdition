/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.instancemanager;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExPCCafePointInfo;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.util.Rnd;

public final class PcCafePointsManager
{
	public PcCafePointsManager()
	{
	}
	
	public void givePcCafePoint(final L2PcInstance player, final long exp)
	{
		if (!Config.ENABLE_PC_BANG)
		{
			return;
		}
		
		if (player.isInsideZone(ZoneId.PEACE) || player.isInsideZone(ZoneId.PVP) || player.isInsideZone(ZoneId.SIEGE) || (player.isOnlineInt() == 0) || player.isJailed())
		{
			return;
		}
		
		if (player.getPcBangPoints() >= Config.MAX_PC_BANG_POINTS)
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_MAXMIMUM_ACCUMULATION_ALLOWED_OF_PC_CAFE_POINTS_HAS_BEEN_EXCEEDED);
			player.sendPacket(sm);
			return;
		}
		int points = (int) (exp * 0.0001 * Config.PC_BANG_POINT_RATE);
		
		if (Config.RANDOM_PC_BANG_POINT)
		{
			points = Rnd.get(points / 2, points);
		}
		
		if ((points == 0) && (exp > 0) && Config.PC_BANG_REWARD_LOW_EXP_KILLS && (Rnd.get(100) < Config.PC_BANG_LOW_EXP_KILLS_CHANCE))
		{
			points = 1; // minimum points
		}
		
		SystemMessage sm = null;
		if (points > 0)
		{
			if (Config.ENABLE_DOUBLE_PC_BANG_POINTS && (Rnd.get(100) < Config.DOUBLE_PC_BANG_POINTS_CHANCE))
			{
				points *= 2;
				sm = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT_DOUBLE);
				sm.addLong(points);
				player.sendPacket(sm);
				player.setPcBangPoints(player.getPcBangPoints() + points);
				player.sendPacket(new ExPCCafePointInfo(player.getPcBangPoints(), points, true, false, 1));
				player.broadcastPacket(new MagicSkillUse(player, player, 2023, 1, 100, 0));
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT);
				sm.addLong(points);
				player.sendPacket(sm);
				player.setPcBangPoints(player.getPcBangPoints() + points);
				player.sendPacket(new ExPCCafePointInfo(player.getPcBangPoints(), points, true, true, 1));
			}
			if ((player.getPcBangPoints() + points) > Config.MAX_PC_BANG_POINTS)
			{
				points = Config.MAX_PC_BANG_POINTS - player.getPcBangPoints();
			}
		}
	}
	
	/**
	 * Gets the single instance of {@code PcCafePointsManager}.
	 * @return single instance of {@code PcCafePointsManager}
	 */
	public static final PcCafePointsManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PcCafePointsManager _instance = new PcCafePointsManager();
	}
}
