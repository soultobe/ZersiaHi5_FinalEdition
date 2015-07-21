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
package com.l2jserver.gameserver.datatables;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.engines.DocumentParser;
import com.l2jserver.gameserver.model.ItemMallProduct;
import com.l2jserver.gameserver.model.StatsSet;

/**
 * @author Pandragon
 */
public class ItemMallData extends DocumentParser
{
	private static final Logger _log = Logger.getLogger(ItemMallData.class.getName());
	private final Map<Integer, ItemMallProduct> _mallList = new HashMap<>();
	
	protected ItemMallData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_mallList.clear();
		parseDatapackFile("data/ItemMall.xml");
	}
	
	@Override
	protected void parseDocument()
	{
		NamedNodeMap attrs;
		Node att;
		StatsSet set = null;
		for (Node a = getCurrentDocument().getFirstChild(); a != null; a = a.getNextSibling())
		{
			if ("list".equalsIgnoreCase(a.getNodeName()))
			{
				for (Node b = a.getFirstChild(); b != null; b = b.getNextSibling())
				{
					if ("product".equalsIgnoreCase(b.getNodeName()))
					{
						attrs = b.getAttributes();
						set = new StatsSet();
						for (int i = 0; i < attrs.getLength(); i++)
						{
							att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						final ItemMallProduct product = new ItemMallProduct(set.getInt("id"), set.getInt("category"), set.getInt("points"), set.getInt("item"), set.getInt("count"));
						_mallList.put(set.getInt("id"), product);
					}
				}
			}
		}
		
		_log.info(getClass().getSimpleName() + ": Loaded " + _mallList.size() + " products.");
	}
	
	public Collection<ItemMallProduct> getAllItems()
	{
		return _mallList.values();
	}
	
	public ItemMallProduct getProduct(int id)
	{
		return _mallList.get(id);
	}
	
	public static ItemMallData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemMallData _instance = new ItemMallData();
	}
}
