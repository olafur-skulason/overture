package org.overture.codegen.tests.exec.base;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;

import org.junit.ComparisonFailure;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.tests.exec.util.CheckerTestBase;
import org.overture.codegen.tests.exec.util.testhandlers.TestHandler;
import org.overture.codegen.vdm2systemc.SystemcCodeGen;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
import org.overture.ast.definitions.SClassDefinition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.lex.LexException;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;

public abstract class SystemcGenTestBase extends CheckerTestBase
{
	public SystemcGenTestBase(File vdmSpec, TestHandler testHandler)
	{
		super(vdmSpec, testHandler);
	}

	private SystemcCodeGen systemcCg;

	public SystemcCodeGen getSystemcGen()
	{
		if(this.systemcCg == null)
		{
			this.systemcCg = new SystemcCodeGen();
		}

		return this.systemcCg;
	}

	//public SyscSettings getSystemcSettings()
	//{
	//	SystemcSettings systemcSettings = new system
	//}

	public static GeneratedData genData(SystemcCodeGen syscCg, List<File> files) 
			throws AnalysisException, ParserException, LexException
	{
		GeneratedData data = null;

		if(Settings.dialect == Dialect.VDM_RT)
		{
			TypeCheckResult<List<SClassDefinition>> tcResult = checkTcResult(TypeCheckerUtil.typeCheckPp(files));
			data = syscCg.generate(CodeGenBase.getNodes(tcResult.result));
		}

		return data;
	}

	public void genSystemcSources(File vdmSource)
	{
		SystemcCodeGen codeGenerator = getSystemcGen();

		try
		{
			List<File> files = new LinkedList<File>();
			files.add(vdmSource);

			GeneratedData data = genData(codeGenerator, files);

			if (data == null)
			{
				Assert.fail("Problems encountered when trying to code generated VDM model");
			}

			codeGenerator.genSystemcSourceFiles(outputDir, data.getClasses(), ".test");
		}
		catch (AnalysisException e)
		{
			Assert.fail("Got unexpected exception when attempting to generate Systemc code: " +
				e.getMessage());
			e.printStackTrace();
		}
		catch(ComparisonFailure error)
		{
			if(codeGenerator.getImplementationSystemcFormat().getMergeVisitor().hasUnsupportedTargLangNodes()) {
				Set<IrNodeInfo> unsupportedInTargLang = codeGenerator.getImplementationSystemcFormat().getMergeVisitor().getUnsupportedInTargLang();

				Assert.fail("Unsupported node encountered: {" + unsupportedInTargLang.iterator().next().toString() + "}");
			}
			if(codeGenerator.getHeaderSystemcFormat().getMergeVisitor().hasUnsupportedTargLangNodes()) {
				Set<IrNodeInfo> unsupportedInTargLang = codeGenerator.getHeaderSystemcFormat().getMergeVisitor().getUnsupportedInTargLang();

				Assert.fail("Unsupported node encountered: {" + unsupportedInTargLang.iterator().next().toString() + "}");
			}
			else
			{
				throw error;
			}
		}
	}

	public void genSourcesAndCompile()
	{
		genSystemcSources(file);
		compile(consCpFiles());
	}
}
