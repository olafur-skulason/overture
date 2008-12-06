//
// THIS FILE IS AUTOMATICALLY GENERATED!!
//
// Generated at Sat 16-Aug-2008 by the VDM++ to JAVA Code Generator
// (v8.1.1b - Fri 06-Jun-2008 09:09:07)
//
// Supported compilers:
// jdk1.4
//

// ***** VDMTOOLS START Name=HeaderComment KEEP=NO
// ***** VDMTOOLS END Name=HeaderComment

// ***** VDMTOOLS START Name=package KEEP=NO
package org.overturetool.ast.imp;

// ***** VDMTOOLS END Name=package

// ***** VDMTOOLS START Name=imports KEEP=YES

import jp.co.csk.vdm.toolbox.VDM.*;
import java.util.*;
import org.overturetool.ast.itf.*;
// ***** VDMTOOLS END Name=imports



public class OmlClass extends OmlNode implements IOmlClass {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=ivIdentifier KEEP=NO
  private String ivIdentifier = null;
// ***** VDMTOOLS END Name=ivIdentifier

// ***** VDMTOOLS START Name=ivGenericTypes KEEP=NO
  private Vector ivGenericTypes = null;
// ***** VDMTOOLS END Name=ivGenericTypes

// ***** VDMTOOLS START Name=ivInheritanceClause KEEP=NO
  private IOmlInheritanceClause ivInheritanceClause = null;
// ***** VDMTOOLS END Name=ivInheritanceClause

// ***** VDMTOOLS START Name=ivClassBody KEEP=NO
  private Vector ivClassBody = null;
// ***** VDMTOOLS END Name=ivClassBody

// ***** VDMTOOLS START Name=ivSystemSpec KEEP=NO
  private Boolean ivSystemSpec = null;
// ***** VDMTOOLS END Name=ivSystemSpec


// ***** VDMTOOLS START Name=OmlClass KEEP=NO
  public OmlClass () throws CGException {
    try {

      ivIdentifier = UTIL.ConvertToString(new String());
      ivGenericTypes = new Vector();
      ivInheritanceClause = null;
      ivClassBody = new Vector();
      ivSystemSpec = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=OmlClass


// ***** VDMTOOLS START Name=identity KEEP=NO
  public String identity () throws CGException {
    return new String("Class");
  }
// ***** VDMTOOLS END Name=identity


// ***** VDMTOOLS START Name=accept KEEP=NO
  public void accept (final IOmlVisitor pVisitor) throws CGException {
    pVisitor.visitClass((IOmlClass) this);
  }
// ***** VDMTOOLS END Name=accept


// ***** VDMTOOLS START Name=OmlClass KEEP=NO
  public OmlClass (final String p1, final Vector p2, final IOmlInheritanceClause p3, final Vector p4, final Boolean p5) throws CGException {

    try {

      ivIdentifier = UTIL.ConvertToString(new String());
      ivGenericTypes = new Vector();
      ivInheritanceClause = null;
      ivClassBody = new Vector();
      ivSystemSpec = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    {

      setIdentifier(p1);
      setGenericTypes(p2);
      setInheritanceClause((IOmlInheritanceClause) p3);
      setClassBody(p4);
      setSystemSpec(p5);
    }
  }
// ***** VDMTOOLS END Name=OmlClass


// ***** VDMTOOLS START Name=OmlClass KEEP=NO
  public OmlClass (final String p1, final Vector p2, final IOmlInheritanceClause p3, final Vector p4, final Boolean p5, final Long line, final Long column) throws CGException {

    try {

      ivIdentifier = UTIL.ConvertToString(new String());
      ivGenericTypes = new Vector();
      ivInheritanceClause = null;
      ivClassBody = new Vector();
      ivSystemSpec = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    {

      setIdentifier(p1);
      setGenericTypes(p2);
      setInheritanceClause((IOmlInheritanceClause) p3);
      setClassBody(p4);
      setSystemSpec(p5);
      setPosition(line, column);
    }
  }
// ***** VDMTOOLS END Name=OmlClass


// ***** VDMTOOLS START Name=init KEEP=NO
  public void init (final HashMap data) throws CGException {

    {

      String fname = new String("identifier");
      Boolean cond_4 = null;
      cond_4 = new Boolean(data.containsKey(fname));
      if (cond_4.booleanValue()) 
        setIdentifier(UTIL.ConvertToString(data.get(fname)));
    }
    {

      String fname = new String("generic_types");
      Boolean cond_13 = null;
      cond_13 = new Boolean(data.containsKey(fname));
      if (cond_13.booleanValue()) 
        setGenericTypes((Vector) data.get(fname));
    }
    {

      String fname = new String("inheritance_clause");
      Boolean cond_22 = null;
      cond_22 = new Boolean(data.containsKey(fname));
      if (cond_22.booleanValue()) 
        setInheritanceClause((IOmlInheritanceClause) data.get(fname));
    }
    {

      String fname = new String("class_body");
      Boolean cond_31 = null;
      cond_31 = new Boolean(data.containsKey(fname));
      if (cond_31.booleanValue()) 
        setClassBody((Vector) data.get(fname));
    }
    {

      String fname = new String("system_spec");
      Boolean cond_40 = null;
      cond_40 = new Boolean(data.containsKey(fname));
      if (cond_40.booleanValue()) 
        setSystemSpec((Boolean) data.get(fname));
    }
  }
// ***** VDMTOOLS END Name=init


// ***** VDMTOOLS START Name=getIdentifier KEEP=NO
  public String getIdentifier () throws CGException {
    return ivIdentifier;
  }
// ***** VDMTOOLS END Name=getIdentifier


// ***** VDMTOOLS START Name=setIdentifier KEEP=NO
  public void setIdentifier (final String parg) throws CGException {
    ivIdentifier = UTIL.ConvertToString(UTIL.clone(parg));
  }
// ***** VDMTOOLS END Name=setIdentifier


// ***** VDMTOOLS START Name=getGenericTypes KEEP=NO
  public Vector getGenericTypes () throws CGException {
    return ivGenericTypes;
  }
// ***** VDMTOOLS END Name=getGenericTypes


// ***** VDMTOOLS START Name=setGenericTypes KEEP=NO
  public void setGenericTypes (final Vector parg) throws CGException {
    ivGenericTypes = (Vector) UTIL.ConvertToList(UTIL.clone(parg));
  }
// ***** VDMTOOLS END Name=setGenericTypes


// ***** VDMTOOLS START Name=addGenericTypes KEEP=NO
  public void addGenericTypes (final IOmlNode parg) throws CGException {
    ivGenericTypes.add(parg);
  }
// ***** VDMTOOLS END Name=addGenericTypes


// ***** VDMTOOLS START Name=getInheritanceClause KEEP=NO
  public IOmlInheritanceClause getInheritanceClause () throws CGException {

    if (!this.pre_getInheritanceClause().booleanValue()) 
      UTIL.RunTime("Run-Time Error:Precondition failure in getInheritanceClause");
    return (IOmlInheritanceClause) ivInheritanceClause;
  }
// ***** VDMTOOLS END Name=getInheritanceClause


// ***** VDMTOOLS START Name=pre_getInheritanceClause KEEP=NO
  public Boolean pre_getInheritanceClause () throws CGException {
    return hasInheritanceClause();
  }
// ***** VDMTOOLS END Name=pre_getInheritanceClause


// ***** VDMTOOLS START Name=hasInheritanceClause KEEP=NO
  public Boolean hasInheritanceClause () throws CGException {
    return new Boolean(!UTIL.equals(ivInheritanceClause, null));
  }
// ***** VDMTOOLS END Name=hasInheritanceClause


// ***** VDMTOOLS START Name=setInheritanceClause KEEP=NO
  public void setInheritanceClause (final IOmlInheritanceClause parg) throws CGException {
    ivInheritanceClause = (IOmlInheritanceClause) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setInheritanceClause


// ***** VDMTOOLS START Name=getClassBody KEEP=NO
  public Vector getClassBody () throws CGException {
    return ivClassBody;
  }
// ***** VDMTOOLS END Name=getClassBody


// ***** VDMTOOLS START Name=setClassBody KEEP=NO
  public void setClassBody (final Vector parg) throws CGException {
    ivClassBody = (Vector) UTIL.ConvertToList(UTIL.clone(parg));
  }
// ***** VDMTOOLS END Name=setClassBody


// ***** VDMTOOLS START Name=addClassBody KEEP=NO
  public void addClassBody (final IOmlNode parg) throws CGException {
    ivClassBody.add(parg);
  }
// ***** VDMTOOLS END Name=addClassBody


// ***** VDMTOOLS START Name=getSystemSpec KEEP=NO
  public Boolean getSystemSpec () throws CGException {
    return ivSystemSpec;
  }
// ***** VDMTOOLS END Name=getSystemSpec


// ***** VDMTOOLS START Name=setSystemSpec KEEP=NO
  public void setSystemSpec (final Boolean parg) throws CGException {
    ivSystemSpec = (Boolean) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setSystemSpec

}
;
