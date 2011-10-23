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
package org.overture.ide.vdmrt.debug.ui.launchconfigurations;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jface.operation.IRunnableContext;
import org.overture.ide.debug.ui.launchconfigurations.MethodSearchEngine;
import org.overture.ide.vdmpp.debug.ui.launchconfigurations.VdmPpApplicationLaunchShortcut;
import org.overture.ide.vdmrt.debug.IVdmRtDebugConstants;
import org.overturetool.vdmj.ast.IAstNode;

public class VdmRtApplicationLaunchShortcut extends VdmPpApplicationLaunchShortcut
{

	
	/**
	 * Returns a title for a type selection dialog used to prompt the user when there is more than one type
	 * that can be launched.
	 * 
	 * @return type selection dialog title
	 */
	@Override
	protected String getTypeSelectionTitle()
	{
		return "Select VDM-RT Application";
	}
	
	/**
	 * Returns an error message to use when the selection does not contain a type that can be launched.
	 * 
	 * @return error message when selection cannot be launched
	 */
	@Override
	protected String getSelectionEmptyMessage()
	{
		return "Selection does not contain a launchable Run operation type within the World class";
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut#getConfigurationType()
	 */
	@Override
	protected ILaunchConfigurationType getConfigurationType()
	{
		return getLaunchManager().getLaunchConfigurationType(IVdmRtDebugConstants.ATTR_VDM_PROGRAM);
	}

	@Override
	protected IAstNode[] filterTypes(Object[] elements, IRunnableContext context)
	{
		return new MethodSearchEngine().searchMainMethods(context, elements, MethodSearchEngine.WORLD_CLASS|MethodSearchEngine.RUN
				| MethodSearchEngine.EXPLICIT_OPERATION
				| MethodSearchEngine.PUBLIC);

	}

}
