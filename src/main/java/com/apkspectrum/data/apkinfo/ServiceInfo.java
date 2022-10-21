package com.apkspectrum.data.apkinfo;

public class ServiceInfo extends ComponentInfo
{
	public Boolean isolatedProcess = null;
	public String process = null; // "string"

	public String getReport()
	{
		StringBuilder report = new StringBuilder();
		report.append("name : " + name + "\n");
		if(enabled != null) report.append("enabled : " + enabled + "\n");
		if(exported != null) report.append("exported : " + exported + "\n");
		if(permission != null) report.append("permission : " + permission + "\n");

		if(intentFilter != null) {
			report.append("\nintent-filter count : " + intentFilter.length + "\n");
			for(IntentFilterInfo info: intentFilter) {
				report.append("intent-filter : \n");
				if(info.ation != null) {
					for(ActionInfo a: info.ation) {
						report.append("  " + a.name + "\n");
					}
				}
				if(info.category != null) {
					for(CategoryInfo c: info.category) {
						report.append("  " + c.name + "\n");
					}
				}
			}
		}

		return report.toString();
	}
}
