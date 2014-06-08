package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.expressions.SNumericBinaryExpCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.transform.BaseTransformationAssistant;

public class UnionTypeTransformation extends DepthFirstAnalysisAdaptor
{
	private BaseTransformationAssistant baseAssistant;
	
	public UnionTypeTransformation(BaseTransformationAssistant baseAssistant)
	{
		this.baseAssistant = baseAssistant;
	}
	
	private SExpCG wrap(SExpCG exp, STypeCG castedType)
	{
		if(exp.getType() instanceof AUnionTypeCG)
		{
			ACastUnaryExpCG casted = new ACastUnaryExpCG();
			casted.setType(castedType.clone());
			casted.setExp(exp.clone());
			
			return casted;
		}
		
		return exp;
	}
	
	@Override
	public void defaultInSNumericBinaryExpCG(SNumericBinaryExpCG node)
			throws AnalysisException
	{
		STypeCG expectedType = node.getType();
		
		SExpCG newLeft = wrap(node.getLeft(), expectedType);
		SExpCG newRight = wrap(node.getRight(), expectedType);

		if(newLeft != node.getLeft())
		{
			baseAssistant.replaceNodeWith(node.getLeft(), newLeft);
			newLeft.apply(this);
		}
		
		if(newRight != node.getRight())
		{
			baseAssistant.replaceNodeWith(node.getRight(), newRight);
			newRight.apply(this);
		}
			
	}
}