/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.n3fr0.n3talk.mirror.entity;

import java.util.List;

/**
 *
 * @author Administrator
 */
public class MirrorEntity {

    public String getClassFullName() {
        return classFullName;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    private String classFullName;
    private List<String> properties;
}
