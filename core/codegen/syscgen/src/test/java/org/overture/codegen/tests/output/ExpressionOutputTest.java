package org.overture.codegen.tests.output;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.core.testing.ParamFineGrainTest;
import org.overture.core.testing.PathsProvider;
import org.overture.codegen.vdm2systemc.SystemcCodeGen;
import org.overture.codegen.vdm2systemc.SystemcCodeGenUtil;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.tests.output.util.OutputTestUtil;
import org.overture.codegen.utils.Generated;

import org.overture.config.Release;
import org.overture.config.Settings;

import com.google.gson.reflect.TypeToken;

@RunWith(Parameterized.class)
public class ExpressionOutputTest extends ParamFineGrainTest<String>
{
	public static final String ROOT = "src" + File.separatorChar + "test" + File.separatorChar + "resources" + File.separatorChar + "expressions";

	public static final SystemcCodeGen systemcCodeGen = new SystemcCodeGen();

	public ExpressionOutputTest(String nameParameter, String testParameter, String resultParameter)
	{
		super(nameParameter, testParameter, resultParameter);
	}

	@BeforeClass
	public static void init()
	{
		Settings.dialect = Dialect.VDM_RT;
		Settings.release = Release.VDM_10;
	}

	@Override
	public String processSource()
	{
		try
		{
			String fileContent = GeneralUtils.readFromFile(new File(modelPath));
			Generated generatedObject = SystemcCodeGenUtil.generateSystemcFromExp(fileContent, systemcCodeGen, Settings.dialect);
			String generatedCode = generatedObject.getContent();
			if(generatedCode == null)
			{
				return "";
			}
			else
			{
				String trimmed = GeneralUtils.cleanupWhiteSpaces(generatedCode);
				return trimmed;
			}
		}
		catch (IOException | AnalysisException e)
		{
			e.printStackTrace();
			Assert.fail("Problems code generating expression to Systemc: " + e.getMessage());
			return null;
		}
	}

	@After
	public void printUnsupported()
	{
		if(systemcCodeGen != null) {
			if (systemcCodeGen.getImplementationSystemcFormat().getMergeVisitor().hasUnsupportedTargLangNodes()) {
				System.out.println("Unsupported nodes found:");
				for (IrNodeInfo info : systemcCodeGen.getImplementationSystemcFormat().getMergeVisitor().getUnsupportedInTargLang()) {
					System.out.println(info.getNode().getClass().getName());
				}
			}
			else if(systemcCodeGen.getHeaderSystemcFormat().getMergeVisitor().hasUnsupportedTargLangNodes())
			{
				System.out.println("Unsupported nodes in header generation:");
				for (IrNodeInfo info : systemcCodeGen.getHeaderSystemcFormat().getMergeVisitor().getUnsupportedInTargLang()) {
					System.out.println(info.getNode().getClass().getName());
				}
			}
		}
	}

	@Override
	public String getUpdatePropertyString()
	{
		return OutputTestUtil.UPDATE_PROPERTY_PREFIX + "exp";
	}

	@Override
	public Type getResultType()
	{
		Type resultType = new TypeToken<String>()
		{}.getType();
		return resultType;
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(ROOT);
	}

	@Override
	public void compareResults(String actual, String expected)
	{
		OutputTestUtil.compare(expected, actual);
	}
	
	@Override
	public String deSerializeResult(String resultPath)
		throws FileNotFoundException, IOException
	{
		return OutputTestUtil.deSerialize(resultPath);
	}
}
