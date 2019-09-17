package org.overture.codegen.vdm2systemc;

import org.overture.codegen.ir.*;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.definitions.*;
import org.overture.ast.node.INode;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.trans.ModuleToClassTransformation;
import org.overture.codegen.trans.DivideTrans;
import org.overture.codegen.merging.MergeVisitor;

import java.util.*;
import java.io.File;

public class SystemcCodeGen extends CodeGenBase 
{

	private SystemcFormat implementationSystemcFormat, headerSystemcFormat;
	private SystemcTransSeries transSeries;
	private SClassDefinition mainClass;

	private List<String> warnings;

	public SystemcSettings systemcSettings;
	public IRSettings irSettings;

	public SystemcCodeGen()
	{
		super();
		this.implementationSystemcFormat = new SystemcFormat(SystemcConstants.SYSTEMC_TEMPLATES_ROOT_FOLDER, SystemcConstants.SYSTEMC_BASE_TEMPLATE_ROOT_FOLDER, generator.getIRInfo());
		this.headerSystemcFormat = new SystemcFormat(SystemcConstants.SYSTEMC_HEADER_TEMPLATE_ROOT_FOLDER, SystemcConstants.SYSTEMC_BASE_TEMPLATE_ROOT_FOLDER, generator.getIRInfo());
		this.transSeries = new SystemcTransSeries(this);
		this.mainClass = null;
		this.warnings = new LinkedList<>();
		clearVdmAstData();
	}

	public SystemcFormat getImplementationSystemcFormat() { return implementationSystemcFormat; }
	public SystemcFormat getHeaderSystemcFormat() { return headerSystemcFormat; }

	private void clearVdmAstData()
	{
		this.mainClass = null;
	}

	@Override
	protected GeneratedData genVdmToTargetLang(
			List<IRStatus<PIR>> statuses) throws AnalysisException
	{
		List<GeneratedModule> genModules = new LinkedList<GeneratedModule>();

		statuses = initialIrEvent(statuses);

		List<IRStatus<AModuleDeclIR>> moduleStatuses = IRStatus.extract(statuses, AModuleDeclIR.class);
		List<IRStatus<PIR>> modulesAsNodes = IRStatus.extract(moduleStatuses);

		ModuleToClassTransformation moduleTransformation = new ModuleToClassTransformation(getInfo(), transAssistant, getModuleDecls(moduleStatuses));

		for (IRStatus<PIR> status : modulesAsNodes)
		{
			try
			{
				generator.applyTotalTransformation(status, moduleTransformation);
			}
			catch(org.overture.codegen.ir.analysis.AnalysisException e)
			{
				log.error("Error when generating code for module " + 
					status.getIrNodeName() + ": " + e.getMessage());
				log.error("Skipping module..");
				e.printStackTrace();
			}
		}

		List<IRStatus<SClassDeclIR>> classStatuses = IRStatus.extract(modulesAsNodes, SClassDeclIR.class);
		classStatuses.addAll(IRStatus.extract(statuses, SClassDeclIR.class));

		List<IRStatus<SClassDeclIR>> canBeGenerated = new LinkedList<IRStatus<SClassDeclIR>>();

		for (IRStatus<SClassDeclIR> status : classStatuses)
		{
			if(status.canBeGenerated())
			{
				canBeGenerated.add(status);
			}
			else
			{
				genModules.add(new GeneratedModule(status.getIrNodeName(), status.getUnsupportedInIr(), new HashSet<IrNodeInfo>(), isTestCase(status)));
			}
		}

		for (DepthFirstAnalysisAdaptor trans : transSeries.getSeries())
		{
			for(IRStatus<SClassDeclIR> status : canBeGenerated)
			{
				try
				{
					if (!getInfo().getDeclAssistant().isLibraryName(status.getIrNodeName()))
					{
						generator.applyPartialTransformation(status, trans);
					}
				}
				catch(org.overture.codegen.ir.analysis.AnalysisException e)
				{
					log.error("Error when generating code for class " +
						status.getIrNodeName() + ": " + e.getMessage());
					log.error("Skipping class..");
					e.printStackTrace();
				}
			}
		}

		MergeVisitor implMergeVisitor = implementationSystemcFormat.getMergeVisitor();
		MergeVisitor headerMergeVisitor = headerSystemcFormat.getMergeVisitor();
		implementationSystemcFormat.setFunctionValueAssistant(transSeries.getFuncValAssist());
		headerSystemcFormat.setFunctionValueAssistant(transSeries.getFuncValAssist());

		for(IRStatus<SClassDeclIR> status : canBeGenerated)
		{
			INode vdmClass = status.getVdmNode();

			if(vdmClass == mainClass)
			{
				SClassDeclIR mainIr = status.getIrNode();
				if (mainIr instanceof ADefaultClassDeclIR)
				{
					status.getIrNode().setTag(new SystemcMainTag((ADefaultClassDeclIR) status.getIrNode()));
				}
				else
				{
					log.error("Expected main class to be a " +
						ADefaultClassDeclIR.class.getSimpleName() +
						". Got: " + status.getIrNode());
				}
			}

			try
			{
				if(shouldGenerateVdmNode(vdmClass))
				{
					genModules.add(genIrModule(headerMergeVisitor, status));
					genModules.add(genIrModule(implMergeVisitor, status));
				}
			}
			catch (org.overture.codegen.ir.analysis.AnalysisException e)
			{
				log.error("Error generating code for class "
					+ status.getIrNodeName() + ": " + e.getMessage());
				log.error("Skipping class..");
				e.printStackTrace();
			}
		}

		GeneratedData data = new GeneratedData();
		data.setClasses(genModules);
	data.setQuoteValues(generateFromVdmQuotes());
		data.setWarnings(warnings);

		return data;
	}

