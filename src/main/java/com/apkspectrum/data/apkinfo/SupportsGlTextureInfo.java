package com.apkspectrum.data.apkinfo;

public class SupportsGlTextureInfo
{
	public String name = null; // "string"
	
	public String getReport()
	{
		StringBuilder report = new StringBuilder();
		
		report.append("supports gl texture : " + name + "\n");

		return report.toString();
	}
}
