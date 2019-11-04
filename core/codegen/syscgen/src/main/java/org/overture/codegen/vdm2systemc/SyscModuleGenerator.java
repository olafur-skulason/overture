package org.overture.codegen.vdm2systemc;

import org.overture.codegen.ir.*;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.APeriodicStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscBusModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.declarations.AThreadThread;
import org.overture.codegen.vdm2systemc.extast.statements.ARemoteMethodCallStmIR;

import java.util.*;

public class SyscModuleGenerator {

    public SyscModuleGenerator()
    {

    }

    public ASyscModuleDeclIR generateModule(SClassDeclIR clazz, List<String> knownModules, boolean includeClock)
    {
        ASyscModuleDeclIR module = new ASyscModuleDeclIR();
        module.setName(clazz.getName());
        module.setDependencies(clazz.getDependencies());
        module.setSourceNode(clazz.getSourceNode());
        module.setMetaData(clazz.getMetaData());
        module.setTag(clazz.getTag());
        module.setOriginalClass(clazz);

        module.setMethods(clazz.getMethods());

        List<ARemoteMethodCallStmIR> remoteMethodCalls = accumulateRemotes(module);
        module.setOutgoingMsg(remoteMethodCalls);

        List<AFieldDeclIR> fields = new LinkedList<>();
        for (AFieldDeclIR field : clazz.getFields()) {
            if(field.getType() instanceof AClassTypeIR) {
                if(knownModules.contains(((AClassTypeIR) field.getType()).getName())) // If is Module
                {
                    ANewExpIR initial = new ANewExpIR();
                    ATypeNameIR initialType = new ATypeNameIR();
                    initialType.setName(((AClassTypeIR) field.getType()).getName());
                    initial.setName(initialType);
                    AStringLiteralExpIR name = new AStringLiteralExpIR();
                    name.setValue(clazz.getName() + "." + field.getName());
                    initial.getArgs().addFirst(name);
                    field.setInitial(initial);
                }
            }
            fields.add(field);
        }

        if(includeClock && !(module.getName().startsWith("cpu") || module.getName().startsWith("bus"))) {
            AFieldDeclIR clk = new AFieldDeclIR();
            clk.setName("clk");
            clk.setVolatile(false);
            clk.setFinal(false);
            AClassTypeIR clkType = new AClassTypeIR();
            clkType.setName("sc_in<bool>");
            clk.setType(clkType);
            clk.setAccess("public");
            fields.add(clk);
        }

        module.setFields(fields);

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

    private List<ARemoteMethodCallStmIR> accumulateRemotes(ASyscModuleDeclIR module) {
        List<ARemoteMethodCallStmIR> remoteMethodCalls = new LinkedList<>();
        for(AMethodDeclIR method: module.getMethods())
        {
            SStmIR body = method.getBody();
            List<ARemoteMethodCallStmIR> methodCalls = accumulateRemotesFromMethods(body);
            for(ARemoteMethodCallStmIR methodCall: methodCalls)
            {
                methodCall.setCallee(module.getName());
            }
            remoteMethodCalls.addAll(methodCalls);
        }
        return remoteMethodCalls;
    }

    private List<ARemoteMethodCallStmIR> accumulateRemotesFromMethods(SStmIR node)
    {
        if(node instanceof ARemoteMethodCallStmIR) {

            return Arrays.asList((ARemoteMethodCallStmIR)node);
        }
        else if(node instanceof ABlockStmIR)
        {
            ABlockStmIR block = (ABlockStmIR)node;
            List<ARemoteMethodCallStmIR> result = new LinkedList<>();
            for(SStmIR inner: block.getStatements())
            {
                result.addAll(accumulateRemotesFromMethods(inner));
            }
            return result;
        }
        return new LinkedList<>();
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

    public ASyscBusModuleDeclIR generateBus(String bus, List<ARemoteMethodCallStmIR> remoteMethodCalls, List<IRStatus<PIR>> statuses) {
        ASyscBusModuleDeclIR busModule = new ASyscBusModuleDeclIR();
        busModule.setName(bus);
        busModule.setChannels(remoteMethodCalls);

        return busModule;
    }
}
