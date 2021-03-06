package org.overture.codegen.vdm2systemc;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.statements.AIdentifierObjectDesignator;
import org.overture.ast.types.ACharBasicType;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.*;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.printer.MsgPrinter;
import org.overture.codegen.runtime.traces.Pair;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscBusModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.statements.AHandleInputStmIR;
import org.overture.codegen.vdm2systemc.extast.statements.AHandleModuleInputStmIR;
import org.overture.codegen.vdm2systemc.extast.statements.ARemoteMethodCallStmIR;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ArchitecturalAnalysis {
    private LinkedList<AFieldDeclIR> systemDeployedObjectDeclarations;
    private LinkedList<ASyscModuleDeclIR> systemDeployedObjects;
    private HashMap<String, Pair<Set<String>, Double>> processingElements;
    private HashMap<String, ASyscModuleDeclIR> systemcModules;
    private ASystemClassDeclIR root;
    private HashMap<String, Pair<List<AIdentifierVarExpIR>, Long>> connectionElements;
    private List<Pair<String, String>> activeObjects;
    private SystemcFormat systemcFormat;
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

    public ArchitecturalAnalysis(SystemcFormat systemcFormat)
    {
        this.systemcFormat = systemcFormat;
        systemDeployedObjectDeclarations = new LinkedList<>();
        systemDeployedObjects = new LinkedList<>();
        processingElements = new HashMap<>();
        systemcModules = new HashMap<>();
        connectionElements = new HashMap<>();
        activeObjects = new LinkedList<>();
        root = null;
    }

    public void FindRoot(List<IRStatus<PIR>> statuses)
    {
        MsgPrinter.getPrinter().println("Finding root.");
        root = null;
        for(IRStatus<PIR> p : statuses)
        {
            if(p.getIrNode() instanceof ASystemClassDeclIR)
            {
                root = (ASystemClassDeclIR) p.getIrNode();
                MsgPrinter.getPrinter().println("Found root: " + root.getName());
            }
        }
    }


    public void AnalyseArchitecture(List<IRStatus<PIR>> statuses) throws AnalysisException, org.overture.codegen.ir.analysis.AnalysisException {
        if(root != null){
            systemcModules.put(root.getName(), null);

            MsgPrinter.getPrinter().println("Analysing system fields: #" + root.getFields().size());
            for(AFieldDeclIR field : root.getFields()) // For each field in system
            {
                if(field.getType() instanceof AClassTypeIR) // If object
                {
                    AClassTypeIR type = (AClassTypeIR) field.getType();

                    if(type.getName().equals("CPU"))
                    {
                        type.setName("cpu_" + field.getName());
                        double frequency = 0;
                        if(((ANewExpIR) field.getInitial()).getArgs().get(1) instanceof ARealLiteralExpIR) {
                            frequency = ((ARealLiteralExpIR) ((ANewExpIR) field.getInitial()).getArgs().get(1)).getValue();
                        }
                        else if (((ANewExpIR) field.getInitial()).getArgs().get(1) instanceof AIntLiteralExpIR) {
                            frequency = ((AIntLiteralExpIR) ((ANewExpIR) field.getInitial()).getArgs().get(1)).getValue();
                        }
                        processingElements.put(type.getName(), new Pair<>(new HashSet<>(), frequency));
                        systemcModules.put("cpu_" + field.getName(), null);
                        field.parent().replaceChild(field, generateModuleField(field, "cpu_"));
                    }
                    else if(type.getName().equals("BUS"))
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
                        if(initial.getArgs().get(1) instanceof AIntLiteralExpIR) {
                            connectionElements.put("bus_" + field.getName(), new Pair<>(connectedCPUS, ((AIntLiteralExpIR) initial.getArgs().get(1)).getValue()));
                        }
                        else if(initial.getArgs().get(1) instanceof ARealLiteralExpIR) {
                            connectionElements.put("bus_" + field.getName(), new Pair<>(connectedCPUS, ((ARealLiteralExpIR) initial.getArgs().get(1)).getValue().longValue()));
                        }
                        type.setName("bus_" + field.getName());
                        systemcModules.put(type.getName(), null);
                        field.parent().replaceChild(field, generateModuleField(field, "bus_"));
                    }
                    else
                    {
                        systemDeployedObjectDeclarations.add(field);
                        systemcModules.put(type.getName(), null);
                        activeObjects.add(new Pair<>(type.getName(), field.getName()));
                    }
                }
            }
            MsgPrinter.getPrinter().println("Root fields analysed");

            List<AIdentifierVarExpIR> cpuIdentifiers = new LinkedList<>();
            for(String cpu : processingElements.keySet())
            {
                AIdentifierVarExpIR id = new AIdentifierVarExpIR();
                AClassTypeIR type = new AClassTypeIR();
                type.setName(cpu.substring(4));
                id.setName(cpu);
                id.setType(type);
                cpuIdentifiers.add(id);
            }
            MsgPrinter.getPrinter().println("Added cpus");

            List<AIdentifierVarExpIR> virtual_bus_identifiers = new LinkedList<>();

            {
                AIdentifierVarExpIR virtual_cpu_identifier = new AIdentifierVarExpIR();
                AClassTypeIR c_type = new AClassTypeIR();
                c_type.setName("cpu_virtual");
                virtual_cpu_identifier.setType(c_type);
                virtual_cpu_identifier.setName("virtual_cpu");
                virtual_bus_identifiers.add(virtual_cpu_identifier);
            }
            for(AIdentifierVarExpIR cpu : cpuIdentifiers) {
                AIdentifierVarExpIR c = new AIdentifierVarExpIR();
                AClassTypeIR c_type = new AClassTypeIR();
                c_type.setName(cpu.getName());
                c.setType(c_type);
                c.setName(((AClassTypeIR)cpu.getType()).getName());
                virtual_bus_identifiers.add(c);
            }
            MsgPrinter.getPrinter().println("Cpus connected to virtual bus");
            connectionElements.put("bus_virtual", new Pair<>(virtual_bus_identifiers, (long)1e12));

            List<String> unDeployedActiveObjects = activeObjects.stream().map(Pair::getSecond).collect(Collectors.toCollection(LinkedList::new));

            MsgPrinter.getPrinter().println("Analyzing methods of root.");
            for(AMethodDeclIR method: root.getMethods())
            {
                if(method.getIsConstructor())
                {
                    MsgPrinter.getPrinter().println("Found root's constructor");
                    if(method.getBody() instanceof ABlockStmIR)
                    {
                        ABlockStmIR body = (ABlockStmIR) method.getBody();

                        for(SStmIR stm :body.getStatements())
                        {
                            if(stm instanceof ACallObjectStmIR)
                            {
                                ACallObjectStmIR callObjectStm = (ACallObjectStmIR) stm;
                                if(callObjectStm.getFieldName().equals("deploy")) {
                                    Pair<Set<String>, Double> depSet = processingElements.get("cpu_" + (callObjectStm.getDesignator().toString()));
                                    SExpIR expIR = callObjectStm.getArgs().getFirst();
                                    String name;
                                    if(expIR instanceof AExplicitVarExpIR) {
                                        name = ((AExplicitVarExpIR) expIR).getName();
                                    }
                                    else if(expIR instanceof AIdentifierVarExpIR) {
                                        name = ((AIdentifierVarExpIR) expIR).getName();
                                    }
                                    else {
                                        throw new org.overture.codegen.ir.analysis.AnalysisException("Deployment operation with invalid parameters");
                                    }
                                    depSet.getFirst().add(name);
                                    processingElements.put("cpu_" + (callObjectStm.getDesignator().toString()), depSet);
                                    unDeployedActiveObjects.removeIf(stringStringPair -> stringStringPair.equals(name));
                                }
                            }
                        }
                    }
                }
            }
            MsgPrinter.getPrinter().println("Finished processing root methods.");

            processingElements.put("cpu_virtual", new Pair<>(new HashSet<>(unDeployedActiveObjects), (double)1e12));

            generateDeployedModules(statuses);
            MsgPrinter.getPrinter().println("Deployed modules created");
            generateInterfaces(statuses);
            MsgPrinter.getPrinter().println("Deployed module host interfaces created");
            generateCPUModules(statuses);
            MsgPrinter.getPrinter().println("Cpus created");
            generateConnectionModules(statuses);
            MsgPrinter.getPrinter().println("Busses created");
            generateTopModule(statuses);
            MsgPrinter.getPrinter().println("System root created.");
        }
    }

    private AFieldDeclIR generateModuleField(AFieldDeclIR original, String prefix) {
        ANewExpIR cpuInitial = null;
        if(original.getInitial() != null) {
            cpuInitial = new ANewExpIR();
            ATypeNameIR cpuInitialCtorName = new ATypeNameIR();
            cpuInitialCtorName.setName(prefix + original.getName());
            cpuInitial.setName(cpuInitialCtorName);
            AStringLiteralExpIR moduleName = new AStringLiteralExpIR();
            moduleName.setValue(original.getName());
            cpuInitial.getArgs().add(moduleName);
        }

        return generateField(original.getAccess(), prefix + original.getName(), original.getName(), cpuInitial);
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
            ASyscBusModuleDeclIR busModule = generator.generateBus(bus, connectionElements.get(bus).getFirst().stream().map( identifier -> identifier.getName()).collect(Collectors.toList()), connectionElements.get(bus).getSecond());
            statuses.add(new IRStatus<>(null, bus, busModule, new HashSet<>()));
        }
    }

    private void generateDeployedModules(List<IRStatus<PIR>> statuses) {
        SyscModuleGenerator generator = new SyscModuleGenerator();
        List<IRStatus<PIR>> systemcModules = new LinkedList<>();
        for(AFieldDeclIR activeObject: systemDeployedObjectDeclarations)
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

                ASyscModuleDeclIR syscModule = generator.generateModule((ADefaultClassDeclIR) status.getIrNode(), this.systemcModules, true);

                syscModule.getFields().add(generateField("private", "i_host_" + syscModule.getName() + " *", "os", null));

                systemDeployedObjects.add(syscModule);
                this.systemcModules.put(syscModule.getName(), syscModule);
                statuses.remove(status);
                systemcModules.add(new IRStatus<>(status.getVdmNode(), status.getIrNodeName(), syscModule, status.getUnsupportedInIr()));
            }
        }

        // Update ingoing messages
        for(IRStatus<PIR> status : systemcModules)
        {
            ASyscModuleDeclIR module = (ASyscModuleDeclIR) status.getIrNode();

            for(IRStatus<PIR> secondStatus : systemcModules)
            {
                ASyscModuleDeclIR secondModule = (ASyscModuleDeclIR) secondStatus.getIrNode();
                if(module != secondModule)
                {
                    for(SStmIR outgoingStm : secondModule.getOutgoingMsg())
                    {
                        if(outgoingStm instanceof ARemoteMethodCallStmIR)
                        {
                            ARemoteMethodCallStmIR outgoingStmFromSecond = (ARemoteMethodCallStmIR) outgoingStm;
                            String calledObjectDesignator = ((AExplicitVarExpIR)((AIdentifierObjectDesignatorIR)outgoingStmFromSecond.getCalled().getDesignator()).getExp()).getName();

                            if(systemDeployedObjectDeclarations.stream().anyMatch(var -> var.getName().equals(calledObjectDesignator) && var.getType().toString().equals(module.getName())))
                            {
                                module.getIncomingMsg().add(outgoingStmFromSecond.clone());
                            }
                        }
                    }
                }
            }
        }

        statuses.addAll(systemcModules);

    }

    private void generateInterfaces(List<IRStatus<PIR>> statuses) throws AnalysisException {
        for(ASyscModuleDeclIR syscModule : systemDeployedObjects){
            ADefaultClassDeclIR clazz = new ADefaultClassDeclIR();
            clazz.setName("i_host_" + syscModule.getName());
            clazz.setAccess("public");

            for (SStmIR stm : syscModule.getOutgoingMsg()) {
                if (stm instanceof ARemoteMethodCallStmIR) { // Should always be true
                    AMethodDeclIR original = getCalledMethodDeclaration(((ARemoteMethodCallStmIR) stm).getCalled().getDesignator(), ((ARemoteMethodCallStmIR) stm).getCalled().getFieldName(), statuses);
                    if(original == null)
                        throw new AnalysisException("Could not find original method for: " + stm.toString());
                    AMethodDeclIR method = new AMethodDeclIR();
                    method.setName(((ARemoteMethodCallStmIR) stm).getCalled().getDesignator() + "_" + ((ARemoteMethodCallStmIR) stm).getCalled().getFieldName()); // a_T(int i);
                    method.setAccess("public");
                    method.setAbstract(true);
                    method.setMethodType(original.getMethodType().clone());
                    method.setFormalParams((List<? extends AFormalParamLocalParamIR>) original.getFormalParams().clone());
                    clazz.getMethods().add(method);
                }
            }

            statuses.add(new IRStatus<PIR>(null, clazz.getName(), clazz, new HashSet<>()));
        }
    }

    private AMethodDeclIR getCalledMethodDeclaration(SObjectDesignatorIR designator, String fieldName, List<IRStatus<PIR>> statuses) {
        String hostType = "";
        for(AFieldDeclIR field: root.getFields()) {
            if(field.getName().equals(designator.toString())) {
                hostType = field.getType().toString();
                break;
            }
        }

        ASyscModuleDeclIR host = null;
        for(IRStatus<PIR> status : statuses) {
            if(status.getIrNode() instanceof ASyscModuleDeclIR) {
                if (((ASyscModuleDeclIR) status.getIrNode()).getName().equals(hostType)) {
                    host = (ASyscModuleDeclIR) status.getIrNode();
                }
            }
        }
        if(host == null) return null;

        for(AMethodDeclIR method : host.getMethods()) {
            if(method.getName().equals(fieldName)) {
                return method;
            }
        }

        return null;
    }

    private void generateCPUModules(List<IRStatus<PIR>> statuses) throws org.overture.codegen.ir.analysis.AnalysisException {
        SyscModuleGenerator generator = new SyscModuleGenerator();
        MsgPrinter.getPrinter().println("Processing cpus: #" + processingElements.keySet().size());
        for(String cpu_name : processingElements.keySet())
        {
            MsgPrinter.getPrinter().println("Starting creation of cpu:" + cpu_name);
            Pair<Set<String>, Double> cpu = processingElements.get(cpu_name);
            Set<String> deployedModules = cpu.getFirst();
            Double speed = cpu.getSecond();

            ADefaultClassDeclIR clazz = new ADefaultClassDeclIR();
            clazz.setName(cpu_name);

            List<AFieldDeclIR> fields = new LinkedList<>();

            AMethodDeclIR ctor = new AMethodDeclIR();
            ctor.setName(clazz.getName());
            String access = "public";
            ctor.setAccess(access);
            ctor.setIsConstructor(true);
            ABlockStmIR ctorBody = new ABlockStmIR();

            // TODO: Still missing some declarations
            for(String fieldName : deployedModules) {
                MsgPrinter.getPrinter().println("Adding deployed module: " + fieldName);
                String fieldType = activeObjects.stream().filter( x -> x.getSecond().equals(fieldName)).findFirst().get().getFirst();
                ASyscModuleDeclIR deployedModule = systemcModules.get(fieldType);
                ANewExpIR initial = new ANewExpIR();
                ATypeNameIR initial_type_name = new ATypeNameIR();
                initial_type_name.setName(fieldType);
                initial.setName(initial_type_name);
                fields.add(generateField(access, fieldType, fieldName, initial));

                AInterfaceDeclIR module_interface = new AInterfaceDeclIR();
                module_interface.setName("i_host_" + fieldType);
                clazz.getInterfaces().add(module_interface);


                for(SStmIR stm : deployedModule.getOutgoingMsg()) {
                    MsgPrinter.getPrinter().println("Handling outgoing messages.");
                    if(stm instanceof ARemoteMethodCallStmIR) {
                        AMethodDeclIR original = getCalledMethodDeclaration(((ARemoteMethodCallStmIR) stm).getCalled().getDesignator(), ((ARemoteMethodCallStmIR) stm).getCalled().getFieldName(), statuses);
                        if(original == null)
                            throw new org.overture.codegen.ir.analysis.AnalysisException("Could not find original method for: " + stm.toString());
                        MsgPrinter.getPrinter().println("Processing outgoing message for operation: " + original.getName());
                        AMethodDeclIR remoteCallInterface = new AMethodDeclIR();
                        remoteCallInterface.setName(((ARemoteMethodCallStmIR) stm).getCalled().getDesignator() + "_" + ((ARemoteMethodCallStmIR) stm).getCalled().getFieldName());
                        remoteCallInterface.setAccess(access);
                        remoteCallInterface.setMethodType(original.getMethodType().clone());
                        //noinspection unchecked
                        List<AFormalParamLocalParamIR> remoteParams = (List<AFormalParamLocalParamIR>) original.getFormalParams().clone();
                        remoteCallInterface.setFormalParams(remoteParams);

                        ABlockStmIR body = new ABlockStmIR();

                        if(remoteCallInterface.getMethodType().getResult() instanceof AVoidTypeIR)
                        {
                            MsgPrinter.getPrinter().println("Outgoing operation is void type.");
                            String hostType = activeObjects.stream().filter( x -> x.getSecond().equals(((ARemoteMethodCallStmIR) stm).getCalled().getDesignator().toString())).findFirst().get().getFirst();
                            MsgPrinter.getPrinter().println("Host object is: " + hostType);
                            String hostCpu = determineHostingCpuName(((ARemoteMethodCallStmIR) stm).getCalled().getDesignator().toString());
                            MsgPrinter.getPrinter().println("Host cpu is: " + hostCpu);
                            String busName = determineBus(cpu_name, hostCpu);
                            MsgPrinter.getPrinter().println("Appropriate bus is: " + busName);
                            APlainCallStmIR callVoidInterface = new APlainCallStmIR();
                            callVoidInterface.setName("write_" + busName + "_void");
                            callVoidInterface.setType(new AVoidTypeIR());

                            AIdentifierVarExpIR objectIdentifier = generateVarIdentifier(String.format("%s_IDENTIFIER::%s", busName.toUpperCase(),  ((ARemoteMethodCallStmIR) stm).getCalled().getDesignator().toString()));
                            MsgPrinter.getPrinter().println("Generated object identifier");

                            AIdentifierVarExpIR methodIdentifier = generateVarIdentifier(String.format("EXPOSED_METHODS_%s::%s", hostType , ((ARemoteMethodCallStmIR) stm).getCalled().getFieldName()));
                            MsgPrinter.getPrinter().println("Generated method identifier");

                            MsgPrinter.getPrinter().println("Outgoing operation has " + remoteParams.size() + "params");
                            if(remoteParams.size() == 0) {
                                callVoidInterface.setName(callVoidInterface.getName() + "<char>");
                                AIntLiteralExpIR dummy = new AIntLiteralExpIR();
                                dummy.setValue((long) 0);
                                callVoidInterface.setArgs(Arrays.asList(objectIdentifier, methodIdentifier, dummy));
                            }
                            else if(remoteParams.size() == 1) {
                                callVoidInterface.setName(callVoidInterface.getName() + "<" + systemcFormat.format(remoteParams.get(0).getType()) + ">");
                                AIdentifierVarExpIR param = generateVarIdentifier(remoteParams.get(0).getPattern().toString());
                                callVoidInterface.setArgs(Arrays.asList(objectIdentifier, methodIdentifier, param));
                            }
                            else {
                                MsgPrinter.getPrinter().println("Unsupported >1 parameter remote method call. ");
                                throw new org.overture.codegen.ir.analysis.AnalysisException("Unsupported >1 parameter remote method call");
                            }

                            body.getStatements().add(callVoidInterface);
                        }

                        remoteCallInterface.setBody(body);

                        clazz.getMethods().add(remoteCallInterface);
                    }
                }

                ACallObjectStmIR assignClk = new ACallObjectStmIR();
                assignClk.setType(new AVoidTypeIR());

                AIdentifierObjectDesignatorIR clkType = new AIdentifierObjectDesignatorIR();
                AIdentifierVarExpIR clkIdentifierVar = new AIdentifierVarExpIR();
                clkIdentifierVar.setName(fieldName);
                clkType.setExp(clkIdentifierVar);
                assignClk.setDesignator(clkType);
                assignClk.setFieldName("clk");
                AIdentifierVarExpIR clkIdentifier = generateVarIdentifier("clk");
                assignClk.getArgs().add(clkIdentifier);
                MsgPrinter.getPrinter().println("Clock field assigned");

                AAssignToExpStmIR assignClkFreq = new AAssignToExpStmIR();
                AIdentifierVarExpIR fieldNameFreq = new AIdentifierVarExpIR();
                fieldNameFreq.setName(fieldName + ".clk_frequency");
                assignClkFreq.setTarget(fieldNameFreq);
                AIdentifierVarExpIR clkFreqIdentifier = new AIdentifierVarExpIR();
                clkFreqIdentifier.setName("clk_frequency");
                assignClkFreq.setExp(clkFreqIdentifier);
                ctorBody.getStatements().addAll(Arrays.asList(assignClk, assignClkFreq));
                MsgPrinter.getPrinter().println("Clock frequency field assigned.");
            }
            MsgPrinter.getPrinter().println("Finished adding deployed modules.");
            fields.add(generator.generateClock(clazz.getName(), speed)); // TODO: Read cpu frequency
            MsgPrinter.getPrinter().println("Clock field created");
            fields.add(generator.generateClockFrequency(speed));
            MsgPrinter.getPrinter().println("Clock frequency field created.");

            for(String channelName : connectionElements.keySet()) { // For all connection elements
                MsgPrinter.getPrinter().println("Creating connection to channel:" + channelName);
                if(connectionElements.get(channelName).getFirst().stream().anyMatch(x -> x.getType().toString().equals(clazz.getName()))) { // If clazz connected to channelName
                    AMethodDeclIR blockingTransportHandler = new AMethodDeclIR();
                    blockingTransportHandler.setIsConstructor(false);
                    blockingTransportHandler.setAccess(access);
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

                    // TODO: Add handleinput statements to blockingTransportHandlerBody
                    ABlockStmIR blockingTransportHandlerBody = new ABlockStmIR();

                    for(AIdentifierVarExpIR connectedCpu: connectionElements.get(channelName).getFirst().stream().filter( x -> ! x.getType().toString().equals(clazz.getName())).collect(Collectors.toList())) {
                        // connectedCpu is not clazz.
                        for(String deployedModuleName : processingElements.get(connectedCpu.getType().toString()).getFirst()) {
                            // deployedModule is a module deployed to connectedCpu.
                            ASyscModuleDeclIR deployedModule = systemcModules.get(activeObjects.stream().filter( x -> x.getSecond().equals(deployedModuleName)).findFirst().get().getFirst());
                            AHandleModuleInputStmIR handleModuleInput = new AHandleModuleInputStmIR();
                            handleModuleInput.setModuleName(deployedModuleName);
                            handleModuleInput.setHostName(clazz.getName());
                            for(SStmIR rmc : deployedModule.getOutgoingMsg()) {
                                ARemoteMethodCallStmIR remoteMethodCall = (ARemoteMethodCallStmIR) rmc;
                                AMethodDeclIR remoteMethod = this.getCalledMethodDeclaration(remoteMethodCall.getCalled().getDesignator(), remoteMethodCall.getCalled().getFieldName(), statuses);
                                AHandleInputStmIR handleInput = new AHandleInputStmIR();
                                AClassTypeIR handlerClass = new AClassTypeIR();
                                handlerClass.setName(deployedModule.getName());
                                handleInput.setHandlerType(handlerClass);
                                handleInput.setHandlerName(remoteMethodCall.getCalled().getDesignator().toString());
                                handleInput.setReturnType(remoteMethodCall.getCalled().getType().clone());
                                handleInput.setHandlerField(remoteMethodCall.getCalled().getFieldName());
                                if(remoteMethod.getFormalParams().size() > 1) {
                                    throw new org.overture.codegen.ir.analysis.AnalysisException(">1 parameters are currently not supported");
                                }
                                else if(remoteMethod.getFormalParams().size() == 1) { // TODO: replace with compound type for >1 parameter
                                    handleInput.setParamType(remoteMethod.getFormalParams().getFirst().getType().clone());
                                }
                                else {
                                    handleInput.setParamType(new ACharBasicTypeIR());
                                }

                                handleModuleInput.getInputs().add(handleInput);
                            }

                            blockingTransportHandlerBody.getStatements().add(handleModuleInput);
                        }
                    }

                    blockingTransportHandler.setBody(blockingTransportHandlerBody);

                    clazz.getMethods().add(blockingTransportHandler);

                    AMethodDeclIR writeSocket = new AMethodDeclIR();
                    writeSocket.setIsConstructor(false);
                    writeSocket.setAccess("private");
                    writeSocket.setName("write_" +channelName);
                    ATemplateTypeIR writeSocketType = new ATemplateTypeIR();
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
                    ATemplateTypeIR parameterParamType = new ATemplateTypeIR();
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
                    AIdentifierVarExpIR thisIdentifier = generateVarIdentifier("this");
                    AIdentifierVarExpIR callbackName = generateVarIdentifier("&" + cpu_name + "::" + channelName + "_input_handler");
                    registerBlockingTransport.setArgs(Arrays.asList(thisIdentifier, callbackName));
                    ctorBody.getStatements().add(registerBlockingTransport);

                    fields.add(generateField(access, "tlm_utils::simple_initiator_socket<" + cpu_name + ">", channelName + "_output", null));
                    fields.add(generateField(access, "tlm_utils::simple_target_socket<" + cpu_name + ">", channelName + "_input", null));
                }
            }
            MsgPrinter.getPrinter().println("Finished connecting modules.");

            ctor.setBody(ctorBody);
            clazz.getMethods().add(ctor);

            clazz.setFields(fields);

            statuses.add(new IRStatus<PIR>(null, cpu_name, generator.generateModule(clazz, systemcModules, true), new HashSet<VdmNodeInfo>()));
        }
    }

    private String determineHostingCpuName(String designator) throws org.overture.codegen.ir.analysis.AnalysisException {
        for(String cpuName : processingElements.keySet()) {
            Pair<Set<String>, Double> deployedModules = processingElements.get(cpuName);
            if(deployedModules != null && deployedModules.getFirst().contains(designator))
                return cpuName;
        }
        throw new org.overture.codegen.ir.analysis.AnalysisException("Could not determine host of " + designator);
    }

    private String determineBus(String client, String server) throws org.overture.codegen.ir.analysis.AnalysisException {
        for(String busName : connectionElements.keySet()) {
            List<AIdentifierVarExpIR> elements = connectionElements.get(busName).getFirst();
            if(elements.stream().filter( x -> x.getType().toString().equals(client) || x.getType().toString().equals(server)).count() == 2) {
                return busName;
            }
        }
        MsgPrinter.getPrinter().println(String.format("No connection between cpus %s and %s found", client, server));
        throw new org.overture.codegen.ir.analysis.AnalysisException(String.format("No connection between cpus %s and %s found", client, server));
    }

    private static AIdentifierVarExpIR generateVarIdentifier(String name) {
        MsgPrinter.getPrinter().println("Generating variable identifier for: " + name);
        AIdentifierVarExpIR objectIdentifier = new AIdentifierVarExpIR();
        objectIdentifier.setName(name);
        return objectIdentifier;
    }

    private static AFieldDeclIR generateField(String access, String type, String name, SExpIR initial) {
        AFieldDeclIR field = new AFieldDeclIR();
        field.setAccess(access);
        AClassTypeIR typeIR = new AClassTypeIR();
        typeIR.setName(type);
        field.setType(typeIR);
        field.setName(name);
        field.setStatic(false);
        field.setVolatile(false);
        field.setFinal(false);
        field.setInitial(initial);
        return field;
    }

    private void generateTopModule(List<IRStatus<PIR>> statuses) {
        MsgPrinter.getPrinter().println("Beginning of generateTopModule");
        SyscModuleGenerator generator = new SyscModuleGenerator();
        ASystemClassDeclIR systemClass = root;
        IRStatus<ASystemClassDeclIR> originalRoot = IRStatus.extract(statuses, ASystemClassDeclIR.class).get(0);

        ASyscModuleDeclIR module = generator.generateModule(systemClass, systemcModules, false);
        module.setTop(true);

        module.getFields().removeAll(systemDeployedObjectDeclarations);

        AFieldDeclIR virtual_cpu = new AFieldDeclIR();
        virtual_cpu.setVolatile(false);
        virtual_cpu.setAccess("public");
        AClassTypeIR virtual_cpu_type = new AClassTypeIR();
        virtual_cpu_type.setName("cpu_virtual");
        virtual_cpu.setName("virtual_cpu");
        ANewExpIR virtual_cpu_initial = new ANewExpIR();
        ATypeNameIR virtual_cpu_initial_type = new ATypeNameIR();
        virtual_cpu_initial_type.setName("cpu_virtual");
        virtual_cpu_initial.setName(virtual_cpu_initial_type);
        AStringLiteralExpIR virtual_cpu_arg_name = new AStringLiteralExpIR();
        virtual_cpu_arg_name.setValue("cpu_virtual");
        virtual_cpu_initial.setArgs(Arrays.asList(virtual_cpu_arg_name));
        virtual_cpu.setInitial(virtual_cpu_initial);
        virtual_cpu.setType(virtual_cpu_type);
        module.getFields().add(virtual_cpu);

        AFieldDeclIR virtual_bus = new AFieldDeclIR();
        virtual_bus.setVolatile(false);
        virtual_bus.setAccess("public");
        AClassTypeIR virtual_bus_type = new AClassTypeIR();
        virtual_bus_type.setName("bus_virtual");
        virtual_bus.setName("virtual_bus");
        ANewExpIR virtual_bus_initial = new ANewExpIR();
        ATypeNameIR virtual_bus_initial_type = new ATypeNameIR();
        virtual_bus_initial_type.setName("bus_virtual");
        virtual_bus_initial.setName(virtual_bus_initial_type);
        AStringLiteralExpIR virtual_bus_arg_name = new AStringLiteralExpIR();
        virtual_bus_arg_name.setValue("bus_virtual");
        virtual_bus_initial.setArgs(Arrays.asList(virtual_bus_arg_name));
        virtual_bus.setInitial(virtual_bus_initial);
        virtual_bus.setType(virtual_bus_type);
        module.getFields().add(virtual_bus);

        AMethodDeclIR ctor = module.getMethods().stream().filter(AMethodDeclIR::getIsConstructor).findFirst().orElse(null);

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
                Optional<AFieldDeclIR> busField = module.getFields().stream().filter( field -> field.getType() instanceof AClassTypeIR && ((AClassTypeIR) field.getType()).getName().equals(busName)).findFirst();
                if(busField.isPresent()) {
                    AIdentifierVarExpIR busIdentifier = generateVarIdentifier(busField.get().getName());
                    for (AIdentifierVarExpIR connected : connectionElements.get(busName).getFirst()) {
                        ACallObjectStmIR output = createBindStm(busName, busIdentifier, connected, "out");
                        body.getStatements().add(output);
                        ACallObjectStmIR input = createBindStm(busName, busIdentifier, connected, "in");
                        body.getStatements().add(input);
                    }
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
        MsgPrinter.getPrinter().println("statuses before count: " + statuses.size());
        statuses.remove(origRoot);
        statuses.add(new IRStatus<PIR>(originalRoot.getVdmNode(), originalRoot.getIrNodeName(), module, originalRoot.getUnsupportedInIr()));
        MsgPrinter.getPrinter().println("statuses after count: " + statuses.size());
    }

    private ACallObjectStmIR createBindStm(String busName, AIdentifierVarExpIR busIdentifier, AIdentifierVarExpIR connected, String ioDesignator) {
        ACallObjectStmIR bind = new ACallObjectStmIR();
        bind.setType(new AVoidTypeIR());
        bind.setFieldName("bind");
        AIdentifierObjectDesignatorIR connectedIdentifier = new AIdentifierObjectDesignatorIR();
        AIdentifierVarExpIR connectedInput = generateVarIdentifier(connected.getName() + "." + busName + "_" + ioDesignator + "put");
        connectedIdentifier.setExp(connectedInput);
        bind.setDesignator(connectedIdentifier);
        AClassTypeIR inputType = new AClassTypeIR();
        inputType.setName(connected.getType() + "." + busName + "_" + ioDesignator + "put");
        AFieldExpIR channel = new AFieldExpIR();
        channel.setObject(busIdentifier.clone());
        channel.setMemberName(ioDesignator + "puts[" + (busName + "_identifier::").toUpperCase() + busName + "_" + connected.getName() + "]");
        bind.setArgs(Arrays.asList(channel));
        return bind;
    }
}
