package custom.EvntEnchant;

import java.util.logging.Logger;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.olympiad.OlympiadManager;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.serverpackets.CharInfo;
import com.l2jserver.gameserver.network.serverpackets.ExBrExtraUserInfo;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;

public class EvntEnchant extends Quest
{
	public static final Logger _log = Logger.getLogger(EvntEnchant.class.getName());
	
	int npcId = 999028;
	
	int itemRequiredWeapon = Config.EVENT_ENCHANTITEM;
	int itemRequiredWeaponCount = Config.EVENT_ENCHANTITEMCOUNT;
	
	String itemType = "";
	
	public EvntEnchant(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(npcId);
		addFirstTalkId(npcId);
		addTalkId(npcId);
	}
	
	public static void main(String[] args)
	{
		if (Config.EVENTENCHANT_ENABLE == true)
		{
			new EvntEnchant(-1, EvntEnchant.class.getSimpleName(), "custom");
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		// String enchantType = "Enchant.htm";
		
		if (player.getQuestState(getName()) == null)
		{
			newQuestState(player);
		}
		else if (player.isInCombat())
		{
			return "EnchantCombat.htm";
		}
		else if (player.getPvpFlag() == 1)
		{
			return "EnchantPvP.htm";
		}
		else if (player.getKarma() != 0)
		{
			return "EnchantKarma.htm";
		}
		else if (OlympiadManager.getInstance().isRegistered(player))
		{
			return "EnchantOlympiad.htm";
		}
		
		return "Enchant.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmlText = event;
		
		String enchantType = "Enchant.htm";
		
		int armorType = -1;
		
		// Armor parts
		// Weapon
		if (event.equals("enchantWeapon"))
		{
			armorType = Inventory.PAPERDOLL_RHAND;
			enchantType = "EnchantWeapon.htm";
			itemType = "Weapon";
			
			htmlText = EvntEnchant(itemType, enchantType, player, armorType, itemRequiredWeapon, itemRequiredWeaponCount);
		}
		return htmlText;
	}
	
	private String EvntEnchant(String itemType, String enchantType, L2PcInstance player, int armorType, int itemRequired, int itemRequiredCount)
	{
		QuestState st = player.getQuestState(getName());
		int currentEnchant = 0;
		int newEnchantLevel = 0;
		int EnchantFlg = 0;
		
		if (st.getQuestItemsCount(itemRequired) >= itemRequiredCount)
		{
			try
			{
				L2ItemInstance item = getItemToEnchant(player, armorType);
				if (item != null)
				{
					currentEnchant = item.getEnchantLevel();
					// Weapon
					if (itemType.equals("Weapon"))
					{
						if (currentEnchant <= (Config.MAXWEAPONMAX_ENCHANT - 2))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.EVENT_ENCHANT, armorType);
							EnchantFlg = 1;
						}
						else if (currentEnchant == (Config.MAXWEAPONMAX_ENCHANT - 1))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + (Config.EVENT_ENCHANT - 1), armorType);
							EnchantFlg = 1;
						}
						else if (currentEnchant >= Config.MAXWEAPONMAX_ENCHANT)
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant, armorType);
							EnchantFlg = 1;
						}
					}
					if ((newEnchantLevel > 0) && (EnchantFlg > 0))
					{
						st.takeItems(itemRequired, itemRequiredCount);
						return "EnchantTrue.htm";
					}
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				return "EnchantNoneItem.htm";
			}
			catch (NumberFormatException e)
			{
				return "EnchantNoneItem.htm";
			}
			return "EnchantNoneItem.htm";
		}
		else
		{
			return "EnchantNoneItemCnt.htm";
		}
	}
	
	private L2ItemInstance getItemToEnchant(L2PcInstance player, int armorType)
	{
		L2ItemInstance itemInstance = null;
		L2ItemInstance parmorInstance = player.getInventory().getPaperdollItem(armorType);
		
		if ((parmorInstance != null) && (parmorInstance.getLocationSlot() == armorType))
		{
			itemInstance = parmorInstance;
			
			if (itemInstance != null)
			{
				return itemInstance;
			}
		}
		
		return null;
	}
	
	private int setEnchant(L2PcInstance player, L2ItemInstance item, int newEnchantLevel, int armorType)
	{
		if (item != null)
		{
			// set enchant value
			player.getInventory().unEquipItemInSlot(armorType);
			item.setEnchantLevel(newEnchantLevel);
			player.getInventory().equipItem(item);
			
			// send packets
			InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(item);
			player.sendPacket(iu);
			player.broadcastPacket(new CharInfo(player));
			player.sendPacket(new UserInfo(player));
			player.broadcastPacket(new ExBrExtraUserInfo(player));
			
			return newEnchantLevel;
		}
		
		return -1;
	}
	
	public String drawHtml(String title, String content, String enchantType)
	{
		String html = "<html>" + "<title>Team Of Zersia</title>" + "<body>" + "<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>" + "<font color=\"FF9900\">" + title + "</font></center><br>" + content + "<br><br>" + "<center><a action=\"bypass -h Quest EvntEnchant " + enchantType + "\">�ڷΰ���</a></center>" + "</body>" + "</html>";
		
		return html;
	}
}