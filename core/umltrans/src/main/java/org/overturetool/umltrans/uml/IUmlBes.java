


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
public abstract class IUmlBes extends IUmlInteractionFragment {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp


// ***** VDMTOOLS START Name=vdm_init_IUmlBes KEEP=NO
  private void vdm_init_IUmlBes () throws CGException {}
// ***** VDMTOOLS END Name=vdm_init_IUmlBes


// ***** VDMTOOLS START Name=IUmlBes KEEP=NO
  public IUmlBes () throws CGException {
    vdm_init_IUmlBes();
  }
// ***** VDMTOOLS END Name=IUmlBes


// ***** VDMTOOLS START Name=getName KEEP=NO
  abstract public String getName () throws CGException ;
// ***** VDMTOOLS END Name=getName


// ***** VDMTOOLS START Name=getStartOs KEEP=NO
  abstract public IUmlMos getStartOs () throws CGException ;
// ***** VDMTOOLS END Name=getStartOs


// ***** VDMTOOLS START Name=getFinishOs KEEP=NO
  abstract public IUmlMos getFinishOs () throws CGException ;
// ***** VDMTOOLS END Name=getFinishOs


// ***** VDMTOOLS START Name=getCovered KEEP=NO
  abstract public HashSet getCovered () throws CGException ;
// ***** VDMTOOLS END Name=getCovered

}
;