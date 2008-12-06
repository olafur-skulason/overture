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
import org.overturetool.ast.itf.*;
// ***** VDMTOOLS END Name=imports



public class OmlScope extends OmlNode implements IOmlScope {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=val KEEP=NO
  private Long val = null;
// ***** VDMTOOLS END Name=val


// ***** VDMTOOLS START Name=OmlScope KEEP=NO
  public OmlScope () throws CGException {
    try {
      val = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=OmlScope


// ***** VDMTOOLS START Name=identity KEEP=NO
  public String identity () throws CGException {
    return new String("Scope");
  }
// ***** VDMTOOLS END Name=identity


// ***** VDMTOOLS START Name=accept KEEP=NO
  public void accept (final IOmlVisitor pVisitor) throws CGException {
    pVisitor.visitScope((IOmlScope) this);
  }
// ***** VDMTOOLS END Name=accept


// ***** VDMTOOLS START Name=OmlScope KEEP=NO
  public OmlScope (final Long pv) throws CGException {

    try {
      val = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    setValue(pv);
  }
// ***** VDMTOOLS END Name=OmlScope


// ***** VDMTOOLS START Name=OmlScope KEEP=NO
  public OmlScope (final Long pv, final Long pline, final Long pcolumn) throws CGException {

    try {
      val = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    {

      setValue(pv);
      setPosition(pline, pcolumn);
    }
  }
// ***** VDMTOOLS END Name=OmlScope


// ***** VDMTOOLS START Name=setValue KEEP=NO
  public void setValue (final Long pval) throws CGException {

    if (!this.pre_setValue(pval).booleanValue()) 
      UTIL.RunTime("Run-Time Error:Precondition failure in setValue");
    val = UTIL.NumberToLong(UTIL.clone(pval));
  }
// ***** VDMTOOLS END Name=setValue


// ***** VDMTOOLS START Name=pre_setValue KEEP=NO
  public Boolean pre_setValue (final Long pval) throws CGException {

    Boolean varRes_2 = null;
    {
      if ((varRes_2 = new Boolean(UTIL.equals(val, null))).booleanValue()) {

        Boolean var2_6 = null;
        var2_6 = OmlScopeQuotes.validQuote(pval);
        varRes_2 = var2_6;
      }
    }
    return varRes_2;
  }
// ***** VDMTOOLS END Name=pre_setValue


// ***** VDMTOOLS START Name=getValue KEEP=NO
  public Long getValue () throws CGException {

    if (!this.pre_getValue().booleanValue()) 
      UTIL.RunTime("Run-Time Error:Precondition failure in getValue");
    return val;
  }
// ***** VDMTOOLS END Name=getValue


// ***** VDMTOOLS START Name=pre_getValue KEEP=NO
  public Boolean pre_getValue () throws CGException {
    return new Boolean(!UTIL.equals(val, null));
  }
// ***** VDMTOOLS END Name=pre_getValue


// ***** VDMTOOLS START Name=getStringValue KEEP=NO
  public String getStringValue () throws CGException {

    if (!this.pre_getStringValue().booleanValue()) 
      UTIL.RunTime("Run-Time Error:Precondition failure in getStringValue");
    String rexpr_1 = null;
    rexpr_1 = OmlScopeQuotes.getQuoteName(val);
    return rexpr_1;
  }
// ***** VDMTOOLS END Name=getStringValue


// ***** VDMTOOLS START Name=pre_getStringValue KEEP=NO
  public Boolean pre_getStringValue () throws CGException {
    return new Boolean(!UTIL.equals(val, null));
  }
// ***** VDMTOOLS END Name=pre_getStringValue

}
;
