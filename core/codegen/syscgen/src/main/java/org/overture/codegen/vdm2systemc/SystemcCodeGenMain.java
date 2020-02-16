

package org.overture.codegen.vdm2systemc;

import org.eclipse.osgi.framework.internal.core.Msg;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.printer.MsgPrinter;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.config.Settings;
import org.overture.codegen.utils.*;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import java.io.File;
import java.util.*;

public class SystemcCodeGenMain
{
	// Command-line args
	public static final String OUTPUT_ARG = "-output";

	public static void main(String[] args)
	{
		long clock = System.currentTimeMillis();
		
		if (args == null || args.length <= 1)
		{
			usage("Too few arguments provided");
		}

		SystemcSettings systemcSettings = new SystemcSettings();

		IRSettings irSettings = new IRSettings();
		irSettings.setGenerateConc(true);

		List<String> listArgs = Arrays.asList(args);
		String exp = null;
		File outputDir = null;

		List<File> files = new LinkedList<File>();

		Settings.dialect = Dialect.VDM_RT;

		for (Iterator<String> i = listArgs.iterator(); i.hasNext();)
		{
			String arg = i.next();

			if(arg.equals(OUTPUT_ARG))
			{
				if(i.hasNext())
				{
					outputDir = new File(i.next());
					outputDir.mkdirs();

					if(!outputDir.isDirectory())
					{
						usage(outputDir + " is not a directory");
					}
				}
				else
				{
					usage(OUTPUT_ARG + " requires a dictionary");
				}
			}
			else
			{
				File file = new File(arg);

				if (file.isFile())
				{
					files.add(file);
				}
				else
				{
					usage("Not a file: " + file);
				}
			}
		}

		if (Settings.dialect == null)
		{
			usage("No VDM dialect specified");
		}

		MsgPrinter.getPrinter().println("Starting code generation...\n");

		if (files.isEmpty())
		{
			usage("Input files are missing");
		}

		if (outputDir == null)
		{
			usage("No output directory specified");
		}

		MsgPrinter.getPrinter().println("Starting generation.");
		handle_files(files, irSettings, systemcSettings, outputDir);

		clock = System.currentTimeMillis() - clock;
		MsgPrinter.getPrinter().println("\nFinished code generation! @" + (clock/1000) + "s");
	}

	public static void usage(String msg)
	{
		MsgPrinter.getPrinter().errorln("VDM-to-SystemC Code Generator: " + msg + "\n");

		System.exit(1);
	}

	public static void handle_files(List<File> files, IRSettings irSettings, SystemcSettings systemcSettings, File outputDir)
	{
		try
		{
			SystemcCodeGen syscCg = new SystemcCodeGen();
			syscCg.systemcSettings = systemcSettings;
			syscCg.irSettings = irSettings;

			TypeCheckResult<List<SClassDefinition>> tcResult = TypeCheckerUtil.typeCheckRt(files);

			if (GeneralCodeGenUtils.hasErrors(tcResult))
			{
				MsgPrinter.getPrinter().error("Found errors in VDM model:");
				MsgPrinter.getPrinter().error(GeneralCodeGenUtils.errorStr(tcResult));
				return;
			}

			syscCg.generate(CodeGenBase.getNodes(tcResult.result));

			processData(outputDir, syscCg, syscCg.GetHeaders(), ".h");
			processData(outputDir, syscCg, syscCg.GetSources(), ".cpp");
		}
		catch (AnalysisException e)
		{
			MsgPrinter.getPrinter().println("Could not transform model: " + e.getMessage());
		}
	}

	public static void processData(final File outputDir, SystemcCodeGen syscCg, GeneratedData data, String fileExtension)
	{
		List<GeneratedModule> generatedClasses = data.getClasses();
		List<String> names = new ArrayList<>();

		int errorsCount = 0;
		int warningCount = 0;
		if(!generatedClasses.isEmpty())
		{
			for(GeneratedModule generatedClass : generatedClasses)
			{
				if(generatedClass.hasMergeErrors())
				{
					MsgPrinter.getPrinter().println(String.format("Class %s could not be merged. Following merge errors were found:", generatedClass.getName()));

					GeneralCodeGenUtils.printMergeErrors(generatedClass.getMergeErrors());
				}
				else if(!generatedClass.canBeGenerated())
				{
					MsgPrinter.getPrinter().println("Could not generate class: " + generatedClass.getName());

					GeneralCodeGenUtils.printUnsupportedNodes(generatedClass.getUnsupportedInTargLang());
				}
				else
				{
					if(outputDir != null)
					{
						syscCg.genFile(outputDir, generatedClass, fileExtension);
					}

					names.add(generatedClass.getName());

					MsgPrinter.getPrinter().println("Generated class: " + generatedClass.getName());

					Set<IrNodeInfo> warnings = generatedClass.getTransformationWarnings();

					if(!warnings.isEmpty())
					{
						MsgPrinter.getPrinter().println("Following transformation warnings were found:");
						warningCount += generatedClass.getTransformationWarnings().size();

						GeneralCodeGenUtils.printUnsupportedNodes(generatedClass.getTransformationWarnings());
					}
				}
			}
		}
		else
		{
			MsgPrinter.getPrinter().println("No classes were generated!");
		}

		List<GeneratedModule> quotes = data.getQuoteValues();

		if (quotes != null && !quotes.isEmpty())
		{
			MsgPrinter.getPrinter().println("\nGeneratedFollowing quotes (" + quotes.size() + "):");

			for(GeneratedModule q : quotes)
			{
				syscCg.genQuoteFile(outputDir, q);
			}
		}
	}
}