	public GeneratedModule genSourceModule(MergeVisitor mergeVisitor, IRStatus<SClassDeclIR> status) throws org.overture.codegen.ir.analysis.AnalysisException
	{

		return genIrModule(mergeVisitor, status);
	}

	public List<GeneratedModule> generateFromVdmQuotes()
	{
		return null;
	}

	public void genSystemcSourceFiles(File root, List<GeneratedModule> generatedClasses)
	{
		for(GeneratedModule classCg : generatedClasses)
		{
			if(classCg.canBeGenerated())
			{
				genFile(new File(root, SystemcConstants.CPP_FILE_FOLDER), classCg);
			}
		}
	}

	public void genFile(File root, GeneratedModule generatedModule)
	{
		File moduleOutputDir = root;
		String fileExtension = ".h";

		if(moduleOutputDir == null)
		{
			return;
		}

		if(generatedModule.canBeGenerated()	&& !generatedModule.hasMergeErrors())
		{
			String fileName;

			org.overture.codegen.ir.INode irNode = generatedModule.getIrNode();

			if(irNode instanceof SClassDeclIR)
			{
				SClassDeclIR generatedClass = (SClassDeclIR)irNode;
				fileName = generatedClass.getName();
			}
			else
			{
				fileName = generatedModule.getName();
			}

			fileName += fileExtension;

			emitCode(moduleOutputDir, fileName, generatedModule.getContent());
		}
	}

	public void genQuoteFile(File root, GeneratedModule generatedModule)
	{
		// TODO: Find out what is supposed to be.
	}

	@Override
	protected void preProcessAst(List<INode> ast) throws AnalysisException
	{
		super.preProcessAst(ast);

		// TODO: Add main method to selected class here.
		/*
		if(getSystemcSettings().getVdmEntryExp() != null)
		{
			try
			{
				mainClass = GeneralCodeGenUtils.consMainClass(getClasses(ast), getSystemcSettings().getVdmEntryExp(), Settings.dialect, , getInfo().getTempVarNameGen());
			}
			catch(Exception e)
			{
				warnings.add("The chosen launch configuration could not be type checked: "
					+ e.getMessage());
				warnings.add("Skipping launch configuration..");
			}
		}
		*/
	}

	public Generated generateSystemcFromVdmExp(PExp exp) throws AnalysisException, org.overture.codegen.ir.analysis.AnalysisException
	{
		IRStatus<SExpIR> expStatus = generator.generateFrom(exp);

		if(expStatus.canBeGenerated())
		{
			generator.applyPartialTransformation(expStatus, new DivideTrans(getInfo()));
		}

		try
		{
			return genIrExp(expStatus, implementationSystemcFormat.getMergeVisitor());
		}
		catch (org.overture.codegen.ir.analysis.AnalysisException e)
		{
			log.error("Could not generate expression: " + exp);
			e.printStackTrace();
			return null;
		}
	}

	private List<AModuleDeclIR> getModuleDecls(List<IRStatus<AModuleDeclIR>> statuses)
	{
		List<AModuleDeclIR> modules = new LinkedList<AModuleDeclIR>();

		for(IRStatus<AModuleDeclIR> status : statuses)
		{
			modules.add(status.getIrNode());
		}

		return modules;
	}
}


