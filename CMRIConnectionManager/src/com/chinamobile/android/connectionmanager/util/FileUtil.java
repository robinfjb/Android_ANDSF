package com.chinamobile.android.connectionmanager.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.chinamobile.android.connectionmanager.model.PolicyModel;

public class FileUtil {
	public static int FORMAT_OVERWRITE = 0x01;
	public static int FORMAT_APPEDN = 0x02;
	
	/**
	 * compare file is same as policy.xml in internal storage
	 * @param context
	 * @param input
	 * @return true--file is the same; false--file not the same or not found
	 */
	@Deprecated
	public static boolean compareFile(Context context, File inputFile){
		FileInputStream fInput = null;
		FileInputStream fTargetInput = null;
		BufferedInputStream newInFile = null;
		BufferedInputStream localInFile = null;
		try {
			fTargetInput = new FileInputStream(inputFile);
			fInput = context.getApplicationContext().openFileInput(Constants.XML_NAME);
			newInFile = new BufferedInputStream(fTargetInput);
			localInFile = new BufferedInputStream(fInput);
			if (newInFile.available() == localInFile.available()) {
				while (newInFile.read() != -1 && localInFile.read() != -1) {
					if (newInFile.read() != localInFile.read()) {
						System.out.println("Files not same");
						return false;
					}
				}
				System.out.println("two files are same !");
				return true;
			} else {
				System.out.println("two files length are different !");
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fInput != null)
					fInput.close();
				if(fTargetInput != null)
					fTargetInput.close();
				if (newInFile != null)
					newInFile.close();
				if (localInFile != null)
					localInFile.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	@Deprecated
	public static void writeTextFile(File file, String str) throws IOException {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new FileOutputStream(file));
			out.write(str.getBytes());
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	/**
	 * save file into internal storage
	 * @param context
	 * @param fileName
	 * @param in
	 */
	private static void saveFile2InternalStorage(Context context, String fileName, InputStream in) {
		FileOutputStream fOut = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {
			fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			int buf = 0;
			while ((buf = br.read()) >= 0) {
				fOut.write((char) buf);
			}
			fOut.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(fOut != null) {
					fOut.close();
				}
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * save file into internal storage
	 * @param context
	 * @param fileName
	 * @param content
	 */
	public static void saveFile2InternalStorage(Context context, String fileName, String content) {
		FileOutputStream fOut = null;
		try {
			fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fOut.write(content.getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(fOut != null) {
					fOut.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * read policy from saved data
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static List<PolicyModel> readPolicyFromSavedData(final Context context, final String fileName) {
		if(!checkSDCardMounted()) {
			InputStream inputStream = null;
			try {
				inputStream = context.getApplicationContext().openFileInput(fileName);
				List<PolicyModel> policys = CommonUtil.parseXml(inputStream);
				return policys;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} finally {
				if(inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/chinamobile_andsf/", fileName);
			if(file.exists()) {
				FileInputStream fis;
				try {
					fis = new FileInputStream(file);
					return CommonUtil.parseXml(fis);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				}
			} else {
				return null;
			}
		}
		
	}
	
	/**
//	 * read data from SD card as stream
	 * <p>this method is for test
	 * @param context
	 * @param fileNam
	 * @return
	 */
	public static InputStream readStreamFormSDCard(final Context context, final String fileName) {
		if(!checkSDCardMounted()) {
//			Toast.makeText(context, "sd card is not prepared!!", Toast.LENGTH_SHORT).show();
		} else {
			File path = new File(Environment.getExternalStorageDirectory()
					+ "/chinamobile_andsf/");
			if(!path.exists()) {
				path.mkdir();
			}
			
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/chinamobile_andsf/", fileName);
			
			if(file.exists()) {
				FileInputStream fis;
				try {
					fis = new FileInputStream(file);
					return fis;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
//				Toast.makeText(context, "file is not exsit!!", Toast.LENGTH_SHORT).show();
			}
		}
		return null;
	}
	/**
	 * save the InputStream into SD card, if SD card is not prepared or no enough space, 
	 * save to internal storage
	 * @param context
	 * @param fileName
	 * @param in
	 */
	public static void saveFile2SdCard(final Context context, final String fileName, final InputStream in) {
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
				FileOutputStream fos = null;
				try {
					final long fileSize = in.available();
					if(!checkSDCardMounted() || fileSize >= getSDCardCapacity()) {
						saveFile2InternalStorage(context, fileName, in);
					} else {
						final File fileDir = new File(Environment.getExternalStorageDirectory()
								+ "/chinamobile_andsf/");
						if(!fileDir.exists()) {
							fileDir.mkdir();
						}
						final File file = new File(fileDir, fileName);
						if (!file.exists()) {
							file.createNewFile();
						}
						
						fos = new FileOutputStream(file);
						byte[] buffer = new byte[1024];
						int count = 0;
		                while ((count = in.read(buffer)) > 0) {
		                    fos.write(buffer, 0, count);
		                }
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (fos != null) {
							fos.close();
						}
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//			}
//		}).start();
		
	}
	
	/**
	 * save file to SD card, path is ./chinamobile_andsf/
	 * @param context
	 * @param fileName
	 * @param content
	 * @param format
	 */
	public static void saveFile2SdCard(final Context context, final String fileName, final String content, final int format) {
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
				FileOutputStream fos = null;
				FileWriter fw = null;
				try {
					final long fileSize = content.getBytes().length;
					if(!checkSDCardMounted() || fileSize >= getSDCardCapacity()) {
						saveFile2InternalStorage(context, fileName, content);
					} else {
						final File fileDir = new File(Environment.getExternalStorageDirectory()
								+ "/chinamobile_andsf/");
						if(!fileDir.exists()) {
							fileDir.mkdir();
						}
						final File file = new File(fileDir, fileName);
						if (!file.exists()) {
							file.createNewFile();
						}
						
						if(format == FORMAT_OVERWRITE) {
							fos = new FileOutputStream(file);
							fos.write(content.getBytes());
						} else if(format == FORMAT_APPEDN) {
							fw = new FileWriter(file,true);
							fw.append(content);
						}
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (fos != null) {
							fos.close();
						}
						
						if (fw != null) {
							fw.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//			}
//		}).start();
		
	}
	
	/**
	 * check the SD card is prepared
	 * @return
	 */
	public static boolean checkSDCardMounted() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * get the SD card rest capacity
	 * @return
	 */
	public static long getSDCardCapacity() {
		String sdcard = Environment.getExternalStorageDirectory().getPath();
		File file = new File(sdcard);
		StatFs statFs = new StatFs(file.getPath());
		return (statFs.getBlockSize() * ((long) statFs.getAvailableBlocks() - 4));
	}
}
