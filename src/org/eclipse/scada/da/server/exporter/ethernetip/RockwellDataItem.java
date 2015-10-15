package org.eclipse.scada.da.server.exporter.ethernetip;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import org.eclipse.scada.core.InvalidOperationException;
import org.eclipse.scada.core.NotConvertableException;
import org.eclipse.scada.core.NullValueException;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.server.OperationParameters;
import org.eclipse.scada.da.core.WriteAttributeResult;
import org.eclipse.scada.da.core.WriteAttributeResults;
import org.eclipse.scada.da.core.WriteResult;
import org.eclipse.scada.da.data.IODirection;
import org.eclipse.scada.da.server.common.AttributeManager;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.common.DataItemBase;
import org.eclipse.scada.da.server.common.DataItemInformationBase;
import org.eclipse.scada.da.server.common.DataItemInputCommon;
import org.eclipse.scada.da.server.common.MemoryDataItem;
import org.eclipse.scada.utils.concurrent.InstantFuture;
import org.eclipse.scada.utils.concurrent.NotifyFuture;

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

public class RockwellDataItem extends DataItemBase {

	private volatile Variant value = Variant.NULL;
	private AttributeManager attributes = null;
	private SimpleLogixCommunicator comm = null;
		
	public RockwellDataItem(final String name, SimpleLogixCommunicator comm) {
		this ( name, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) );
		
		this.comm = comm; 
	}

	public RockwellDataItem(final String name, EnumSet<IODirection> ioDirection) {
		  super ( new DataItemInformationBase ( name, ioDirection ) );
	        this.attributes = new AttributeManager ( this );
	}
	
	@Override
    public NotifyFuture<Variant> readValue () throws InvalidOperationException
    {
        return new InstantFuture<Variant> ( this.value );
    }
	
    @Override
    public NotifyFuture<WriteResult> startWriteValue ( final Variant value, final OperationParameters operationParameters )
    {
        if ( !this.value.equals ( value ) )
        {
            this.value = value;
            notifyData ( value, null );
            
            try {
				this.comm.write(this.getInformation().getName(), value.asInteger());
			} catch (PathSegmentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ItemNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResponseBufferOverflowException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProcessingAttributesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InsufficientCommandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InsufficientNrOfAttributesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OtherWithExtendedCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (EmbeddedServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotImplementedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotConvertableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        // we can handle this directly
        return new InstantFuture<WriteResult> ( new WriteResult () );
    }

    @Override
    public Map<String, Variant> getAttributes ()
    {
        return this.attributes.get ();
    }

    @Override
    protected Map<String, Variant> getCacheAttributes ()
    {
        return this.attributes.get ();
    }

    @Override
    protected Variant getCacheValue ()
    {
        return this.value;
    }

    @Override
    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        final WriteAttributeResults writeAttributeResults = new WriteAttributeResults ();

        this.attributes.update ( null, attributes );

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            writeAttributeResults.put ( entry.getKey (), WriteAttributeResult.OK );
        }

        return new InstantFuture<WriteAttributeResults> ( writeAttributeResults );
    }
    
    /**
     * Update the value of this data item
     * 
     * @param value
     *            the new value
     */
    public synchronized void updateData ( Variant value, final Map<String, Variant> attributes, final AttributeMode mode )
    {
        if ( this.value == null || !this.value.equals ( value ) )
        {
            this.value = value;
        }
        else
        {
            value = null;
        }

        this.attributes.update ( value, attributes, mode );
    }

}
