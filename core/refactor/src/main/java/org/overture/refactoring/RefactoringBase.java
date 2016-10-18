package org.overture.refactoring;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.util.modules.ModuleList;
import org.overture.codegen.ir.IRGenerator;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.printer.MsgPrinter;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.config.Settings;
import org.overture.convert.function.to.operation.ConvertFunctionToOperation;
import org.overture.extract.Extraction;
import org.overture.extract.Extractor;
import org.overture.extract.RefactoringExtractionCollector;
import org.overture.rename.RefactoringRenameCollector;
import org.overture.rename.Renamer;
import org.overture.signature.RefactoringSignatureChangeCollector;
import org.overture.signature.SignatureChanger;
import org.overture.signature.SignatureChange;
import org.overture.rename.Renaming;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
import org.overture.unreachable.stm.remover.Removal;
import org.overture.unreachable.stm.remover.UnreachableStmRemover;

public class RefactoringBase {
	
	protected IRGenerator generator;
	private List<Renaming> allRenamings;
	private List<Extraction> allExtractions;
	private List<SignatureChange> allSignatureChanges;
	private List<Removal> allRemovals;
	private GeneratedData generatedData;
	public RefactoringBase(){
		this.generator = new IRGenerator();
		
		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(true);
		irSettings.setGeneratePreConds(false);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(false);
		irSettings.setGeneratePostCondChecks(false);
		generator.getIRInfo().setSettings(irSettings);
	}
	
	public List<INode> generateRenaming(List<INode> ast, String[] parameters) throws AnalysisException
	{

		List<INode> userModules = extractUserModules(ast);
		
		allRenamings = new LinkedList<Renaming>();
		allRenamings.addAll(performRenaming(userModules, getInfo().getIdStateDesignatorDefs(), parameters));

		if(generatedData == null){
			generatedData = new GeneratedData();
		}
		generatedData.setAllRenamings(allRenamings);

		return userModules;
	}
	
	public List<INode> generateExtraction(List<INode> ast, String[] parameters) throws AnalysisException
	{

		List<INode> userModules = extractUserModules(ast);
		
		allExtractions = new LinkedList<Extraction>();
		allExtractions.addAll(performExtraction(userModules, getInfo().getIdStateDesignatorDefs(), parameters));

		if(generatedData == null){
			generatedData = new GeneratedData();
		}
		generatedData.setAllExtractions(allExtractions);

		return userModules;
	}

	public List<INode> generateSignatureChanges(List<INode> ast, String[] parameters) throws AnalysisException
	{
		List<INode> userModules = extractUserModules(ast);
		
		allSignatureChanges = new LinkedList<SignatureChange>();
		allSignatureChanges.addAll(performSignatureChanges(userModules, getInfo().getIdStateDesignatorDefs(), parameters));

		if(generatedData == null){
			generatedData = new GeneratedData();
		}
		generatedData.setAllSignatureChanges(allSignatureChanges);

		return userModules;
	}

	public List<INode> removeUnreachableStm(List<INode> ast) throws AnalysisException
	{

		List<INode> userModules = extractUserModules(ast);
		
		
		UnreachableStmRemover stmRemover = new UnreachableStmRemover();
		for (INode node : userModules)
		{
			node.apply(stmRemover);
		}
		if(generatedData == null){
			generatedData = new GeneratedData();
		}
		generatedData.setAllRemovals(stmRemover.getAllRemovals());

		return userModules;
	}
	
	public List<INode> convertFunctionToOperation(List<INode> ast, int line) throws AnalysisException
	{

		List<INode> userModules = extractUserModules(ast);
		
		
		ConvertFunctionToOperation converter = new ConvertFunctionToOperation(line);
		for (INode node : userModules)
		{
			node.apply(converter);
		}
		if(generatedData == null){
			generatedData = new GeneratedData();
		}
//		generatedData.setAllRemovals(stmRemover.getAllRemovals());

		return userModules;
	}
	
	public List<INode> extractUserModules(List<INode> ast) {
		if (Settings.dialect == Dialect.VDM_SL)
		{
			ModuleList moduleList = new ModuleList(getModules(ast));
			moduleList.combineDefaults();
			ast = getNodes(moduleList);
		}
		
		List<INode> userModules = getUserModules(ast);
		return userModules;
	}
	
