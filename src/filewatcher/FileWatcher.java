package filewatcher;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import filewatcher.mail.SendMailSSL;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class FileWatcher extends SendMailSSL {

	public void doWath(String directory) throws IOException {

		System.out.println("WatchService in " + directory);

		Path directoryToWatch = Paths.get(directory);
		if (directoryToWatch == null) {
			throw new UnsupportedOperationException("Directory not found");
		}

		final Map<WatchKey, Path> keys = new HashMap<>();
		WatchService watchService = directoryToWatch.getFileSystem().newWatchService();
		System.out.println("Started WatchService in " + directory);

		Files.walkFileTree(directoryToWatch, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				WatchKey watchKey = dir.register(watchService, ENTRY_CREATE);
				keys.put(watchKey, dir);
				return FileVisitResult.CONTINUE;
			}
		});

		try {

			while (true) {
				WatchKey key;
				try {
					key = watchService.take();
				} catch (InterruptedException ex) {
					return;
				}

				final Path dir = keys.get(key);
				if (dir == null) {
					System.err.println("WatchKey " + key + " not recognized!");
					continue;
				}

				while (key != null) {
					for (WatchEvent event : key.pollEvents()) {
						String eventKind = event.kind().toString();
						String file = event.context().toString();
						Path fileName = (Path) event.context();
						Path name = ((WatchEvent<Path>) event).context();
						Path child = directoryToWatch.resolve(name);
						System.out.println("Event : " + eventKind + " in File " + dir.toString() + "/" + file);
						sendMail("pruebato@gmail.com", "Event : " + eventKind + " in File " + dir.toString() + "/" + file,  "Evento ABA");
					}

					boolean valid = key.reset();
					key = watchService.take();

					if (!valid) {
						break;
					}
				}
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
