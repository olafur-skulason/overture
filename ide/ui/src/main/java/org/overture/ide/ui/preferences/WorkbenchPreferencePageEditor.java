/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.themes.ColorUtil;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;

public class WorkbenchPreferencePageEditor  extends FieldEditorPreferencePage implements
IWorkbenchPreferencePage {

		
	@Override
	protected void createFieldEditors()
	{
		addField(new BooleanFieldEditor(IVdmUiConstants.ENABLE_EDITOR_RECONFILER, "Syntax checking while you type", getFieldEditorParent()));

		
		ColorFieldEditor default_cfe = new ColorFieldEditor(VdmColorProvider.DEFAULT, "Default color", getFieldEditorParent());
		default_cfe.getColorSelector().addListener(   
				  new IPropertyChangeListener() { 
					public void propertyChange(PropertyChangeEvent event) {
				          VdmColorProvider.changeColor(VdmColorProvider.DEFAULT,(RGB) event.getNewValue());
				    }
				  }
				);
		addField(default_cfe);

		ColorFieldEditor keyword_cfe = new ColorFieldEditor(VdmColorProvider.KEYWORD, "Keyword color", getFieldEditorParent());
		keyword_cfe.getColorSelector().addListener(   
				  new IPropertyChangeListener() { 
					public void propertyChange(PropertyChangeEvent event) {
				          VdmColorProvider.changeColor(VdmColorProvider.KEYWORD,(RGB) event.getNewValue());
				    }
				  }
				);		
		addField(keyword_cfe);
		
		ColorFieldEditor latex_cfe = new ColorFieldEditor(VdmColorProvider.LATEX, "Latex color", getFieldEditorParent());
		latex_cfe.getColorSelector().addListener(   
				  new IPropertyChangeListener() { 
					public void propertyChange(PropertyChangeEvent event) {
				          VdmColorProvider.changeColor(VdmColorProvider.LATEX,(RGB) event.getNewValue());
				    }
				  }
				);		
		addField(latex_cfe);

		ColorFieldEditor line_com_cfe = new ColorFieldEditor(VdmColorProvider.SINGLE_LINE_COMMENT, "Line comment color", getFieldEditorParent());
		line_com_cfe.getColorSelector().addListener(   
				  new IPropertyChangeListener() { 
					public void propertyChange(PropertyChangeEvent event) {
				          VdmColorProvider.changeColor(VdmColorProvider.SINGLE_LINE_COMMENT,(RGB) event.getNewValue());
				    }
				  }
				);		
		addField(line_com_cfe);
		
		ColorFieldEditor type_cfe = new ColorFieldEditor(VdmColorProvider.TYPE, "Type color", getFieldEditorParent());
		default_cfe.getColorSelector().addListener(   
				  new IPropertyChangeListener() { 
					public void propertyChange(PropertyChangeEvent event) {
				          VdmColorProvider.changeColor(VdmColorProvider.TYPE,(RGB) event.getNewValue());
				    }
				  }
				);		
		addField(type_cfe);	
		
	}
	
	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return VdmUIPlugin.getDefault().getPreferenceStore();
	}
	
	@Override
	protected void performDefaults()
	{
		IPreferenceStore store = getPreferenceStore();

		store.setToDefault(IVdmUiConstants.ENABLE_EDITOR_RECONFILER);
        store.setToDefault(VdmColorProvider.DEFAULT);
        store.setToDefault(VdmColorProvider.SINGLE_LINE_COMMENT);
        store.setToDefault(VdmColorProvider.KEYWORD);
        store.setToDefault(VdmColorProvider.TYPE);
        store.setToDefault(VdmColorProvider.STRING);
        store.setToDefault(VdmColorProvider.DEFAULT);
        store.setToDefault(VdmColorProvider.LATEX);
        
        VdmColorProvider.resetColorTable();
        
		super.performDefaults();
	}

	public void init(IWorkbench workbench)
	{
		super.initialize();
	}

}
