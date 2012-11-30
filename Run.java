/*Amarok GNU GPLv2+ Checker and Stat Generator

Copyright (C) 2012 Jason Spriggs <jason@jasonspriggs.com>

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import java.io.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Run {

	private static ArrayList<AuthorInfo> authorList = new ArrayList<AuthorInfo>();
	
	public static void main(String[] args) throws InterruptedException{
        // print banner
		System.out.println("-------Amarok GPLv2 License Header Checker--------");
		System.out.println("-Created by Jason Spriggs for Google Code-In 2012-");
		System.out.println("--------------------------------------------------");

        // handle args
        if (args.length == 0 || args.length > 1) {
            System.out.println("Usage: java Run AMAROK_DIR");
            return;
        }

		String fullPath = args[0]; //Change this to your working directory

		// start reporting
        System.out.println("Report Started At " + getTime());
		try {
			PrintWriter out = new PrintWriter(new FileWriter("check.log"));
			PrintWriter outStats = new PrintWriter(new FileWriter("stats.log"));
			out.println("-------Amarok GPLv2 License Header Checker--------");
			out.println("-Created by Jason Spriggs for Google Code-In 2012-");
			out.println("--------------------------------------------------");
			out.println("Report Started At " + getTime());
			out.println("--------------------------------------------------");
			listAndCheck(new File(fullPath), out);
			out.println("--------------------------------------------------");
			out.println("END OF REPORT");
			out.close();
			outStats.println("-------Amarok GPLv2 License Header Checker--------");
			outStats.println("-Created by Jason Spriggs for Google Code-In 2012-");
			outStats.println("--------------------------------------------------");
			outStats.println("Report Started At " + getTime());
			outStats.println("--------------------------------------------------");
			listAuthorStats(outStats);
			outStats.println("--------------------------------------------------");
			outStats.println("END OF REPORT");
			outStats.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static Date getTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
		try {
			return dateFormat.parse(dateFormat.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	private static void listAndCheck(File file, PrintWriter out) {
        if (!file.exists()) {
            System.out.println("Warning: File does not exist: " + file);
            return;
        }

		String[] files = file.list();
		for(int i=0 ; i < files.length ; i++){
			File fileInQuestion = new File(file, files[i]);
	    	if(fileInQuestion.isFile()) {
	    		String fileName = fileInQuestion.getName();
	    		if(fileName.endsWith(".png") || fileName.endsWith(".notifyrc") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
	    			fileName.endsWith(".cmake") || fileName.endsWith(".ogg") || fileName.endsWith(".xml") || fileName.endsWith(".svg") || 
	    			fileName.endsWith(".svgz") || fileName.endsWith(".sh") || fileName.endsWith(".gif") || fileName.endsWith(".protocol") || 
	    			fileName.endsWith(".kcfg") || fileName.endsWith(".kcfgc") || fileName.endsWith(".desktop") || fileName.endsWith(".ui") || 
	    			fileName.endsWith(".htm") || fileName.endsWith(".html") || fileName.endsWith(".css") || fileName.endsWith(".knsrc") || 
	    			fileName.endsWith(".xcf") || fileName.endsWith(".spec") || fileName.endsWith(".js") || fileName.equals("ChangeLog") || 
	    			fileName.equals("CMakeLists.txt") || fileName.equals(".kateconfig") || fileName.equals("TODO") || fileName.equals("README") || 
	    			fileName.equals("NOTES") || fileName.equals("COPYING") || fileName.equals("ScriptWriting-HOWTO")){
    	    		String name = fileInQuestion.getAbsolutePath();
    	    		for(int n = name.length(); n < 140; n++)
    	    			name += " ";
                	out.println(name + "\t | SKIPPED!");
	    		} else if(fileInQuestion.getAbsolutePath().contains("core/capabilities/FingerprintCapability.cpp")) {
	    			String name = fileInQuestion.getAbsolutePath();
	    			for(int n = name.length(); n < 140; n++)
    	    			name += " ";
                	out.println(name + "\t | SKIPPED! - Blank File");
	    		} else {
	    			if(checkHeader(fileInQuestion.getAbsolutePath(), out)) {
	    				processAuthors(getAuthors(fileInQuestion.getAbsolutePath(), out), fileInQuestion.getAbsolutePath());
	    			}
	    			fileInQuestion = null; //Garbage Collection :D
	    		}
	    	} else {
				listAndCheck(new File(file, files[i]), out);
	    	}
	    }
	}
	private static boolean checkHeader(String fileLoc, PrintWriter out) {
		try {
            FileInputStream fstream = new FileInputStream(fileLoc);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while(true) {
                line = br.readLine();
                if (line == null) {
    	    		String name = fileLoc;
    	    		for(int n = name.length(); n < 140; n++)
    	    			name += " ";
                	out.println(name + "\t | NOT FOUND!");
                	br.close();
                    return false;
                } else {
                    if(line.contains("either version 2 of the License, or") || 
                    		line.contains("the Free Software Foundation; either version 2 of the License, or") ||
                    		line.contains("version 2.1 of the License, or (at your option) any later version.") ||
                    		line.contains("version 2.1 of the License, or (at your option) version 3, or any") ||
                    		line.contains("published by the Free Software Foundation; either version 2 of") ||
                    		line.contains("Software Foundation; either version 2.1 of the License, or") ||
                    		line.contains("License: GNU General Public License V2")){ 
        	    		
                    	String name = fileLoc;
        	    		for(int n = name.length(); n < 140; n++)
        	    			name += " ";
                    	out.println(name + "\t | FOUND!");
                    	br.close();
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
		return false;
	}
	private static ArrayList<String> getAuthors(String fileLoc, PrintWriter out) {
		ArrayList<String> authors = new ArrayList<String>();
		try {
            FileInputStream fstreamAuthor = new FileInputStream(fileLoc);
            DataInputStream inAuthor = new DataInputStream(fstreamAuthor);
            BufferedReader brAuthor = new BufferedReader(new InputStreamReader(inAuthor));
            String line = brAuthor.readLine().toLowerCase();
            do {
                if (line == null) {
                	brAuthor.close();
                } else {
                    if(line.toLowerCase().contains("Copyright (c)".toLowerCase())){
                    	String[] contactArray = line.split("<");
                    	if(contactArray.length == 2){
                    		String[] email = contactArray[1].split(">");
                    		if(email.length == 2){
                            	authors.add(email[0]);
                    		}
                    	}
                    }
                    line = brAuthor.readLine();
                }
            } while(line != null);
            if(authors.size() == 0)
            	out.println("ERROR - No Authors Detected");
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return authors;
	}
	private static void processAuthors(ArrayList<String> authorsOfFile, String loc) {
		if(authorsOfFile.size() == 0 || authorsOfFile == null){
			return;
		}
		for(int i = 0; i < authorsOfFile.size(); i++){
			boolean found = false;
			for(int n = 0; n < authorList.size(); n++){
				if(authorList.get(n).getEmail().equals(authorsOfFile.get(i))){
					authorList.get(n).addFile(loc);
					found = true;
				}
			}
			if(!found){
				authorList.add(new AuthorInfo(authorsOfFile.get(i)));
			}
		}
	}
	private static void listAuthorStats(PrintWriter out){
		for(int i = 0; i < authorList.size(); i++)
			out.println(authorList.get(i).getEmail() + " has edited " + authorList.get(i).getNumOfFiles());
	}
}

// kate: tab-width 4; replace-tabs off; tab-indents on; space-indent off;
