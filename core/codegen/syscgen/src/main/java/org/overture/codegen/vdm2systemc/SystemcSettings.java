package org.overture.codegen.vdm2systemc;

public class SystemcSettings
{
	private boolean printVdmLocations;

	public SystemcSettings()
	{
		this.printVdmLocations = false;
	}

	public boolean printVdmLocations()
	{
		return printVdmLocations;
	}
}
