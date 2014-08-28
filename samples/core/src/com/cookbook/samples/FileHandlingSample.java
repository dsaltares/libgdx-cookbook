package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileHandlingSample extends GdxSample {
	
	@Override
	public void create() {
		System.out.println("======================");
		System.out.println("File basic information");
		System.out.println("======================");
		FileHandle file = Gdx.files.internal("data/sfx/sfx_01.wav");
		FileHandle dir = Gdx.files.internal("data/sfx");
		
		printHandleInfo(file);
		printHandleInfo(dir);
		
		System.out.println("");
		System.out.println("=================");
		System.out.println("Directory listing");
		System.out.println("=================");
		try {
			printTree(Gdx.files.internal("data"), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("");
		System.out.println("==============");
		System.out.println("File existence");
		System.out.println("==============");
		FileHandle doesNotExist = Gdx.files.internal("file.txt");
		System.out.println(doesNotExist.name() + " " + (doesNotExist.exists() ? "does exist" : "does not exist"));
		FileHandle doesExist = Gdx.files.internal("data/sfx/sfx_01.wav");
		System.out.println(doesExist.name() + " " + (doesExist.exists() ? "does exist" : "does not exist"));
		
		System.out.println("");
		System.out.println("==================================");
		System.out.println("Write, read, copy and delete files");
		System.out.println("==================================");
		String testString = "This is a test file";
		FileHandle testFile = Gdx.files.external("test.txt");
		testFile.writeString(testString, false);
		System.out.println("Wrote '" + testString + "' to file " + testFile.name());
		
		FileHandle testFile2 = Gdx.files.external("test.txt");
		String string = testFile2.readString();
		System.out.println(string.equals(testString) ? "Successfully wrote and read file" : "Failed to read or write file");
		
		FileHandle testCopy = Gdx.files.external("test-copy.txt");
		System.out.println("Copying " + testFile.name() + " to " + testCopy.name());
		testFile.copyTo(Gdx.files.external("test-copy.txt"));
		string = testCopy.readString();
		System.out.println(string.equals(testString) ? "Successfully copied file" : "Failed to copy file");
		
		System.out.println("Deleting test files");
		testFile.delete();
		testCopy.delete();
		System.out.println(!testFile.exists() && !testCopy.exists() ? "Successfully deleted files" : "Failed to delete files");
		
		Gdx.app.exit();
	}
	
	private void printHandleInfo(FileHandle handle) {
		System.out.println(handle.isDirectory() ? "Directory info" : "File info");
		System.out.println("Name: " + handle.name());
		System.out.println("Name without extension: " + handle.nameWithoutExtension());
		System.out.println("Extension: " + handle.extension());
		System.out.println("Last modified in ms " + handle.lastModified());
		System.out.println("Path " + handle.path());
		System.out.println("Path without extension " + handle.pathWithoutExtension());
		
		if (!handle.isDirectory()) {
			System.out.println("Size: " + handle.length() + " bytes");
		}
		
		System.out.println();
	}
	
	private void printTree(FileHandle handle, int level) {
		
		addPadding(level);
		System.out.println("|- " + handle.name());
		
		if (handle.isDirectory()) {
			FileHandle[] children = handle.list();
			
			for (FileHandle child : children) {
				printTree(child, level + 1);
			}
			
			addPadding(level + 1);
			System.out.println("\\");	
			addPadding(level + 1);
			System.out.println();
		}
	}
	
	private void addPadding(int level) {
		for (int i = 0; i < level; ++i) {
			System.out.print("|    ");
		}
	}
}
