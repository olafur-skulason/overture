package org.overture.codegen.vdm2systemc;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.trans.AssignStmTrans;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;
import org.overture.codegen.trans.letexps.FuncTrans;
import org.overture.codegen.vdm2systemc.transformations.*;

public class SystemcTransSeries
{

	private List<DepthFirstAnalysisAdaptor> series;
	private List<DepthFirstAnalysisAdaptor> preAnalysisSeries;
	private FuncValAssistant funcValAssist;

	private SystemcCodeGen codeGen;

	private String rootName;


	public SystemcTransSeries(SystemcCodeGen codeGen, String rootName)
	{
		this.series = new LinkedList<>();
		this.preAnalysisSeries = new LinkedList<>();
		this.funcValAssist = new FuncValAssistant();
		this.codeGen = codeGen;
		this.rootName = rootName;
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

	private void setupAnalysis()
	{
		// Data and functionality to support the transformations

        IRInfo info = codeGen.getIRGenerator().getIRInfo();
        TransAssistantIR transAssist = codeGen.getTransAssistant();

		// Construct the transformations
		DependenciesTransformation dependenciesTransformation = new DependenciesTransformation(info);
		FuncTrans funcTr = new FuncTrans(transAssist);
		AssignStmTrans assignTr = new AssignStmTrans(transAssist);
		CPUInitializationTransformation cpuInitializationTransformation = new CPUInitializationTransformation();
		BusInitializationTransformation busInitializationTransformation = new BusInitializationTransformation();

        // Set up order of transformations
		series.add(dependenciesTransformation);
		series.add(funcTr);
		series.add(assignTr);
		series.add(cpuInitializationTransformation);
		series.add(busInitializationTransformation);


		// Construct pre analysis transformations
		RemoteMethodCallTransformations remoteMethodCallTransformations = new RemoteMethodCallTransformations(rootName);

		// Set up pre analysis transformation order
		preAnalysisSeries.add(remoteMethodCallTransformations);
	}

	public List<DepthFirstAnalysisAdaptor> getPreArchitecturalSeries() {
		return preAnalysisSeries;
	}
}
