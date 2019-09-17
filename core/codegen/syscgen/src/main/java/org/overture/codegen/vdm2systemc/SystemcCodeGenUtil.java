
package org.overture.codegen.vdm2systemc;

import org.apache.log4j.Logger;

import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.utils.Generated;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Settings;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.ast.expressions.PExp;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import java.io.File;

public class SystemcCodeGenUtil
{
	private static Logger log = Logger.getLogger(SystemcCodeGenUtil.class.getName());

	public static Generated generateSystemcFromExp(String exp, SystemcCodeGen systemcCg, Dialect dialect) throws AnalysisException
	{
		Settings.dialect = dialect;
		TypeCheckResult<PExp> typeCheckResult = GeneralCodeGenUtils.validateExp(exp);

		if (typeCheckResult.errors.size() > 0)
		{
			throw new AnalysisException("Unable to type check expression" + exp);
		}

		try
		{
			return systemcCg.generateSystemcFromVdmExp(typeCheckResult.result);
		}
		catch (AnalysisException | org.overture.codegen.ir.analysis.AnalysisException e)
		{
			throw new AnalysisException("Unable to generate code from expression: " + exp + ". Exception message: " + e.getMessage());
		}
	}
}
