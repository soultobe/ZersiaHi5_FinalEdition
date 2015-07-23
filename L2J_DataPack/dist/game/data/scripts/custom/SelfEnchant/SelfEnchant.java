package custom.SelfEnchant;

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

public class SelfEnchant extends Quest
{
	public static final Logger _log = Logger.getLogger(SelfEnchant.class.getName());
	
	int npcId = 999012;
	
	int itemRequiredWeapon = Config.WEAPON_ITEM;
	int itemRequiredWeaponCount = Config.WEAPON_ITEMCOUNT;
	
	int itemRequiredArmor = Config.ARMOR_ITEM;
	int itemRequiredArmorCount = Config.ARMOR_ITEMCOUNT;
	
	int itemRequiredJewels = Config.JEWELS_ITEM;
	int itemRequiredJewelsCount = Config.JEWELS_ITEMCOUNT;
	
	int itemRequiredBeltShirt = Config.BELTSHIRT_ITEM;
	int itemRequiredBeltShirtCount = Config.BELTSHIRT_ITEMCOUNT;
	
	String itemType = "";
	
	public SelfEnchant(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(npcId);
		addFirstTalkId(npcId);
		addTalkId(npcId);
	}
	
	public static void main(String[] args)
	{
		if (Config.SELFENCHANT_ENABLE == true)
		{
			new SelfEnchant(-1, SelfEnchant.class.getSimpleName(), "custom");
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
		if (event.equals("enchantHelmet"))
		{
			armorType = Inventory.PAPERDOLL_HEAD;
			enchantType = "EnchantArmor.htm";
			itemType = "Armor";
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantChest"))
		{
			armorType = Inventory.PAPERDOLL_CHEST;
			enchantType = "EnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantLeggings"))
		{
			armorType = Inventory.PAPERDOLL_LEGS;
			enchantType = "EnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantGloves"))
		{
			armorType = Inventory.PAPERDOLL_GLOVES;
			enchantType = "EnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantBoots"))
		{
			armorType = Inventory.PAPERDOLL_FEET;
			enchantType = "EnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantShieldOrSigil"))
		{
			armorType = Inventory.PAPERDOLL_LHAND;
			enchantType = "EnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		// Jewels
		else if (event.equals("enchantUpperEarring"))
		{
			armorType = Inventory.PAPERDOLL_LEAR;
			enchantType = "EnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		else if (event.equals("enchantLowerEarring"))
		{
			armorType = Inventory.PAPERDOLL_REAR;
			enchantType = "EnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		else if (event.equals("enchantNecklace"))
		{
			armorType = Inventory.PAPERDOLL_NECK;
			enchantType = "EnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		else if (event.equals("enchantUpperRing"))
		{
			armorType = Inventory.PAPERDOLL_LFINGER;
			enchantType = "EnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		else if (event.equals("enchantLowerRing"))
		{
			armorType = Inventory.PAPERDOLL_RFINGER;
			enchantType = "EnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		// Belt/Shirt
		else if (event.equals("enchantBelt"))
		{
			armorType = Inventory.PAPERDOLL_BELT;
			enchantType = "EnchantBeltShirt.htm";
			itemType = "BeltShirt";
			
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredBeltShirt, itemRequiredBeltShirtCount);
		}
		else if (event.equals("enchantShirt"))
		{
			armorType = Inventory.PAPERDOLL_UNDER;
			enchantType = "EnchantBeltShirt.htm";
			itemType = "BeltShirt";
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredBeltShirt, itemRequiredBeltShirtCount);
		}
		// Weapon
		else if (event.equals("enchantWeapon"))
		{
			armorType = Inventory.PAPERDOLL_RHAND;
			enchantType = "EnchantWeapon.htm";
			itemType = "Weapon";
			
			htmlText = SelfEnchant(itemType, enchantType, player, armorType, itemRequiredWeapon, itemRequiredWeaponCount);
		}
		return htmlText;
	}
	
	private String SelfEnchant(String itemType, String enchantType, L2PcInstance player, int armorType, int itemRequired, int itemRequiredCount)
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
					
					if (currentEnchant < 1)
					{
						// Weapon
						if (itemType.equals("Weapon"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.WEAPONMAX_ENCHANT, armorType);
						}
						// Armor parts
						else if (itemType.equals("Armor"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.ARMORMAX_ENCHANT, armorType);
						}
						else if (itemType.equals("Armor"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.ARMORMAX_ENCHANT, armorType);
						}
						else if (itemType.equals("Armor"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.ARMORMAX_ENCHANT, armorType);
						}
						else if (itemType.equals("Armor"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.ARMORMAX_ENCHANT, armorType);
						}
						else if (itemType.equals("Armor"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.ARMORMAX_ENCHANT, armorType);
						}
						else if (itemType.equals("Armor"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.ARMORMAX_ENCHANT, armorType);
						}
						// Jewels
						else if (itemType.equals("Jewels"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.JEWELSMAX_ENCHANT, armorType);
						}
						else if (itemType.equals("Jewels"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.JEWELSMAX_ENCHANT, armorType);
						}
						else if (itemType.equals("Jewels"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.JEWELSMAX_ENCHANT, armorType);
						}
						else if (itemType.equals("Jewels"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.JEWELSMAX_ENCHANT, armorType);
						}
						else if (itemType.equals("Jewels"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.JEWELSMAX_ENCHANT, armorType);
						}
						// Belt/Shirt
						else if (itemType.equals("BeltShirt"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.BELTSHIRTMAX_ENCHANT, armorType);
						}
						else if (itemType.equals("BeltShirt"))
						{
							newEnchantLevel = setEnchant(player, item, currentEnchant + Config.BELTSHIRTMAX_ENCHANT, armorType);
						}
						
						if (newEnchantLevel > 0)
						{
							st.takeItems(itemRequired, itemRequiredCount);
							return "EnchantTrue.htm";
						}
					}
					else
					{
						return "EnchantFalse.htm";
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
		String html = "<html>" + "<title>Team Of Zersia</title>" + "<body>" + "<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>" + "<font color=\"FF9900\">" + title + "</font></center><br>" + content + "<br><br>" + "<center><a action=\"bypass -h Quest SelfEnchant " + enchantType + "\">�ڷΰ���</a></center>" + "</body>" + "</html>";
		
		return html;
	}
}