


//
// THIS FILE IS AUTOMATICALLY GENERATED!!
//
// Generated at 2009-11-26 by the VDM++ to JAVA Code Generator
// (v8.2 - Fri 29-May-2009 11:13:11)
//
// Supported compilers: jdk 1.4/1.5/1.6
//

// ***** VDMTOOLS START Name=HeaderComment KEEP=NO
// ***** VDMTOOLS END Name=HeaderComment

// ***** VDMTOOLS START Name=package KEEP=NO
package org.overturetool.umltrans.uml;

// ***** VDMTOOLS END Name=package

// ***** VDMTOOLS START Name=imports KEEP=NO

import jp.co.csk.vdm.toolbox.VDM.*;
import java.util.*;
import org.overturetool.ast.itf.*;
import org.overturetool.ast.imp.*;
import org.overturetool.api.io.*;
import org.overturetool.api.io.*;
import org.overturetool.api.xml.*;
import org.overturetool.umltrans.api.*;
import org.overturetool.umltrans.*;
import org.overturetool.umltrans.uml.*;
import org.overturetool.umltrans.uml2vdm.*;
import org.overturetool.umltrans.vdm2uml.*;
// ***** VDMTOOLS END Name=imports



@SuppressWarnings({"all","unchecked","unused"})
public class UmlCallEvent extends IUmlCallEvent {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=ivOperation KEEP=NO
  private IUmlOperation ivOperation = null;
// ***** VDMTOOLS END Name=ivOperation


// ***** VDMTOOLS START Name=vdm_init_UmlCallEvent KEEP=NO
  private void vdm_init_UmlCallEvent () throws CGException {
    try {
      ivOperation = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=vdm_init_UmlCallEvent


// ***** VDMTOOLS START Name=UmlCallEvent KEEP=NO
  public UmlCallEvent () throws CGException {
    vdm_init_UmlCallEvent();
  }
// ***** VDMTOOLS END Name=UmlCallEvent


// ***** VDMTOOLS START Name=identity KEEP=NO
  public String identity () throws CGException {
    return new String("CallEvent");
  }
// ***** VDMTOOLS END Name=identity


// ***** VDMTOOLS START Name=accept#1|IUmlVisitor KEEP=NO
  public void accept (final IUmlVisitor pVisitor) throws CGException {
    pVisitor.visitCallEvent((IUmlCallEvent) this);
  }
// ***** VDMTOOLS END Name=accept#1|IUmlVisitor


// ***** VDMTOOLS START Name=UmlCallEvent#1|IUmlOperation KEEP=NO
  public UmlCallEvent (final IUmlOperation p1) throws CGException {

    vdm_init_UmlCallEvent();
    setOperation((IUmlOperation) p1);
  }
// ***** VDMTOOLS END Name=UmlCallEvent#1|IUmlOperation


// ***** VDMTOOLS START Name=UmlCallEvent#3|IUmlOperation|Long|Long KEEP=NO
  public UmlCallEvent (final IUmlOperation p1, final Long line, final Long column) throws CGException {

    vdm_init_UmlCallEvent();
    {

      setOperation((IUmlOperation) p1);
      setPosition(line, column);
    }
  }
// ***** VDMTOOLS END Name=UmlCallEvent#3|IUmlOperation|Long|Long


// ***** VDMTOOLS START Name=init#1|HashMap KEEP=NO
  public void init (final HashMap data) throws CGException {

    String fname = new String("operation");
    Boolean cond_4 = null;
    cond_4 = new Boolean(data.containsKey(fname));
    if (cond_4.booleanValue()) 
      setOperation((IUmlOperation) data.get(fname));
  }
// ***** VDMTOOLS END Name=init#1|HashMap


// ***** VDMTOOLS START Name=getOperation KEEP=NO
  public IUmlOperation getOperation () throws CGException {
    return (IUmlOperation) ivOperation;
  }
// ***** VDMTOOLS END Name=getOperation


// ***** VDMTOOLS START Name=setOperation#1|IUmlOperation KEEP=NO
  public void setOperation (final IUmlOperation parg) throws CGException {
    ivOperation = (IUmlOperation) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setOperation#1|IUmlOperation

}
;