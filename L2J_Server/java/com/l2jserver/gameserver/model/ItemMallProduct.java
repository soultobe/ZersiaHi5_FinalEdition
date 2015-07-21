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
package com.l2jserver.gameserver.model;

import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.items.L2Item;

/**
 * @author Pandragon
 */
public class ItemMallProduct
{
	private final int _productId;
	private final int _category;
	private final int _points;
	private final int _item;
	private final int _count;
	
	private final int _weight;
	private final boolean _tradable;
	
	public ItemMallProduct(int productId, int category, int points, int item, int count)
	{
		_productId = productId;
		_category = category;
		_points = points;
		_item = item;
		_count = count;
		
		final L2Item itemTemplate = ItemTable.getInstance().getTemplate(item);
		if (itemTemplate != null)
		{
			_weight = itemTemplate.getWeight();
			_tradable = itemTemplate.isTradeable();
		}
		else
		{
			_weight = 0;
			_tradable = true;
		}
	}
	
	public int getProductId()
	{
		return _productId;
	}
	
	public int getCategory()
	{
		return _category;
	}
	
	public int getPrice()
	{
		return _points;
	}
	
	public int getItemId()
	{
		return _item;
	}
	
	public int getItemCount()
	{
		return _count;
	}
	
	public int getItemWeight()
	{
		return _weight;
	}
	
	public boolean isTradable()
	{
		return _tradable;
	}
}
