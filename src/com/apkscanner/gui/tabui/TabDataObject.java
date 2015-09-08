package com.apkscanner.gui.tabui;

import com.apkscanner.core.ApktoolManager.ApkInfo;

public abstract interface TabDataObject
{
	public void initialize();
	public void setData(ApkInfo apkInfo);
	public void reloadResource();
}
