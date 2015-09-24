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

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import javolution.util.FastMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.l2jserver.Config;
import com.l2jserver.gameserver.enums.ItemLocation;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.PcInventory;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

public class BonusExpManager
{
	private final Logger _log = Logger.getLogger(getClass().getName());
	private final Map<Integer, BonusItem> _bonusItems = new FastMap<>();
	
	public BonusExpManager()
	{
		load();
	}
	
	public static final BonusExpManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private void load()
	{
		try
		{
			int itemId = 0;
			double bonusExp = 0.0D;
			double bonusSp = 0.0D;
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			
			File file = new File(Config.DATAPACK_ROOT + "/data/bonusExpItems.xml");
			if (!file.exists())
			{
				_log.log(Level.INFO, "[" + getClass().getSimpleName() + "]Missing" + Config.DATAPACK_ROOT + "/data/bonusExpItems.xml - Thescriptwontworkwithoutit!");
				return;
			}
			Document doc = factory.newDocumentBuilder().parse(file);
			Node first = doc.getFirstChild();
			if ((first != null) && ("list".equalsIgnoreCase(first.getNodeName())))
			{
				for (Node n = first.getFirstChild(); n != null; n = n.getNextSibling())
				{
					if ("bonus".equalsIgnoreCase(n.getNodeName()))
					{
						for (Node cd = n.getFirstChild(); cd != null; cd = cd.getNextSibling())
						{
							if ("item".equalsIgnoreCase(cd.getNodeName()))
							{
								Node att = cd.getAttributes().getNamedItem("itemId");
								if (att != null)
								{
									itemId = Integer.parseInt(att.getNodeValue());
								}
								else
								{
									_log.severe("[" + getClass().getSimpleName() + "]MissingItemid,skipping");
									continue;
								}
								att = cd.getAttributes().getNamedItem("exp");
								if (att != null)
								{
									bonusExp = Double.parseDouble(att.getNodeValue());
								}
								else
								{
									_log.severe("[" + getClass().getSimpleName() + "]Missingexp,skipping");
									continue;
								}
								att = cd.getAttributes().getNamedItem("sp");
								if (att != null)
								{
									bonusSp = Double.parseDouble(att.getNodeValue());
								}
								else
								{
									_log.severe("[" + getClass().getSimpleName() + "]Missingsp,skipping");
									continue;
								}
								_bonusItems.put(Integer.valueOf(itemId), new BonusItem(bonusExp, bonusSp));
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		_log.info("[" + getClass().getSimpleName() + "]:Loaded:" + _bonusItems.size() + "Items");
	}
	
	public long[] getBonusExpAndSp(L2PcInstance player, long exp, long sp)
	{
		double bonusExp = 0.0D;
		double bonusSp = 0.0D;
		if (player != null)
		{
			PcInventory inv = player.getInventory();
			for (Integer integer : _bonusItems.keySet())
			{
				int itemId = integer.intValue();
				
				L2ItemInstance item = inv.getItemByItemId(itemId);
				if ((item != null) && (item.getItemLocation() == ItemLocation.INVENTORY))
				{
					BonusItem bonus = _bonusItems.get(Integer.valueOf(itemId));
					bonusExp += bonus.getBonusExpMultiplyer();
					bonusSp += bonus.getBonusSpMultiplyer();
				}
			}
			return new long[]
			{
				(long) ((exp * bonusExp) / 1000.0D),
				(long) ((sp * bonusSp) / 1000.0D)
			};
		}
		return new long[]
		{
			0L,
			0L
		};
	}
	
	private final class BonusItem
	{
		private final double _bonusExp;
		private final double _bonusSp;
		
		public BonusItem(double exp, double sp)
		{
			_bonusExp = exp;
			_bonusSp = sp;
		}
		
		public double getBonusExpMultiplyer()
		{
			return _bonusExp;
		}
		
		public double getBonusSpMultiplyer()
		{
			return _bonusSp;
		}
	}
	
	private static class SingletonHolder
	{
		protected static final BonusExpManager _instance = new BonusExpManager();
	}
}
