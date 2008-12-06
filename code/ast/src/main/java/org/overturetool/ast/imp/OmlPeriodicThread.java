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



public class OmlPeriodicThread extends OmlThreadSpecification implements IOmlPeriodicThread {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=ivArgs KEEP=NO
  private Vector ivArgs = null;
// ***** VDMTOOLS END Name=ivArgs

// ***** VDMTOOLS START Name=ivName KEEP=NO
  private IOmlName ivName = null;
// ***** VDMTOOLS END Name=ivName


// ***** VDMTOOLS START Name=OmlPeriodicThread KEEP=NO
  public OmlPeriodicThread () throws CGException {
    try {

      ivArgs = new Vector();
      ivName = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=OmlPeriodicThread


// ***** VDMTOOLS START Name=identity KEEP=NO
  public String identity () throws CGException {
    return new String("PeriodicThread");
  }
// ***** VDMTOOLS END Name=identity


// ***** VDMTOOLS START Name=accept KEEP=NO
  public void accept (final IOmlVisitor pVisitor) throws CGException {
    pVisitor.visitPeriodicThread((IOmlPeriodicThread) this);
  }
// ***** VDMTOOLS END Name=accept


// ***** VDMTOOLS START Name=OmlPeriodicThread KEEP=NO
  public OmlPeriodicThread (final Vector p1, final IOmlName p2) throws CGException {

    try {

      ivArgs = new Vector();
      ivName = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    {

      setArgs(p1);
      setName((IOmlName) p2);
    }
  }
// ***** VDMTOOLS END Name=OmlPeriodicThread


// ***** VDMTOOLS START Name=OmlPeriodicThread KEEP=NO
  public OmlPeriodicThread (final Vector p1, final IOmlName p2, final Long line, final Long column) throws CGException {

    try {

      ivArgs = new Vector();
      ivName = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    {

      setArgs(p1);
      setName((IOmlName) p2);
      setPosition(line, column);
    }
  }
// ***** VDMTOOLS END Name=OmlPeriodicThread


// ***** VDMTOOLS START Name=init KEEP=NO
  public void init (final HashMap data) throws CGException {

    {

      String fname = new String("args");
      Boolean cond_4 = null;
      cond_4 = new Boolean(data.containsKey(fname));
      if (cond_4.booleanValue()) 
        setArgs((Vector) data.get(fname));
    }
    {

      String fname = new String("name");
      Boolean cond_13 = null;
      cond_13 = new Boolean(data.containsKey(fname));
      if (cond_13.booleanValue()) 
        setName((IOmlName) data.get(fname));
    }
  }
// ***** VDMTOOLS END Name=init


// ***** VDMTOOLS START Name=getArgs KEEP=NO
  public Vector getArgs () throws CGException {
    return ivArgs;
  }
// ***** VDMTOOLS END Name=getArgs


// ***** VDMTOOLS START Name=setArgs KEEP=NO
  public void setArgs (final Vector parg) throws CGException {
    ivArgs = (Vector) UTIL.ConvertToList(UTIL.clone(parg));
  }
// ***** VDMTOOLS END Name=setArgs


// ***** VDMTOOLS START Name=addArgs KEEP=NO
  public void addArgs (final IOmlNode parg) throws CGException {
    ivArgs.add(parg);
  }
// ***** VDMTOOLS END Name=addArgs


// ***** VDMTOOLS START Name=getName KEEP=NO
  public IOmlName getName () throws CGException {
    return (IOmlName) ivName;
  }
// ***** VDMTOOLS END Name=getName


// ***** VDMTOOLS START Name=setName KEEP=NO
  public void setName (final IOmlName parg) throws CGException {
    ivName = (IOmlName) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setName

}
;
