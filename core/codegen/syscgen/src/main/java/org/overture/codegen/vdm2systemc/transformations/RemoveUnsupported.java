package org.overture.codegen.vdm2systemc.transformations;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.statements.AStartStmIR;
import org.overture.codegen.vdm2systemc.extast.analysis.DepthFirstAnalysisSystemCAdaptor;

public class RemoveUnsupported extends DepthFirstAnalysisSystemCAdaptor {

    @Override
    public void caseAStartStmIR(AStartStmIR node) throws AnalysisException {
        super.caseAStartStmIR(node);

        // Start statements are not supported.
        // All threads are assumed started at start.

        node.parent().removeChild(node);

    }
}
