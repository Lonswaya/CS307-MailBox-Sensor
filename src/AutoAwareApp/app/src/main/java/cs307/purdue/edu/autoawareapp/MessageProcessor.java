package cs307.purdue.edu.autoawareapp;
/* To be used for any class that wants to receive something from CentralServer
 * Must implement ProcessMessage
 */
import cs307.purdue.edu.autoawareapp.*;
public interface MessageProcessor {
	public abstract void ProcessMessage(Message msg);
}
