import java.io.IOException;
import java.net.URI;
import java.util.Calendar;

import net.tootallnate.websocket.WebSocketClient;

public class WebsocketClient extends WebSocketClient implements SocketClient {
	private SocketClientEventListener listener;
	
	protected static int nextId = 0;
	protected int id;	
	
	public WebsocketClient(URI server, SocketClientEventListener listener) {
		super(server);
		
		// Store listener
		this.listener = listener;
		
		// Get myself new id
		id = nextId;
		nextId++;
	}
	
	@Override
	public void onClose() {
		this.listener.onClose();
	}

	@Override
	public void onIOError(IOException arg0) {
		System.out.println("error: " + arg0);
	}	
	
	@Override
	public void onMessage(String message) {
		long messageArrivedAt = Calendar.getInstance().getTimeInMillis();

		// Quick and dirty message unpacking
		long roundtripTime;
		String[] payloadParts = message.split(":");
		if(new Integer(this.id).toString().compareTo(payloadParts[0])==0) {
			roundtripTime = messageArrivedAt - new Long(payloadParts[1]);
			this.listener.messageArrivedWithRoundtrip(roundtripTime);					
		}

		this.listener.onMessage(message);
	}

	@Override
	public void onOpen() {
		this.listener.onOpen();
	}

	// Interface implementation
	public void sendMessage(String message) {
		try {
			this.send(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendTimestamp() {
		String message = this.id + ":" + new Long(Calendar.getInstance().getTimeInMillis()).toString();
		this.sendMessage(message);
	}
	
	public void connect()
	{
		super.connect();
	}
	
	public void close() throws IOException
	{
		super.close();
	}
}
