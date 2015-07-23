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
package handlers.communityboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IParseBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * EditChar admin command implementation.
 */
public class PlayerListBoard implements IParseBoardHandler
{
	// SQL Queries
	private static final String SELECT_PLAYERS = "SELECT char_name,base_class,level,onlinetime,online FROM `characters` Where accesslevel = 0 and `online` <> ? ORDER BY `char_name`";
	private static final String COUNT_PLAYERS = "SELECT COUNT(*) AS players FROM `characters` Where accesslevel = 0 and `online` <> ?";
	
	private static final String[] COMMANDS =
	{
		"_bbsplayer_list"
	};
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("_bbsplayer_list"))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Players", command);
			// Load Favorite links
			final String list = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/players_list.html");
			final StringBuilder sb = new StringBuilder();
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(SELECT_PLAYERS))
			{
				ps.setInt(1, 0);
				try (ResultSet rs = ps.executeQuery())
				{
					String link = null;
					while (rs.next())
					{
						String timeon = getPlayerRunTime(rs.getInt("onlinetime"));
						int onlineflg = rs.getInt("online");
						switch (onlineflg)
						{
							case 1:
								link = list.replaceAll("%fav_charname%", "*" + rs.getString("char_name"));
								break;
							case 2:
								link = list.replaceAll("%fav_charname%", rs.getString("char_name"));
								break;
							case 3:
								link = list.replaceAll("%fav_charname%", rs.getString("char_name"));
								break;
						}
						link = link.replaceAll("%fav_class%", className(rs.getInt("base_class")));
						link = link.replaceAll("%fav_lv%", String.valueOf(rs.getInt("level")));
						link = link.replaceAll("%fav_time%", timeon);
						sb.append(link);
					}
				}
				String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/players.html");
				html = html.replaceAll("%fav_list%", sb.toString());
				html = html.replaceAll("%player_count%", Integer.toString(getPlayersCount(activeChar)));
				CommunityBoardHandler.separateAndSend(html, activeChar);
			}
			catch (Exception e)
			{
				LOG.warning(FavoriteBoard.class.getSimpleName() + ": Couldn't load PlayerList links for PlayerList " + activeChar.getName());
			}
		}
		return true;
	}
	
	public String getPlayerRunTime(int secs)
	{
		String timeResult = "";
		if (secs >= 86400)
		{
			timeResult = Integer.toString(secs / 86400) + "�� " + Integer.toString((secs % 86400) / 3600) + "�ð�";
		}
		else
		{
			timeResult = Integer.toString(secs / 3600) + "�ð� " + Integer.toString((secs % 3600) / 60) + "��";
		}
		return timeResult;
	}
	
	public String className(int classid)
	{
		String result = "";
		switch (classid)
		{
			case 0:
				result = "�޸�������";
				break;
			case 1:
				result = "������";
				break;
			case 2:
				result = "�۶������";
				break;
			case 3:
				result = "���ε�";
				break;
			case 4:
				result = "����Ʈ";
				break;
			case 5:
				result = "�ӷ���";
				break;
			case 6:
				result = "��ũ���";
				break;
			case 7:
				result = "�α�";
				break;
			case 8:
				result = "Ʈ��������";
				break;
			case 9:
				result = "ȣũ����";
				break;
			case 10:
				result = "�޸ո�����";
				break;
			case 11:
				result = "������";
				break;
			case 12:
				result = "�Ҽ���";
				break;
			case 13:
				result = "��ũ�θǼ�";
				break;
			case 14:
				result = "����";
				break;
			case 15:
				result = "Ŭ����";
				break;
			case 16:
				result = "���";
				break;
			case 17:
				result = "������";
				break;
			case 18:
				result = "����������";
				break;
			case 19:
				result = "���쳪��Ʈ";
				break;
			case 20:
				result = "���ó���Ʈ";
				break;
			case 21:
				result = "�ҵ�̾�";
				break;
			case 22:
				result = "���콺ī��Ʈ";
				break;
			case 23:
				result = "�÷��ο�Ŀ";
				break;
			case 24:
				result = "�ǹ�������";
				break;
			case 25:
				result = "���������";
				break;
			case 26:
				result = "����������";
				break;
			case 27:
				result = "����̾�";
				break;
			case 28:
				result = "������Ż���ӳ�";
				break;
			case 29:
				result = "����Ŭ";
				break;
			case 30:
				result = "����";
				break;
			case 31:
				result = "��ũ������";
				break;
			case 32:
				result = "�ӷ�������Ʈ";
				break;
			case 33:
				result = "�Ǹ�������Ʈ";
				break;
			case 34:
				result = "���̵��";
				break;
			case 35:
				result = "��ؽ�";
				break;
			case 36:
				result = "��񽺿�Ŀ";
				break;
			case 37:
				result = "���ҷ�����";
				break;
			case 38:
				result = "��ũ������";
				break;
			case 39:
				result = "��ũ������";
				break;
			case 40:
				result = "�����Ͽ﷯";
				break;
			case 41:
				result = "���Ҽ��ӳ�";
				break;
			case 42:
				result = "�Ǹ�������Ŭ";
				break;
			case 43:
				result = "�Ǹ�������";
				break;
			case 44:
				result = "��ũ������";
				break;
			case 45:
				result = "��ũ���̴�";
				break;
			case 46:
				result = "��Ʈ���̾�";
				break;
			case 47:
				result = "��ũ��ũ";
				break;
			case 48:
				result = "Ÿ�̷�Ʈ";
				break;
			case 49:
				result = "��ũ������";
				break;
			case 50:
				result = "��ũ����";
				break;
			case 51:
				result = "�����ε�";
				break;
			case 52:
				result = "��ũ���̾�";
				break;
			case 53:
				result = "�����������";
				break;
			case 54:
				result = "��ĳ����";
				break;
			case 55:
				result = "�ٿ�Ƽ����";
				break;
			case 56:
				result = "��Ƽ��";
				break;
			case 57:
				result = "�����̽�";
				break;
			case 88:
				result = "��󸮽�Ʈ";
				break;
			case 89:
				result = "�巹���Ʈ";
				break;
			case 90:
				result = "�Ǵн�����Ʈ";
				break;
			case 91:
				result = "�ﳪ��Ʈ";
				break;
			case 92:
				result = "����Ÿ���콺";
				break;
			case 93:
				result = "��庥�ķ�";
				break;
			case 94:
				result = "��ũ������";
				break;
			case 95:
				result = "�ҿ�����Ŀ";
				break;
			case 96:
				result = "�Ƹ�ī���ε�";
				break;
			case 97:
				result = "ī��";
				break;
			case 98:
				result = "���̷���Ʈ";
				break;
			case 99:
				result = "���ٽ����÷�";
				break;
			case 100:
				result = "�ҵ����";
				break;
			case 101:
				result = "������̴�";
				break;
			case 102:
				result = "������Ʈ��Ƽ��";
				break;
			case 103:
				result = "�̽�ƽ����";
				break;
			case 104:
				result = "������Ż������";
				break;
			case 105:
				result = "���ٽ�����Ʈ";
				break;
			case 106:
				result = "�Ǹ������÷�";
				break;
			case 107:
				result = "����Ʈ����";
				break;
			case 108:
				result = "��Ʈ����";
				break;
			case 109:
				result = "��Ʈ��Ƽ��";
				break;
			case 110:
				result = "���轺ũ����";
				break;
			case 111:
				result = "����Ʈ��������";
				break;
			case 112:
				result = "�Ǹ�������Ʈ";
				break;
			case 113:
				result = "Ÿ��ź";
				break;
			case 114:
				result = "�׷���ī��Ÿ��";
				break;
			case 115:
				result = "���̳�����";
				break;
			case 116:
				result = "��ũ���̾�";
				break;
			case 117:
				result = "�����Ŀ";
				break;
			case 118:
				result = "������Ʈ��";
				break;
			case 123:
				result = "ī��������(��)";
				break;
			case 124:
				result = "ī��������(��)";
				break;
			case 125:
				result = "Ʈ����";
				break;
			case 126:
				result = "����";
				break;
			case 127:
				result = "����Ŀ";
				break;
			case 128:
				result = "�ҿ�극��Ŀ(��)";
				break;
			case 129:
				result = "�ҿ�극��Ŀ(��)";
				break;
			case 130:
				result = "�ƹٷ�����";
				break;
			case 131:
				result = "�Һ긵��";
				break;
			case 132:
				result = "�ҿ��Ͽ��(��)";
				break;
			case 133:
				result = "�ҿ��Ͽ��(��)";
				break;
			case 134:
				result = "Ʈ������";
				break;
			case 135:
				result = "�ν�����";
				break;
			case 136:
				result = "���������";
				break;
		}
		return result;
	}
	
	private static int getPlayersCount(L2PcInstance player)
	{
		int count = 0;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(COUNT_PLAYERS))
		{
			ps.setInt(1, 0);
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					count = rs.getInt("players");
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(FavoriteBoard.class.getSimpleName() + ": Coudn't load players count for PlayerCount " + player.getName());
		}
		return count;
	}
	
}