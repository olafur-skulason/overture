package org.overture.codegen.vdm2systemc;

import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.types.AClassType;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.*;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.runtime.traces.Pair;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscBusModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.statements.ARemoteMethodCallStmIR;

import java.util.*;

public class ArchitecturalAnalysis {
    private LinkedList<AFieldDeclIR> systemDeployedObjects;
    private HashMap<String, Set<SVarExpIR>> processingElements;
    private LinkedList<String> systemcModuleNames;
    private ASystemClassDeclIR root;
    private HashMap<String, Pair<List<AIdentifierVarExpIR>, Long>> connectionElements;
    private List<Pair<String, String>> activeObjects;
    // Things to do in this class
    // * Convert System to SystemC Module
    // * Convert CPUs to SystemC Module
    // * Initialize all CPUs and virtual CPU in System Module
    // * Add deployed objects to CPU
    // * Convert deployed objects to SystemC Modules
    // * Initialize deployed objects in CPU Modules
    // * Generate Bus Module shells
    // * Determine method calls.
    // * Review OOOS bus implementations, consider replicating.

    public ArchitecturalAnalysis()
    {
        systemDeployedObjects = new LinkedList<>();
        processingElements = new HashMap<>();
        systemcModuleNames = new LinkedList<>();
        connectionElements = new HashMap<>();
        activeObjects = new LinkedList<>();
        root = null;
    }

    public void FindRoot(List<IRStatus<PIR>> statuses)
    {
        // 1. Build deployment tree.
        // 1.1 Find root.
        root = null;
        for(IRStatus<PIR> p : statuses)
        {
            if(p.getIrNode() instanceof ASystemClassDeclIR)
            {
                root = (ASystemClassDeclIR) p.getIrNode();
            }
        }
    }


    public void AnalyseArchitecture(List<IRStatus<PIR>> statuses)
    {
        if(root != null){
            systemcModuleNames.add(root.getName());
            for(AFieldDeclIR field : root.getFields()) // For each field in system
            {
                if(field.getType() instanceof AClassTypeIR) // If object
                {
                    AClassTypeIR type = (AClassTypeIR) field.getType();

                    if(type.getName() == "CPU")
                    {
                        processingElements.put("cpu_" + field.getName(), new HashSet<>());
                        systemcModuleNames.add("cpu_" + field.getName());
                        type.setName("cpu_" + field.getName());
                        field.parent().replaceChild(field, generateModuleField(field, "cpu_"));
                    }
                    else if(type.getName() == "BUS")
                    {
                        ANewExpIR initial = (ANewExpIR) field.getInitial();
                        List<AIdentifierVarExpIR> connectedCPUS = new LinkedList<>();
                        for(SExpIR member : ((AEnumSetExpIR)initial.getArgs().get(2)).getMembers())
                        {
                            AClassTypeIR cpuType = new AClassTypeIR();
                            cpuType.setName("cpu_" + ((AIdentifierVarExpIR)member).getName());
                            member.setType(cpuType);
                            connectedCPUS.add((AIdentifierVarExpIR) member);
                        }
                        connectionElements.put(field.getName(), new Pair<List<AIdentifierVarExpIR>, Long>(connectedCPUS, ((AIntLiteralExpIR)initial.getArgs().get(1)).getValue()));
                        type.setName("bus_" + field.getName());
                        field.parent().replaceChild(field, generateModuleField(field, "bus_"));
                    }
                    else
                    {
                        systemDeployedObjects.add(field);
                        systemcModuleNames.add(type.getName());
                        activeObjects.add(new Pair<>(type.getName(), field.getName()));
                    }
                }
            }

            for(AMethodDeclIR method: root.getMethods())
            {
                if(method.getIsConstructor())
                {
                    if(method.getBody() instanceof ABlockStmIR)
                    {
                        ABlockStmIR body = (ABlockStmIR) method.getBody();

                        for(SStmIR stm :body.getStatements())
                        {
                            if(stm instanceof ACallObjectStmIR)
                            {
                                ACallObjectStmIR callObjectStm = (ACallObjectStmIR) stm;
                                if(callObjectStm.getFieldName().equals("deploy")) {
                                    Set<SVarExpIR> depSet = processingElements.get("cpu_" + (callObjectStm.getDesignator().toString()));

                                    depSet.add((SVarExpIR) callObjectStm.getArgs().getFirst());
                                    processingElements.put("cpu_" + (callObjectStm.getDesignator().toString()), depSet);
                                }
                            }
                        }
                    }
                }
            }

            generateTopModule(statuses);
            generateCPUModules(statuses);
            generateDeployedModules(statuses);
            generateConnectionModules(statuses);
        }
    }

