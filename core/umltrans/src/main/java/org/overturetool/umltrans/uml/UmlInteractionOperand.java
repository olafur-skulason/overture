


//
// THIS FILE IS AUTOMATICALLY GENERATED!!
//
// Generated at 2009-11-27 by the VDM++ to JAVA Code Generator
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
public class UmlInteractionOperand extends IUmlInteractionOperand {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=ivName KEEP=NO
  private String ivName = null;
// ***** VDMTOOLS END Name=ivName

// ***** VDMTOOLS START Name=ivFragments KEEP=NO
  private Vector ivFragments = null;
// ***** VDMTOOLS END Name=ivFragments

// ***** VDMTOOLS START Name=ivCovered KEEP=NO
  private HashSet ivCovered = new HashSet();
// ***** VDMTOOLS END Name=ivCovered

// ***** VDMTOOLS START Name=ivGuard KEEP=NO
  private IUmlInteractionConstraint ivGuard = null;
// ***** VDMTOOLS END Name=ivGuard


// ***** VDMTOOLS START Name=vdm_init_UmlInteractionOperand KEEP=NO
  private void vdm_init_UmlInteractionOperand () throws CGException {
    try {

      ivName = UTIL.ConvertToString(new String());
      ivFragments = new Vector();
      ivCovered = new HashSet();
      ivGuard = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=vdm_init_UmlInteractionOperand


// ***** VDMTOOLS START Name=UmlInteractionOperand KEEP=NO
  public UmlInteractionOperand () throws CGException {
    vdm_init_UmlInteractionOperand();
  }
// ***** VDMTOOLS END Name=UmlInteractionOperand


// ***** VDMTOOLS START Name=identity KEEP=NO
  public String identity () throws CGException {
    return new String("InteractionOperand");
  }
// ***** VDMTOOLS END Name=identity


// ***** VDMTOOLS START Name=accept#1|IUmlVisitor KEEP=NO
  public void accept (final IUmlVisitor pVisitor) throws CGException {
    pVisitor.visitInteractionOperand((IUmlInteractionOperand) this);
  }
// ***** VDMTOOLS END Name=accept#1|IUmlVisitor


// ***** VDMTOOLS START Name=UmlInteractionOperand#4|String|Vector|HashSet|IUmlInteractionConstraint KEEP=NO
  public UmlInteractionOperand (final String p1, final Vector p2, final HashSet p3, final IUmlInteractionConstraint p4) throws CGException {

    vdm_init_UmlInteractionOperand();
    {

      setName(p1);
      setFragments(p2);
      setCovered(p3);
      setGuard((IUmlInteractionConstraint) p4);
    }
  }
// ***** VDMTOOLS END Name=UmlInteractionOperand#4|String|Vector|HashSet|IUmlInteractionConstraint


// ***** VDMTOOLS START Name=UmlInteractionOperand#6|String|Vector|HashSet|IUmlInteractionConstraint|Long|Long KEEP=NO
  public UmlInteractionOperand (final String p1, final Vector p2, final HashSet p3, final IUmlInteractionConstraint p4, final Long line, final Long column) throws CGException {

    vdm_init_UmlInteractionOperand();
    {

      setName(p1);
      setFragments(p2);
      setCovered(p3);
      setGuard((IUmlInteractionConstraint) p4);
      setPosition(line, column);
    }
  }
// ***** VDMTOOLS END Name=UmlInteractionOperand#6|String|Vector|HashSet|IUmlInteractionConstraint|Long|Long


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

      String fname = new String("fragments");
      Boolean cond_13 = null;
      cond_13 = new Boolean(data.containsKey(fname));
      if (cond_13.booleanValue()) 
        setFragments((Vector) data.get(fname));
    }
    {

      String fname = new String("covered");
      Boolean cond_22 = null;
      cond_22 = new Boolean(data.containsKey(fname));
      if (cond_22.booleanValue()) 
        setCovered((HashSet) data.get(fname));
    }
    {

      String fname = new String("guard");
      Boolean cond_31 = null;
      cond_31 = new Boolean(data.containsKey(fname));
      if (cond_31.booleanValue()) 
        setGuard((IUmlInteractionConstraint) data.get(fname));
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


// ***** VDMTOOLS START Name=getFragments KEEP=NO
  public Vector getFragments () throws CGException {
    return ivFragments;
  }
// ***** VDMTOOLS END Name=getFragments


// ***** VDMTOOLS START Name=setFragments#1|Vector KEEP=NO
  public void setFragments (final Vector parg) throws CGException {
    ivFragments = (Vector) UTIL.ConvertToList(UTIL.clone(parg));
  }
// ***** VDMTOOLS END Name=setFragments#1|Vector


// ***** VDMTOOLS START Name=addFragments#1|IUmlNode KEEP=NO
  public void addFragments (final IUmlNode parg) throws CGException {
    ivFragments.add(parg);
  }
// ***** VDMTOOLS END Name=addFragments#1|IUmlNode


// ***** VDMTOOLS START Name=getCovered KEEP=NO
  public HashSet getCovered () throws CGException {
    return ivCovered;
  }
// ***** VDMTOOLS END Name=getCovered


// ***** VDMTOOLS START Name=setCovered#1|HashSet KEEP=NO
  public void setCovered (final HashSet parg) throws CGException {
    ivCovered = (HashSet) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setCovered#1|HashSet


// ***** VDMTOOLS START Name=addCovered#1|IUmlNode KEEP=NO
  public void addCovered (final IUmlNode parg) throws CGException {
    ivCovered.add(parg);
  }
// ***** VDMTOOLS END Name=addCovered#1|IUmlNode


// ***** VDMTOOLS START Name=getGuard KEEP=NO
  public IUmlInteractionConstraint getGuard () throws CGException {
    return (IUmlInteractionConstraint) ivGuard;
  }
// ***** VDMTOOLS END Name=getGuard


// ***** VDMTOOLS START Name=hasGuard KEEP=NO
  public Boolean hasGuard () throws CGException {
    return new Boolean(!UTIL.equals(ivGuard, null));
  }
// ***** VDMTOOLS END Name=hasGuard


// ***** VDMTOOLS START Name=setGuard#1|IUmlInteractionConstraint KEEP=NO
  public void setGuard (final IUmlInteractionConstraint parg) throws CGException {
    ivGuard = (IUmlInteractionConstraint) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setGuard#1|IUmlInteractionConstraint

}
;