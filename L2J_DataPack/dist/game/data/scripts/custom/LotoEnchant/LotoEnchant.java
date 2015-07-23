package custom.LotoEnchant;

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

public class LotoEnchant extends Quest
{
	public static final Logger _log = Logger.getLogger(LotoEnchant.class.getName());
	
	int npcId = 999013;
	
	// WEAPON
	int itemRequiredWeapon = Config.LOTOWEAPON_ITEM;
	int itemRequiredWeaponCount = Config.LOTOWEAPON_ITEMCOUNT;
	int LotoWeaponMaxEnchant = Config.LOTOWEAPONMAX_ENCHANT;
	
	// ARMOR
	int itemRequiredArmor = Config.LOTOARMOR_ITEM;
	int itemRequiredArmorCount = Config.LOTOARMOR_ITEMCOUNT;
	int LotoArmorMaxEnchant = Config.LOTOARMORMAX_ENCHANT;
	
	// JEWELS
	int itemRequiredJewels = Config.LOTOJEWELS_ITEM;
	int itemRequiredJewelsCount = Config.LOTOJEWELS_ITEMCOUNT;
	int LotoJewelsMaxEnchant = Config.LOTOJEWELSMAX_ENCHANT;
	
	// Belt&Shirt
	int itemRequiredBeltShirt = Config.LOTOBELTSHIRT_ITEM;
	int itemRequiredBeltShirtCount = Config.LOTOBELTSHIRT_ITEMCOUNT;
	int LotoBeltShirtMaxEnchant = Config.LOTOBELTSHIRTMAX_ENCHANT;
	
	double dblrandum;
	int intEnchantCnt = 0;
	int intEnchantFlg = 0;
	// itemType
	String itemType = "";
	
	public LotoEnchant(int questId, String name, String descr)
	{
		super(questId, name, descr);
		
		addStartNpc(npcId);
		addFirstTalkId(npcId);
		addTalkId(npcId);
	}
	
	public static void main(String[] args)
	{
		if (Config.LOTOENCHANT_ENABLE == true)
		{
			new LotoEnchant(-1, LotoEnchant.class.getSimpleName(), "custom");
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		// String enchantType = "LotoEnchant.htm";
		
		if (player.getQuestState(getName()) == null)
		{
			newQuestState(player);
		}
		else if (player.isInCombat())
		{
			return "LotoEnchantCombat.htm";
		}
		else if (player.getPvpFlag() == 1)
		{
			return "LotoEnchantPvP.htm";
		}
		else if (player.getKarma() != 0)
		{
			return "LotoEnchantKarma.htm";
		}
		else if (OlympiadManager.getInstance().isRegistered(player))
		{
			return "LotoEnchantOlympiad.htm";
		}
		
		return "LotoEnchant.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmlText = event;
		
		String enchantType = "LotoEnchant.htm";
		
		int armorType = -1;
		
		// Armor parts
		if (event.equals("enchantHelmet"))
		{
			armorType = Inventory.PAPERDOLL_HEAD;
			enchantType = "LotoEnchantArmor.htm";
			itemType = "Armor";
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantChest"))
		{
			armorType = Inventory.PAPERDOLL_CHEST;
			enchantType = "LotoEnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantLeggings"))
		{
			armorType = Inventory.PAPERDOLL_LEGS;
			enchantType = "LotoEnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantGloves"))
		{
			armorType = Inventory.PAPERDOLL_GLOVES;
			enchantType = "LotoEnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantBoots"))
		{
			armorType = Inventory.PAPERDOLL_FEET;
			enchantType = "LotoEnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		else if (event.equals("enchantShieldOrSigil"))
		{
			armorType = Inventory.PAPERDOLL_LHAND;
			enchantType = "LotoEnchantArmor.htm";
			itemType = "Armor";
			
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredArmor, itemRequiredArmorCount);
		}
		// Jewels
		else if (event.equals("enchantUpperEarring"))
		{
			armorType = Inventory.PAPERDOLL_LEAR;
			enchantType = "LotoEnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		else if (event.equals("enchantLowerEarring"))
		{
			armorType = Inventory.PAPERDOLL_REAR;
			enchantType = "LotoEnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		else if (event.equals("enchantNecklace"))
		{
			armorType = Inventory.PAPERDOLL_NECK;
			enchantType = "LotoEnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		else if (event.equals("enchantUpperRing"))
		{
			armorType = Inventory.PAPERDOLL_LFINGER;
			enchantType = "LotoEnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		else if (event.equals("enchantLowerRing"))
		{
			armorType = Inventory.PAPERDOLL_RFINGER;
			enchantType = "LotoEnchantJewels.htm";
			itemType = "Jewels";
			
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredJewels, itemRequiredJewelsCount);
		}
		// Belt/Shirt
		else if (event.equals("enchantBelt"))
		{
			armorType = Inventory.PAPERDOLL_BELT;
			enchantType = "LotoEnchantBeltShirt.htm";
			itemType = "BeltShirt";
			
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredBeltShirt, itemRequiredBeltShirtCount);
		}
		else if (event.equals("enchantShirt"))
		{
			armorType = Inventory.PAPERDOLL_UNDER;
			enchantType = "LotoEnchantBeltShirt.htm";
			itemType = "BeltShirt";
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredBeltShirt, itemRequiredBeltShirtCount);
		}
		// Weapon
		else if (event.equals("enchantWeapon"))
		{
			armorType = Inventory.PAPERDOLL_RHAND;
			enchantType = "LotoEnchantWeapon.htm";
			itemType = "Weapon";
			
			htmlText = LotoEnchant(itemType, enchantType, player, armorType, itemRequiredWeapon, itemRequiredWeaponCount);
		}
		return htmlText;
	}
	
