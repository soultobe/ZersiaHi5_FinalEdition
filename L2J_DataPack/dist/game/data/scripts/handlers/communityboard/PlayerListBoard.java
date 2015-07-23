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
			timeResult = Integer.toString(secs / 86400) + "일 " + Integer.toString((secs % 86400) / 3600) + "시간";
		}
		else
		{
			timeResult = Integer.toString(secs / 3600) + "시간 " + Integer.toString((secs % 3600) / 60) + "분";
		}
		return timeResult;
	}
	
	public String className(int classid)
	{
		String result = "";
		switch (classid)
		{
			case 0:
				result = "휴먼파이터";
				break;
			case 1:
				result = "워리어";
				break;
			case 2:
				result = "글라디에이터";
				break;
			case 3:
				result = "워로드";
				break;
			case 4:
				result = "나이트";
				break;
			case 5:
				result = "팰러딘";
				break;
			case 6:
				result = "다크어벤져";
				break;
			case 7:
				result = "로그";
				break;
			case 8:
				result = "트레져헌터";
				break;
			case 9:
				result = "호크아이";
				break;
			case 10:
				result = "휴먼메이지";
				break;
			case 11:
				result = "위저드";
				break;
			case 12:
				result = "소서러";
				break;
			case 13:
				result = "네크로맨서";
				break;
			case 14:
				result = "워록";
				break;
			case 15:
				result = "클레릭";
				break;
			case 16:
				result = "비숍";
				break;
			case 17:
				result = "프로핏";
				break;
			case 18:
				result = "엘븐파이터";
				break;
			case 19:
				result = "엘븐나이트";
				break;
			case 20:
				result = "템플나이트";
				break;
			case 21:
				result = "소드싱어";
				break;
			case 22:
				result = "엘븐스카우트";
				break;
			case 23:
				result = "플래인워커";
				break;
			case 24:
				result = "실버레인져";
				break;
			case 25:
				result = "엘븐메이지";
				break;
			case 26:
				result = "엘븐위저드";
				break;
			case 27:
				result = "스펠싱어";
				break;
			case 28:
				result = "엘레멘탈서머너";
				break;
			case 29:
				result = "오라클";
				break;
			case 30:
				result = "엘더";
				break;
			case 31:
				result = "다크파이터";
				break;
			case 32:
				result = "팰러스나이트";
				break;
			case 33:
				result = "실리엔나이트";
				break;
			case 34:
				result = "블레이드댄서";
				break;
			case 35:
				result = "어쌔신";
				break;
			case 36:
				result = "어비스워커";
				break;
			case 37:
				result = "팬텀레인져";
				break;
			case 38:
				result = "다크메이지";
				break;
			case 39:
				result = "다크위저드";
				break;
			case 40:
				result = "스펠하울러";
				break;
			case 41:
				result = "팬텀서머너";
				break;
			case 42:
				result = "실리엔오라클";
				break;
			case 43:
				result = "실리엔엘더";
				break;
			case 44:
				result = "오크파이터";
				break;
			case 45:
				result = "오크레이더";
				break;
			case 46:
				result = "디스트로이어";
				break;
			case 47:
				result = "오크몽크";
				break;
			case 48:
				result = "타이런트";
				break;
			case 49:
				result = "오크메이지";
				break;
			case 50:
				result = "오크샤먼";
				break;
			case 51:
				result = "오버로드";
				break;
			case 52:
				result = "워크라이어";
				break;
			case 53:
				result = "드워븐파이터";
				break;
			case 54:
				result = "스캐빈져";
				break;
			case 55:
				result = "바운티헌터";
				break;
			case 56:
				result = "아티산";
				break;
			case 57:
				result = "워스미스";
				break;
			case 88:
				result = "듀얼리스트";
				break;
			case 89:
				result = "드레드노트";
				break;
			case 90:
				result = "피닉스나이트";
				break;
			case 91:
				result = "헬나이트";
				break;
			case 92:
				result = "사지타리우스";
				break;
			case 93:
				result = "어드벤쳐러";
				break;
			case 94:
				result = "아크메이지";
				break;
			case 95:
				result = "소울테이커";
				break;
			case 96:
				result = "아르카나로드";
				break;
			case 97:
				result = "카디날";
				break;
			case 98:
				result = "하이로펀트";
				break;
			case 99:
				result = "에바스템플러";
				break;
			case 100:
				result = "소드뮤즈";
				break;
			case 101:
				result = "윈드라이더";
				break;
			case 102:
				result = "문라이트센티넬";
				break;
			case 103:
				result = "미스틱뮤즈";
				break;
			case 104:
				result = "엘레멘탈마스터";
				break;
			case 105:
				result = "에바스세인트";
				break;
			case 106:
				result = "실리엔템플러";
				break;
			case 107:
				result = "스펙트럴댄서";
				break;
			case 108:
				result = "고스트헌터";
				break;
			case 109:
				result = "고스트센티넬";
				break;
			case 110:
				result = "스톰스크리머";
				break;
			case 111:
				result = "스펙트럴마스터";
				break;
			case 112:
				result = "실리엔세인트";
				break;
			case 113:
				result = "타이탄";
				break;
			case 114:
				result = "그랜드카바타리";
				break;
			case 115:
				result = "도미네이터";
				break;
			case 116:
				result = "둠크라이어";
				break;
			case 117:
				result = "포츈시커";
				break;
			case 118:
				result = "마에스트로";
				break;
			case 123:
				result = "카마엘솔져(남)";
				break;
			case 124:
				result = "카마엘솔져(여)";
				break;
			case 125:
				result = "트루퍼";
				break;
			case 126:
				result = "워더";
				break;
			case 127:
				result = "버서커";
				break;
			case 128:
				result = "소울브레이커(남)";
				break;
			case 129:
				result = "소울브레이커(여)";
				break;
			case 130:
				result = "아바레스터";
				break;
			case 131:
				result = "둠브링거";
				break;
			case 132:
				result = "소울하운드(남)";
				break;
			case 133:
				result = "소울하운드(여)";
				break;
			case 134:
				result = "트릭스터";
				break;
			case 135:
				result = "인스펙터";
				break;
			case 136:
				result = "쥬디케이터";
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