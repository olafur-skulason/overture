


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
package org.overturetool.api.xml;

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
public abstract class XmlEvent {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=estack KEEP=NO
  private Vector estack = null;
// ***** VDMTOOLS END Name=estack


// ***** VDMTOOLS START Name=vdm_init_XmlEvent KEEP=NO
  private void vdm_init_XmlEvent () throws CGException {
    try {
      estack = new Vector();
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=vdm_init_XmlEvent


// ***** VDMTOOLS START Name=XmlEvent KEEP=NO
  public XmlEvent () throws CGException {
    vdm_init_XmlEvent();
  }
// ***** VDMTOOLS END Name=XmlEvent


// ***** VDMTOOLS START Name=pushEntity#1|XmlEntity KEEP=NO
  protected void pushEntity (final XmlEntity ppe) throws CGException {

    Vector rhs_2 = null;
    Vector var1_3 = null;
    var1_3 = new Vector();
    var1_3.add(ppe);
    rhs_2 = (Vector) var1_3.clone();
    rhs_2.addAll(estack);
    estack = (Vector) UTIL.ConvertToList(UTIL.clone(rhs_2));
  }
// ***** VDMTOOLS END Name=pushEntity#1|XmlEntity


// ***** VDMTOOLS START Name=popEntity KEEP=NO
  protected XmlEntity popEntity () throws CGException {

    XmlEntity res = (XmlEntity) estack.get(0);
    estack = (Vector) UTIL.ConvertToList(UTIL.clone(new Vector(estack.subList(1, estack.size()))));
    return (XmlEntity) res;
  }
// ***** VDMTOOLS END Name=popEntity


// ***** VDMTOOLS START Name=peekEntity KEEP=NO
  protected XmlEntity peekEntity () throws CGException {
    return (XmlEntity) (XmlEntity) estack.get(0);
  }
// ***** VDMTOOLS END Name=peekEntity


// ***** VDMTOOLS START Name=stackDepth KEEP=NO
  protected Long stackDepth () throws CGException {
    return new Long(estack.size());
  }
// ***** VDMTOOLS END Name=stackDepth


// ***** VDMTOOLS START Name=StartDocument KEEP=NO
  abstract protected Boolean StartDocument () throws CGException ;
// ***** VDMTOOLS END Name=StartDocument


// ***** VDMTOOLS START Name=StopDocument KEEP=NO
  abstract protected Boolean StopDocument () throws CGException ;
// ***** VDMTOOLS END Name=StopDocument


// ***** VDMTOOLS START Name=StartEntity#1|String KEEP=NO
  abstract protected Boolean StartEntity (final String var_1_1) throws CGException ;
// ***** VDMTOOLS END Name=StartEntity#1|String


// ***** VDMTOOLS START Name=StopEntity#1|String KEEP=NO
  abstract protected Boolean StopEntity (final String var_1_1) throws CGException ;
// ***** VDMTOOLS END Name=StopEntity#1|String


// ***** VDMTOOLS START Name=StartAttribute#2|String|String KEEP=NO
  abstract protected Boolean StartAttribute (final String var_1_1, final String var_2_2) throws CGException ;
// ***** VDMTOOLS END Name=StartAttribute#2|String|String


// ***** VDMTOOLS START Name=StartData#1|String KEEP=NO
  abstract protected Boolean StartData (final String var_1_1) throws CGException ;
// ***** VDMTOOLS END Name=StartData#1|String


// ***** VDMTOOLS START Name=parse#1|String KEEP=NO
  public void parse (final String fname) throws CGException {

    XmlParser parser = new XmlParser(fname, this);
    parser.parse();
  }
// ***** VDMTOOLS END Name=parse#1|String

}
;