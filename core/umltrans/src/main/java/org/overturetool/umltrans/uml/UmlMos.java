


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
public class UmlMos extends IUmlMos {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=ivName KEEP=NO
  private String ivName = null;
// ***** VDMTOOLS END Name=ivName

// ***** VDMTOOLS START Name=ivMessage KEEP=NO
  private IUmlMessage ivMessage = null;
// ***** VDMTOOLS END Name=ivMessage

// ***** VDMTOOLS START Name=ivCovered KEEP=NO
  private IUmlLifeLine ivCovered = null;
// ***** VDMTOOLS END Name=ivCovered

// ***** VDMTOOLS START Name=ivEvent KEEP=NO
  private IUmlCallEvent ivEvent = null;
// ***** VDMTOOLS END Name=ivEvent


// ***** VDMTOOLS START Name=vdm_init_UmlMos KEEP=NO
  private void vdm_init_UmlMos () throws CGException {
    try {

      ivName = UTIL.ConvertToString(new String());
      ivMessage = null;
      ivCovered = null;
      ivEvent = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=vdm_init_UmlMos


// ***** VDMTOOLS START Name=UmlMos KEEP=NO
  public UmlMos () throws CGException {
    vdm_init_UmlMos();
  }
// ***** VDMTOOLS END Name=UmlMos


// ***** VDMTOOLS START Name=identity KEEP=NO
  public String identity () throws CGException {
    return new String("Mos");
  }
// ***** VDMTOOLS END Name=identity


// ***** VDMTOOLS START Name=accept#1|IUmlVisitor KEEP=NO
  public void accept (final IUmlVisitor pVisitor) throws CGException {
    pVisitor.visitMos((IUmlMos) this);
  }
// ***** VDMTOOLS END Name=accept#1|IUmlVisitor


// ***** VDMTOOLS START Name=UmlMos#4|String|IUmlMessage|IUmlLifeLine|IUmlCallEvent KEEP=NO
  public UmlMos (final String p1, final IUmlMessage p2, final IUmlLifeLine p3, final IUmlCallEvent p4) throws CGException {

    vdm_init_UmlMos();
    {

      setName(p1);
      setMessage((IUmlMessage) p2);
      setCovered((IUmlLifeLine) p3);
      setEvent((IUmlCallEvent) p4);
    }
  }
// ***** VDMTOOLS END Name=UmlMos#4|String|IUmlMessage|IUmlLifeLine|IUmlCallEvent


// ***** VDMTOOLS START Name=UmlMos#6|String|IUmlMessage|IUmlLifeLine|IUmlCallEvent|Long|Long KEEP=NO
  public UmlMos (final String p1, final IUmlMessage p2, final IUmlLifeLine p3, final IUmlCallEvent p4, final Long line, final Long column) throws CGException {

    vdm_init_UmlMos();
    {

      setName(p1);
      setMessage((IUmlMessage) p2);
      setCovered((IUmlLifeLine) p3);
      setEvent((IUmlCallEvent) p4);
      setPosition(line, column);
    }
  }
// ***** VDMTOOLS END Name=UmlMos#6|String|IUmlMessage|IUmlLifeLine|IUmlCallEvent|Long|Long


// ***** VDMTOOLS START Name=init#1|HashMap KEEP=NO
  public void init (final HashMap data) throws CGException {

    {

      String fname = new String("name");
      Boolean cond_4 = null;
      cond_4 = new Boolean(data.containsKey(fname));
      if (cond_4.booleanValue()) 
        setName(UTIL.ConvertToString(data.get(fname)));
    }
    {

      String fname = new String("message");
      Boolean cond_13 = null;
      cond_13 = new Boolean(data.containsKey(fname));
      if (cond_13.booleanValue()) 
        setMessage((IUmlMessage) data.get(fname));
    }
    {

      String fname = new String("covered");
      Boolean cond_22 = null;
      cond_22 = new Boolean(data.containsKey(fname));
      if (cond_22.booleanValue()) 
        setCovered((IUmlLifeLine) data.get(fname));
    }
    {

      String fname = new String("event");
      Boolean cond_31 = null;
      cond_31 = new Boolean(data.containsKey(fname));
      if (cond_31.booleanValue()) 
        setEvent((IUmlCallEvent) data.get(fname));
    }
  }
// ***** VDMTOOLS END Name=init#1|HashMap


// ***** VDMTOOLS START Name=getName KEEP=NO
  public String getName () throws CGException {
    return ivName;
  }
// ***** VDMTOOLS END Name=getName


// ***** VDMTOOLS START Name=setName#1|String KEEP=NO
  public void setName (final String parg) throws CGException {
    ivName = UTIL.ConvertToString(UTIL.clone(parg));
  }
// ***** VDMTOOLS END Name=setName#1|String


// ***** VDMTOOLS START Name=getMessage KEEP=NO
  public IUmlMessage getMessage () throws CGException {
    return (IUmlMessage) ivMessage;
  }
// ***** VDMTOOLS END Name=getMessage


// ***** VDMTOOLS START Name=hasMessage KEEP=NO
  public Boolean hasMessage () throws CGException {
    return new Boolean(!UTIL.equals(ivMessage, null));
  }
// ***** VDMTOOLS END Name=hasMessage


// ***** VDMTOOLS START Name=setMessage#1|IUmlMessage KEEP=NO
  public void setMessage (final IUmlMessage parg) throws CGException {
    ivMessage = (IUmlMessage) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setMessage#1|IUmlMessage


// ***** VDMTOOLS START Name=getCovered KEEP=NO
  public IUmlLifeLine getCovered () throws CGException {
    return (IUmlLifeLine) ivCovered;
  }
// ***** VDMTOOLS END Name=getCovered


// ***** VDMTOOLS START Name=setCovered#1|IUmlLifeLine KEEP=NO
  public void setCovered (final IUmlLifeLine parg) throws CGException {
    ivCovered = (IUmlLifeLine) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setCovered#1|IUmlLifeLine


// ***** VDMTOOLS START Name=getEvent KEEP=NO
  public IUmlCallEvent getEvent () throws CGException {
    return (IUmlCallEvent) ivEvent;
  }
// ***** VDMTOOLS END Name=getEvent


// ***** VDMTOOLS START Name=hasEvent KEEP=NO
  public Boolean hasEvent () throws CGException {
    return new Boolean(!UTIL.equals(ivEvent, null));
  }
// ***** VDMTOOLS END Name=hasEvent


// ***** VDMTOOLS START Name=setEvent#1|IUmlCallEvent KEEP=NO
  public void setEvent (final IUmlCallEvent parg) throws CGException {
    ivEvent = (IUmlCallEvent) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setEvent#1|IUmlCallEvent

}
;