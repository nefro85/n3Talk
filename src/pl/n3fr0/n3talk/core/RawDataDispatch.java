/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.n3fr0.n3talk.core;

import pl.n3fr0.n3talk.core.data.RawData;

/**
 *
 * @author Administrator
 */
public interface RawDataDispatch {
    void onRawData(RawData data);
}
