package org.overture.codegen.vdm2systemc;

import org.eclipse.core.internal.utils.Convert;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.statements.ACyclesStmIR;
import org.overture.codegen.ir.statements.ADurationStmIR;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.definitions.*;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.trans.ModuleToClassTransformation;
import org.overture.codegen.trans.DivideTrans;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscBusModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscModuleDeclIR;
import org.overture.codegen.vdm2systemc.transformations.RemoteMethodCallTransformations;

import java.util.*;
import java.io.File;

public class SystemcCodeGen extends CodeGenBase 
{

	private SystemcFormat implementationSystemcFormat, headerSystemcFormat;
	private SClassDefinition mainClass;
	private ArchitecturalAnalysis architecturalAnalysis;

	private List<String> warnings;

	public SystemcSettings systemcSettings;
	public IRSettings irSettings;
	private GeneratedData generatedHeaders;
	private GeneratedData generatedSources;

	public GeneratedData GetHeaders()
	{
		return generatedHeaders;
	}

	public GeneratedData GetSources()
	{
		return generatedSources;
	}

	public SystemcCodeGen()
	{
		super();
		this.implementationSystemcFormat = new SystemcFormat(SystemcConstants.SYSTEMC_TEMPLATES_ROOT_FOLDER, SystemcConstants.SYSTEMC_BASE_TEMPLATE_ROOT_FOLDER, generator.getIRInfo());
		this.headerSystemcFormat = new SystemcFormat(SystemcConstants.SYSTEMC_HEADER_TEMPLATE_ROOT_FOLDER, SystemcConstants.SYSTEMC_BASE_TEMPLATE_ROOT_FOLDER, generator.getIRInfo());
		this.mainClass = null;
		this.warnings = new LinkedList<>();
		this.architecturalAnalysis = new ArchitecturalAnalysis(this.implementationSystemcFormat);
		generatedHeaders = null;
		generatedSources = null;
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
		List<GeneratedModule> genHeaders = new LinkedList<GeneratedModule>();
		List<GeneratedModule> genSources = new LinkedList<GeneratedModule>();
		List<GeneratedModule> genModules = new LinkedList<GeneratedModule>();

		statuses = initialIrEvent(statuses);

		List<IRStatus<PIR>> canBeGenerated = new LinkedList<IRStatus<PIR>>();

		for (IRStatus<PIR> status : statuses)
		{
			if(status.canBeGenerated())
			{
				canBeGenerated.add(status);
			}
			else
			{
				genModules.add(new GeneratedModule(status.getIrNodeName(), status.getUnsupportedInIr(), new HashSet<>(), isTestCase(status)));
			}
		}
		architecturalAnalysis.FindRoot(canBeGenerated);

		SystemcTransSeries transSeries = new SystemcTransSeries(this, architecturalAnalysis.getRootName());
		RunTransformations(canBeGenerated, transSeries.getPreArchitecturalSeries());

		try {
			architecturalAnalysis.AnalyseArchitecture(canBeGenerated);
		}
		catch(org.overture.codegen.ir.analysis.AnalysisException e) {
			warnings.add("Error in architectural analysis. " + e.getMessage());
		}

		RunTransformations(canBeGenerated, transSeries.getSeries());

		MergeVisitor implMergeVisitor = implementationSystemcFormat.getMergeVisitor();
		MergeVisitor headerMergeVisitor = headerSystemcFormat.getMergeVisitor();
		implementationSystemcFormat.setFunctionValueAssistant(transSeries.getFuncValAssist());
		headerSystemcFormat.setFunctionValueAssistant(transSeries.getFuncValAssist());

		for(IRStatus<PIR> status : canBeGenerated)
		{
			org.overture.ast.node.INode vdmClass = status.getVdmNode();

			if(vdmClass == mainClass)
			{
				PIR mainIr = status.getIrNode();
				if (mainIr instanceof ADefaultClassDeclIR)
				{
					status.getIrNode().setTag(new SystemcMainTag((ADefaultClassDeclIR) status.getIrNode()));
				}
				else if (mainIr instanceof ASyscModuleDeclIR)
				{
					status.getIrNode().setTag(new SystemcMainTag((ASyscModuleDeclIR) status.getIrNode()));
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
					genHeaders.add(genIrModule(headerMergeVisitor, status));
					genSources.add(genIrModule(implMergeVisitor, status));
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

		generatedHeaders = new GeneratedData();
		generatedHeaders.setClasses(genHeaders);
		generatedHeaders.setQuoteValues(generateFromVdmQuotes());
		generatedHeaders.setWarnings(warnings);

		generatedSources = new GeneratedData();
		generatedSources.setClasses(genSources);
		generatedSources.setQuoteValues(generateFromVdmQuotes());
		generatedSources.setWarnings(warnings);

		return data;
	}

	private void RunTransformations(List<IRStatus<PIR>> canBeGenerated, List<DepthFirstAnalysisAdaptor> transformations) {
		for (DepthFirstAnalysisAdaptor trans : transformations)
		{
			for(IRStatus<PIR> status : canBeGenerated)
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
	}

	public GeneratedModule genSourceModule(MergeVisitor mergeVisitor, IRStatus<SClassDeclIR> status) throws org.overture.codegen.ir.analysis.AnalysisException
	{

		return genIrModule(mergeVisitor, status);
	}

	public List<GeneratedModule> generateFromVdmQuotes()
	{
		return null;
	}

	public void genSystemcSourceFiles(File root, List<GeneratedModule> generatedClasses, String fileExtension)
	{
		for(GeneratedModule classCg : generatedClasses)
		{
			if(classCg.canBeGenerated())
			{
				genFile(new File(root, SystemcConstants.CPP_FILE_FOLDER), classCg, fileExtension);
			}
		}
	}

	public void genFile(File root, GeneratedModule generatedModule, String fileExtension)
	{
		File moduleOutputDir = root;

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
	protected void preProcessAst(List<org.overture.ast.node.INode> ast) throws AnalysisException
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
			System.out.println("Could not generate expression: " + exp.toString());
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

	@Override
	protected boolean isTestCase(IRStatus<? extends PIR> status) {
		if(status.getIrNode().getSourceNode() != null)
			return super.isTestCase(status);
		else
			return false;
	}

	/*
	protected void generateSyscModules(List<IRStatus<PIR>> statuses)
	{
		try
		{
			Collection<? extends IRStatus<PIR>> moduleDefinitions = new SyscModuleGenerator().generateModules(IRStatus.extract(statuses, ADefaultClassDeclIR.class));

			for(IRStatus<PIR> m : moduleDefinitions)
			{

				modules.add(m.getIrNodeName());

				for(IRStatus<PIR> orig : statuses)
				{
					if(orig.getIrNodeName() == m.getIrNodeName())
					{
						statuses.remove(orig);
					}
				}
				statuses.add(m);
			}
		}
		catch (org.overture.codegen.ir.analysis.AnalysisException e)
		{
			log.error("Could not generate module headers:" + e.getMessage());
		}
	}

	 */
}


