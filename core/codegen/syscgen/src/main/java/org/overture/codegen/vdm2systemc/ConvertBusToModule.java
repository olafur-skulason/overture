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
                ASyscModuleDeclIR m = convertBusModule((ASyscBusModuleDeclIR) s.getIrNode());
                conversions.add(new Pair<>(s,m));
            }
        }

        for(Pair<IRStatus<PIR>, ASyscModuleDeclIR> toReplace:conversions)
        {
            statuses.remove(toReplace.getFirst());
            statuses.add(new IRStatus<PIR>(toReplace.getFirst().getVdmNode(), toReplace.getFirst().getIrNodeName(), toReplace.getSecond(), toReplace.getFirst().getUnsupportedInIr()));
        }
    }

    private static ASyscModuleDeclIR convertBusModule(ASyscBusModuleDeclIR node) {
        ASyscModuleDeclIR module = new ASyscModuleDeclIR();
        module.setName(node.getName());

        List<AFieldDeclIR> fields = module.getFields();
        List<AMethodDeclIR> methods = module.getMethods();
        List<ASensitiveStmIR> sensitives = new LinkedList<>();

        for(SStmIR c : node.getChannels())
        {
            if(c instanceof ARemoteMethodCallStmIR)
            {
                ARemoteMethodCallStmIR call = (ARemoteMethodCallStmIR) c;
                boolean hasArgs = call.getArgs().size() > 0;
                boolean hasReturn = !(call.getType() instanceof AVoidTypeIR);

                if(hasArgs && hasReturn) // Operation Op: int ==> int
                {
                    // Field:
                    // sc_out<bool> {call.getCallee()}_{call.getCalled()}_{call.getName()}_out
                    // sc_in<int> {call.getCallee()}_{call.getCalled()}_{call.getName()}_in
                    // Method: value ==> ()
                    // out.write(b);
                    // WAIT<in>
                    // return read(in);
                    String fieldName = String.format("%s_%s_%s", call.getCallee(), call.getCalled(), call.getName());
                    deriveRemoteCall(fields, methods, sensitives, fieldName + "_in", "_in_t");
                    deriveRemoteCall(fields, methods, sensitives, fieldName + "_out", fieldName + "_out_t");

                }
                else if(hasArgs && !hasReturn) //Setter Op: int ==> ()
                {
                    String fieldName = String.format("%s_%s_%s", call.getCallee(), call.getCalled(), call.getName());
                    deriveRemoteCall(fields, methods, sensitives, fieldName, fieldName + "_in_t");
                }
                else if(!hasArgs && hasReturn) // Getter Op: () ==> int
                {
                    // Field:
                    // sc_out<bool> {call.getCallee()}_{call.getCalled()}_{call.getName()}_out
                    // sc_in<int> {call.getCallee()}_{call.getCalled()}_{call.getName()}_in
                    // Method:
                    // static bool b = false;
                    // b = !b;
                    // out.write(b);
                    // WAIT<in>
                    // return read(in);
                    String fieldName = String.format("%s_%s_%s", call.getCallee(), call.getCalled(), call.getName());
                    deriveRemoteCall(fields, methods, sensitives, fieldName + "_in", "bool");
                    deriveRemoteCall(fields, methods, sensitives, fieldName + "_out", fieldName + "_out_t");
                }
                else // HUP. () ==> ()
                {
                    // Field:
                    // sc_out<bool> {call.getCallee()}_{call.getCalled()}_{call.getName()}_out
                    // Method: no args.
                    // Body:
                    // static bool b = false;
                    // b = !b;
                    // out.write(b);
                    String fieldName = String.format("%s_%s_%s", call.getCallee(), call.getCalled(), call.getName());
                    deriveRemoteCall(fields, methods, sensitives, fieldName + "_in", "bool");
                }
            }
        }

        addSensitiveToCtor(module, sensitives);

        return module;
    }

    private static void deriveRemoteCall(List<AFieldDeclIR> fields, List<AMethodDeclIR> methods, List<ASensitiveStmIR> sensitives, String fieldName, String type) {
        AFieldDeclIR inField = new AFieldDeclIR();
        AClassTypeIR inFieldType = new AClassTypeIR();
        inFieldType.setName(String.format("sc_in<%s>", type));
        inField.setType(inFieldType);
        inField.setInitial(null);
        inField.setAccess("public");
        inField.setName(fieldName + "_in");

        AFieldDeclIR outField = inField.clone();
        outField.setName(fieldName + "_out");

        fields.addAll(Arrays.asList(inField, outField));

        AMethodDeclIR method = new AMethodDeclIR();
        method.setName(fieldName);
        method.setAccess("private");
        AMethodTypeIR methodType = new AMethodTypeIR();
        AVoidTypeIR methodTypeResult = new AVoidTypeIR();
        methodType.setResult(methodTypeResult);
        method.setMethodType(methodType);
        method.setFormalParams(new LinkedList<>());
        ABlockStmIR methodBody = new ABlockStmIR();

        AApplyExpIR readExp = new AApplyExpIR();
        AIdentifierVarExpIR inputName = new AIdentifierVarExpIR();
        inputName.setName(fieldName + "_in.read");
        readExp.setArgs(new LinkedList<>());
        AClassTypeIR readType = new AClassTypeIR();
        readType.setName(type);
        readExp.setType(readType);
        readExp.setRoot(inputName);

        AIdentifierVarExpIR inputArgs = new AIdentifierVarExpIR();
        inputArgs.setType(readType.clone());
        inputArgs.setIsLocal(true);
        inputArgs.setName("arguments");

        AVarDeclIR getArguments = new AVarDeclIR();
        getArguments.setType(readType.clone());
        getArguments.setExp(readExp);
        AIdentifierPatternIR getArgumentsPattern = new AIdentifierPatternIR();
        getArgumentsPattern.setName("arguments");
        getArguments.setPattern(getArgumentsPattern);

        APlainCallStmIR writeForward = new APlainCallStmIR();
        AClassTypeIR writeForwardClassType = new AClassTypeIR();
        writeForwardClassType.setName(fieldName + "_out");
        writeForward.setClassType(writeForwardClassType);
        writeForward.setName("write");
        writeForward.setArgs(Arrays.asList(inputArgs));
        writeForward.setType(new AVoidTypeIR());

        methodBody.getStatements().add(writeForward);
        method.setBody(methodBody);
        methodBody.setLocalDefs(Arrays.asList(getArguments));

        methods.add(method);

        ASensitiveStmIR sensitive = new ASensitiveStmIR();
        sensitive.setInput(fieldName + "_in");
        sensitive.setMethod(fieldName);

        sensitives.add(sensitive);
    }

    private static void addSensitiveToCtor(ASyscModuleDeclIR module, List<ASensitiveStmIR> sensitives) {

        ABlockStmIR ctorBody = new ABlockStmIR();

        ctorBody.setStatements(sensitives);


        AMethodDeclIR ctor = new AMethodDeclIR();
        ctor.setIsConstructor(true);
        ctor.setAccess("public");
        ctor.setName(module.getName());
        ctor.setBody(ctorBody);

        module.getMethods().add(ctor);
    }


}
