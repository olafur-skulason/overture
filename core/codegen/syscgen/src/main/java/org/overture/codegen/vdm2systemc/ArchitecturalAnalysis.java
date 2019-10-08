package org.overture.codegen.vdm2systemc;

import org.overture.ast.intf.lex.ILexCommentList;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.types.AClassType;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.AStringLiteralExpIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.ACallObjectStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscModuleDeclIR;
import sun.awt.image.ImageWatched;

import java.util.*;

public class ArchitecturalAnalysis {
    private LinkedList<AFieldDeclIR> systemDeployedObjects;
    private HashMap<String, Set<SExpIR>> processingElements;
    private LinkedList<String> systemcModuleNames;
    private IRStatus<PIR> root;
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
        root = null;
    }

    public void AnalyseArchitecture(List<IRStatus<PIR>> statuses)
    {
        // 1. Build deployment tree.
        // 1.1 Find root.
        root = null;
        for(IRStatus<PIR> p : statuses)
        {
            if(p.getIrNode() instanceof ASystemClassDeclIR)
            {
                root = p;
            }
        }

        if(root != null){
            ASystemClassDeclIR system = (ASystemClassDeclIR)root.getIrNode().clone();
            systemcModuleNames.add(system.getName());

            for(AFieldDeclIR field : system.getFields()) // For each field in system
            {
                if(field.getType() instanceof AClassTypeIR) // If object
                {
                    AClassTypeIR type = (AClassTypeIR) field.getType();

                    if(type.getName() == "CPU")
                    {
                        HashSet<SExpIR> set = new HashSet<>();
                        processingElements.put(field.getName(), set);
                        systemcModuleNames.add(field.getName());
                    }
                    else if(type.getName() == "BUS")
                    {
                        // TODO
                    }
                    else
                    {
                        systemDeployedObjects.add(field);
                        systemcModuleNames.add(((AClassTypeIR) field.getType()).getName());
                    }
                }
            }

            for(AMethodDeclIR method: system.getMethods())
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

                                Set<SExpIR> depSet = processingElements.get(callObjectStm.getDesignator().toString());

                                depSet.add(callObjectStm.getArgs().getFirst());
                                processingElements.put(callObjectStm.getDesignator().toString(), depSet);
                            }
                        }
                    }
                }
            }

            generateTopModule(statuses);
            generateCPUModules(statuses);
            generateDeployedModules(statuses);
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
                statuses.add(new IRStatus<PIR>(status.getVdmNode(), status.getIrNodeName(), generator.generateModule((ADefaultClassDeclIR) status.getIrNode(), systemcModuleNames), status.getUnsupportedInIr()));
            }
        }
    }

    private void generateCPUModules(List<IRStatus<PIR>> statuses) {
        SyscModuleGenerator generator = new SyscModuleGenerator();
        for(String cpu_name : processingElements.keySet())
        {
            Set<SExpIR> deployedModules = processingElements.get(cpu_name);

            ADefaultClassDeclIR clazz = new ADefaultClassDeclIR();
            clazz.setName("sc_"+cpu_name);

            List<AFieldDeclIR> fields = new LinkedList<>();

            // TODO: Still missing some declarations
            for(SExpIR m : deployedModules) {
                if(m instanceof AIdentifierVarExpIR) {
                    AIdentifierVarExpIR module = (AIdentifierVarExpIR) m;
                    AFieldDeclIR field = new AFieldDeclIR();
                    field.setAccess("public");
                    field.setType(module.getType());
                    field.setName(module.getName());
                    field.setStatic(false);
                    field.setVolatile(false);
                    field.setFinal(false);
                    ANewExpIR initial = new ANewExpIR();
                    ATypeNameIR initial_type_name = new ATypeNameIR();
                    initial_type_name.setName(module.getName());
                    initial.setName(initial_type_name);
                    AStringLiteralExpIR instanceName = new AStringLiteralExpIR();
                    instanceName.setValue(clazz.getName() + "." + field.getName());
                    initial.setArgs(Arrays.asList(instanceName));
                    field.setInitial(initial);
                    fields.add(field);

                }
            }
            fields.add(generator.generateClock(clazz.getName(), 100));

            clazz.setFields(fields);

            statuses.add(new IRStatus<PIR>(null, cpu_name, generator.generateModule(clazz, systemcModuleNames), new HashSet<VdmNodeInfo>()));
        }
    }

    private void generateTopModule(List<IRStatus<PIR>> statuses) {
        SyscModuleGenerator generator = new SyscModuleGenerator();
        ASystemClassDeclIR systemClass = (ASystemClassDeclIR) root.getIrNode();

        ASyscModuleDeclIR module = generator.generateModule(systemClass, systemcModuleNames);
        module.setTop(true);

        statuses.remove(root);
        statuses.add(new IRStatus<PIR>(root.getVdmNode(), root.getIrNodeName(), module, root.getUnsupportedInIr()));
    }
}
