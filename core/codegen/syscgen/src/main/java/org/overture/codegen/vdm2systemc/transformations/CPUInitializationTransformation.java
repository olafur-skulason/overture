package org.overture.codegen.vdm2systemc.transformations;

import org.overture.ast.types.AClassType;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.statements.AAssignmentStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.vdm2systemc.extast.analysis.DepthFirstAnalysisSystemCAdaptor;

public class CPUInitializationTransformation extends DepthFirstAnalysisSystemCAdaptor {

    @Override
    public void caseAAssignmentStmIR(AAssignmentStmIR node) throws AnalysisException {
        super.caseAAssignmentStmIR(node);
    }

    @Override
    public void caseAFieldDeclIR(AFieldDeclIR node) throws AnalysisException {
        if(node.getType() instanceof AClassTypeIR)
        {
            if(((AClassTypeIR)(node.getType())).getName() == "CPU")
            {
                AClassTypeIR cpuType = new AClassTypeIR();
                cpuType.setName("sc_"+node.getName());

                node.setType(cpuType);
                if(node.getInitial() != null)
                {
                    ANewExpIR initial = new ANewExpIR();
                    ATypeNameIR initialTypeName = new ATypeNameIR();
                    initialTypeName.setName("sc_" + node.getName());
                    initial.setName(initialTypeName);
                    node.setInitial(initial);
                }
            }
        }
    }

    @Override
    public void caseANewExpIR(ANewExpIR node) throws AnalysisException {
        if(node.getType() instanceof AClassTypeIR)
        {
            AClassTypeIR nodeType = (AClassTypeIR) node.getType();
            if(nodeType.getName() == "CPU")
            {
                if(node.parent() instanceof AFieldDeclIR)
                {

                    return;
                }
            }
        }
        super.caseANewExpIR(node);
    }
}
