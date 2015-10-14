package org.eclipse.scada.da.server.exporter.ethernetip;

import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.common.impl.HiveCommon;

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
	}

}
