package filewatcher;

import java.io.IOException;

public class FileWatcherMain {
	
	public static void main(String[] args) throws IOException {
		
        FileWatcher fileChangeWatcher = new FileWatcher();
        fileChangeWatcher.doWath("/home/carmen/monitor/");
      //  fileChangeWatcher.doWath("/home/aba/SFTP/operadoras/");
 
    }
}
