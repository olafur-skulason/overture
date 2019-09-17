package org.overture.codegen.vdm2systemc;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.IROperatorLookup;
import org.overture.codegen.ir.IROperatorInfo;
import org.overture.codegen.ir.expressions.ADivideNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ASubtractNumericBinaryExpIR;

public class SystemcPrecedence
{
	public static boolean mustIsolate(SExpIR parentExp, SExpIR exp, boolean leftChild)
	{
		IROperatorLookup opLookup = new IROperatorLookup();
		IROperatorInfo parentOpInfo = opLookup.find(parentExp.getClass());

		if(parentOpInfo == null)
		{
			return false;
		}

		IROperatorInfo expOpInfo = opLookup.find(exp.getClass());

		if(expOpInfo == null)
		{
			return false;
		}

        // Case 1: Protect against cases like "1 / (2*3*4)"
        // Don't care about left children, i.e. "(2*3*4)/1 = 2*3*4/1"

        // Similar for subtract: "1 - (1+2+3)" and "1+2+3-3"

        // We don't need to consider 'mod' and 'rem' operators since these are constructed
        // using other operators and isolated if needed using the isolation expression
		boolean case1 = !leftChild
				&& (parentExp instanceof ADivideNumericBinaryExpIR
					|| parentExp instanceof ASubtractNumericBinaryExpIR)
				&& (parentOpInfo.getPrecedence() >= expOpInfo.getPrecedence());

		if(case1)
		{
			return true;
		}

        // Case 2: Protect against case like 1 / (1+2+3)
        boolean case2 = parentOpInfo.getPrecedence() > expOpInfo.getPrecedence();

        return case2;
	}

}
