/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.debug.core.model;

/**
 * Represents an 'complex' script type
 */
public class ComplexVdmType extends AtomicVdmType
{

	public ComplexVdmType(String name)
	{
		super(name);
	}

	public boolean isAtomic()
	{
		return false;
	}

	public boolean isComplex()
	{
		return true;
	}

	public String formatDetails(IVdmValue value)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getName());

		String address = value.getMemoryAddress();
		if (address == null)
		{
			address = "unknown";
		}

		sb.append("@" + address); //$NON-NLS-1$

		return sb.toString();
	}

	public String formatValue(IVdmValue value)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getName());

		appendInstanceId(value, sb);

		return sb.toString();
	}
}
