package org.eclipse.scada.da.server.exporter.ethernetip;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.common.impl.HiveCommon;
import org.eclipse.scada.utils.collection.MapBuilder;
import org.eclipse.scada.utils.concurrent.NamedThreadFactory;

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

public class Rockwell extends HiveCommon implements Runnable {
	private static String hiveId = "org.eclipse.scada.da.server.exporter.ethernetip.Rockwell";
	private FolderCommon rootFolder;
	private SimpleLogixCommunicator comm;
	private ScheduledExecutorService scheduler = null;
	private RockwellDataItem item = null;
	private static String tagName = "tag1[1]";
		
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
		item = new RockwellDataItem(tagName);
		registerItem(item);
		this.rootFolder.add(tagName, item, null);
		
		this.scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(this.hiveId));
		this.scheduler.scheduleAtFixedRate(this, 0, 1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	protected void performStop() throws Exception {
		if (this.scheduler != null) {
			this.scheduler.shutdown();
		}
		
		super.performStop();
	}

	@Override
	public void run() {
		String[] tags = { tagName };
        Object[] objects;
		try {
			objects = comm.read(tags);
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
		} catch (PathSegmentException | ItemNotFoundException
				| ProcessingAttributesException | InsufficientCommandException
				| InsufficientNrOfAttributesException
				| OtherWithExtendedCodeException
				| ResponseBufferOverflowException | InvalidTypeException
				| IOException | EmbeddedServiceException
				| NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
