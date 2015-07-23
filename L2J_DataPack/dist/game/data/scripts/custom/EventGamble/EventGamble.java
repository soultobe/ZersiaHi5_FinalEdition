package custom.EventGamble;

import java.util.logging.Logger;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.olympiad.OlympiadManager;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

public class EventGamble extends Quest
{
	public static final Logger _log = Logger.getLogger(EventGamble.class.getName());
	
	int npcId = 999029;
	
	int itemReqGambleRankSet = Config.GAMBLERANKSET;
	int itemReqGambleItem = Config.GAMBLEITEM;
	int itemReqGambleItemCount = Config.GAMBLEITEMCOUNT;
	double dblrandum;
	
	public EventGamble(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(npcId);
		addFirstTalkId(npcId);
		addTalkId(npcId);
	}
	
	public static void main(String[] args)
	{
		if (Config.EVENTGAMBLE_ENABLE == true)
		{
			new EventGamble(-1, EventGamble.class.getSimpleName(), "custom");
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		// String enchantType = "EventGamble.htm";
		
		if (player.getQuestState(getName()) == null)
		{
			newQuestState(player);
		}
		else if (player.isInCombat())
		{
			return "EventGambleCombat.htm";
		}
		else if (player.getPvpFlag() == 1)
		{
			return "EventGamblePvP.htm";
		}
		else if (player.getKarma() != 0)
		{
			return "EventGambleKarma.htm";
		}
		else if (OlympiadManager.getInstance().isRegistered(player))
		{
			return "EventGambleOlympiad.htm";
		}
		return "EventGamble.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("EventGambleStart"))
		{
			QuestState st = player.getQuestState(getName());
			if (st.getQuestItemsCount(itemReqGambleItem) >= itemReqGambleItemCount)
			{
				try
				{
					dblrandum = Math.random() * 110;
					if (((int) dblrandum == 0) && (itemReqGambleRankSet >= 1))
					{
						st.takeItems(itemReqGambleItem, itemReqGambleItemCount);
						st.giveItems(Config.GAMBLERANKITEM01, Config.GAMBLERANKITEMCOUNT01);
						return "EventGamble01.htm";
					}
					else if (((int) dblrandum >= 1) && ((int) dblrandum <= 3) && (itemReqGambleRankSet >= 2))
					{
						st.takeItems(itemReqGambleItem, itemReqGambleItemCount);
						st.giveItems(Config.GAMBLERANKITEM02, Config.GAMBLERANKITEMCOUNT02);
						return "EventGamble02.htm";
					}
					else if (((int) dblrandum >= 4) && ((int) dblrandum <= 8) && (itemReqGambleRankSet >= 3))
					{
						st.takeItems(itemReqGambleItem, itemReqGambleItemCount);
						st.giveItems(Config.GAMBLERANKITEM03, Config.GAMBLERANKITEMCOUNT03);
						return "EventGamble03.htm";
					}
					else if (((int) dblrandum >= 9) && ((int) dblrandum <= 15) && (itemReqGambleRankSet >= 4))
					{
						st.takeItems(itemReqGambleItem, itemReqGambleItemCount);
						st.giveItems(Config.GAMBLERANKITEM04, Config.GAMBLERANKITEMCOUNT04);
						return "EventGamble04.htm";
					}
					else if (((int) dblrandum >= 16) && ((int) dblrandum <= 24) && (itemReqGambleRankSet >= 5))
					{
						st.takeItems(itemReqGambleItem, itemReqGambleItemCount);
						st.giveItems(Config.GAMBLERANKITEM05, Config.GAMBLERANKITEMCOUNT05);
						return "EventGamble05.htm";
					}
					else if (((int) dblrandum >= 25) && ((int) dblrandum <= 35) && (itemReqGambleRankSet >= 6))
					{
						st.takeItems(itemReqGambleItem, itemReqGambleItemCount);
						st.giveItems(Config.GAMBLERANKITEM06, Config.GAMBLERANKITEMCOUNT06);
						return "EventGamble06.htm";
					}
					else if (((int) dblrandum >= 36) && ((int) dblrandum <= 48) && (itemReqGambleRankSet >= 7))
					{
						st.takeItems(itemReqGambleItem, itemReqGambleItemCount);
						st.giveItems(Config.GAMBLERANKITEM07, Config.GAMBLERANKITEMCOUNT07);
						return "EventGamble07.htm";
					}
					else if (((int) dblrandum >= 49) && ((int) dblrandum <= 63) && (itemReqGambleRankSet >= 8))
					{
						st.takeItems(itemReqGambleItem, itemReqGambleItemCount);
						st.giveItems(Config.GAMBLERANKITEM08, Config.GAMBLERANKITEMCOUNT08);
						return "EventGamble08.htm";
					}
					else if (((int) dblrandum >= 64) && ((int) dblrandum <= 80) && (itemReqGambleRankSet >= 9))
					{
						st.takeItems(itemReqGambleItem, itemReqGambleItemCount);
						st.giveItems(Config.GAMBLERANKITEM09, Config.GAMBLERANKITEMCOUNT09);
						return "EventGamble09.htm";
					}
					else if (((int) dblrandum >= 81) && ((int) dblrandum <= 100) && (itemReqGambleRankSet >= 10))
					{
						st.takeItems(itemReqGambleItem, itemReqGambleItemCount);
						st.giveItems(Config.GAMBLERANKITEM10, Config.GAMBLERANKITEMCOUNT10);
						return "EventGamble10.htm";
					}
					else
					{
						st.takeItems(itemReqGambleItem, itemReqGambleItemCount);
						return "EventGambleFalse.htm";
					}
				}
				catch (StringIndexOutOfBoundsException e)
				{
					return "EventGamble.htm.htm";
				}
				catch (NumberFormatException e)
				{
					return "EventGamble.htm.htm";
				}
			}
			else
			{
				return "EventGambleNoneItemCnt.htm";
			}
		}
		return "EventGamble.htm";
	}
}