package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameList;

public class AUntypedDefinitionAssistantTC {

	public static List<PDefinition> getDefinitions(AUntypedDefinition d) {
		
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(d);
		return result;
	}

	public static LexNameList getVariableNames(AUntypedDefinition d) {
		return new LexNameList(d.getName());
	}

}
