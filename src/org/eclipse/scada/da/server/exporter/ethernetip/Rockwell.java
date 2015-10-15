package org.eclipse.scada.da.server.exporter.ethernetip;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
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
	private static String HIVE_ID = "org.eclipse.scada.da.server.exporter.ethernetip.Rockwell";
	private FolderCommon rootFolder;
	private SimpleLogixCommunicator comm;
	private ScheduledExecutorService scheduler = null;
	private Map<String, RockwellDataItem> memory;
	private static int MAX_READ_TAGS = 16;
	private static int MEMORY_BLOCK_SIZE = 64;
		
	public Rockwell() {
		super();
		
		this.rootFolder = new FolderCommon();
		
		this.setRootFolder(this.rootFolder);
		
		this.memory = new Hashtable<>();
	}
	
	@Override
	public String getHiveId() {
		return HIVE_ID;
	}
	
	@Override
	protected void performStart() throws Exception {
		super.performStart();
		
		final String host = "127.0.0.1";
        final int port = 44818;
        comm = new SimpleLogixCommunicator(host, port);
        
        for (int i = 0; i < MEMORY_BLOCK_SIZE; i++) {
			final String tagName = String.format("tag1[%d]", i);
			final RockwellDataItem item = new RockwellDataItem(tagName, comm);
			registerItem(item);
			memory.put(tagName, item);
			this.rootFolder.add(tagName, item, null);
		}
		this.scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(this.HIVE_ID));
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
		try {
			Object[] objects = ((Object[])comm.read("tag1", MEMORY_BLOCK_SIZE));
			for (int i=0; i < objects.length; i++) {
				Object value = objects[i];
				String key = String.format("tag1[%d]", i);
				RockwellDataItem item = memory.get(key);
				item.updateData (
		                Variant.valueOf ( value ),
		                new MapBuilder<String, Variant> ()
		                        //.put ( "description", ariant.valueOf ( "some item" ) )
		                        .put ( "timestamp",
		                                Variant.valueOf ( System.currentTimeMillis () ) )
		                        .getMap (), AttributeMode.UPDATE );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}
	}
	
}
