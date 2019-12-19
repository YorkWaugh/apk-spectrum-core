package com.apkscanner.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import com.apkscanner.gui.tabpanels.ResourceObject;
import com.apkscanner.util.SystemUtil;

@SuppressWarnings("serial")
public class OpenResFileSystemSetAction extends AbstractApkScannerAction
{
	public static final String ACTION_COMMAND = "ACT_CMD_OPEN_RESOURCE_FILE_SYSTEM_SET";

	public OpenResFileSystemSetAction(ActionEventHandler h) { super(h); }

	@Override
	public void actionPerformed(ActionEvent e) {
		JComponent comp = (JComponent) e.getSource();
		ResourceObject resObj = (ResourceObject) comp.getClientProperty(ResourceObject.class);

		if(resObj== null || resObj.isFolder) return;

		String resPath = uncompressRes(resObj);
		SystemUtil.openFile(resPath);
	}
}
