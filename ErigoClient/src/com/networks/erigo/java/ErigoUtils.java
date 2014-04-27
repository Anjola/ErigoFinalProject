package com.networks.erigo.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.res.AssetManager;

public class ErigoUtils {
	
	
	public static List<String> categories = new ArrayList<String>();
	public static HashSet<String> badwords = new HashSet<String>();
	
	public static void populateBadWords(InputStream is)
	{
		BufferedReader reader = null;
		try {
			
		    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		    String line;
		    while ((line = reader.readLine()) != null) {
		        badwords.add(line);
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
	 
	        try {
	        if(reader!=null)
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		}
	}

	public static boolean isProfanePost(String post)
	{
		//get words from post removing all punctuations before space
		String[] words = post.replaceAll("[^a-zA-Z0-9]\\s","").toLowerCase().split("\\s+");
		
		for(String word:words)
		{
			//for symbol replacements 
			word = word.replaceAll("@", "a").replaceAll("[ I3 l3 i3]","b").replaceAll("(","c");
		    word = word.replaceAll("3","e").replaceAll("6","g").replaceAll("!","i");
		    word = word.replaceAll("1","l").replaceAll("0", "o").replaceAll("$","s");
		    word = word.replaceAll("7","t").replaceAll("9", "q");
		
		    //to match repeated characters
		    // 3 or more to avoid common double
			if(badwords.contains(word.replaceAll("(.)\\2+", "$1")))
				return true;
			
			//for alphabet replacements 
			//build word variations list 
			List<String> wordVariations = new ArrayList<String>();
			wordVariations.add(word.replaceAll("ph","f"));
			wordVariations.add(word.replaceAll("l","i"));
			wordVariations.add(word.replaceAll("i","l"));
			wordVariations.add(word.replaceAll("c","k"));
			wordVariations.add(word.replaceAll("k","c"));

			//check through variations
			for(String variation:wordVariations)
			{
				if(badwords.contains(variation.replaceAll("(.)\\2+", "$1")));
					return true;
			}
				
		}
		return false;
	}
}


