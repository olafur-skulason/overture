package org.overture.codegen.tests.exec;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.tests.exec.base.SystemcGenTestBase;
import org.overture.codegen.tests.exec.util.testhandlers.ExpressionTestHandler;
import org.overture.codegen.tests.exec.util.testhandlers.TestHandler;
import org.overture.codegen.tests.output.ExpressionOutputTest;
import org.overture.config.Release;

@RunWith(value = Parameterized.class)
public class SystemcGenExpressionTest extends SystemcGenTestBase
{
	public SystemcGenExpressionTest(String name, File vdmSpec, TestHandler testHandler)
	{
		super(vdmSpec, testHandler);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		return collectTests(new File(ExpressionOutputTest.ROOT), new ExpressionTestHandler(Release.VDM_10, Dialect.VDM_RT));
	}

	@Override
	protected String getPropertyId()
	{
		return "exp";
	}
}
