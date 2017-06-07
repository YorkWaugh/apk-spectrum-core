package com.apkscanner.core.installer;

import java.util.ArrayList;

import com.android.ddmlib.IDevice;
import com.apkscanner.data.apkinfo.CompactApkInfo;
import com.apkscanner.resource.Resource;
import com.apkscanner.tool.adb.AdbDeviceHelper;
import com.apkscanner.tool.adb.PackageManager;
import com.apkscanner.util.Log;

public class ApkInstaller
{
	private IDevice device;

	public ApkInstaller() {
		this(null);	
	}

	public ApkInstaller(IDevice device) {
		setDevice(device);	
	}

	public void setDevice(IDevice device) {
		this.device = device;
	}

	public void install(final CompactApkInfo apkInfo, final OptionsBundle options) {
		install(device, apkInfo, options);
	}

	public static String install(final IDevice device, final CompactApkInfo apkInfo, final OptionsBundle options)
	{
		String errMessage = null;
		//Log.i("InstallApk() device : " + name + ", apkPath: " + apkPath);
		if(apkInfo == null || apkInfo.filePath == null || apkInfo.filePath.isEmpty()) {
			errMessage = "No such file: " + apkInfo.filePath;
			return errMessage;
		}

		if(options.isDontInstallOptions()) {
			errMessage = "Can not install";
		} else if(options.isNoInstallOptions()) {
			errMessage = "No install";
		}

		if(errMessage == null) {
			if(options.isInstallOptions()) {
				errMessage = installApk(device, apkInfo, options);
			} else if(options.isPushOptions()) {
				errMessage = pushApk(device, apkInfo, options);
			}
		}

		return errMessage;
	}

	public static String installApk(final IDevice device, final CompactApkInfo apkInfo, final OptionsBundle options) {
		String errMessage = null;

		boolean reinstall = options.isSetReplace();

		ArrayList<String> extraArgs = new ArrayList<String> ();
		if(options.isSetForwardLock()) {
			extraArgs.add("-l");
		}
		if(options.isSetAllowTestPackage()) {
			extraArgs.add("-t");
		}
		if(options.isSetOnSdcard()) {
			extraArgs.add("-s");
		}
		if(options.isSetDowngrade()) {
			extraArgs.add("-d");
		}
		if(options.isSetGrantPermissions()) {
			extraArgs.add("-g");
		}

		errMessage = PackageManager.installPackage(device, apkInfo.filePath, reinstall, extraArgs.isEmpty() ? null : extraArgs.toArray(new String[extraArgs.size()]));
		if(errMessage == null || errMessage.isEmpty()) {
			if(options.isSetLaunch()) {
				String activity = options.getLaunchActivity();
				if(activity != null) {
					String pacakgeName = apkInfo.packageName;
					if(pacakgeName != null) {
						String[] cmdResult = AdbDeviceHelper.launchActivity(device, pacakgeName + "/" + activity);
						if(cmdResult == null || (cmdResult.length >= 2 && cmdResult[1].startsWith("Error")) ||
								(cmdResult.length >= 1 && cmdResult[0].startsWith("error"))) {
							Log.e("activity start faile : " + pacakgeName + "/" + activity);

							if(cmdResult != null) {
								StringBuilder sb = new StringBuilder("cmd: adb shell start -n " + pacakgeName + "/" + activity + "\n\n");
								for(String s : cmdResult) sb.append(s+"\n");
								errMessage = sb.toString();
								Log.e(errMessage);
							}
						} else if((boolean)Resource.PROP_TRY_UNLOCK_AF_LAUNCH.getData()) {
							AdbDeviceHelper.tryDismissKeyguard(device);
						}
					}
				}
			}
		}
		return errMessage;
	}

	private static String pushApk(final IDevice device, final CompactApkInfo apkInfo, final OptionsBundle options) {
		String errMessage = null;


		return errMessage;
	}
}
