package com.redhat.training.jb421.utils;

import java.io.File;
import java.io.FileFilter;

public class GetFilesOnly implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isFile();
	}

}
