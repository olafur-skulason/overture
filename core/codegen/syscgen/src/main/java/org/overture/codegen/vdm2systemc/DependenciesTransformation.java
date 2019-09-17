package org.overture.codegen.vdm2systemc;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ACpuClassDeclIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.ASystemClassDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;

import java.util.LinkedList;
import java.util.List;

public class DependenciesTransformation extends DepthFirstAnalysisAdaptor
{
    private IRInfo info;

    public DependenciesTransformation(IRInfo info) { this.info = info; }

    @Override
    public void caseADefaultClassDeclIR(ADefaultClassDeclIR node) throws AnalysisException {
        handleClass(node);
    }

    @Override
    public void caseASystemClassDeclIR(ASystemClassDeclIR node) throws AnalysisException {
        handleClass(node);
    }

    @Override
    public void caseACpuClassDeclIR(ACpuClassDeclIR node) throws AnalysisException {
        handleClass(node);
    }

    private void handleClass(SClassDeclIR classDecl)
    {

        List<ClonableString> dependencies = new LinkedList<>();

        // TODO: Find usage of standard libraries
        // seq = std::list -> <list>
        dependencies.add(new ClonableString("<list>"));

        // TODO: Determine usage of Systemc
        // systemc |-> <systemc>
        // true |-> <systemc>
        dependencies.add(new ClonableString("<systemc>"));

        // TODO: Determine usage of internal objects
        for(SClassDeclIR classes : info.getClasses())
        {
            if(classes.getName() != classDecl.getName()) {
                dependencies.add(new ClonableString(String.format("\"%s.h\"", classes.getName())));
            }
        }

        classDecl.setDependencies(dependencies);
    }
}

