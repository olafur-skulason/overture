package org.overture.codegen.vdm2systemc;

import org.overture.codegen.ir.*;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.APeriodicStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ATemplateTypeIR;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscBusModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.declarations.AThreadThread;
import org.overture.codegen.vdm2systemc.extast.statements.ARemoteMethodCallStmIR;

import java.util.*;
import java.util.stream.Collectors;

public class SyscModuleGenerator {

    public SyscModuleGenerator()
    {

    }

    public ASyscModuleDeclIR generateModule(SClassDeclIR clazz, HashMap<String, ASyscModuleDeclIR> knownModules, boolean includeClock)
    {
        ASyscModuleDeclIR module = new ASyscModuleDeclIR();
        module.setName(clazz.getName());
        module.setDependencies(clazz.getDependencies());
        module.setSourceNode(clazz.getSourceNode());
        module.setMetaData(clazz.getMetaData());
        module.setTag(clazz.getTag());
        module.setOriginalClass(clazz);
        module.setInterfaces(clazz.getInterfaces().stream().map(AInterfaceDeclIR::getName).collect(Collectors.toList()));

        //noinspection unchecked
        module.setMethods((List<? extends AMethodDeclIR>) clazz.getMethods().clone());

        List<ARemoteMethodCallStmIR> outgoingMethodCalls = accumulateOutgoingRemotes(module);
        module.setOutgoingMsg(outgoingMethodCalls);

        List<AFieldDeclIR> fields = new LinkedList<>();
        for (AFieldDeclIR field : clazz.getFields()) {
            if(field.getType() instanceof AClassTypeIR) {
                if(knownModules.containsKey(((AClassTypeIR) field.getType()).getName())) // If is Module
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
            fields.add(field.clone());
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

            AFieldDeclIR clkFreq = new AFieldDeclIR();
            clkFreq.setName("clk_frequency");
            clkFreq.setVolatile(false);
            clkFreq.setFinal(false);
            clkFreq.setType(new AIntNumericBasicTypeIR());
            clkFreq.setAccess("public");
            fields.add(clkFreq);
        }

        module.setFields(fields);

        if (clazz.getThread() != null) {
            AThreadDeclIR originalThread = clazz.getThread();
            AThreadThread thread = new AThreadThread();

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

    private List<ARemoteMethodCallStmIR> accumulateOutgoingRemotes(ASyscModuleDeclIR module) {
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

            return Arrays.asList((ARemoteMethodCallStmIR)node.clone());
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

    public AFieldDeclIR generateClock(String parent, Double interval) {
        AClassTypeIR clk_type = new AClassTypeIR();
        clk_type.setName("sc_clock");

        ANewExpIR initial = new ANewExpIR();
        ATypeNameIR type = new ATypeNameIR();
        type.setName("sc_clock");
        initial.setName(type);
        AStringLiteralExpIR name = new AStringLiteralExpIR();
        name.setValue(parent + ".clk");
        AIntLiteralExpIR interval_ = new AIntLiteralExpIR();
        interval_.setValue(new Double(interval).longValue());
        initial.setArgs(Arrays.asList(name, interval_));

        AFieldDeclIR clk = new AFieldDeclIR();
        clk.setAccess("public");
        clk.setName("clk");
        clk.setType(clk_type.clone());
        clk.setInitial(initial);
        clk.setVolatile(false);

        return clk;
    }


    public AFieldDeclIR generateClockFrequency(Double frequency) {
        AIntNumericBasicTypeIR clkType = new AIntNumericBasicTypeIR();

        AIntLiteralExpIR initial = new AIntLiteralExpIR();
        initial.setValue(new Double(frequency).longValue());

        AFieldDeclIR clk = new AFieldDeclIR();
        clk.setAccess("public");
        clk.setName("clk_frequency");
        clk.setType(clkType.clone());
        clk.setInitial(initial);
        clk.setVolatile(false);

        return clk;
    }

    public ASyscBusModuleDeclIR generateBus(String bus, List<String> connectedElements, Long speed) {
        ASyscBusModuleDeclIR busModule = new ASyscBusModuleDeclIR();
        busModule.setName(bus);
        busModule.setConnectedElementCount(connectedElements.size());
        busModule.setConnectedElements(connectedElements);
        busModule.setSpeed(speed);
        return busModule;
    }
}
