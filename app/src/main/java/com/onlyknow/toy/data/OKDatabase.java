package com.onlyknow.toy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.onlyknow.toy.data.model.OKGanKResultModel;

import java.sql.SQLException;

/**
 * Created by Administrator on 2017/12/11.
 */

public class OKDatabase extends OrmLiteSqliteOpenHelper {
    private static final String TABLE_NAME = "gank.db";
    private Dao<OKGanKResultModel, Integer> gankDao;

    private OKDatabase(Context context) {
        super(context, TABLE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, OKGanKResultModel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, OKGanKResultModel.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static OKDatabase instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized OKDatabase getHelper(Context context) {
        if (instance == null) {
            synchronized (OKDatabase.class) {
                if (instance == null)
                    instance = new OKDatabase(context);
            }
        }
        return instance;
    }

    /**
     * 获得CardDao
     *
     * @return
     * @throws SQLException
     */
    public Dao<OKGanKResultModel, Integer> getCardDao() throws SQLException {
        if (gankDao == null) {
            gankDao = getDao(OKGanKResultModel.class);
        }
        return gankDao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        gankDao = null;
    }
}
