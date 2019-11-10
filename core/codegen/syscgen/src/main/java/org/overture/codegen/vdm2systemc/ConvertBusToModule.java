package org.overture.codegen.vdm2systemc;

import org.jgrapht.alg.util.Pair;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.types.AVoidType;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.*;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.AObjectTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.vdm2systemc.extast.analysis.DepthFirstAnalysisSystemCAdaptor;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscBusModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.statements.ARemoteMethodCallStmIR;
import org.overture.codegen.vdm2systemc.extast.statements.ASensitiveStmIR;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ConvertBusToModule {

    private List<IRStatus<PIR>> statuses;

    public ConvertBusToModule(IRInfo info)
    {
        //this.statuses = statuses;
    }

    public static void convertBusModules(List<IRStatus<PIR>> statuses)
    {
        List<Pair<IRStatus<PIR>, ASyscModuleDeclIR>> conversions = new LinkedList<>();
        for(IRStatus<PIR> s: statuses)
        {
            if(s.getIrNode() instanceof ASyscBusModuleDeclIR)
            {
                //ASyscModuleDeclIR m = convertBusModule((ASyscBusModuleDeclIR) s.getIrNode());
                //conversions.add(new Pair<>(s,m));
            }
        }

        for(Pair<IRStatus<PIR>, ASyscModuleDeclIR> toReplace:conversions)
        {
            statuses.remove(toReplace.getFirst());
            statuses.add(new IRStatus<PIR>(toReplace.getFirst().getVdmNode(), toReplace.getFirst().getIrNodeName(), toReplace.getSecond(), toReplace.getFirst().getUnsupportedInIr()));
        }
    }
}
