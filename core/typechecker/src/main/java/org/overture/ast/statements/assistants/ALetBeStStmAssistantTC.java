package org.overture.ast.statements.assistants;

import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.types.assistants.PTypeSet;

public class ALetBeStStmAssistantTC {

	public static PTypeSet exitCheck(ALetBeStStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
