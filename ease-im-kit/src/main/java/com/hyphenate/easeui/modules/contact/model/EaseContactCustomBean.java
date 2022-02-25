package com.hyphenate.easeui.modules.contact.model;

public class EaseContactCustomBean {
    private int id;
    private String name;
    private int resourceId;
    private String image;
    private boolean haveNoReadPoint;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isHaveNoReadPoint() {
        return haveNoReadPoint;
    }

    public void setHaveNoReadPoint(boolean haveNoReadPoint) {
        this.haveNoReadPoint = haveNoReadPoint;
    }
}

