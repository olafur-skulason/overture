


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
public class XmlEntity {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=name KEEP=NO
  public String name = null;
// ***** VDMTOOLS END Name=name

// ***** VDMTOOLS START Name=attributes KEEP=NO
  public XmlAttributeList attributes = null;
// ***** VDMTOOLS END Name=attributes

// ***** VDMTOOLS START Name=entities KEEP=NO
  public XmlEntityList entities = null;
// ***** VDMTOOLS END Name=entities

// ***** VDMTOOLS START Name=data KEEP=NO
  public XmlData data = null;
// ***** VDMTOOLS END Name=data


// ***** VDMTOOLS START Name=vdm_init_XmlEntity KEEP=NO
  private void vdm_init_XmlEntity () throws CGException {
    try {

      name = new String("");
      attributes = (XmlAttributeList) new XmlAttributeList();
      entities = (XmlEntityList) new XmlEntityList();
      data = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=vdm_init_XmlEntity


// ***** VDMTOOLS START Name=XmlEntity KEEP=NO
  public XmlEntity () throws CGException {
    vdm_init_XmlEntity();
  }
// ***** VDMTOOLS END Name=XmlEntity


// ***** VDMTOOLS START Name=XmlEntity#1|String KEEP=NO
  public XmlEntity (final String pname) throws CGException {

    vdm_init_XmlEntity();
    {

      name = UTIL.ConvertToString(UTIL.clone(pname));
      data = null;
    }
  }
// ***** VDMTOOLS END Name=XmlEntity#1|String


// ***** VDMTOOLS START Name=addAttribute#1|XmlAttribute KEEP=NO
  public void addAttribute (final XmlAttribute pattr) throws CGException {
    attributes.addTail((XmlAttribute) pattr);
  }
// ***** VDMTOOLS END Name=addAttribute#1|XmlAttribute


// ***** VDMTOOLS START Name=addEntity#1|XmlEntity KEEP=NO
  public void addEntity (final XmlEntity pent) throws CGException {
    entities.addTail((XmlEntity) pent);
  }
// ***** VDMTOOLS END Name=addEntity#1|XmlEntity


// ***** VDMTOOLS START Name=addData#1|XmlData KEEP=NO
  public void addData (final XmlData pdata) throws CGException {
    data = (XmlData) UTIL.clone(pdata);
  }
// ***** VDMTOOLS END Name=addData#1|XmlData


// ***** VDMTOOLS START Name=accept#1|XmlVisitor KEEP=NO
  public void accept (final XmlVisitor pxv) throws CGException {
    pxv.VisitXmlEntity((XmlEntity) this);
  }
// ***** VDMTOOLS END Name=accept#1|XmlVisitor

}
;