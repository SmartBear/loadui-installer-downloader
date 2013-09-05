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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBar
{
	private static final String MAVEN_LOADUI_DOWNLOADER_PRO = "maven.loadui.downloader.pro";
	private static final String LOADUI_PIC_24_BIT_PNG = "Installer_pic_loadUI_PNG.png";
	private static final String PRO_LOADUI_PIC_24_BIT_PNG = "loadUI-pro-general_installer-pic-24-bit-PNG.png";
	private static final String FRAME_ICON = "16-perc.gif";
	private static final String RESOURCES = "/com/eviware/resources/";
	private static final String FRAME_TITLE = "Setup - soapUI installer";
	private static final String PRO_FRAME_ICON = "16-orange-perc.gif";;
	private JFrame frame;
	private JProgressBar progressBar;
	private boolean pro;

	public ProgressBar( int size )
	{
		if( System.getProperty( MAVEN_LOADUI_DOWNLOADER_PRO ) != null )
		{
			this.pro = Boolean.valueOf( System.getProperty( MAVEN_LOADUI_DOWNLOADER_PRO ) );
		}
		frame = new JFrame( FRAME_TITLE );
		frame.setIconImage( getFrameIcon().getImage() );

		frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		addClosingListener();

		frame.setResizable( false );
		frame.setLocation( 300, 300 );

		Container content = frame.getContentPane();
		progressBar( size );

		content.add( westPanel(), BorderLayout.WEST );

		content.add( centerPanel(), BorderLayout.CENTER );

		content.add( eastPanel(), BorderLayout.EAST );

		frame.setSize( 500, 350 );
		frame.setVisible( true );
	}

	private ImageIcon getLoaduiImage()
	{
		String path = null;
		if( pro )
		{
			path = RESOURCES + PRO_LOADUI_PIC_24_BIT_PNG;
		}
		else
		{
			path = RESOURCES + LOADUI_PIC_24_BIT_PNG;
		}
		return getImageIcon( path );
	}

	private ImageIcon getImageIcon( String path )
	{
		java.net.URL imgURL = null;

		try
		{
			File file = new File( path );
			if( file.exists() )
			{
				imgURL = file.toURI().toURL();
			}
			else
			{
				imgURL = getClass().getResource( path );
			}
			ImageIcon imageIcon = new ImageIcon( imgURL );
			return imageIcon;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return null;
	}

	private ImageIcon getFrameIcon()
	{
		String path = null;
		if( pro )
		{
			path = RESOURCES + PRO_FRAME_ICON;
		}
		else
		{
			path = RESOURCES + FRAME_ICON;
		}
		return getImageIcon( path );
	}

	private void progressBar( int size )
	{
		if( size != -1 )
		{
			progressBar = new JProgressBar( 0, size );
		}
		else
		{
			progressBar = new JProgressBar();
		}
		progressBar.setStringPainted( true );
		progressBar.setString( "" );
		progressBar.setValue( 0 );
		progressBar.setBorderPainted( false );
		progressBar.setBackground( Color.WHITE );
		progressBar.setBorder( BorderFactory.createMatteBorder( 5, 7, 5, 7, Color.WHITE ) );
		if( pro )
		{
			progressBar.setForeground( new Color( 255, 127, 32 ) );
		}
		else
		{
			progressBar.setForeground( Color.GREEN );
		}
	}

	private JLabel westPanel()
	{
		JLabel westArea = new JLabel( getLoaduiImage() );
		return westArea;
	}

	private JPanel centerPanel()
	{
		JPanel centerArea = new JPanel();
		centerArea.setBackground( Color.WHITE );
		centerArea.setLayout( new BoxLayout( centerArea, BoxLayout.Y_AXIS ) );
		centerArea.add( Box.createRigidArea( new Dimension( 0, 140 ) ) );
		centerArea.add( new JLabel( "Downloading loadUI " + ( pro ? "Pro" : "" ) ) );
		centerArea.add( new JLabel( "Please wait..." ) );
		centerArea.add( Box.createRigidArea( new Dimension( 0, 20 ) ) );
		centerArea.add( progressBar );
		centerArea.add( Box.createVerticalGlue() );
		return centerArea;
	}

	private JPanel eastPanel()
	{
		JPanel eastArea = new JPanel();
		eastArea.setBackground( Color.WHITE );
		eastArea.setBorder( BorderFactory.createMatteBorder( 5, 16, 5, 16, Color.WHITE ) );
		eastArea.setLayout( new BoxLayout( eastArea, BoxLayout.Y_AXIS ) );
		JButton button = new JButton( new CancelAction() );
		button.setText( "Cancel" );
		eastArea.add( Box.createRigidArea( new Dimension( 0, 280 ) ) );
		eastArea.add( button, BorderLayout.CENTER );
		eastArea.add( Box.createVerticalGlue() );
		return eastArea;
	}

	public void setProgressValue( int progress )
	{
		progressBar.setValue( progress );
	}

	protected JProgressBar getProgressBar()
	{
		return progressBar;
	}

	private void addClosingListener()
	{
		frame.addWindowListener( new WindowAdapter()
		{
			public void windowClosing( WindowEvent e )
			{
				confirmClose();
			}

		} );
	}

	private void confirmClose()
	{
		int confirmed = JOptionPane.showConfirmDialog( null, "Are you sure?", "Cancel download",
				JOptionPane.YES_NO_OPTION );
		if( confirmed == JOptionPane.YES_OPTION )
		{
			Downloader.removeTempFile();
			closeWindow();
		}
	}

	public void closeWindow()
	{
		if( frame != null )
		{
			frame.setVisible( false );
			frame.dispose();
		}
		System.exit( 0 );
	}

	private class CancelAction extends AbstractAction
	{
		public void actionPerformed( ActionEvent e )
		{
			confirmClose();

		}
	}

	public JFrame getFrame()
	{
		return frame;
	}
}