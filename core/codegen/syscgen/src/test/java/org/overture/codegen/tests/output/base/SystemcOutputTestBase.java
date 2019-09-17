package org.overture.codegen.tests.output.base;

import java.util.List;

import org.junit.After;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.vdm2systemc.SystemcCodeGen;
import org.overture.ast.node.INode;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.ir.CodeGenBase;

import org.overture.codegen.tests.output.util.OutputTestBase;

import org.overture.ast.definitions.SClassDefinition;

public abstract class SystemcOutputTestBase extends OutputTestBase
{
	private SystemcCodeGen systemcGen;

	public SystemcOutputTestBase(String nameParameter, String inputParameter, String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	public SystemcCodeGen getSystemcGen()
	{
		if(systemcGen == null)
		{
			systemcGen = new SystemcCodeGen();
		}

		return systemcGen;
	}

	@After
	public void printUnsupported()
	{
		if(systemcGen != null) {
			if (systemcGen.getImplementationSystemcFormat().getMergeVisitor().hasUnsupportedTargLangNodes()) {
				System.out.println("Unsupported nodes found:");
				for (IrNodeInfo info : systemcGen.getImplementationSystemcFormat().getMergeVisitor().getUnsupportedInTargLang()) {
					System.out.println(info.getNode().getClass().getName() + " :: " + info.getReason());
				}
			}
			if (systemcGen.getHeaderSystemcFormat().getMergeVisitor().hasUnsupportedTargLangNodes()) {
				System.out.println("Unsupported nodes found:");
				for (IrNodeInfo info : systemcGen.getHeaderSystemcFormat().getMergeVisitor().getUnsupportedInTargLang()) {
					System.out.println(info.getNode().getClass().getName() + " :: " + info.getReason());
				}
			}
		}
	}

	public GeneratedData genCode(List<INode> ast) throws AnalysisException
	{
		List<SClassDefinition> classes = buildClassList(ast);
		return getSystemcGen().generate(CodeGenBase.getNodes(classes));
	}
}
