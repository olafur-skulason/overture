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



public class OmlScopeQuotes implements IOmlScopeQuotes {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp





// ***** VDMTOOLS START Name=qmap KEEP=NO
  private static HashMap qmap = new HashMap();
// ***** VDMTOOLS END Name=qmap


// ***** VDMTOOLS START Name=static KEEP=NO
  static {
    try {

      OmlScopeQuotes.qmap = new HashMap();
      OmlScopeQuotes.qmap.put(IQPROTECTED, new String("<PROTECTED>"));
      OmlScopeQuotes.qmap.put(IQPRIVATE, new String("<PRIVATE>"));
      OmlScopeQuotes.qmap.put(IQPUBLIC, new String("<PUBLIC>"));
      OmlScopeQuotes.qmap.put(IQDEFAULT, new String("<DEFAULT>"));
    }
    catch (Throwable e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=static


// ***** VDMTOOLS START Name=OmlScopeQuotes KEEP=NO
  public OmlScopeQuotes () throws CGException {}
// ***** VDMTOOLS END Name=OmlScopeQuotes


// ***** VDMTOOLS START Name=getQuoteName KEEP=NO
  static public String getQuoteName (final Long pid) throws CGException {

    if (!pre_getQuoteName(pid).booleanValue()) 
      UTIL.RunTime("Run-Time Error:Precondition failure in getQuoteName");
    return UTIL.ConvertToString(qmap.get(pid));
  }
// ***** VDMTOOLS END Name=getQuoteName


// ***** VDMTOOLS START Name=pre_getQuoteName KEEP=NO
  static public Boolean pre_getQuoteName (final Long pid) throws CGException {
    return validQuote(pid);
  }
// ***** VDMTOOLS END Name=pre_getQuoteName


// ***** VDMTOOLS START Name=validQuote KEEP=NO
  static public Boolean validQuote (final Long pid) throws CGException {

    Boolean rexpr_2 = null;
    rexpr_2 = new Boolean(qmap.containsKey(pid));
    return rexpr_2;
  }
// ***** VDMTOOLS END Name=validQuote

}
;
