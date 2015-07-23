package custom.MaxEnchant;

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

public class MaxEnchant extends Quest
{
	public static final Logger _log = Logger.getLogger(MaxEnchant.class.getName());
	
	int npcId = 999022;
	
	// WEAPON
	int itemRequiredWeapon = Config.MAXWEAPON_ITEM;
	int itemRequiredWeaponCount = Config.MAXWEAPON_ITEMCOUNT;
	int MaxWeaponMaxEnchant = Config.MAXWEAPONMAX_ENCHANT;
	
	// ARMOR
	int itemRequiredArmor = Config.MAXARMOR_ITEM;
	int itemRequiredArmorCount = Config.MAXARMOR_ITEMCOUNT;
	int MaxArmorMaxEnchant = Config.MAXARMORMAX_ENCHANT;
	
	// JEWELS
	int itemRequiredJewels = Config.MAXJEWELS_ITEM;
	int itemRequiredJewelsCount = Config.MAXJEWELS_ITEMCOUNT;
	int MaxJewelsMaxEnchant = Config.MAXJEWELSMAX_ENCHANT;
	
	// Belt&Shirt
	int itemRequiredBeltShirt = Config.MAXBELTSHIRT_ITEM;
	int itemRequiredBeltShirtCount = Config.MAXBELTSHIRT_ITEMCOUNT;
	int MaxBeltShirtMaxEnchant = Config.MAXBELTSHIRTMAX_ENCHANT;
	
	// itemType
	String itemType = "";
	
	public MaxEnchant(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(npcId);
		addFirstTalkId(npcId);
		addTalkId(npcId);
	}
	
	public static void main(String[] args)
	{
		if (Config.MAXENCHANT_ENABLE == true)
		{
			new MaxEnchant(-1, MaxEnchant.class.getSimpleName(), "custom");
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		// String enchantType = "MaxEnchant.htm";
		
		if (player.getQuestState(getName()) == null)
		{
			newQuestState(player);
		}
		else if (player.isInCombat())
		{
			return "MaxEnchantCombat.htm";
		}
		else if (player.getPvpFlag() == 1)
		{
			return "MaxEnchantPvP.htm";
		}
		else if (player.getKarma() != 0)
		{
			return "MaxEnchantKarma.htm";
		}
		else if (OlympiadManager.getInstance().isRegistered(player))
		{
			return "MaxEnchantOlympiad.htm";
		}
		
		return "MaxEnchant.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmlText = event;
		
		String enchantType = "MaxEnchant.htm";
		
		int armorType = -1;
		
		// Armor parts
		if (event.equals("enchantHelmet"))
		{
			armorType = Inventory.PAPERDOLL_HEAD;
			enchantType = "MaxEnchantArmor.htm";
			itemType = "Armor";
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantChest"))
		{
			armorType = Inventory.PAPERDOLL_CHEST;
			enchantType = "MaxEnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantLeggings"))
		{
			armorType = Inventory.PAPERDOLL_LEGS;
			enchantType = "MaxEnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantGloves"))
		{
			armorType = Inventory.PAPERDOLL_GLOVES;
			enchantType = "MaxEnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantBoots"))
		{
			armorType = Inventory.PAPERDOLL_FEET;
			enchantType = "MaxEnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantShieldOrSigil"))
		{
			armorType = Inventory.PAPERDOLL_LHAND;
			enchantType = "MaxEnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		// Jewels
		else if (event.equals("enchantUpperEarring"))
		{
			armorType = Inventory.PAPERDOLL_LEAR;
			enchantType = "MaxEnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		else if (event.equals("enchantLowerEarring"))
		{
			armorType = Inventory.PAPERDOLL_REAR;
			enchantType = "MaxEnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		else if (event.equals("enchantNecklace"))
		{
			armorType = Inventory.PAPERDOLL_NECK;
			enchantType = "MaxEnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		else if (event.equals("enchantUpperRing"))
		{
			armorType = Inventory.PAPERDOLL_LFINGER;
			enchantType = "MaxEnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		else if (event.equals("enchantLowerRing"))
		{
			armorType = Inventory.PAPERDOLL_RFINGER;
			enchantType = "MaxEnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		// Belt/Shirt
		else if (event.equals("enchantBelt"))
		{
			armorType = Inventory.PAPERDOLL_BELT;
			enchantType = "MaxEnchantBeltShirt.htm";
			itemType = "BeltShirt";
			
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredBeltShirt, itemRequiredBeltShirtCount);
		}
		else if (event.equals("enchantShirt"))
		{
			armorType = Inventory.PAPERDOLL_UNDER;
			enchantType = "MaxEnchantBeltShirt.htm";
			itemType = "BeltShirt";
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredBeltShirt, itemRequiredBeltShirtCount);
		}
		// Weapon
		else if (event.equals("enchantWeapon"))
		{
			armorType = Inventory.PAPERDOLL_RHAND;
			enchantType = "MaxEnchantWeapon.htm";
			itemType = "Weapon";
			
			htmlText = MaxEnchant(itemType, enchantType, player, armorType, itemRequiredWeapon, itemRequiredWeaponCount);
		}
		return htmlText;
	}
	
	private String MaxEnchant(String itemType, String enchantType, L2PcInstance player, int armorType, int itemRequired, int itemRequiredCount)
	{
		QuestState st = player.getQuestState(getName());
		int currentEnchant = 0;
		int newEnchantLevel = 0;
		
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
						if (currentEnchant < MaxWeaponMaxEnchant)
						{
							newEnchantLevel = setEnchant(player, item, MaxWeaponMaxEnchant, armorType);
						}
					}
					// Armor parts
					else if (itemType.equals("Armor"))
					{
						if (currentEnchant < MaxArmorMaxEnchant)
						{
							newEnchantLevel = setEnchant(player, item, MaxArmorMaxEnchant, armorType);
						}
					}
					// Jewels
					else if (itemType.equals("Jewels"))
					{
						if (currentEnchant < MaxWeaponMaxEnchant)
						{
							newEnchantLevel = setEnchant(player, item, MaxJewelsMaxEnchant, armorType);
						}
					}
					// Belt/Shirt
					else if (itemType.equals("BeltShirt"))
					{
						if (currentEnchant < MaxWeaponMaxEnchant)
						{
							newEnchantLevel = setEnchant(player, item, MaxBeltShirtMaxEnchant, armorType);
						}
					}
					if (newEnchantLevel > 0)
					{
						st.takeItems(itemRequired, itemRequiredCount);
						return "MaxEnchantTrue.htm";
					}
					else
					{
						return "MaxEnchantFalse.htm";
					}
				}
				else
				{
					return "MaxEnchantNoneItem.htm";
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				return "MaxEnchantNoneItem.htm";
			}
			catch (NumberFormatException e)
			{
				return "MaxEnchantNoneItem.htm";
			}
		}
		else
		{
			return "MaxEnchantNoneItemCnt.htm";
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
		String html = "<html>" + "<title>Team Of Zersia</title>" + "<body>" + "<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>" + "<font color=\"FF9900\">" + title + "</font></center><br>" + content + "<br><br>" + "<center><a action=\"bypass -h Quest MaxEnchant " + enchantType + "\">�ڷΰ���</a></center>" + "</body>" + "</html>";
		
		return html;
	}
}