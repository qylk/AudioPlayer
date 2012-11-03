package cn.qylk.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import cn.qylk.app.APP;

/**
 * 程序崩溃记录
 * 
 * @author Administrator
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	private static CrashHandler crashhandelr = new CrashHandler();
	public static CrashHandler GetInstance() {
		return crashhandelr;
	}

	UncaughtExceptionHandler defaulthandler;

	private boolean handleExption(Thread thread, Throwable ex) {
		File logfile = new File(APP.LOGPATH + "log.txt");
		try {
			if (!logfile.exists())
				logfile.createNewFile();
			FileOutputStream fileoutstream = new FileOutputStream(logfile, true);
			OutputStreamWriter osw = new OutputStreamWriter(fileoutstream);
			PrintWriter pw = new PrintWriter(osw);
			pw.println(ex.getLocalizedMessage());
			StackTraceElement[] ste = ex.getStackTrace();
			for (int i = 0; i < ste.length; i++) {
				pw.println(ste[i].toString());
			}
			pw.println("\n\n\n");
			pw.close();
			osw.close();
			fileoutstream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void init() {
		defaulthandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleExption(thread, ex) && defaulthandler != null) {// 如果用户没有处理则让系统默认的异常处理器来处理
			defaulthandler.uncaughtException(thread, ex);
		} else { // Sleep一会后结束程序
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}
	}
}
