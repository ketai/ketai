/* 
 * Simple queue class to store sensor events, based on the event queue class
 * in Processing core.
 * 
 */

package ketai.sensors;

import processing.core.PApplet;

/**
 * Instantiates a queue for sensor events
 * 
 */
public class SensorQueue {
  private Object[] queue = new Object[10];
  private int offset;
  private int count;
    
  synchronized void add(Object val) {
    if (count == queue.length) {
      queue = (Object[]) PApplet.expand(queue);
    }
    queue[count++] = val;
  }
  
  synchronized Object remove() {
    if (offset == count) {
      throw new RuntimeException("Sensor queue is empty.");
    }
    Object outgoing = queue[offset++];
    if (offset == count) {
      // All done, time to reset
      offset = 0;
      count = 0;
    }
    return outgoing;
  }
  
  synchronized boolean available() {
    return count != 0;
  }  
}
