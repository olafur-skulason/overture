package org.overture.codegen.tests.output;

import java.util.Collection;
import java.io.File;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.codegen.tests.output.util.OutputTestUtil;

import org.overture.core.testing.PathsProvider;

import org.overture.codegen.tests.output.base.SystemcOutputTestBase;

@RunWith(Parameterized.class)
public class ClassicOutputTest extends SystemcOutputTestBase
{
	public static final String ROOT = "src" + File.separatorChar + "test" 
			+ File.separatorChar + "resources" + File.separatorChar 
			+ "classic_specs";

	public ClassicOutputTest(String nameParameter, String inputParameter, String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	@Before
	public void init()
	{
		super.init();
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(ROOT);
	}

	@Override
	protected String getUpdatePropertyString()
	{
		return OutputTestUtil.UPDATE_PROPERTY_PREFIX + "classic";
	}
}
