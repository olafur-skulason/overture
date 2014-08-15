/*
 * #%~
 * Overture Testing Framework
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.core.tests.examples;

import java.util.List;

import org.overture.ast.node.INode;

/**
 * Processed test data of an Overture example. This class holds the name and AST (List of {@link INode}) of an Overture
 * example.
 * 
 * @author ldc
 */
public class ExampleAstData
{

	String exampleName;
	List<INode> model;

	public ExampleAstData(String exampleName, List<INode> model)
	{
		this.exampleName = exampleName;
		this.model = model;
	}

	public String getExampleName()
	{
		return exampleName;
	}

	public List<INode> getModel()
	{
		return model;
	}

	@Override
	public String toString()
	{
		return this.exampleName;
	}

}
