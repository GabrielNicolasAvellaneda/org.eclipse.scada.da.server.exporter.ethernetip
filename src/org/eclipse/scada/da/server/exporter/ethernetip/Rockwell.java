package org.eclipse.scada.da.server.exporter.ethernetip;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.common.impl.HiveCommon;
import org.eclipse.scada.utils.collection.MapBuilder;

public class Rockwell extends HiveCommon {

	private static String hiveId = "org.eclipse.scada.da.server.exporter.ethernetip.Rockwell";
	private FolderCommon rootFolder;
	
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
		
		final RockwellDataItem item = new RockwellDataItem("some-item");
		registerItem(item);
		this.rootFolder.add("some-item", item, null);
		
		item.updateData (
                Variant.valueOf ( 0 ),
                new MapBuilder<String, Variant> ()
                        .put ( "description",
                                Variant.valueOf ( "some item" ) )
                        .put ( "timestamp",
                                Variant.valueOf ( System.currentTimeMillis () ) )
                        .getMap (), AttributeMode.UPDATE );
	}

}
