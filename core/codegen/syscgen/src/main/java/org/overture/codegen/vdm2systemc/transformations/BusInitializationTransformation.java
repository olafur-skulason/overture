package org.overture.codegen.vdm2systemc.transformations;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.AStringLiteralExpIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.vdm2systemc.extast.analysis.DepthFirstAnalysisSystemCAdaptor;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscModuleDeclIR;

import java.util.Arrays;

public class BusInitializationTransformation extends DepthFirstAnalysisSystemCAdaptor {

    @Override
    public void caseAFieldDeclIR(AFieldDeclIR node) throws AnalysisException {
        if(node.getType() instanceof AClassTypeIR)
        {
            if(((AClassTypeIR) (node.getType())).getName().equals("BUS")) {
                AClassTypeIR busType = new AClassTypeIR();
                busType.setName(node.getName());

                node.setType(busType);
                if(node.getInitial() != null)
                {
                    ANewExpIR initial = new ANewExpIR();
                    ATypeNameIR initialTypeName = new ATypeNameIR();
                    initialTypeName.setName(node.getName());
                    initial.setName(initialTypeName);
                    AStringLiteralExpIR instanceName = new AStringLiteralExpIR();
                    instanceName.setValue(((ASyscModuleDeclIR)node.parent()).getName() + "." + node.getName());
                    initial.setArgs(Arrays.asList(instanceName));
                    node.setInitial(initial);
                }

            }
        }
    }
}