	public List<INode> getAST(String fileName) throws AnalysisException
	{
		File file = new File(fileName);
		
		TypeCheckResult<List<AModuleModules>> tcResult = TypeCheckerUtil.typeCheckSl(file);

		if (GeneralCodeGenUtils.hasErrors(tcResult))
		{
			MsgPrinter.getPrinter().error("Found errors in VDM model:");
			MsgPrinter.getPrinter().errorln(GeneralCodeGenUtils.errorStr(tcResult));
			return null;
		}

		List<INode> ast = getNodes(tcResult.result);
		
		List<INode> userModules = extractUserModules(ast);
		return userModules;
	}
	
	public static List<INode> getNodes(List<? extends INode> ast)
	{
		List<INode> nodes = new LinkedList<>();

		nodes.addAll(ast);

		return nodes;
	}
	
	public static List<AModuleModules> getModules(List<INode> ast)
	{
		List<AModuleModules> modules = new LinkedList<>();

		for (INode n : ast)
		{
			if (n instanceof AModuleModules)
			{
				modules.add((AModuleModules) n);
			}
		}

		return modules;
	}
	
	private List<Renaming> performRenaming(List<INode> mergedParseLists,
			Map<AIdentifierStateDesignator, PDefinition> idDefs, String[] parameters)
			throws AnalysisException
	{

		List<Renaming> allRenamings = new LinkedList<Renaming>();

		RefactoringRenameCollector renamingsCollector = new RefactoringRenameCollector(generator.getIRInfo().getTcFactory(), idDefs);
		Renamer renamer = new Renamer();
		renamingsCollector.setRefactoringParameters(parameters);
		for (INode node : mergedParseLists)
		{
			Set<Renaming> currentRenamings = renamer.computeRenamings(node, renamingsCollector);

			if (!currentRenamings.isEmpty())
			{
				allRenamings.addAll(currentRenamings);
			}
		}

		Collections.sort(allRenamings);

		return allRenamings;
	}
	
	private List<Extraction> performExtraction(List<INode> mergedParseLists,
			Map<AIdentifierStateDesignator, PDefinition> idDefs, String[] parameters)
			throws AnalysisException
	{

		List<Extraction> allExtractions = new LinkedList<Extraction>();

		RefactoringExtractionCollector extractionsCollector = new RefactoringExtractionCollector();
		Extractor extractor = new Extractor();
		extractionsCollector.setRefactoringParameters(parameters);
		for (INode node : mergedParseLists)
		{
			Set<Extraction> currentExtractions = extractor.computeExtractions(node, extractionsCollector);

			if (!currentExtractions.isEmpty())
			{
				allExtractions.addAll(currentExtractions);
			}
		}

		Collections.sort(allExtractions);

		return allExtractions;
	}
	
	private List<SignatureChange> performSignatureChanges(List<INode> mergedParseLists,
			Map<AIdentifierStateDesignator, PDefinition> idDefs, String[] parameters)
			throws AnalysisException
	{

		List<SignatureChange> allSignatureChanges = new LinkedList<SignatureChange>();

		RefactoringSignatureChangeCollector signatureChangeCollector = new RefactoringSignatureChangeCollector(generator.getIRInfo().getTcFactory(), idDefs);
		SignatureChanger changer = new SignatureChanger();
		signatureChangeCollector.setRefactoringParameters(parameters);
		for (INode node : mergedParseLists)
		{
			Set<SignatureChange> currentSignatureChanges = changer.computeSignatureChanges(node, signatureChangeCollector);

			if (!currentSignatureChanges.isEmpty())
			{
				allSignatureChanges.addAll(currentSignatureChanges);
			}
		}

		Collections.sort(allSignatureChanges);

		return allSignatureChanges;
	}
	
	protected List<INode> getUserModules(List<? extends INode> ast)
	{
		List<INode> userModules = new LinkedList<INode>();

		for (INode node : ast)
		{
			if (!getInfo().getDeclAssistant().isLibrary(node))
			{
				userModules.add(node);
			}
		}

		return userModules;
	}

	public IRInfo getInfo()
	{
		return generator.getIRInfo();
	}
	
	public GeneratedData getGeneratedData(){
		return generatedData;
	}
	
	public void init(){	
		generatedData = new GeneratedData();
	}
}
