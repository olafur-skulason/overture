
package org.overture.codegen.vdm2systemc;

public class SystemcValueSemantics
{
	private SystemcFormat systemcFormat;
	private SystemcSettings systemcSettings;

	public SystemcValueSemantics(SystemcFormat systemcFormat)
	{
		this.systemcFormat = systemcFormat;
		this.systemcSettings = new SystemcSettings();
	}

	public SystemcSettings getSystemcSettings()
	{
		return systemcSettings;
	}
}
