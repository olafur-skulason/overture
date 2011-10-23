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
package org.overture.ide.ui.templates;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;

public class VdmTemplateCompletionProposal extends TemplateProposal {

//	private final Template fTemplate;
//	private final TemplateContext fContext;
//	private final Image fImage;
//	private final IRegion fRegion;
//	private int fRelevance;
//	private String fDisplayString;
	
	public VdmTemplateCompletionProposal(Template template,
			TemplateContext context, IRegion region, Image image, int relevance) {
		super(template, context, region, image, relevance);
//		fTemplate=template;
//		 fContext = context;
//		 fImage=image;
//		 fRegion=region;
//		 fRelevance=relevance;
//		 fDisplayString=null;
	}

//	@Override
//
//	public String getAdditionalProposalInfo() {
//	 return StringUtils.convertToHTMLContent(fTemplate.getPattern());
//
//	 }

}
