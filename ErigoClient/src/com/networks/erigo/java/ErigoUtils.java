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
import android.util.Log;

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
			word = word.replace("@", "a").replace("i3","b").replace("l3","b").replace("(","c");
		    word = word.replace("3","e").replace("6","g").replace("!","i");
		    word = word.replace("1","l").replace("0", "o").replace("$","s");
		    word = word.replace("7","t").replace("9", "q");
		
		    //to match repeated characters
		    // 3 or more to avoid common double
			if(badwords.contains(word.replaceAll("(.)\\1+", "$1")))
			{
				Log.i("Profane",word.replaceAll("(.)\\1+", "$1"));
				return true;
			}
			
			//for alphabet replacements 
			//build word variations list 
			List<String> wordVariations = new ArrayList<String>();
			wordVariations.add(word.replace("ph","f"));
			wordVariations.add(word.replace("l","i"));
			wordVariations.add(word.replace("i","l"));
			wordVariations.add(word.replace("c","k"));
			wordVariations.add(word.replace("k","c"));

			//check through variations
			for(String variation:wordVariations)
			{
				if(badwords.contains(variation)){
					Log.i("Profane",word.replaceAll("(.)\\1+", "$1"));
					return true;
				}
			}
				
		}
		return false;
	}
}


