package com.apkspectrum.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import com.apkspectrum.util.SystemUtil;

public enum _RFile implements ResFile<String>, ResString<String>
{
	BIN_PATH					(Type.BIN, ""),

	BIN_ADB_LNX					(Type.BIN, "linux/adb", "nux"),
	BIN_ADB_MAC					(Type.BIN, "darwin/adb", "mac"),
	BIN_ADB_WIN					(Type.BIN, "windows\\adb.exe", "win"),
	BIN_ADB						(Type.BIN, new _RFile[]{ BIN_ADB_WIN, BIN_ADB_LNX, BIN_ADB_MAC }),

	BIN_AAPT_LNX				(Type.BIN, "linux/aapt", "nux"),
	BIN_AAPT_MAC				(Type.BIN, "darwin/darwin/aapt", "mac"),
	BIN_AAPT_WIN				(Type.BIN, "windows\\aapt.exe", "win"),
	BIN_AAPT					(Type.BIN, new _RFile[]{ BIN_AAPT_WIN, BIN_AAPT_LNX, BIN_AAPT_MAC }),

	BIN_JDGUI					(Type.BIN, "jd-gui-1.6.2.jar"),

	BIN_DEX2JAR_LNX				(Type.BIN, "dex2jar/d2j-dex2jar.sh", "nux"),
	BIN_DEX2JAR_MAC				(Type.BIN, "dex2jar/d2j-dex2jar.sh", "mac"),
	BIN_DEX2JAR_WIN				(Type.BIN, "dex2jar\\d2j-dex2jar.bat", "win"),
	BIN_DEX2JAR					(Type.BIN, new _RFile[]{ BIN_DEX2JAR_WIN, BIN_DEX2JAR_LNX, BIN_DEX2JAR_MAC }),

	BIN_JADX_LNX				(Type.BIN, "jadx/bin/jadx-gui", "nux"),
	BIN_JADX_MAC				(Type.BIN, "jadx/bin/jadx-gui", "mac"),
	BIN_JADX_WIN				(Type.BIN, "jadx\\bin\\jadx-gui.bat", "win"),
	BIN_JADX_GUI				(Type.BIN, new _RFile[]{ BIN_JADX_WIN, BIN_JADX_LNX, BIN_JADX_MAC }),

	BIN_BYTECODE_VIEWER			(Type.BIN, "Bytecode-Viewer-2.9.22.jar"),

	BIN_SIGNAPK					(Type.BIN, "signapk.jar"),

	BIN_IMG_EXTRACTOR_WIN		(Type.BIN, "windows\\ImgExtractor.exe", "win"),

	PLUGIN_PATH					(Type.PLUGIN, ""),
	PLUGIN_CONF_PATH			(Type.PLUGIN, "plugins.conf"),

	SSL_TRUSTSTORE_PATH			(Type.SECURITY, "trustStore.jks"),

	DATA_PATH					(Type.DATA, ""),
	DATA_STRINGS_EN				(Type.DATA, "strings.xml"),
	DATA_PERMISSIONS_HISTORY	(Type.DATA, "PermissionsHistory.xml"),

	RAW_ROOT_PATH				(Type.RES_ROOT, ""),
	RAW_ANDROID_MANIFEST		(Type.RES_ROOT, "AndroidManifest.xml"),

	RAW_VALUES_PATH				(Type.RES_VALUE, ""),
	RAW_STRINGS_EN				(Type.RES_VALUE, "_strings.xml"),
	RAW_STRINGS_KO				(Type.RES_VALUE, "_strings-ko.xml"),

	RAW_PUBLIC_XML				(Type.RES_VALUE, "public.xml"),
	RAW_PERMISSIONS_HISTORY		(Type.RES_VALUE, "PermissionsHistory.xml"),

	RAW_SDK_INFO_FILE			(Type.RES_VALUE, "sdk-info.xml"),
	RAW_PROTECTION_LEVELS_HTML	(Type.RES_VALUE, "ProtectionLevels.html"),
	RAW_PERMISSION_REFERENCE_HTML(Type.RES_VALUE, "PermissionReference.html"),

	ETC_SETTINGS_FILE			(Type.ETC, "settings.txt"),
	; // ENUM END

	private static String corePath = "core";

	private String value;
	private Type type;
	private String os;

	private _RFile(Type type, String value) {
		this(type, value, null);
	}

	private _RFile(Type type, String value, String os) {
		this.type = type;
		this.value = value;
		this.os = os;
	}

	private _RFile(Type type, _RFile[] cfgResources) {
		if(cfgResources == null | cfgResources.length == 0) {
			throw new IllegalArgumentException();
		}

		this.type = type;
		for(_RFile r: cfgResources) {
			if(SystemUtil.OS.contains(r.os)) {
				this.value = r.value;
				this.os = r.os;
				break;
			}
		}
		if(this.value == null || os == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return getPath();
	}

	@Override
	public String getPath() {
		if(type == Type.RES_VALUE || type == Type.RES_ROOT) {
			return getURL().toExternalForm();
		}
		return getUTF8Path() + value;
	}

	@Override
	public URL getURL() {
		switch(type){
		case RES_ROOT:
			return getClass().getResource("/" + value);
		case RES_VALUE:
			if(value == null || value.isEmpty())
				return getClass().getResource("/values");
			else
				return getClass().getResource("/values/" + value);
		default:
			try {
				return new File(getPath()).toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String get() {
		return getPath();
	}

	@Override
	public String getString() {
		switch(type){
		case RES_VALUE:
			try (InputStream is= getURL().openStream();
				 InputStreamReader ir = new InputStreamReader(is);
				 BufferedReader br = new BufferedReader(ir)) {
		        StringBuilder out = new StringBuilder();
		        String line;
		        while ((line = br.readLine()) != null) {
		            out.append(line);
		        }
		        return out.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		default:
			return null;
		}
	}

	public URL getResource() {
		return getURL();
	}

	public InputStream getResourceAsStream() {
		switch(type){
		case RES_VALUE:
			return getClass().getResourceAsStream("/values/" + value);
		default:
			try {
				return getURL().openStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void setCorePath(String path) {
		corePath = path;
	}

	private String getUTF8Path() {
		File binary = new File(_RFile.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String resourcePath = binary.getParentFile().getPath();
		switch(type) {
		case BIN: case SECURITY:
			if(corePath != null && binary.isDirectory()) {
				resourcePath += File.separator + corePath;
			}
		default:
			resourcePath += File.separator + type.getValue() + File.separator;
		}

		try {
			resourcePath = URLDecoder.decode(resourcePath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return resourcePath;
	}
}
