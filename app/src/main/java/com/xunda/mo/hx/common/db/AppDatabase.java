package com.xunda.mo.hx.common.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.xunda.mo.hx.common.db.converter.DateConverter;
import com.xunda.mo.hx.common.db.dao.AppKeyDao;
import com.xunda.mo.hx.common.db.dao.EmUserDao;
import com.xunda.mo.hx.common.db.dao.InviteMessageDao;
import com.xunda.mo.hx.common.db.dao.MsgTypeManageDao;
import com.xunda.mo.hx.common.db.entity.AppKeyEntity;
import com.xunda.mo.hx.common.db.entity.EmUserEntity;
import com.xunda.mo.hx.common.db.entity.InviteMessage;
import com.xunda.mo.hx.common.db.entity.MsgTypeManageEntity;


@Database(entities = {EmUserEntity.class,
        InviteMessage.class,
        MsgTypeManageEntity.class,
        AppKeyEntity.class},
        version = 17)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract EmUserDao userDao();

    public abstract InviteMessageDao inviteMessageDao();

    public abstract MsgTypeManageDao msgTypeManageDao();

    public abstract AppKeyDao appKeyDao();
}
