package org.overture.codegen.vdm2systemc.transformations;

import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.statements.ACyclesStmIR;
import org.overture.codegen.ir.statements.ADurationStmIR;
import org.overture.codegen.vdm2systemc.extast.analysis.DepthFirstAnalysisSystemCAdaptor;

public class DurationToCyclesTransformation extends DepthFirstAnalysisSystemCAdaptor {

    @Override
    public void caseADurationStmIR(ADurationStmIR node) throws AnalysisException {
        ACyclesStmIR stm = new ACyclesStmIR();
        stm.setStm(node.getStm());
        ATimesNumericBinaryExpIR cycleCount = new ATimesNumericBinaryExpIR();

        ATimesNumericBinaryExpIR adjustUnit = new ATimesNumericBinaryExpIR();
        AIdentifierVarExpIR identifier = new AIdentifierVarExpIR();
        identifier.setName("clk_frequency");

        ARealLiteralExpIR unitShift = new ARealLiteralExpIR();
        unitShift.setValue(0.000000001);

        cycleCount.setLeft(node.getDuration());
        cycleCount.setRight(adjustUnit);
        adjustUnit.setLeft(identifier);
        adjustUnit.setRight(unitShift);

        stm.setCycles(cycleCount);

        node.parent().replaceChild(node, stm);
    }
}
