package org.overture.codegen.vdm2systemc;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.trans.AssignStmTrans;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;
import org.overture.codegen.trans.letexps.FuncTrans;
import org.overture.codegen.vdm2systemc.transformations.CPUInitializationTransformation;
import org.overture.codegen.vdm2systemc.transformations.DependenciesTransformation;

public class SystemcTransSeries
{

	private List<DepthFirstAnalysisAdaptor> series;
	private FuncValAssistant funcValAssist;

	private SystemcCodeGen codeGen;

	public SystemcTransSeries(SystemcCodeGen codeGen)
	{
		this.series = new LinkedList<>();
		this.funcValAssist = new FuncValAssistant();
		this.codeGen = codeGen;
		setupAnalysis();
	}

	public List<DepthFirstAnalysisAdaptor> getSeries()
	{
		return series;
	}

	public FuncValAssistant getFuncValAssist()
	{
		return funcValAssist;
	}

	private List<DepthFirstAnalysisAdaptor> setupAnalysis()
	{
		// Data and functionality to support the transformations

        IRInfo info = codeGen.getIRGenerator().getIRInfo();
        /*
        JavaVarPrefixManager varMan = codeGen.getVarPrefixManager();
        IterationVarPrefixes iteVarPrefixes = varMan.getIteVarPrefixes();
        Exp2StmVarPrefixes exp2stmPrefixes = varMan.getExp2stmPrefixes();
        TraceNames tracePrefixes = varMan.getTracePrefixes();
        FuncValPrefixes funcValPrefixes = varMan.getFuncValPrefixes();
        PatternVarPrefixes patternPrefixes = varMan.getPatternPrefixes();
        UnionTypeVarPrefixes unionTypePrefixes = varMan.getUnionTypePrefixes();
        List<INode> cloneFreeNodes = codeGen.getJavaFormat().getValueSemantics().getCloneFreeNodes();
		*/
        TransAssistantIR transAssist = codeGen.getTransAssistant();
        //IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(varMan.postCheckMethodName());


        // Construct the transformations
		DependenciesTransformation dependenciesTransformation = new DependenciesTransformation(info);
		FuncTrans funcTr = new FuncTrans(transAssist);
		AssignStmTrans assignTr = new AssignStmTrans(transAssist);
		CPUInitializationTransformation cpuInitializationTransformation = new CPUInitializationTransformation();
		/*
        RenamedTrans renamedTr = new RenamedTrans(transAssist);
        ModuleRenamerTrans moduleRenamerTr = new ModuleRenamerTrans(transAssist);
        UnsupportedLibWarningTrans libWarnTr = new UnsupportedLibWarningTrans(transAssist);
        FieldOrderTrans fieldOrderTr = new FieldOrderTrans();
        AtomicStmTrans atomicTr = new AtomicStmTrans(transAssist, varMan.atomicTmpVar());
        NonDetStmTrans nonDetTr = new NonDetStmTrans(transAssist);
        DivideTrans divideTr = new DivideTrans(info);
        CallObjStmTrans callObjTr = new CallObjStmTrans(info);
        PrePostTrans prePostTr = new PrePostTrans(info);
        IfExpTrans ifExpTr = new IfExpTrans(transAssist);
        PolyFuncTrans polyTr = new PolyFuncTrans(transAssist);
        FuncValTrans funcValTr = new FuncValTrans(transAssist, funcValAssist, funcValPrefixes);
        ILanguageIterator langIte = new JavaLanguageIterator(transAssist, iteVarPrefixes);
        LetBeStTrans letBeStTr = new LetBeStTrans(transAssist, langIte, iteVarPrefixes);
        WhileStmTrans whileTr = new WhileStmTrans(transAssist, varMan.whileCond());
        Exp2StmTrans exp2stmTr = new Exp2StmTrans(iteVarPrefixes, transAssist, consCounterData(), langIte, exp2stmPrefixes);
        PatternTrans patternTr = new PatternTrans(iteVarPrefixes, transAssist, patternPrefixes, varMan.casesExp());
        PreCheckTrans preCheckTr = new PreCheckTrans(transAssist, new JavaValueSemanticsTag(false));
        PostCheckTrans postCheckTr = new PostCheckTrans(postCheckCreator, transAssist, varMan.funcRes(), new JavaValueSemanticsTag(false));
        IsExpSimplifyTrans isExpSimplifyTr = new IsExpSimplifyTrans(transAssist);
        IsExpTrans isExpTr = new IsExpTrans(transAssist, varMan.isExpSubject());
        SeqConvTrans seqConvTr = new SeqConvTrans(transAssist);
        TracesTrans tracesTr = new TracesTrans(transAssist, iteVarPrefixes, tracePrefixes, langIte, new JavaCallStmToStringBuilder(), cloneFreeNodes);
        UnionTypeTrans unionTypeTr = new UnionTypeTrans(transAssist, unionTypePrefixes, cloneFreeNodes);
        JavaToStringTrans javaToStringTr = new JavaToStringTrans(info);
        RecMethodsTrans recTr = new RecMethodsTrans(codeGen.getJavaFormat().getRecCreator());
        ConstructorTrans ctorTr = new ConstructorTrans(transAssist, OBJ_INIT_CALL_NAME_PREFIX);
        ImportsTrans impTr = new ImportsTrans(info);
        SlAccessTrans slAccessTr = new SlAccessTrans();
        JUnit4Trans junitTr = new JUnit4Trans(transAssist, codeGen);
		*/

        // Start concurrency transformations
		/*
        SentinelTrans sentinelTr = new SentinelTrans(info, varMan.getConcPrefixes());
        MainClassConcTrans mainClassTr = new MainClassConcTrans(info, varMan.getConcPrefixes());
        MutexDeclTrans mutexTr = new MutexDeclTrans(info, varMan.getConcPrefixes());
        EvalPermPredTrans evalPermPredTr = new EvalPermPredTrans(transAssist, varMan.getConcPrefixes());
		*/
        // End concurrency transformations

        // Set up order of transformations
		series.add(dependenciesTransformation);
		series.add(funcTr);
		series.add(assignTr);
		series.add(cpuInitializationTransformation);

		return series;
	}
}
