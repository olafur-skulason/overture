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
package org.overture.ide.ui.editor.syntax;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.themes.ColorUtil;
import org.overture.ide.ui.VdmUIPlugin;

public class VdmColorProvider {
	
	// Default syntax highlighting preferences
	public static final String SINGLE_LINE_COMMENT = "ovt_rgb_single_line_comment";
	public static final String KEYWORD             = "ovt_rgb_keyword";
	public static final String TYPE                = "ovt_rgb_type";
	public static final String STRING              = "ovt_rgb_string";
	public static final String DEFAULT             = "ovt_rgb_default";
	public static final String LATEX               = "ovt_rgb_latex";
	
	protected static Map<String, Color> fColorTable = new HashMap<String, Color>(10);

	public void dispose() {
	
		Iterator<Color> e = fColorTable.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();
	}

	public Color getColor(String key) {
		
		Color color = (Color) fColorTable.get(key);
		
		if (color == null) {
			IPreferenceStore store = VdmUIPlugin.getDefault().getPreferenceStore();

			String pref = store.getString(key);
			RGB rgb = ColorUtil.getColorValue(pref);
			color = new Color(Display.getCurrent(), rgb);

			fColorTable.put(key, color);
		}
		
		return color;
	}
	
	public static void changeColor(String key, RGB rgb) {
		
		if(fColorTable.get(key) != null)
			fColorTable.get(key).dispose();
			
		fColorTable.put(key, new Color(Display.getCurrent(), rgb));
	}
	
	public static void resetColorTable() {
		
		Iterator<Color> e = fColorTable.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();
		
		fColorTable.clear();
	}

	
}
