/* To be used for any class that wants to receive something from Central Server
 * Must implement ProcessMessage
 */
package cs307.purdue.edu.autoawareapp;
interface MessageProcessor {
	public abstract void ProcessMessage(Message msg);
}
