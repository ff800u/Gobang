package log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum LogRecoder {
	WEB("web"), EVENT("event");
	private static final String DEBUG = "debug";
	private static final String WARN = "warn";
	private static final String ERROR = "error";

	private File logTxt;

	private LogRecoder(String logName) {
		try {
			boolean dirExsit = true;
			String dir = "D:\\gobanglog";
			File fileDir = new File(dir);
			if (!fileDir.exists()) {
				dirExsit = fileDir.mkdir();
			}
			if (!dirExsit) {
				System.out.println("Fail to make dir : " + dir);
				return;
			}
			File file = new File(dir + "\\" + logName + ".txt");
			this.logTxt = file;
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void debug(String logInfo) {
		log(DEBUG, logInfo);
	}

	public void error(String logInfo) {
		log(ERROR, logInfo);
	}

	public void warn(String logInfo) {
		log(WARN, logInfo);
	}

	private void log(String level, String logInfo) {
		if (logTxt != null && logTxt.exists()) {
			try {
				FileWriter writer = new FileWriter(logTxt, true);
				writer.append(getLogInfo(level, logInfo + "\n"));
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String getLogInfo(String level, String info) {
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
		return format.format(now) + " [" + level + "] : " + info;
	}
}
