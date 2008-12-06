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



public class OmlTraceDefinitionItem extends OmlTraceDefinition implements IOmlTraceDefinitionItem {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=ivBind KEEP=NO
  private Vector ivBind = null;
// ***** VDMTOOLS END Name=ivBind

// ***** VDMTOOLS START Name=ivTest KEEP=NO
  private IOmlTraceCoreDefinition ivTest = null;
// ***** VDMTOOLS END Name=ivTest

// ***** VDMTOOLS START Name=ivRegexpr KEEP=NO
  private IOmlTraceRepeatPattern ivRegexpr = null;
// ***** VDMTOOLS END Name=ivRegexpr


// ***** VDMTOOLS START Name=OmlTraceDefinitionItem KEEP=NO
  public OmlTraceDefinitionItem () throws CGException {
    try {

      ivBind = new Vector();
      ivTest = null;
      ivRegexpr = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=OmlTraceDefinitionItem


// ***** VDMTOOLS START Name=identity KEEP=NO
  public String identity () throws CGException {
    return new String("TraceDefinitionItem");
  }
// ***** VDMTOOLS END Name=identity


// ***** VDMTOOLS START Name=accept KEEP=NO
  public void accept (final IOmlVisitor pVisitor) throws CGException {
    pVisitor.visitTraceDefinitionItem((IOmlTraceDefinitionItem) this);
  }
// ***** VDMTOOLS END Name=accept


// ***** VDMTOOLS START Name=OmlTraceDefinitionItem KEEP=NO
  public OmlTraceDefinitionItem (final Vector p1, final IOmlTraceCoreDefinition p2, final IOmlTraceRepeatPattern p3) throws CGException {

    try {

      ivBind = new Vector();
      ivTest = null;
      ivRegexpr = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    {

      setBind(p1);
      setTest((IOmlTraceCoreDefinition) p2);
      setRegexpr((IOmlTraceRepeatPattern) p3);
    }
  }
// ***** VDMTOOLS END Name=OmlTraceDefinitionItem


// ***** VDMTOOLS START Name=OmlTraceDefinitionItem KEEP=NO
  public OmlTraceDefinitionItem (final Vector p1, final IOmlTraceCoreDefinition p2, final IOmlTraceRepeatPattern p3, final Long line, final Long column) throws CGException {

    try {

      ivBind = new Vector();
      ivTest = null;
      ivRegexpr = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    {

      setBind(p1);
      setTest((IOmlTraceCoreDefinition) p2);
      setRegexpr((IOmlTraceRepeatPattern) p3);
      setPosition(line, column);
    }
  }
// ***** VDMTOOLS END Name=OmlTraceDefinitionItem


// ***** VDMTOOLS START Name=init KEEP=NO
  public void init (final HashMap data) throws CGException {

    {

      String fname = new String("bind");
      Boolean cond_4 = null;
      cond_4 = new Boolean(data.containsKey(fname));
      if (cond_4.booleanValue()) 
        setBind((Vector) data.get(fname));
    }
    {

      String fname = new String("test");
      Boolean cond_13 = null;
      cond_13 = new Boolean(data.containsKey(fname));
      if (cond_13.booleanValue()) 
        setTest((IOmlTraceCoreDefinition) data.get(fname));
    }
    {

      String fname = new String("regexpr");
      Boolean cond_22 = null;
      cond_22 = new Boolean(data.containsKey(fname));
      if (cond_22.booleanValue()) 
        setRegexpr((IOmlTraceRepeatPattern) data.get(fname));
    }
  }
// ***** VDMTOOLS END Name=init


// ***** VDMTOOLS START Name=getBind KEEP=NO
  public Vector getBind () throws CGException {
    return ivBind;
  }
// ***** VDMTOOLS END Name=getBind


// ***** VDMTOOLS START Name=setBind KEEP=NO
  public void setBind (final Vector parg) throws CGException {
    ivBind = (Vector) UTIL.ConvertToList(UTIL.clone(parg));
  }
// ***** VDMTOOLS END Name=setBind


// ***** VDMTOOLS START Name=addBind KEEP=NO
  public void addBind (final IOmlNode parg) throws CGException {
    ivBind.add(parg);
  }
// ***** VDMTOOLS END Name=addBind


// ***** VDMTOOLS START Name=getTest KEEP=NO
  public IOmlTraceCoreDefinition getTest () throws CGException {
    return (IOmlTraceCoreDefinition) ivTest;
  }
// ***** VDMTOOLS END Name=getTest


// ***** VDMTOOLS START Name=setTest KEEP=NO
  public void setTest (final IOmlTraceCoreDefinition parg) throws CGException {
    ivTest = (IOmlTraceCoreDefinition) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setTest


// ***** VDMTOOLS START Name=getRegexpr KEEP=NO
  public IOmlTraceRepeatPattern getRegexpr () throws CGException {

    if (!this.pre_getRegexpr().booleanValue()) 
      UTIL.RunTime("Run-Time Error:Precondition failure in getRegexpr");
    return (IOmlTraceRepeatPattern) ivRegexpr;
  }
// ***** VDMTOOLS END Name=getRegexpr


// ***** VDMTOOLS START Name=pre_getRegexpr KEEP=NO
  public Boolean pre_getRegexpr () throws CGException {
    return hasRegexpr();
  }
// ***** VDMTOOLS END Name=pre_getRegexpr


// ***** VDMTOOLS START Name=hasRegexpr KEEP=NO
  public Boolean hasRegexpr () throws CGException {
    return new Boolean(!UTIL.equals(ivRegexpr, null));
  }
// ***** VDMTOOLS END Name=hasRegexpr


// ***** VDMTOOLS START Name=setRegexpr KEEP=NO
  public void setRegexpr (final IOmlTraceRepeatPattern parg) throws CGException {
    ivRegexpr = (IOmlTraceRepeatPattern) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setRegexpr

}
;
