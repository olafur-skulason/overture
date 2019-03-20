/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.annotations.examples;

import java.util.List;
import java.util.Vector;

import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.VDMToken;
import org.overture.parser.annotations.ASTAnnotationAdapter;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;

/**
 * A test of an annotation with an overridden parse function.
 */
public class GhostAnnotation extends ASTAnnotationAdapter
{
	public GhostAnnotation()
	{
		super();
	}

	
	public boolean typecheckArgs()
	{
		return false;		// Ghosts must be ner variables and so non-existent anyway
	}

	/**
	 * Override the default parse, and look for @Ghost <name> = <exp>;
	 */
	@Override
	public List<PExp> parse(LexTokenReader ltr) throws LexException, ParserException
	{
		ltr.nextToken();
		List<PExp> args = new Vector<PExp>();
		ExpressionReader er = new ExpressionReader(ltr);
		PExp exp = er.readExpression();
		
		if (exp instanceof AEqualsBinaryExp)		// Should parse as an equals expression
		{
			AEqualsBinaryExp eqexp = (AEqualsBinaryExp)exp;
			
			if (eqexp.getLeft() instanceof AVariableExp)
			{
				args.add(eqexp.getLeft());
				args.add(eqexp.getRight());
			}
			else
			{
				parseException("expecting <name> = <exp>;", eqexp.getLocation());
			}
		}
		else
		{
			parseException("expecting <name> = <exp>;", exp.getLocation());
		}
		
		if (ltr.getLast().isNot(VDMToken.SEMICOLON))
		{
			parseException("missing ;", ltr.getLast().getLocation());
		}
		
		return args;
	}
}
