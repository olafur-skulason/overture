


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
public abstract class IUmlMessage extends IUmlNode {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp


// ***** VDMTOOLS START Name=vdm_init_IUmlMessage KEEP=NO
  private void vdm_init_IUmlMessage () throws CGException {}
// ***** VDMTOOLS END Name=vdm_init_IUmlMessage


// ***** VDMTOOLS START Name=IUmlMessage KEEP=NO
  public IUmlMessage () throws CGException {
    vdm_init_IUmlMessage();
  }
// ***** VDMTOOLS END Name=IUmlMessage


// ***** VDMTOOLS START Name=getName KEEP=NO
  abstract public String getName () throws CGException ;
// ***** VDMTOOLS END Name=getName


// ***** VDMTOOLS START Name=getMessageKind KEEP=NO
  abstract public IUmlMessageKind getMessageKind () throws CGException ;
// ***** VDMTOOLS END Name=getMessageKind


// ***** VDMTOOLS START Name=getMessageSort KEEP=NO
  abstract public IUmlMessageSort getMessageSort () throws CGException ;
// ***** VDMTOOLS END Name=getMessageSort


// ***** VDMTOOLS START Name=getSendEvent KEEP=NO
  abstract public IUmlMos getSendEvent () throws CGException ;
// ***** VDMTOOLS END Name=getSendEvent


// ***** VDMTOOLS START Name=getSendReceive KEEP=NO
  abstract public IUmlMos getSendReceive () throws CGException ;
// ***** VDMTOOLS END Name=getSendReceive


// ***** VDMTOOLS START Name=getArgument KEEP=NO
  abstract public Vector getArgument () throws CGException ;
// ***** VDMTOOLS END Name=getArgument

}
;