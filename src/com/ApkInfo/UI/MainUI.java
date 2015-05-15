package com.ApkInfo.UI;

import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;

import com.ApkInfo.Core.*;


public class MainUI extends JFrame implements WindowListener{

	private JFrame frame;
	static MyApkInfo mApkInfo;
	private static String Title = "";
		
	static String Osname ="";
	static String FolderDefault = "";
	static String apkFilePath ="";
	
	static MyCoreThead startCore;
	
	static MainUI window;
	
	//window position
	static public int nPositionX,nPositionY;
	
	//for waiting
	static public JFrame WaitingDlg;
	static public MyProgressBarDemo ProgressBarDlg;
	/**
	 * Launch the application.
	 */
	public static MyApkInfo GetMyApkInfo(){
		return mApkInfo;
	}	
	
	class MyCoreThead extends Thread {
		public void run() {
			try {
				//OS 분기
				Osname = System.getProperty("os.name");
				System.out.println("OS : " + Osname);
				File apkFile = new File(apkFilePath);
				System.out.println("target file :" + apkFile.getAbsolutePath());
				if(!apkFile.exists()) {
					System.out.println("파일이 존재 하지 않습니다 :");
				} else {
					apkFilePath = apkFile.getAbsolutePath();
				}
				
				//if(Osname.indexOf("Windows") ==-1) { //for linux
					//FolderDefault = "/tmp/ApkInfo" + apkFilePath.substring(0,apkFilePath.lastIndexOf("."));
				//} else { //for windows
				System.out.println("File.separator : " + File.separator);
				System.out.println("java.io.tmpdir : " + System.getProperty("java.io.tmpdir"));
				System.out.println("user.dir : " + System.getProperty("user.dir"));

				FolderDefault = System.getProperty("java.io.tmpdir") 
						+ File.separator + "ApkInfo" + File.separator 
						+ apkFilePath.substring(apkFilePath.indexOf(File.separator),apkFilePath.lastIndexOf("."));
				FolderDefault = FolderDefault.replaceAll(File.separator+File.separator, File.separator);
				//}
				
				System.out.println("DefaultFolderName : " +FolderDefault);
				//APK 풀기 
				CoreApkTool.solveAPK(apkFilePath,FolderDefault);
				
				int index = apkFilePath.lastIndexOf(File.separator);					
				Title = "APK Info - "+ apkFilePath.substring(index+1);
				
				System.out.println(FolderDefault);
				mApkInfo = CoreXmlTool.XmlToMyApkinfo(FolderDefault);
				mApkInfo.lApkSize = apkFile.length();
				System.out.println("APK size : " + mApkInfo.lApkSize + " byte");
				
				//iamge 찾기
				mApkInfo.ImagePathList = CoreApkTool.findfileforResource(new File(CoreApkTool.DefaultPath + File.separator + "res"));
				System.out.println("Resource(*.png) Count :  : " + mApkInfo.ImagePathList.size());
				
				//lib 찾기
				mApkInfo.LibPathList = CoreApkTool.findfileforLib(new File(CoreApkTool.DefaultPath+File.separator+"lib"));
				System.out.println("Lib Count : " + mApkInfo.LibPathList.size());
				
				initialize();				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	public static void main(final String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {	
				//args = file path					
				apkFilePath = args[0];
				System.out.println("Target APK : " + args[0]);
				
				//CoreApkTool.makeFolder("temp");
				
				ProgressBarDlg = new MyProgressBarDemo();
				
				WaitingDlg = MyProgressBarDemo.createAndShowGUI();
				
				window = new MainUI();
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainUI() {
		//initialize();
		startCore = new MyCoreThead();
		startCore.start();	
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(this);
		frame.setBounds(100, 100, 550, 500);
		frame.setTitle(Title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		frame.add(new MyTabUI(), BorderLayout.CENTER);
		
		frame.getContentPane().add(new MyButtonPanel(), BorderLayout.SOUTH);
		frame.setResizable( false );
		
		//frame.add(new MyButtonPanel(), BorderLayout.NORTH);
		
		frame.setVisible(true);
		
		nPositionX = frame.getLocationOnScreen().x;
		nPositionY = frame.getLocationOnScreen().y;
		
		frame.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener(){
			@Override
			public void ancestorMoved(HierarchyEvent e) {
				// TODO Auto-generated method stub
				
				nPositionX = frame.getLocationOnScreen().x;
				nPositionY = frame.getLocationOnScreen().y;
			}

			@Override
			public void ancestorResized(HierarchyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub		
		System.out.println("delete Folder : "  + FolderDefault);		
		if(FolderDefault.length()>0)CoreApkTool.deleteDirectory(new File(FolderDefault));
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}
