package com.eviware.downloader;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Anders Jaensson
 */
public class VersionDetectorTest
{

	@Test
	@Ignore("Not a unit test and should not run automatically")
	public void testGetLatestVersion() throws ParserConfigurationException, SAXException, IOException
	{
		VersionDetector detector = new VersionDetector();
		String latestVersionAvailable = detector.getLatestVersionAvailable( null );
		assertEquals( "2.6.1", latestVersionAvailable );

	}
}
