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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;


public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = VdmUIPlugin.getDefault().getPreferenceStore();

		store.setDefault(IVdmUiConstants.ENABLE_EDITOR_RECONFILER, true);
		store.setDefault(VdmColorProvider.DEFAULT,"0,0,0");
		store.setDefault(VdmColorProvider.KEYWORD,"127,0,85");
		store.setDefault(VdmColorProvider.LATEX,"153,180,209");
		store.setDefault(VdmColorProvider.SINGLE_LINE_COMMENT,"63,127,95");
		store.setDefault(VdmColorProvider.STRING,"42,0,255");
		store.setDefault(VdmColorProvider.TYPE,"0,0,128");		
	}

}
