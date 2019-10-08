package org.overture.codegen.vdm2systemc;

import org.overture.codegen.ir.INode;
import org.overture.codegen.merging.TemplateData;
import org.overture.codegen.merging.TemplateManager;

import java.io.InputStream;

public class SystemcTemplateManager extends TemplateManager
{
	protected String specializedTemplatePath;
	public SystemcTemplateManager(String baseTemplatePath, String specializedTemplatePath)
	{
		super(baseTemplatePath);
		this.specializedTemplatePath = specializedTemplatePath;
	}


	@Override
	public TemplateData getTemplateData(Class<? extends INode> nodeClass)
	{
		if(isUserDefined(nodeClass))
		{
			return userDefinedPaths.get(nodeClass);
		}
		else
		{
			String path = derivePath(specializedTemplatePath, nodeClass);
			String filePath = specializedTemplatePath;

			InputStream file = nodeClass.getResourceAsStream( '/' + path.replace("\\", "/"));

			//System.out.println("Attempting to load template from: " + path);
			if(file == null)
			{
				//System.out.println("Failed to load template.");
				path = derivePath(root, nodeClass);
				filePath = root;
				file = nodeClass.getResourceAsStream('/' + path.replace("\\", "/"));
				//System.out.println("Attempting to load base template: " + path);
				//System.out.println(file == null ? "FAILURE" : "SUCCESS");
			}

			return new TemplateData(templateLoadRef, derivePath(filePath, nodeClass));
		}
	}

}
