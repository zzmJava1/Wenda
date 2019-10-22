package com.newcode.async;

import java.util.HashMap;
import java.util.Map;

/*
事件载体
 */
public class EventModel {
    private EventType eventType;//事件类型
    private int actorId;//执行者
    private int entityType;//被执行
    private int entityId;
    private  int entityOwner;
    //扩展字段用来记录其他信息
    private Map<String,String> exts = new HashMap<>();

    public EventModel() {

    }

    public EventModel(EventType eventType) {
        this.eventType = eventType;

    }



    public EventModel setExt(String key,String value){
        exts.put(key,value);
        return this;
    }
    public String getExt(String key){
        return exts.get(key);
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwner() {
        return entityOwner;
    }

    public EventModel setEntityOwner(int entityOwner) {
        this.entityOwner = entityOwner;
        return this;

    }

    public Map<String, String> getMap() {
        return exts;
    }

    public void setMap(Map<String, String> map) {
        this.exts = map;
    }
}
