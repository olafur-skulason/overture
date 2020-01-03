package org.overture.codegen.vdm2systemc.transformations;

import org.overture.ast.types.AClassType;
import org.overture.codegen.ir.SNameIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AMutexSyncDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectStmIR;
import org.overture.codegen.ir.statements.AIdentifierObjectDesignatorIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.vdm2systemc.extast.analysis.DepthFirstAnalysisSystemCAdaptor;
public class MutexImplementationTransformation extends DepthFirstAnalysisSystemCAdaptor {

    public MutexImplementationTransformation() {

    }

    @Override
    public void caseAMutexSyncDeclIR(AMutexSyncDeclIR node) throws AnalysisException
    {
        ADefaultClassDeclIR clazz = node.getAncestor(ADefaultClassDeclIR.class);
        AFieldDeclIR mutex = new AFieldDeclIR();
        mutex.setAccess("private");
        mutex.setName("mutex");
        AClassTypeIR mutexType = new AClassTypeIR();
        mutexType.setName("sc_mutex");
        mutex.setType(mutexType);
        mutex.setVolatile(false);
        clazz.getFields().add(mutex);

        for(SNameIR opName : node.getOpnames()) {
            for(AMethodDeclIR method : clazz.getMethods()) {
                if(method.getName().equals(opName.toString())) {
                    AIdentifierObjectDesignatorIR mutexDesignator = new AIdentifierObjectDesignatorIR();
                    AIdentifierVarExpIR mutexDesignatorExp = new AIdentifierVarExpIR();
                    mutexDesignatorExp.setName("mutex");
                    mutexDesignator.setExp(mutexDesignatorExp);

                    ACallObjectStmIR lock = new ACallObjectStmIR();
                    lock.setFieldName("lock");
                    lock.setType(new AVoidTypeIR());
                    lock.setDesignator(mutexDesignator.clone());

                    ACallObjectStmIR unlock = new ACallObjectStmIR();
                    unlock.setFieldName("unlock");
                    unlock.setType(new AVoidTypeIR());
                    unlock.setDesignator(mutexDesignator.clone());

                    if(method.getBody() instanceof ABlockStmIR) {
                        ((ABlockStmIR) method.getBody()).getStatements().addFirst(lock.clone());
                        ((ABlockStmIR) method.getBody()).getStatements().add(unlock.clone());
                    }
                }
            }
        }
    }
}
