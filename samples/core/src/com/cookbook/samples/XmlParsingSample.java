package com.cookbook.samples;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class XmlParsingSample extends GdxSample {
	
	@Override
	public void create() {
		try {
			XmlReader reader = new XmlReader();
			Element root = reader.parse(Gdx.files.internal("data/credits.xml"));

			System.out.println("=========");
			System.out.println("Book data");
			System.out.println("=========");
			
			Element bookElement = root.getChildByName("Book");
			
			System.out.println("Title: " + bookElement.getText());
			System.out.println("Year: " + bookElement.getInt("year"));
			System.out.println("Number of pages: " + bookElement.getInt("pages"));
			
			Array<Element> authors = root.getChildrenByNameRecursively("Author");
			
			System.out.println("Authors: ");
			
			for (Element author : authors) {
				System.out.println("  * " + author.getText());
			}
			
			Array<Element> reviewers = root.getChildrenByNameRecursively("Reviewer");
			
			System.out.println("Reviewers: ");
			
			for (Element reviewer : reviewers) {
				System.out.println("  * " + reviewer.getText());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Gdx.app.exit();
	}
}
