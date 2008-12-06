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



public class OmlMaplet extends OmlNode implements IOmlMaplet {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=ivDomExpression KEEP=NO
  private IOmlExpression ivDomExpression = null;
// ***** VDMTOOLS END Name=ivDomExpression

// ***** VDMTOOLS START Name=ivRngExpression KEEP=NO
  private IOmlExpression ivRngExpression = null;
// ***** VDMTOOLS END Name=ivRngExpression


// ***** VDMTOOLS START Name=OmlMaplet KEEP=NO
  public OmlMaplet () throws CGException {
    try {

      ivDomExpression = null;
      ivRngExpression = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=OmlMaplet


// ***** VDMTOOLS START Name=identity KEEP=NO
  public String identity () throws CGException {
    return new String("Maplet");
  }
// ***** VDMTOOLS END Name=identity


// ***** VDMTOOLS START Name=accept KEEP=NO
  public void accept (final IOmlVisitor pVisitor) throws CGException {
    pVisitor.visitMaplet((IOmlMaplet) this);
  }
// ***** VDMTOOLS END Name=accept


// ***** VDMTOOLS START Name=OmlMaplet KEEP=NO
  public OmlMaplet (final IOmlExpression p1, final IOmlExpression p2) throws CGException {

    try {

      ivDomExpression = null;
      ivRngExpression = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    {

      setDomExpression((IOmlExpression) p1);
      setRngExpression((IOmlExpression) p2);
    }
  }
// ***** VDMTOOLS END Name=OmlMaplet


// ***** VDMTOOLS START Name=OmlMaplet KEEP=NO
  public OmlMaplet (final IOmlExpression p1, final IOmlExpression p2, final Long line, final Long column) throws CGException {

    try {

      ivDomExpression = null;
      ivRngExpression = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    {

      setDomExpression((IOmlExpression) p1);
      setRngExpression((IOmlExpression) p2);
      setPosition(line, column);
    }
  }
// ***** VDMTOOLS END Name=OmlMaplet


// ***** VDMTOOLS START Name=init KEEP=NO
  public void init (final HashMap data) throws CGException {

    {

      String fname = new String("dom_expression");
      Boolean cond_4 = null;
      cond_4 = new Boolean(data.containsKey(fname));
      if (cond_4.booleanValue()) 
        setDomExpression((IOmlExpression) data.get(fname));
    }
    {

      String fname = new String("rng_expression");
      Boolean cond_13 = null;
      cond_13 = new Boolean(data.containsKey(fname));
      if (cond_13.booleanValue()) 
        setRngExpression((IOmlExpression) data.get(fname));
    }
  }
// ***** VDMTOOLS END Name=init


// ***** VDMTOOLS START Name=getDomExpression KEEP=NO
  public IOmlExpression getDomExpression () throws CGException {
    return (IOmlExpression) ivDomExpression;
  }
// ***** VDMTOOLS END Name=getDomExpression


// ***** VDMTOOLS START Name=setDomExpression KEEP=NO
  public void setDomExpression (final IOmlExpression parg) throws CGException {
    ivDomExpression = (IOmlExpression) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setDomExpression


// ***** VDMTOOLS START Name=getRngExpression KEEP=NO
  public IOmlExpression getRngExpression () throws CGException {
    return (IOmlExpression) ivRngExpression;
  }
// ***** VDMTOOLS END Name=getRngExpression


// ***** VDMTOOLS START Name=setRngExpression KEEP=NO
  public void setRngExpression (final IOmlExpression parg) throws CGException {
    ivRngExpression = (IOmlExpression) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setRngExpression

}
;
