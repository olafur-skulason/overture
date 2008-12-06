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



public class OmlExternals extends OmlNode implements IOmlExternals {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=ivExtList KEEP=NO
  private Vector ivExtList = null;
// ***** VDMTOOLS END Name=ivExtList


// ***** VDMTOOLS START Name=OmlExternals KEEP=NO
  public OmlExternals () throws CGException {
    try {
      ivExtList = new Vector();
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=OmlExternals


// ***** VDMTOOLS START Name=identity KEEP=NO
  public String identity () throws CGException {
    return new String("Externals");
  }
// ***** VDMTOOLS END Name=identity


// ***** VDMTOOLS START Name=accept KEEP=NO
  public void accept (final IOmlVisitor pVisitor) throws CGException {
    pVisitor.visitExternals((IOmlExternals) this);
  }
// ***** VDMTOOLS END Name=accept


// ***** VDMTOOLS START Name=OmlExternals KEEP=NO
  public OmlExternals (final Vector p1) throws CGException {

    try {
      ivExtList = new Vector();
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    setExtList(p1);
  }
// ***** VDMTOOLS END Name=OmlExternals


// ***** VDMTOOLS START Name=OmlExternals KEEP=NO
  public OmlExternals (final Vector p1, final Long line, final Long column) throws CGException {

    try {
      ivExtList = new Vector();
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    {

      setExtList(p1);
      setPosition(line, column);
    }
  }
// ***** VDMTOOLS END Name=OmlExternals


// ***** VDMTOOLS START Name=init KEEP=NO
  public void init (final HashMap data) throws CGException {

    String fname = new String("ext_list");
    Boolean cond_4 = null;
    cond_4 = new Boolean(data.containsKey(fname));
    if (cond_4.booleanValue()) 
      setExtList((Vector) data.get(fname));
  }
// ***** VDMTOOLS END Name=init


// ***** VDMTOOLS START Name=getExtList KEEP=NO
  public Vector getExtList () throws CGException {
    return ivExtList;
  }
// ***** VDMTOOLS END Name=getExtList


// ***** VDMTOOLS START Name=setExtList KEEP=NO
  public void setExtList (final Vector parg) throws CGException {
    ivExtList = (Vector) UTIL.ConvertToList(UTIL.clone(parg));
  }
// ***** VDMTOOLS END Name=setExtList


// ***** VDMTOOLS START Name=addExtList KEEP=NO
  public void addExtList (final IOmlNode parg) throws CGException {
    ivExtList.add(parg);
  }
// ***** VDMTOOLS END Name=addExtList

}
;
