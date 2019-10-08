package org.overture.codegen.vdm2systemc;

import org.overture.ast.types.AClassType;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.AStringLiteralExpIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.statements.APeriodicStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.declarations.AThreadThread;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SyscModuleGenerator {

    public SyscModuleGenerator()
    {

    }

    public ASyscModuleDeclIR generateModule(SClassDeclIR clazz, List<String> knownModules)
    {
        ASyscModuleDeclIR module = new ASyscModuleDeclIR();
        module.setName(clazz.getName());
        module.setDependencies(clazz.getDependencies());
        module.setMethods(clazz.getMethods());
        module.setFields(clazz.getFields());
        module.setSourceNode(clazz.getSourceNode());
        module.setMetaData(clazz.getMetaData());
        module.setTag(clazz.getTag());
        module.setOriginalClass(clazz);

        for (AMethodDeclIR method : clazz.getMethods()) {
            method.parent(module);
        }

        for (AFieldDeclIR field : clazz.getFields()) {
            if(field.getType() instanceof AClassTypeIR) {
                if(knownModules.contains(((AClassTypeIR) field.getType()).getName())) // If is Module
                {
                    ANewExpIR initial = (ANewExpIR) field.getInitial();
                    if(initial != null) {
                        AStringLiteralExpIR name = new AStringLiteralExpIR();
                        name.setValue(clazz.getName() + "." + field.getName());
                        initial.getArgs().addFirst(name);
                    }
                }
            }
            field.parent(module);
        }

        if (clazz.getThread() != null) {
            AThreadThread thread = new AThreadThread();
            AThreadDeclIR originalThread = clazz.getThread();

            if (originalThread.getStm() instanceof APeriodicStmIR) {
                thread.setMethod(((APeriodicStmIR) originalThread.getStm()).getOpname());
                thread.setPeriod(((APeriodicStmIR) originalThread.getStm()).getArgs().get(0));
                thread.setJitter(((APeriodicStmIR) originalThread.getStm()).getArgs().get(0));
                thread.setDelay(((APeriodicStmIR) originalThread.getStm()).getArgs().get(0));
                thread.setOffset(((APeriodicStmIR) originalThread.getStm()).getArgs().get(0));
            }

            module.setCThreads(thread);
        }
        return module;
    }

    public AFieldDeclIR generateClock(String parent, int interval) {
        AClassTypeIR clk_type = new AClassTypeIR();
        clk_type.setName("sc_clock");

        ANewExpIR initial = new ANewExpIR();
        ATypeNameIR type = new ATypeNameIR();
        type.setName("sc_clock");
        initial.setName(type);
        AStringLiteralExpIR name = new AStringLiteralExpIR();
        name.setValue(parent + ".clk");
        AIntLiteralExpIR interval_ = new AIntLiteralExpIR();
        interval_.setValue((long) interval);
        initial.setArgs(Arrays.asList(name, interval_));

        AFieldDeclIR clk = new AFieldDeclIR();
        clk.setAccess("public");
        clk.setName("clk");
        clk.setType(clk_type.clone());
        clk.setInitial(initial);
        clk.setVolatile(false);

        return clk;
    }
}
