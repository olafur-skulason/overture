
package org.overture.codegen.vdm2systemc;

import java.util.List;
import java.util.LinkedList;
import java.io.StringWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import org.overture.ast.expressions.AUndefinedExp;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.statements.AForLoopStmIR;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.assistant.LocationAssistantIR;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscModuleDeclIR;

public class SystemcFormat
{
	protected Logger log = Logger.getLogger(this.getClass().getName());

	protected MergeVisitor mergeVisitor;
	protected org.overture.codegen.vdm2systemc.SystemcValueSemantics valueSemantics;
	protected FuncValAssistant funcValAssist;

	protected IRInfo info;

	public SystemcFormat(String implTemplateRoot, String baseTemplateRoot, IRInfo info)
	{
		valueSemantics = new SystemcValueSemantics(this);
		TemplateCallable[] templateCallables = TemplateCallableManager.constructTemplateCallables(this, valueSemantics);
		mergeVisitor = new MergeVisitor(new SystemcTemplateManager(baseTemplateRoot, implTemplateRoot), templateCallables);
		funcValAssist = null;
		this.info = info;
	}

	public MergeVisitor getMergeVisitor()
	{
		return mergeVisitor;
	}

	public String format(INode node) throws AnalysisException
	{
		return format(node, false);
	}

	public void setFunctionValueAssistant(FuncValAssistant functionValueAssistant)
	{
		funcValAssist = functionValueAssistant;
	}

	public String format(AMethodTypeIR methodType) throws AnalysisException
	{
		final String NONE = "";

		if(funcValAssist == null)
		{
			return NONE;
		}
		
		AInterfaceDeclIR methodTypeInterface = funcValAssist.findInterface(methodType);

		AInterfaceTypeIR methodClass = new AInterfaceTypeIR();
		methodClass.setName(methodTypeInterface.getName());

		LinkedList<STypeIR> params = methodType.getParams();
		
		for(STypeIR param : params)
		{
			methodClass.getTypes().add(param.clone());
		}

		methodClass.getTypes().add(methodType.getResult().clone());

		if(methodType.parent() != null)
		{
			methodType.parent().replaceChild(methodType, methodClass);
		}

		return methodClass != null ? format(methodClass) : NONE;
	}

	public String escapeChar(char c)
	{
		return GeneralUtils.isEscapeSequence(c) ? StringEscapeUtils.escapeJavaScript(c + "") : c + "";
	}

	public String formatName(INode node) throws AnalysisException
	{
		if(node instanceof ANewExpIR)
		{
			ANewExpIR newExp = (ANewExpIR)node;

			return formatTypeName(node, newExp.getName());
		}
		else if( node instanceof ARecordTypeIR)
		{
			ARecordTypeIR record = (ARecordTypeIR)node;

			return formatTypeName(node, record.getName());
		}

		throw new AnalysisException("Unexpected node in formatName: " + node.getClass().getName());
	}

	public String formatTypeName(INode node, ATypeNameIR typeName)
	{
		// This is where namespace and encapsulated class prefixes should be determined.
		return typeName.getName();
	}

	public String formatArgs(List<? extends SExpIR> exps) throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if(exps.size() <= 0)
		{
			return "";
		}

		SExpIR firstExp = exps.get(0);
		writer.append(format(firstExp));

		for(int i = 1; i < exps.size(); i++)
		{
			writer.append(", " + format(exps.get(i)));
		}

		return writer.toString();
	}

	public String formatOperationBody(SStmIR body) throws AnalysisException
	{
		String NEWLINE = "\n";
		if(body == null)
		{
			return ";";
		}

		StringWriter generatedBody = new StringWriter();

		generatedBody.append("{" + NEWLINE);
		generatedBody.append(handleOpBody(body));
		generatedBody.append(NEWLINE+"}");

		return generatedBody.toString();
	}

	private String handleOpBody(SStmIR body) throws AnalysisException
	{
		AMethodDeclIR method = body.getAncestor(AMethodDeclIR.class);

		if(method == null)
		{
			log.error("Could not find enclosing method when formatting operation body. Got: " + body);
		}
		
		return format(body);
	}


	public String format(List<AFormalParamLocalParamIR> params) throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if(params.size() <= 0)
		{
			return "";
		}

		for (int i = 0; i < params.size(); i++)
		{
			if(i != 0)
			{
				writer.append(", ");
			}
			AFormalParamLocalParamIR param = params.get(i);
			writer.append(format(param));
		}
		return writer.toString();
	}

	public String format(SClassDeclIR class_decl)
	{
		return "";
	}

	public String format(SExpIR exp, boolean leftChild) throws AnalysisException
	{
		String formattedExp = format(exp);

		INode parent = exp.parent();

		if(!(parent instanceof SExpIR))
		{
			return formattedExp;
		}

		boolean isolate = SystemcPrecedence.mustIsolate((SExpIR)parent, exp, leftChild);

		return isolate ? "(" + formattedExp.trim() + ")" : formattedExp;
	}

	/*public String format(PIRBase baseExp)
	{
		return baseExp.getClass().getName();
	}*/

	public String getInclude(ClonableString c)
	{
		// TODO: Evaluate if c is a local include or external. 
		boolean localInclude = true;

		return (localInclude ? "\"" : "<") + c.value + (localInclude ? "\"" : ">");
	}

	public boolean isSystemcCtor(ADefaultClassDeclIR classDefinition, AMethodDeclIR method)
	{
		// This needs to be adjusted for remote method calls.
		return classDefinition != null && method.getIsConstructor();
	}

	public static boolean isScoped(ABlockStmIR block)
	{
		return block != null && block.getScoped() != null && block.getScoped();
	}

	public boolean isLoopVar(AVarDeclIR localVar)
	{
		return localVar.parent() instanceof AForLoopStmIR;
	}

	public String formatVdmSource(PIR irNode)
	{
        if (getSystemcSettings().printVdmLocations() && irNode != null)
        {
            org.overture.ast.node.INode vdmNode = LocationAssistantIR.getVdmNode(irNode);

            if (vdmNode != null)
            {
                ILexLocation loc = info.getLocationAssistant().findLocation(vdmNode);

                if (loc != null)
                {
                    return String.format("/* %s %d:%d */\n", loc.getFile().getName(), loc.getStartLine(), loc.getStartPos());
                }
            }

        }

        return "";

	}

	public SystemcSettings getSystemcSettings()
	{
		return valueSemantics.getSystemcSettings();
	}

	private String format(INode node, boolean ignoreContext) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(mergeVisitor, writer);

		return writer.toString();
	}

	public String formatUnary(SExpIR exp) throws AnalysisException
	{
		return format(exp, false);
	}

	public String formatEqualsBinaryExp(AEqualsBinaryExpIR node) throws AnalysisException
	{
		STypeIR leftNodeType = node.getLeft().getType();

		if(leftNodeType instanceof SSeqTypeIR
			|| leftNodeType instanceof SSetTypeIR
			|| leftNodeType instanceof SMapTypeIR)
		{
			return "";
		}
		else
		{
			return String.format("%s == %s", format(node.getLeft()), format(node.getRight()));
		}
	}

	public String formatInitialExp(SExpIR exp) throws AnalysisException
	{
		return exp == null || exp instanceof AUndefinedExp ? "" : " = " + format(exp);
	}

	public String formatIdentifierVar(AIdentifierVarExpIR var)
	{
		String varName = var.getName();
		return varName;
	}

	public static String getString(ClonableString c) { return c.value; }

	public static String getName(ASyscModuleDeclIR m) {return m.getName(); }

	public static boolean isNull(INode node)
	{
		return node == null;
	}
}
