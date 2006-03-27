/*
 *Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 *
 *This program is free software; you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation; either version 2 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 *USA
 *
 *http://www.gnu.org/licenses/gpl.txt
 */

package org.lamsfoundation.lams.tool.deploy;
import java.io.File;

/**
 * Deploys required language files to the lams ear. Copies all the language files
 * in a particular directory to a given package name.
 * @author chris
 */
public class DeployLanguageFilesTask extends FilesTask
{
    
	private static final String LANGUAGE_JAR_DIRECTORY = "lams-dictionary.jar";
    
    /**
     * Holds value of dictionary package i.e. the tool signature or a unique id for a complex activity.
     */
    protected String dictionaryPacket;

    /**
     *Executes the task
     */
    public void execute() throws DeployException
    {
    	// check the lams ear is okay by getting the file object - this will check 
    	// for any problems with the lams ear.
    	getLamsEar();

    	// Go through each language file and copy it into the directory named for the tool's signature.
    	// This means the file ends up as <toolsignature>.ApplicationResources
    	File dictionaryDir = getLamsDictionary();
        for (String languageFilename : deployFiles)
        {
        	System.out.println("Copying file "+languageFilename+" to "+dictionaryDir);
            copyFile(languageFilename, dictionaryDir);
        }
    }
    
    /** Gets the package directory in the dictionary directory in the LAMS ear directory and 
     * checks that it exists, is a directory and is writable. Assumes that lams.ear exists and it 
     * has already been checked to be directory, writable, etc. If it doesn't exist, it creates 
     * the directory (as long as the lams ear exists). */
    private File getLamsDictionary() throws DeployException {
    	
    	// convert dictionary packet org.lamsfoundation.lams.tool.web to org/lamsfoundation/lams/tool/web
    	if ( dictionaryPacket != null ) {
    		System.out.println("dictionaryPacket "+dictionaryPacket+" being updated");
    		dictionaryPacket = dictionaryPacket.replace('.',File.separatorChar);
    		System.out.println("dictionaryPacket now"+dictionaryPacket);
    	}
    	String packageName = lamsEarPath+File.separator+LANGUAGE_JAR_DIRECTORY+File.separator+dictionaryPacket;
    	File dictionaryDir = new File(packageName);
	    if (!dictionaryDir.exists())
	    {
	    	dictionaryDir.mkdirs();
	    }
	    else if (!dictionaryDir.isDirectory())
	    {
	        throw new DeployException("Dictionary package "+packageName+" exists but it is not a directory");
	    }
	    else if (!dictionaryDir.canWrite())
	    {
	        throw new DeployException("Dictionary package "+packageName+" exists but it is not writable");
	    }
	    return dictionaryDir;
    }

    /** Name of the dictionary package i.e. the tool signature or a unique id for a complex activity. */
	public String getDictionaryPacket() {
		return dictionaryPacket;
	}

	public void setDictionaryPacket(String dictionaryPacket) {
		System.out.println("DeployLanguageFile: Setting dictionary packet to "+dictionaryPacket);
		this.dictionaryPacket = dictionaryPacket;
	}

}
