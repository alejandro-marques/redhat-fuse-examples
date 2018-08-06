package com.redhat.training.jb421;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FTPRouteTest extends CamelSpringTestSupport {

	private static final String LOG_DIR = "orders";
	private static final String FTP_HOST = "localhost";

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("META-INF/spring/bundle-context.xml");
	}

	@Test
	public void testDroppingOrder() throws Exception {
		dropFileOnFTPServer();
		
		NotifyBuilder builder = new NotifyBuilder(context).whenDone(1).create();
		builder.matches(5000, TimeUnit.MILLISECONDS);
		
		// Check a file was created with the name of the FTP host in the proper directory
		File file = new File(LOG_DIR + File.separator + FTP_HOST);
		assertTrue(file.exists());
	}

	
	private void dropFileOnFTPServer() throws IOException {
		String ftpUrl = "ftp://%s:%s@%s/%s;type=i";
		String host = FTP_HOST;
		String user = "user";
		String pass = "password";
		String filePath = "../data/orders/order-1.xml";
		String uploadPath = "order-1.xml";

		ftpUrl = String.format(ftpUrl, user, pass, host, uploadPath);
		System.out.println("Upload URL: " + ftpUrl);

		URL url = new URL(ftpUrl);
		URLConnection conn = url.openConnection();
		try (OutputStream outputStream = conn.getOutputStream();
			FileInputStream inputStream = new FileInputStream(filePath)) {
	
			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			System.out.println("File uploaded!");
		}
	}

	@Before
	public void cleanUpPreviousTests() {
		File file = new File(LOG_DIR + File.separator + FTP_HOST);
		if (file.exists()) {
			file.delete();
		}
	}

}
