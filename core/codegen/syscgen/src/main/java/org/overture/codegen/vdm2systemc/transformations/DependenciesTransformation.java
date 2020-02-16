package org.overture.codegen.vdm2systemc.transformations;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AInterfaceDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.vdm2systemc.extast.analysis.DepthFirstAnalysisSystemCAdaptor;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscBusModuleDeclIR;
import org.overture.codegen.vdm2systemc.extast.declarations.ASyscModuleDeclIR;

import java.util.LinkedList;
import java.util.List;

public class DependenciesTransformation extends DepthFirstAnalysisSystemCAdaptor
{
    private IRInfo info;

    public DependenciesTransformation(IRInfo info)
    {
        this.info = info;
    }

    @Override
    public void caseADefaultClassDeclIR(ADefaultClassDeclIR node) throws AnalysisException {
        for(AInterfaceDeclIR iface : node.getInterfaces()) {
            node.setDependencies(addDependency(node.getDependencies(), "\"" + iface.getName() + ".h\""));
        }
        super.caseADefaultClassDeclIR(node);
    }

    @Override
    public void caseASyscModuleDeclIR(ASyscModuleDeclIR node) throws AnalysisException {
        for(String iface : node.getInterfaces()) {
            node.setDependencies(addDependency(node.getDependencies(), "\"" + iface + ".h\""));
        }
        super.caseASyscModuleDeclIR(node);
    }

    @Override
    public void caseASeqSeqTypeIR(ASeqSeqTypeIR node) throws AnalysisException {
        INode object = getParentClass(node);

        if(object instanceof ADefaultClassDeclIR) {
            ADefaultClassDeclIR parent = (ADefaultClassDeclIR) object;
            parent.setDependencies(addDependency(parent.getDependencies(), "<list>"));
        }
        else if(object instanceof ASyscModuleDeclIR) {
            ASyscModuleDeclIR parent = (ASyscModuleDeclIR) object;
            parent.setDependencies(addDependency(parent.getDependencies(), "<list>"));
        }
        else if(object instanceof ASyscBusModuleDeclIR) {
            ASyscBusModuleDeclIR parent = (ASyscBusModuleDeclIR) object;
            parent.setDependencies(addDependency(parent.getDependencies(), "<list>"));
        }
        super.caseASeqSeqTypeIR(node);
    }

    @Override
    public void caseAClassTypeIR(AClassTypeIR node) throws  AnalysisException {
        if(node.getName().startsWith("tlm::"))
            return;
        if(node.getName().startsWith("tlm_utils::"))
            return;
        if(node.getName().startsWith("sc_"))
            return;

        INode object = getParentClass(node);

        String nodeName = formatDependencyName(node.getName());

        String dependencyString = String.format("\"%s.h\"", nodeName);
        if(object instanceof ADefaultClassDeclIR) {
            ADefaultClassDeclIR parent = (ADefaultClassDeclIR) object;

            if (parent.getName().equals(node.getName())) // Need not include self.
                return;

            parent.setDependencies(addDependency(parent.getDependencies(), dependencyString));
        }
        else if(object instanceof ASyscModuleDeclIR) {
            ASyscModuleDeclIR parent = (ASyscModuleDeclIR) object;

            if (parent.getName().equals(node.getName())) // Need not include self.
                return;

            parent.setDependencies(addDependency(parent.getDependencies(), dependencyString));
        }
        else if(object instanceof ASyscBusModuleDeclIR) {
            ASyscBusModuleDeclIR parent = (ASyscBusModuleDeclIR) object;

            if (parent.getName().equals(node.getName())) // Need not include self.
                return;

            parent.setDependencies(addDependency(parent.getDependencies(), dependencyString));
        }

        super.caseAClassTypeIR(node);
    }

    private String formatDependencyName(String name) {
        String result = name;
        if(result.endsWith("*"))
            result = result.substring(0, result.length() - 1).trim();

        return result;
    }

    private INode getParentClass(INode node) throws AnalysisException {
        if(node instanceof SClassDeclIR || node instanceof ASyscModuleDeclIR || node instanceof ASyscBusModuleDeclIR)
        {
            return node;
        }
        else if(node.parent() == null) {
            throw new AnalysisException("Parent class type not found.");
        }
        else {
            return getParentClass(node.parent());
        }
    }

    private List<ClonableString> addDependency(List<? extends ClonableString> originalDependencies, String dependency)
    {
        ClonableString dep = new ClonableString(dependency);

        List<ClonableString> dependencies = new LinkedList<>();
        dependencies.addAll(originalDependencies);

        if(originalDependencies.stream().anyMatch(t -> t.value.equals(dependency)))
            return dependencies;

        dependencies.add(dep);

        return dependencies;
    }
}

