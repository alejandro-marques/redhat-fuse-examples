package com.redhat.training.jb421.filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.redhat.training.jb421.utils.GetFilesOnly;

public class FilterPatternTest extends CamelSpringTestSupport {

	@Before
	public void setupTest() throws Exception {
		createDirectories();
		copyFilesFromBackup();
		removeFilesFromDestination();
	}

	private void createDirectories() {
		File origin = new File("origin");
		File destination = new File("destination");
		origin.mkdir();
		destination.mkdir();
	}

	private void copyFilesFromBackup() throws IOException {
		File backup = new File("../data/orders");
		File[] filesBackup = backup.listFiles();
		Path origin = Paths.get("origin");
		for (File file : filesBackup) {
			Path path = Paths.get(file.getCanonicalPath());
			Files.copy(path, origin.resolve(file.getName()), StandardCopyOption.COPY_ATTRIBUTES,
					StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private void removeFilesFromDestination() {
		File destination = new File("destination");
		if (!destination.exists()) {
			return;
		}
		File[] files = destination.listFiles();
		for (File file : files) {
			file.delete();
		}
	}

	@Test
	public void testRouteIsProcessingAllFiles() throws Exception {
		NotifyBuilder notify = new NotifyBuilder(context).whenDone(6).create();
		notify.matches(5, TimeUnit.SECONDS);
		assertEquals("Not all origin files were processed", 0, getNumberOfFiles("origin"));
		assertEquals("The journal file was not generated", 1, getNumberOfFiles("destination"));
		int counter = countStringOccurrences("destination/output.xml", "ORly");
		assertEquals("Two orders should have been processed", 2, counter);

	}

	private int countStringOccurrences(String filename, String search) throws FileNotFoundException {
		File file = new File(filename);
		int counter = 0;
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String string = scanner.nextLine();
				if (string.contains(search))
					counter++;
			}
		}
		return counter;
	}

	private int getNumberOfFiles(String dir) {
		File dirOrigin = new File(dir);
		return dirOrigin.listFiles(new GetFilesOnly()).length;
	}

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("META-INF/spring/bundle-context.xml");
	}

}
