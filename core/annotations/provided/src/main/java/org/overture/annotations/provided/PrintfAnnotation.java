/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of Overture
 *
 ******************************************************************************/

package org.overture.annotations.provided;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.annotations.INAnnotation;
import org.overture.interpreter.eval.ExpressionEvaluator;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.Value;
import org.overture.parser.annotations.ASTAnnotationAdapter;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.annotations.TCAnnotation;
import org.overture.typechecker.visitor.TypeCheckVisitor;

public class PrintfAnnotation extends ASTAnnotationAdapter implements TCAnnotation, INAnnotation
{
	public PrintfAnnotation()
	{
		super();
	}
	
	@Override
	public boolean typecheckArgs()
	{
		return true;	// Check args
	}
	
	/**
	 * Type checker...
	 */

	@Override
	public void tcBefore(PDefinition node, TypeCheckInfo question)
	{
		TypeChecker.report(6005, "@Printf only applies to expressions and statements", ast.getName().getLocation());
	}

	@Override
	public void tcBefore(PExp node, TypeCheckInfo question)
	{
		checkArgs(node, question);
	}

	@Override
	public void tcBefore(PStm node, TypeCheckInfo question)
	{
		checkArgs(node, question);
	}

	@Override
	public void tcBefore(AModuleModules node, TypeCheckInfo question)
	{
		TypeChecker.report(6005, "@Printf only applies to expressions and statements", ast.getName().getLocation());
	}

	@Override
	public void tcBefore(SClassDefinition node, TypeCheckInfo question)
	{
		TypeChecker.report(6005, "@Printf only applies to expressions and statements", ast.getName().getLocation());
	}

	public void checkArgs(INode node, TypeCheckInfo question)
	{
		if (ast.getArgs().isEmpty())
		{
			TypeChecker.report(6008, "@Printf must srart with a string argument", ast.getName().getLocation());
		}
		else
		{
			if (ast.getArgs().get(0) instanceof AStringLiteralExp)
			{
				for (PExp arg: ast.getArgs())
				{
					try
					{
						arg.apply(new TypeCheckVisitor(), question);
					}
					catch (AnalysisException e)
					{
						// Ignore?
					}
				}
			}
			else
			{
				TypeChecker.report(6008, "@Printf must start with a string argument", ast.getName().getLocation());
			}
		}
	}

	@Override
	public void tcAfter(PDefinition node, TypeCheckInfo question)
	{
		// Nothing
	}

	@Override
	public void tcAfter(PExp node, TypeCheckInfo question)
	{
		// Nothing
	}

	@Override
	public void tcAfter(PStm node, TypeCheckInfo question)
	{
		// Nothing
	}

	@Override
	public void tcAfter(AModuleModules node, TypeCheckInfo question)
	{
		// Nothing
	}

	@Override
	public void tcAfter(SClassDefinition node, TypeCheckInfo question)
	{
		// Nothing
	}
	
	/**
	 * Interpreter...
	 */

	@Override
	public void inBefore(PStm node, Context ctxt) throws AnalysisException
	{
		printArgs(ctxt);
	}

	@Override
	public void inAfter(PStm node, Value value, Context ctxt)
	{
		// Nothing
	}

	@Override
	public void inBefore(PExp node, Context ctxt) throws AnalysisException
	{
		printArgs(ctxt);
	}

	@Override
	public void inAfter(PExp node, Value value, Context ctxt)
	{
		// Nothing
	}

	private void printArgs(Context ctxt) throws AnalysisException
	{
		Object[] values = new Value[ast.getArgs().size() - 1];
		ExpressionEvaluator eval = new ExpressionEvaluator();
		
		for (int p=1; p < ast.getArgs().size(); p++)
		{
			values[p-1] = ast.getArgs().get(p).apply(eval, ctxt);
		}
		
		AStringLiteralExp fmt = (AStringLiteralExp)ast.getArgs().get(0);
		System.out.printf(fmt.getValue().getValue(), values);
	}
}
