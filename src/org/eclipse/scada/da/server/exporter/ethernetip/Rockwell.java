package org.eclipse.scada.da.server.exporter.ethernetip;

import java.io.IOException;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.common.impl.HiveCommon;
import org.eclipse.scada.utils.collection.MapBuilder;

import se.opendataexchange.ethernetip4j.clx.SimpleLogixCommunicator;
import se.opendataexchange.ethernetip4j.exceptions.EmbeddedServiceException;
import se.opendataexchange.ethernetip4j.exceptions.InsufficientCommandException;
import se.opendataexchange.ethernetip4j.exceptions.InsufficientNrOfAttributesException;
import se.opendataexchange.ethernetip4j.exceptions.InvalidTypeException;
import se.opendataexchange.ethernetip4j.exceptions.ItemNotFoundException;
import se.opendataexchange.ethernetip4j.exceptions.NotImplementedException;
import se.opendataexchange.ethernetip4j.exceptions.OtherWithExtendedCodeException;
import se.opendataexchange.ethernetip4j.exceptions.PathSegmentException;
import se.opendataexchange.ethernetip4j.exceptions.ProcessingAttributesException;
import se.opendataexchange.ethernetip4j.exceptions.ResponseBufferOverflowException;

public class Rockwell extends HiveCommon {
	private static String hiveId = "org.eclipse.scada.da.server.exporter.ethernetip.Rockwell";
	private FolderCommon rootFolder;
	private SimpleLogixCommunicator comm;
	
	public Rockwell() {
		super();
		
		this.rootFolder = new FolderCommon();
		
		this.setRootFolder(this.rootFolder);
	}
	
	@Override
	public String getHiveId() {
		return hiveId;
	}
	
	@Override
	protected void performStart() throws Exception {
		super.performStart();
		
		final String host = "127.0.0.1";
        final int port = 44818;
        comm = new SimpleLogixCommunicator(host, port);
        final String tagName = "tag1[1]";
		
		final RockwellDataItem item = new RockwellDataItem(tagName);
		registerItem(item);
		this.rootFolder.add(tagName, item, null);
		
		String[] tags = { tagName };
        Object[] objects = comm.read(tags);
        if (objects.length > 0) {
        	System.out.println(objects[0]);
			item.updateData (
	                Variant.valueOf ( objects[0] ),
	                new MapBuilder<String, Variant> ()
	                        .put ( "description",
	                                Variant.valueOf ( "some item" ) )
	                        .put ( "timestamp",
	                                Variant.valueOf ( System.currentTimeMillis () ) )
	                        .getMap (), AttributeMode.UPDATE );
	        }
	}
	
}
