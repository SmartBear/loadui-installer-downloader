/*
 *  soapUI, copyright (C) 2004-2011 smartbear.com 
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class VersionDetector
{
	static final String LATEST_VERSION_XML_LOCATION =  "http://dl.eviware.com/version-update/loadui-version.xml" ;

	private String latestVersion;

	public String getLatestVersionAvailable( Proxy proxy ) throws IOException, ParserConfigurationException, SAXException
	{
		Document doc = getVersionDocument( proxy );

		doc.getDocumentElement().normalize();
		NodeList nodeLst = doc.getElementsByTagName( "version" );

		Node fstNode = nodeLst.item( 0 );

		if( fstNode.getNodeType() == Node.ELEMENT_NODE )
		{

			Element fstElmnt = ( Element )fstNode;

			NodeList vrsnNmbrElmntLst = fstElmnt.getElementsByTagName( "version-number" );
			Element vrsnNmbrElmnt = ( Element )vrsnNmbrElmntLst.item( 0 );
			NodeList vrsnNmbr = vrsnNmbrElmnt.getChildNodes();
			latestVersion = ( ( Node )vrsnNmbr.item( 0 ) ).getNodeValue().toString();
		}
		return latestVersion;
	}


	protected Document getVersionDocument( Proxy proxy ) throws ParserConfigurationException,
			SAXException, IOException
	{
		URL versionUrl = new URL( LATEST_VERSION_XML_LOCATION );

		URLConnection conn;

		if( proxy != null )
		{
			conn = versionUrl.openConnection( proxy );
		} else {
			conn = versionUrl.openConnection();
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse( conn.getInputStream() );
		return doc;
	}


}