	private String LotoEnchant(String itemType, String enchantType, L2PcInstance player, int armorType, int itemRequired, int itemRequiredCount)
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
					// Weapon
					if (itemType.equals("Weapon"))
					{
						currentEnchant = item.getEnchantLevel();
						if (currentEnchant < (LotoWeaponMaxEnchant - 2))
						{
							dblrandum = Math.random() * 100;
							if (((int) dblrandum >= 0) && ((int) dblrandum <= 3))
							{
								intEnchantCnt = 2;
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
								intEnchantFlg = 3;
							}
							else if (((int) dblrandum >= 4) && ((int) dblrandum <= 20))
							{
								intEnchantCnt = 1;
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
								intEnchantFlg = 2;
							}
							else if (((int) dblrandum >= 21) && ((int) dblrandum <= 60))
							{
								intEnchantCnt = 0;
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
								intEnchantFlg = 1;
							}
							else
							{
								intEnchantFlg = 0;
								if (currentEnchant < 1)
								{
									intEnchantCnt = 0;
								}
								else
								{
									intEnchantCnt = -1;
								}
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
							}
						}
						else
						{
							return "LotoEnchantFalse.htm";
						}
					}
					// Armor parts
					else if (itemType.equals("Armor"))
					{
						currentEnchant = item.getEnchantLevel();
						if (currentEnchant < (LotoArmorMaxEnchant - 2))
						{
							dblrandum = Math.random() * 100;
							if (((int) dblrandum >= 0) && ((int) dblrandum <= 3))
							{
								intEnchantCnt = 2;
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
								intEnchantFlg = 3;
							}
							else if (((int) dblrandum >= 4) && ((int) dblrandum <= 20))
							{
								intEnchantCnt = 1;
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
								intEnchantFlg = 2;
							}
							else if (((int) dblrandum >= 21) && ((int) dblrandum <= 60))
							{
								intEnchantCnt = 0;
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
								intEnchantFlg = 1;
							}
							else
							{
								intEnchantFlg = 0;
								if (currentEnchant < 1)
								{
									intEnchantCnt = 0;
								}
								else
								{
									intEnchantCnt = -1;
								}
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
							}
						}
						else
						{
							return "LotoEnchantFalse.htm";
						}
					}
					// Jewels
					else if (itemType.equals("Jewels"))
					{
						currentEnchant = item.getEnchantLevel();
						if (currentEnchant < (LotoJewelsMaxEnchant - 2))
						{
							dblrandum = Math.random() * 100;
							if (((int) dblrandum >= 0) && ((int) dblrandum <= 3))
							{
								intEnchantCnt = 2;
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
								intEnchantFlg = 3;
							}
							else if (((int) dblrandum >= 4) && ((int) dblrandum <= 20))
							{
								intEnchantCnt = 1;
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
								intEnchantFlg = 2;
							}
							else if (((int) dblrandum >= 21) && ((int) dblrandum <= 60))
							{
								intEnchantCnt = 0;
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
								intEnchantFlg = 1;
							}
							else
							{
								intEnchantFlg = 0;
								if (currentEnchant < 1)
								{
									intEnchantCnt = 0;
								}
								else
								{
									intEnchantCnt = -1;
								}
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
							}
						}
						else
						{
							return "LotoEnchantFalse.htm";
						}
					}
					// Belt/Shirt
					else if (itemType.equals("BeltShirt"))
					{
						currentEnchant = item.getEnchantLevel();
						if (currentEnchant < (LotoBeltShirtMaxEnchant - 2))
						{
							dblrandum = Math.random() * 100;
							if (((int) dblrandum >= 0) && ((int) dblrandum <= 3))
							{
								intEnchantCnt = 2;
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
								intEnchantFlg = 3;
							}
							else if (((int) dblrandum >= 4) && ((int) dblrandum <= 20))
							{
								intEnchantCnt = 1;
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
								intEnchantFlg = 2;
							}
							else if (((int) dblrandum >= 21) && ((int) dblrandum <= 60))
							{
								intEnchantCnt = 0;
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
								intEnchantFlg = 1;
							}
							else
							{
								intEnchantFlg = 0;
								if (currentEnchant < 1)
								{
									intEnchantCnt = 0;
								}
								else
								{
									intEnchantCnt = -1;
								}
								newEnchantLevel = setEnchant(player, item, currentEnchant + intEnchantCnt, armorType);
							}
						}
						else
						{
							return "LotoEnchantFalse.htm";
						}
					}
					if (intEnchantFlg == 3)
					{
						st.takeItems(itemRequired, itemRequiredCount);
						return "LotoEnchantVeryGood.htm";
					}
					else if (intEnchantFlg == 2)
					{
						st.takeItems(itemRequired, itemRequiredCount);
						return "LotoEnchantGood.htm";
					}
					else if (intEnchantFlg == 1)
					{
						st.takeItems(itemRequired, itemRequiredCount);
						return "LotoEnchantSave.htm";
					}
					else
					{
						st.takeItems(itemRequired, itemRequiredCount);
						return "LotoEnchantLost.htm";
					}
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				return "LotoEnchantFalse.htm";
			}
			catch (NumberFormatException e)
			{
				return "LotoEnchantFalse.htm";
			}
			return "LotoEnchantNoneItem.htm";
		}
		else
		{
			return "LotoEnchantNoneItemCnt.htm";
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
		if ((item != null) && (newEnchantLevel < LotoWeaponMaxEnchant))
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
		String html = "<html>" + "<title>Team Of Zersia</title>" + "<body>" + "<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>" + "<font color=\"FF9900\">" + title + "</font></center><br>" + content + "<br><br>" + "<center><a action=\"bypass -h Quest LotoEnchant " + enchantType + "\">�ڷΰ���</a></center>" + "</body>" + "</html>";
		
		return html;
	}
}