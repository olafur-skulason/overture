package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlDclStatement extends IOmlStatement
{
	abstract Vector getDefinitionList() throws CGException;
}

