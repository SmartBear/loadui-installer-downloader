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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

/**
 * downloader
 */
public class Downloader
{
	// prefix used for creating temp file
	private static final String PREFIX = "loadUI";

	// temporary file
	private static File tempFile;

	private static ProgressBar progressBar;

	private static Proxy proxy;

	public static void main( String[] args )
	{
		if( args.length < 2 )
		{
			System.out.println( "you have to specify download url link and temporary file path!" );
			return;
		}

		initProxy( args );
		boolean success = download( args[0] );

		if( success && shouldInstall() )
		{
			startInstaller();
		}

		removeTempFile();
		closeWindow();
	}

	private static boolean shouldInstall()
	{
		int confirmed = JOptionPane
				.showConfirmDialog( null, "loadUI has now been downloaded, the installer is located here:\n" + tempFile
						+ "\nDo you want to start the loadUI installer?", "Start loadUI installer", JOptionPane.YES_NO_OPTION );
		if( confirmed == JOptionPane.YES_OPTION )
		{
			return true;
		}
		return false;
	}

	private static void initProxy( String[] args )
	{
		if( args.length >= 4 )
		{
			String host = args[2];
			String port = args[3];
			if( host.length() == 0 || port.length() == 0 )
			{
				return;
			}
			int portNum = -1;
			try
			{
				portNum = Integer.parseInt( port );
			}
			catch( NumberFormatException nfe )
			{
				System.out.println( "you have to specify valid port number!" );
				return;
			}
			SocketAddress addr = new InetSocketAddress( host, portNum );
			proxy = new Proxy( Proxy.Type.HTTP, addr );
		}
	}

	private static boolean download( String urlLink )
	{
		BufferedInputStream in = null;
		BufferedOutputStream fout = null;
		try
		{
			URL url = new URL( urlLink );
			URLConnection conn = null;

			if( proxy != null )
			{
				conn = url.openConnection( proxy );
			}
			else
			{
				conn = url.openConnection();
			}
			if( conn != null )
			{
				conn.setConnectTimeout( 20000 );
			}
			int size = conn.getContentLength();
			in = new BufferedInputStream( conn.getInputStream() );
			tempFile = File.createTempFile( PREFIX, OS.EXTENSION );
			fout = new BufferedOutputStream( new FileOutputStream( tempFile ) );
			downloading( in, fout, size );
			return true;
		}
		catch( Exception e )
		{
			if( progressBar != null )
			{
				JOptionPane.showMessageDialog( progressBar.getFrame(), "Error - loadUI installer was not downloaded!\n "
						+ e.getMessage() );
			}
			else
			{
				System.out.println( "Error - loadUI installer was not downloaded!\n " + e.getMessage() );
			}
			return false;
		}
		finally
		{
			try
			{
				if( in != null )
					in.close();
				if( fout != null )
					fout.close();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	private static void downloading( BufferedInputStream in, BufferedOutputStream fout, int size ) throws IOException
	{
		byte data[] = new byte[1024];
		int count = 0;
		progressBar = new ProgressBar( size );
		while( ( count = in.read( data, 0, 1024 ) ) != -1 )
		{
			fout.write( data, 0, count );
			int progress = progressBar.getProgressBar().getValue() + count;
			progressBar.setProgressValue( progress );
		}
		progressBar.setProgressValue( progressBar.getProgressBar().getMaximum() );
	}

	private static Process startInstaller()
	{
		ProcessBuilder pb = new ProcessBuilder( OS.createCommand( tempFile.getAbsolutePath() ) );
		pb.directory( tempFile.getParentFile() );
		Process p = null;
		try
		{
			byte[] buffer = new byte[512];
			p = pb.start();
			BufferedInputStream reader = new BufferedInputStream( p.getInputStream() );
			reader.read( buffer, 0, buffer.length );

			if( OS.isMac() )
			{
				/*
				 * in case of Mac OSX after dmg is mounted than start installer.
				 */
				String response = new String( buffer );
				response = response.replaceAll( "[\t]", ";" );
				String[] fileNames = response.split( ";" );
				System.out
						.println( "Looking for files in " + fileNames[fileNames.length - 1].trim().replaceAll( "\n", "" ) );
				File targetDir = new File( fileNames[fileNames.length - 1].trim().replaceAll( "\n", "" ) );
				System.out.println( "Is Directory " + targetDir.getAbsolutePath() + "__" + targetDir.isDirectory() );
				File[] files = targetDir.listFiles();
				System.out.println( "Running installer " + files[0] );
				if( files.length > 0 )
				{
					String[] arg = new String[2];
					arg[0] = "open";
					arg[1] = files[0].getAbsolutePath();
					pb = new ProcessBuilder( arg );
					pb.start().waitFor();
				}

			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		return p;
	}

	public static void removeTempFile()
	{
		if( tempFile.exists() )
		{
			tempFile.deleteOnExit();
		}
	}

	public static void closeWindow()
	{
		if( progressBar != null )
		{
			if( progressBar.getFrame() != null )
			{
				progressBar.getFrame().setVisible( false );
				progressBar.getFrame().dispose();
			}
			System.exit( 0 );
		}
	}
}
