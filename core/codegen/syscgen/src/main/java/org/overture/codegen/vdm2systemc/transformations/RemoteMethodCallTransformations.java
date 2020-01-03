package org.overture.codegen.vdm2systemc.transformations;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.ACallObjectStmIR;
import org.overture.codegen.ir.statements.AIdentifierObjectDesignatorIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.vdm2systemc.extast.analysis.DepthFirstAnalysisSystemCAdaptor;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.statements.ARemoteMethodCallStmIR;

public class RemoteMethodCallTransformations extends DepthFirstAnalysisSystemCAdaptor {

    private String _rootSystemName;

    public RemoteMethodCallTransformations(String rootSystemName)
    {
        _rootSystemName = rootSystemName;
    }

    @Override
    public void caseACallObjectStmIR(ACallObjectStmIR node) throws AnalysisException {
        ACallObjectStmIR call = node;
        if(call.getDesignator() instanceof AIdentifierObjectDesignatorIR) {
            AIdentifierObjectDesignatorIR objectDesignator = (AIdentifierObjectDesignatorIR) call.getDesignator();
            if(objectDesignator.getExp() instanceof AExplicitVarExpIR) {
                AExplicitVarExpIR explicitVarExp = (AExplicitVarExpIR) objectDesignator.getExp();
                if(explicitVarExp.getClassType() instanceof AClassTypeIR) {
                    AClassTypeIR classTypeIR = (AClassTypeIR) explicitVarExp.getClassType();
                    if(classTypeIR.getName().equals(_rootSystemName)) {
                        ARemoteMethodCallStmIR result = new ARemoteMethodCallStmIR();
                        result.setCalled(call.clone());
                        result.setCallee(call.getAncestor(ADefaultClassDeclIR.class).getName());
                        node.parent().replaceChild(node, result);
                    }
                }
            }
        }
    }
}
