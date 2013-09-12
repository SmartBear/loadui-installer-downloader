/*
 *  soapUI, copyright (C) 2004-2011 eviware.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */
package com.eviware.downloader;

public class OS
{
	//linux installer extension
	private static final String SH = ".sh";

	//windows installer extension
	private static final String EXE = ".exe";

	//mac installer extension
	private static final String DMG = ".dmg";

	public static final String EXTENSION = ( isWindows() ? EXE : isMac() ? DMG : SH );

	private static boolean isWindows()
	{
		Boolean isWindows = new Boolean( System.getProperty( "os.name" ).indexOf( "Windows" ) >= 0 );
		return isWindows.booleanValue();
	}

	public static boolean isMac()
	{
		Boolean isMac = new Boolean( System.getProperty( "os.name" ).indexOf( "Mac" ) >= 0 );
		return isMac.booleanValue();
	}

	public static String[] createCommand( String file )
	{
		String[] commandsWin = new String[] { "cmd.exe", "/c", file };
		String[] commandsLinux = new String[] { "sh", file };
		String[] commandsMac = new String[] { "hdiutil", "attach", file }; // .dmg is disk image. it needs to be mounted first

		return OS.isWindows() ? commandsWin : OS.isMac() ? commandsMac : commandsLinux;
	}
}