    private AFieldDeclIR generateModuleField(AFieldDeclIR original, String prefix) {
        AFieldDeclIR cpuField = new AFieldDeclIR();
        AClassTypeIR cpuType = new AClassTypeIR();
        cpuType.setName(prefix + original.getName());
        cpuField.setType(cpuType);
        cpuField.setName(original.getName());
        cpuField.setAccess(original.getAccess());
        cpuField.setVolatile(false);

        if(original.getInitial() != null) {
            ANewExpIR cpuInitial = new ANewExpIR();
            AIdentifierVarExpIR cpuInitialCtor = new AIdentifierVarExpIR();
            ATypeNameIR cpuInitialCtorName = new ATypeNameIR();
            cpuInitialCtorName.setName(prefix + original.getName());
            cpuInitial.setName(cpuInitialCtorName);
            AStringLiteralExpIR moduleName = new AStringLiteralExpIR();
            moduleName.setValue(original.getName());
            cpuInitial.getArgs().add(moduleName);
            cpuField.setInitial(cpuInitial);
        }

        return cpuField;
    }

    public String getRootName() {
        if(root != null)
            return root.getName();
        else
            return "";
    }

    private void generateConnectionModules(List<IRStatus<PIR>> statuses) {
        SyscModuleGenerator generator = new SyscModuleGenerator();
        for(String bus: connectionElements.keySet())
        {
            Pair<List<AIdentifierVarExpIR>, Long> exp = connectionElements.get(bus);
            List<AIdentifierVarExpIR> _elems = exp.getFirst();
            List<ARemoteMethodCallStmIR> remoteMethodCalls = new LinkedList<>();
            for(AIdentifierVarExpIR from: _elems)
            {
                for(AIdentifierVarExpIR to: _elems)
                {
                    if (from != to)
                    {
                        AIdentifierVarExpIR fromType = from;
                        AIdentifierVarExpIR toType = to;
                        Set<SVarExpIR> outObjects = processingElements.get("cpu_" + fromType.getName());
                        Set<SVarExpIR> inObjects = processingElements.get("cpu_" + toType.getName());

                        for(SVarExpIR outObj: outObjects)
                        {
                            ASyscModuleDeclIR outModule = null;
                            for(IRStatus<PIR> status: statuses)
                            {
                                if(status.getIrNode() instanceof ASyscModuleDeclIR && ((ASyscModuleDeclIR) status.getIrNode()).getName().equals(((AClassTypeIR)outObj.getType()).getName()))
                                {
                                    outModule = (ASyscModuleDeclIR) status.getIrNode();
                                }
                            }

                            for(SVarExpIR inObj: inObjects)
                            {
                                ASyscModuleDeclIR inModule = null;
                                for(IRStatus<PIR> status: statuses)
                                {
                                    if(status.getIrNode() instanceof ASyscModuleDeclIR && ((ASyscModuleDeclIR) status.getIrNode()).getName().equals(((AClassTypeIR)inObj.getType()).getName()))
                                    {
                                        inModule = (ASyscModuleDeclIR) status.getIrNode();
                                    }
                                }

                                for(SStmIR m: outModule.getOutgoingMsg())
                                {
                                    ARemoteMethodCallStmIR msg = (ARemoteMethodCallStmIR) m;
                                    if(msg.getCalled().equals(inModule.getName()))
                                    {
                                        remoteMethodCalls.add(msg);
                                        inModule.getIncomingMsg().add(msg);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ASyscBusModuleDeclIR busModule = generator.generateBus(bus, remoteMethodCalls, statuses);
            statuses.add(new IRStatus<PIR>(null, bus, busModule, new HashSet<VdmNodeInfo>()));
        }
    }

    private void generateDeployedModules(List<IRStatus<PIR>> statuses) {
        SyscModuleGenerator generator = new SyscModuleGenerator();
        for(AFieldDeclIR activeObject: systemDeployedObjects)
        {
            if(activeObject.getType() instanceof AClassTypeIR) {
                IRStatus<PIR> status = null;
                for (IRStatus<PIR> s : statuses) {
                    if (s.getIrNode() instanceof ADefaultClassDeclIR) {
                        ADefaultClassDeclIR clazz = (ADefaultClassDeclIR) s.getIrNode();
                        if (clazz.getName() == ((AClassTypeIR)activeObject.getType()).getName()) {
                            status = s;
                        }
                    }
                }
                statuses.remove(status);
                statuses.add(new IRStatus<PIR>(status.getVdmNode(), status.getIrNodeName(), generator.generateModule((ADefaultClassDeclIR) status.getIrNode(), systemcModuleNames, true), status.getUnsupportedInIr()));
            }
        }
    }

    private void generateCPUModules(List<IRStatus<PIR>> statuses) {
        SyscModuleGenerator generator = new SyscModuleGenerator();
        for(String cpu_name : processingElements.keySet())
        {
            Set<SVarExpIR> deployedModules = processingElements.get(cpu_name);

            ADefaultClassDeclIR clazz = new ADefaultClassDeclIR();
            clazz.setName(cpu_name);

            List<AFieldDeclIR> fields = new LinkedList<>();

            // TODO: Still missing some declarations
            for(SExpIR m : deployedModules) {
                if(m instanceof AIdentifierVarExpIR) {
                    AIdentifierVarExpIR module = (AIdentifierVarExpIR) m;
                    AFieldDeclIR field = new AFieldDeclIR();
                    field.setAccess("public");
                    field.setType(module.getType().clone());
                    field.setName(module.getName());
                    field.setStatic(false);
                    field.setVolatile(false);
                    field.setFinal(false);
                    ANewExpIR initial = new ANewExpIR();
                    ATypeNameIR initial_type_name = new ATypeNameIR();
                    initial_type_name.setName(module.getType().clone().toString());
                    initial.setName(initial_type_name);
                    field.setInitial(initial);
                    fields.add(field);
                }
            }
            fields.add(generator.generateClock(clazz.getName(), 100));

            AMethodDeclIR ctor = new AMethodDeclIR();
            ctor.setName(clazz.getName());
            ctor.setAccess("public");
            ctor.setIsConstructor(true);
            ABlockStmIR ctorBody = new ABlockStmIR();
            for(SExpIR module: deployedModules)
            {
                String moduleName = "::ERROR::";
                if(module instanceof AIdentifierVarExpIR) {
                    moduleName = (((AIdentifierVarExpIR) module).getName());
                }
                else if(module instanceof AExplicitVarExpIR) {
                    moduleName = (((AExplicitVarExpIR) module).getName());
                }

                APlainCallStmIR assignClk = new APlainCallStmIR();
                assignClk.setType(new AVoidTypeIR());
                AClassTypeIR clkType = new AClassTypeIR();
                clkType.setName(moduleName);
                assignClk.setClassType(clkType);
                assignClk.setName("clk");
                AIdentifierVarExpIR clkIdentifier = new AIdentifierVarExpIR();
                clkIdentifier.setName("clk");
                assignClk.getArgs().add(clkIdentifier);
                ctorBody.getStatements().add(assignClk);

                AFieldDeclIR moduleDecl = new AFieldDeclIR();
                moduleDecl.setVolatile(false);
                moduleDecl.setAccess("public");
                moduleDecl.setType(module.getType().clone());
                moduleDecl.setName(moduleName);
                ANewExpIR moduleDeclInitial = new ANewExpIR();
                ATypeNameIR moduleDeclInitialTypeName = new ATypeNameIR();
                moduleDeclInitialTypeName.setName(((AClassTypeIR)(module.getType())).getName());
                moduleDeclInitial.setName(moduleDeclInitialTypeName);
                AStringLiteralExpIR moduleDeclName = new AStringLiteralExpIR();
                moduleDeclName.setValue(cpu_name + "." + moduleName);
                moduleDeclInitial.setArgs(Arrays.asList());
                moduleDecl.setInitial(moduleDeclInitial);
                fields.add(moduleDecl);
            }

            for(String channelName : connectionElements.keySet()) {
                for(AIdentifierVarExpIR cpu: connectionElements.get(channelName).getFirst()) {
                    if(((AClassTypeIR)cpu.getType()).getName().equals(cpu_name)) {
                        AMethodDeclIR blockingTransportHandler = new AMethodDeclIR();
                        blockingTransportHandler.setIsConstructor(false);
                        blockingTransportHandler.setAccess("public");
                        blockingTransportHandler.setName(channelName + "_input_handler");
                        AMethodTypeIR voidType = new AMethodTypeIR();
                        voidType.setResult(new AVoidTypeIR());
                        blockingTransportHandler.setMethodType(voidType);

                        AFormalParamLocalParamIR payload = new AFormalParamLocalParamIR();
                        AClassTypeIR payloadType = new AClassTypeIR();
                        payloadType.setName("tlm::tlm_generic_payload &");
                        payload.setType(payloadType);
                        AIdentifierPatternIR payloadName = new AIdentifierPatternIR();
                        payloadName.setName("payload");
                        payload.setPattern(payloadName);

                        AFormalParamLocalParamIR delay = new AFormalParamLocalParamIR();
                        AClassTypeIR delayType = new AClassTypeIR();
                        delayType.setName("sc_time &");
                        delay.setType(delayType);
                        AIdentifierPatternIR delayName = new AIdentifierPatternIR();
                        delayName.setName("delay");
                        delay.setPattern(delayName);

                        blockingTransportHandler.setFormalParams(Arrays.asList(payload, delay));
                        clazz.getMethods().add(blockingTransportHandler);

                        AMethodDeclIR writeSocket = new AMethodDeclIR();
                        writeSocket.setIsConstructor(false);
                        writeSocket.setAccess("private");
                        writeSocket.setName("write_" +channelName);
                        AClassTypeIR writeSocketType = new AClassTypeIR();
                        writeSocketType.setName("result");
                        AMethodTypeIR writeSocketMethodType = new AMethodTypeIR();
                        writeSocketMethodType.setResult(writeSocketType);
                        writeSocket.setMethodType(writeSocketMethodType);

                        ATemplateTypeIR resultTemplateType = new ATemplateTypeIR();
                        resultTemplateType.setName("typename result");
                        ATemplateTypeIR paramsTemplateType = new ATemplateTypeIR();
                        paramsTemplateType.setName("typename params");
                        writeSocket.setTemplateTypes(Arrays.asList(resultTemplateType, paramsTemplateType));

                        AFormalParamLocalParamIR destinationParam = new AFormalParamLocalParamIR();
                        destinationParam.setType(new AIntNumericBasicTypeIR());
                        AIdentifierPatternIR destinationParamName = new AIdentifierPatternIR();
                        destinationParamName.setName("destination");
                        destinationParam.setPattern(destinationParamName);

                        AFormalParamLocalParamIR methodParam = new AFormalParamLocalParamIR();
                        methodParam.setType(new AIntNumericBasicTypeIR());
                        AIdentifierPatternIR methodParamName = new AIdentifierPatternIR();
                        methodParamName.setName("method");
                        methodParam.setPattern(methodParamName);

                        AFormalParamLocalParamIR parameterParam = new AFormalParamLocalParamIR();
                        AClassTypeIR parameterParamType = new AClassTypeIR();
                        parameterParamType.setName("params");
                        parameterParam.setType(parameterParamType);
                        AIdentifierPatternIR parameterParamName = new AIdentifierPatternIR();
                        parameterParamName.setName("parameters");
                        parameterParam.setPattern(parameterParamName);

                        writeSocket.setFormalParams(Arrays.asList(destinationParam, methodParam, parameterParam));

                        AMethodDeclIR writeSocket_void = writeSocket.clone();
                        writeSocket_void.setMethodType(voidType.clone());
                        writeSocket_void.getTemplateTypes().remove(0);
                        writeSocket_void.setName(writeSocket.getName() + "_void");

                        clazz.getMethods().addAll(Arrays.asList(writeSocket, writeSocket_void));

                        APlainCallStmIR registerBlockingTransport = new APlainCallStmIR();
                        registerBlockingTransport.setType(new AVoidTypeIR());
                        registerBlockingTransport.setName(channelName + "_input.register_b_transport");
                        AIdentifierVarExpIR thisIdentifier = new AIdentifierVarExpIR();
                        thisIdentifier.setName("this");
                        AIdentifierVarExpIR callbackName = new AIdentifierVarExpIR();
                        callbackName.setName("&" + cpu_name + "::" + channelName + "_input_handler");
                        registerBlockingTransport.setArgs(Arrays.asList(thisIdentifier, callbackName));
                        ctorBody.getStatements().add(registerBlockingTransport);

                        AFieldDeclIR channelOutput = new AFieldDeclIR();
                        AClassTypeIR channelOutputType = new AClassTypeIR();
                        channelOutputType.setName("tlm_utils::simple_initiator_socket<" + cpu_name + ">");
                        channelOutput.setType(channelOutputType);
                        channelOutput.setName(channelName + "_output");
                        channelOutput.setVolatile(false);
                        channelOutput.setAccess("public");
                        fields.add(channelOutput);

                        AFieldDeclIR channelInput = new AFieldDeclIR();
                        AClassTypeIR channelInputType = new AClassTypeIR();
                        channelInputType.setName("tlm_utils::simple_target_socket<" + cpu_name + ">");
                        channelInput.setType(channelInputType);
                        channelInput.setName(channelName + "_input");
                        channelInput.setVolatile(false);
                        channelInput.setAccess("public");
                        fields.add(channelInput);
                    }
                }
            }

            ctor.setBody(ctorBody);
            clazz.getMethods().add(ctor);

            clazz.setFields(fields);

            statuses.add(new IRStatus<PIR>(null, cpu_name, generator.generateModule(clazz, systemcModuleNames, true), new HashSet<VdmNodeInfo>()));
        }
    }

    private void generateTopModule(List<IRStatus<PIR>> statuses) {
        SyscModuleGenerator generator = new SyscModuleGenerator();
        ASystemClassDeclIR systemClass = root;
        IRStatus<ASystemClassDeclIR> originalRoot = IRStatus.extract(statuses, ASystemClassDeclIR.class).get(0);

        ASyscModuleDeclIR module = generator.generateModule(systemClass, systemcModuleNames, false);
        module.setTop(true);

        for(AFieldDeclIR field : systemDeployedObjects)
        {
            module.getFields().remove(field);
        }

        AMethodDeclIR ctor = null;
        for(AMethodDeclIR method : module.getMethods())
            if(method.getIsConstructor())
            {
                ctor = method;
            }

        if(ctor == null)
        {
            ctor = new AMethodDeclIR();
            ctor.setName(systemClass.getName());
            ctor.setIsConstructor(true);
            ctor.setBody(new ABlockStmIR());
        }

        if(ctor.getBody() instanceof ABlockStmIR)
        {
            ABlockStmIR body = (ABlockStmIR) ctor.getBody();
            body.getStatements().removeIf(stm -> stm instanceof ACallObjectStmIR && ((ACallObjectStmIR) stm).getFieldName().equals("deploy"));

            for(String busName : connectionElements.keySet()) {
                AIdentifierVarExpIR busIdentifier = new AIdentifierVarExpIR();
                busIdentifier.setName(busName);
                for(AIdentifierVarExpIR connected : connectionElements.get(busName).getFirst())
                {
                    ACallObjectStmIR output = createBindStm(busName, busIdentifier, connected, "outputs");
                    body.getStatements().add(output);
                    ACallObjectStmIR input = createBindStm(busName, busIdentifier, connected, "inputs");
                    body.getStatements().add(input);
                }
            }

        }
        else
        {
            throw new IllegalArgumentException("Ctor body is not a block statement.");
        }


        IRStatus<PIR> origRoot = null;
        for(IRStatus<PIR> status: statuses) {
            if(status.getIrNode() instanceof ASystemClassDeclIR) {
                origRoot = status;
            }
        }
        statuses.remove(origRoot);
        statuses.add(new IRStatus<PIR>(originalRoot.getVdmNode(), originalRoot.getIrNodeName(), module, originalRoot.getUnsupportedInIr()));
    }

    private ACallObjectStmIR createBindStm(String busName, AIdentifierVarExpIR busIdentifier, AIdentifierVarExpIR connected, String ioDesignator) {
        ACallObjectStmIR bind = new ACallObjectStmIR();
        bind.setType(new AVoidTypeIR());
        bind.setFieldName("bind");
        AIdentifierObjectDesignatorIR connectedIdentifier = new AIdentifierObjectDesignatorIR();
        AIdentifierVarExpIR connectedInput = new AIdentifierVarExpIR();
        connectedInput.setName(connected.getName() + "." + busName + "_" + ioDesignator);
        connectedIdentifier.setExp(connectedInput);
        bind.setDesignator(connectedIdentifier);
        AClassTypeIR inputType = new AClassTypeIR();
        inputType.setName(connected.getType() + "." + busName + "_" + ioDesignator);
        AFieldExpIR channel = new AFieldExpIR();
        channel.setObject(busIdentifier.clone());
        channel.setMemberName(ioDesignator + "[" + (busName + "_identifier::").toUpperCase() + connected.getName() + "]");
        bind.setArgs(Arrays.asList(channel));
        return bind;
    }
}
