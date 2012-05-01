package org.overture.ast.modules.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistant;
import org.overture.ast.definitions.assistants.PDefinitionListAssistantTC;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.SValueImport;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class SValueImportAssistantTC {

	public static List<PDefinition> getDefinitions(SValueImport imp,
			AModuleModules module) {

		List<PDefinition> list = new Vector<PDefinition>();
		imp.setFrom(module);
		LexNameToken name = imp.getName();
		
		PDefinition expdef = PDefinitionListAssistantTC.findName(module.getExportdefs(),name, NameScope.NAMES);

		if (expdef == null)
		{
			TypeCheckerErrors.report(3193, "No export declared for import of value " + name + " from " + module.getName(),imp.getLocation(),imp);
		}
		else
		{
			if (imp.getRenamed() != null)
			{
				expdef =  new ARenamedDefinition(imp.getRenamed().location, imp.getRenamed(), expdef.getNameScope(), false, null, PAccessSpecifierAssistant.getDefault(), null, expdef);
			}
			else
			{
				expdef = new AImportedDefinition(expdef.getLocation(), expdef.getNameScope(), false, null, PAccessSpecifierAssistant.getDefault(), null, expdef, expdef.getName());
			}

			list.add(expdef);
		}

		return list;
	}

}
