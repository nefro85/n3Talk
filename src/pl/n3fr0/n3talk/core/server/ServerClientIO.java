/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.n3fr0.n3talk.core.server;

import pl.n3fr0.n3talk.core.RawDataSend;

/**
 *
 * @author Administrator
 */
public interface ServerClientIO extends RawDataSend {
    String getUID();
}
