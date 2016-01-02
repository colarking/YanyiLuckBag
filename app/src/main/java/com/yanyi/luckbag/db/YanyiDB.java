package com.yanyi.luckbag.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yanyi.luckbag.activity.MatrixApplication;
import com.yanyi.luckbag.bean.IncomeBean;
import com.yanyi.luckbag.bean.YanyiBean;
import com.yanyi.luckbag.util.AmayaEvent;
import com.yanyi.luckbag.util.AmayaLog;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import de.greenrobot.event.EventBus;

/**
 * Created by amayababy
 * 2015-12-18
 * 下午2:46
 */
public class YanyiDB extends SQLiteOpenHelper {

    private static final String TAG = YanyiDB.class.getSimpleName();
    private static YanyiDB amayaDB;
    private SQLiteDatabase db;
    private AtomicInteger mOpenCounter = new AtomicInteger();

    private YanyiDB(Context context) {
        super(context, "yanyilove.db", null, 1);
    }

    private YanyiDB(Context context, int version) {
        super(context, "yanyilove.db", null, version);
    }

    public YanyiDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public YanyiDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public static YanyiDB getInstance() {
        if (amayaDB == null)
            amayaDB = new YanyiDB(MatrixApplication.mContext);
        return amayaDB;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**
         private String name,time,sendBagName;
         private float price;
         private boolean isBest;
         private int luckId;
         private long insertTime;
         */
        String sql = "create table if not exists YanyiBean(luckId integer primary key autoincrement default -1," +
                "name varchar,time varchar,sendBagName varchar," +
                "money float,isBest integer,insertTime long,bagIndex integer,timeMD varchar);";
        db.execSQL(sql);
        sql = "create table if not exists IncomeBean(name varchar primary key,inMoney float,outMoney float,lastInMoney float,inOutMoney float,insertTime long);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public synchronized void initDataBase() {
        int incrementAndGet = mOpenCounter.incrementAndGet();
        if (db == null) {
            db = getWritableDatabase();
        } else if (!db.isOpen()) {
            db = getWritableDatabase();
        }
    }

    public void closeDB() {
        int decrementAndGet = mOpenCounter.decrementAndGet();
        AmayaLog.e(TAG, "amayaDB...closeDB()...decrementAndGet=" + decrementAndGet);
        if (decrementAndGet == 0) {
            try {
                if (db != null) {
                    db.close();
                    db = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void releaseDB() {
        while (mOpenCounter.decrementAndGet() <= 0) {
            try {
                if (db != null) db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        }
    }

    public void insert(ArrayList<YanyiBean> beans, YanyiBean sendUser, boolean close, boolean equals) {
        try {
            initDataBase();
            db.beginTransaction();
            boolean exists = insertYanyiBean(sendUser, false, beans.size(), equals);
            if (exists) {
                AmayaLog.e(TAG, "insert()..旧包");
                EventBus.getDefault().post(new AmayaEvent.AlreadyInsertEvent(true));
                return;
            } else {
                AmayaLog.e(TAG, "insert()..新包");
                EventBus.getDefault().post(new AmayaEvent.AlreadyInsertEvent(false));
            }
            long startTime = System.currentTimeMillis();
            boolean insertSendUser = false;
            for (int i = 0; i < beans.size(); i++) {
                YanyiBean bean = beans.get(i);
                if (bean != null) {
                    insertYanyiBean(bean, exists, 0, equals);

                    //查询收入库数据
                    String sql = "select * from IncomeBean where name=?";
                    Cursor cursor = db.rawQuery(sql, new String[]{bean.getName()});
                    IncomeBean incomeBean = new IncomeBean();
                    if (cursor != null && cursor.getCount() > 0) {
                        //存在用户
                        cursor.moveToFirst();
                        incomeBean.setName(cursor.getString(0));
                        float inMoney = cursor.getFloat(1);
                        incomeBean.setInMoney(inMoney + bean.getMoney());
                        incomeBean.setOutMoney(cursor.getFloat(2));
                        incomeBean.setLastInMoney(cursor.getFloat(3));
                        incomeBean.setInOutMoney(cursor.getFloat(4) + bean.getMoney());
                        incomeBean.setInsertTime(startTime);
                    } else {
                        //不存在用户
                        incomeBean.setName(bean.getName());
                        incomeBean.setInMoney(bean.getMoney());
                        incomeBean.setLastInMoney(bean.getMoney());
                        incomeBean.setInOutMoney(bean.getMoney());
                        incomeBean.setInsertTime(startTime);
                    }
                    cursor.close();
                    boolean sameUser = sendUser.getName().equals(bean.getName());
                    if (!insertSendUser && sameUser) {
                        insertSendUser = true;
                    }
                    if (sameUser) {
                        incomeBean.setOutMoney(incomeBean.getOutMoney() + sendUser.getMoney());
                        incomeBean.setInOutMoney(incomeBean.getInOutMoney() - sendUser.getMoney());
                    }
                    insertIncomeBean(incomeBean);
                }
            }
            if (!insertSendUser) {
                String sql = "select * from IncomeBean where name=?";
                Cursor cursor = db.rawQuery(sql, new String[]{sendUser.getName()});
                IncomeBean incomeBean = new IncomeBean();
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    incomeBean.setName(cursor.getString(0));
                    float inMoney = cursor.getFloat(1);
                    incomeBean.setInMoney(inMoney);
                    incomeBean.setOutMoney(cursor.getFloat(2) + sendUser.getMoney());
                    incomeBean.setLastInMoney(cursor.getFloat(3));
                    incomeBean.setInOutMoney(cursor.getFloat(4) - sendUser.getMoney());
                    incomeBean.setInsertTime(startTime);
                } else {
                    incomeBean.setName(sendUser.getName());
                    incomeBean.setInsertTime(startTime);
                    incomeBean.setOutMoney(sendUser.getMoney());
                    incomeBean.setInOutMoney(-sendUser.getMoney());
                }
                cursor.close();
                insertIncomeBean(incomeBean);
            }
            db.setTransactionSuccessful();
            long dis = System.currentTimeMillis() - startTime;
            AmayaLog.e(TAG, "insert()...插入总共耗时：" + dis / 1000f + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            if (close) closeDB();
        }
    }

    private void insertIncomeBean(IncomeBean incomeBean) {
        ContentValues cv2 = new ContentValues();
        cv2.put("name", incomeBean.getName());
        cv2.put("inMoney", incomeBean.getInMoney());
        cv2.put("outMoney", incomeBean.getOutMoney());
        cv2.put("lastInMoney", incomeBean.getLastInMoney());
        cv2.put("inOutMoney", incomeBean.getInOutMoney());
        cv2.put("insertTime", incomeBean.getInsertTime());
        long insert = db.replace("IncomeBean", "name", cv2);
        AmayaLog.e(TAG, "insertIncomeBean()...insert=" + insert + "--" + cv2.toString());
    }

    private boolean insertYanyiBean(YanyiBean bean, boolean exists, int size, boolean equals) {
        ContentValues cv = new ContentValues(5);
        cv.put("name", bean.getName());
        cv.put("time", bean.getTime());
        cv.put("sendBagName", bean.getSendBagName());
        cv.put("money", bean.getMoney());
        cv.put("isBest", bean.isBest());
        cv.put("timeMD", bean.getTimeMD());
        cv.put("bagIndex", bean.getBagIndex());
        //插入信息库
        if (size > 0) {
            Cursor cursor = db.rawQuery("select money from YanyiBean where insertTime = (select insertTime from YanyiBean where time=? and money = ? )", new String[]{String.valueOf(bean.getTime()), String.valueOf(bean.getMoney())});
            if (cursor != null) {
                int count = cursor.getCount();
                cursor.close();
                if (count > 0 && count >= (equals ? size : size + 1)) {
                    return true;
                }
            }
        }
        Cursor cursor = db.rawQuery("select insertTime from YanyiBean where time=? and name = ? and money = ? and bagIndex= ?", new String[]{String.valueOf(bean.getTime()), bean.getName(), Float.toString(bean.getMoney()), Integer.toString(bean.getBagIndex())});
        if (cursor != null) {
            int count = cursor.getCount();
            AmayaLog.e(TAG, "insertYanyiBean()...queryCount=" + count);
            cursor.close();
            if (count == 1) {
                return true;
            }
        }
        int update = db.update("YanyiBean", cv, "name=? and time = ? and sendBagName is not null", new String[]{bean.getName(), bean.getTime()});
        AmayaLog.e(TAG, "insertYanyiBean()...update=" + update + "--" + cv.toString());
        if (update == 0) {
            cv.put("insertTime", bean.getInsertTime());
            long replace = db.insert("YanyiBean", "name", cv);
            AmayaLog.e(TAG, "insertYanyiBean()...replace=" + replace + "--" + cv.toString());
        } else {


        }
        return update > 0;
//        long replace = db.replace("YanyiBean", "luckId", cv);
    }


    public void listAll(int limit, int count) {
        String sql = "select * from IncomeBean where insertTime >= " + MatrixApplication.TODAY_MILLS + " order by inOutMoney desc limit " + limit + "," + count;
        AmayaLog.e(TAG, "listAll()...limit=" + limit + "--count=" + count);
        try {
            initDataBase();
            Cursor cursor1 = db.rawQuery(sql, new String[]{});
            findIncomeBeans(cursor1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB();
        }
    }

    public void listAll() {
        try {
            initDataBase();
            Cursor cursor = db.rawQuery("select * from YanyiBean", null);
            findYanyiBeans(cursor);
            Cursor cursor1 = db.rawQuery("select * from IncomeBean order by inOutMoney desc", null);
            findIncomeBeans(cursor1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB();
        }
    }

    private void findIncomeBeans(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            int i = 0;
            ArrayList<IncomeBean> been = new ArrayList<>();
            while (cursor.moveToNext()) {
                IncomeBean incomeBean = new IncomeBean();
                incomeBean.setName(cursor.getString(0));
                float inMoney = cursor.getFloat(1);
                incomeBean.setInMoney(inMoney);
                incomeBean.setOutMoney(cursor.getFloat(2));
                incomeBean.setLastInMoney(cursor.getFloat(3));
                incomeBean.setInOutMoney(cursor.getFloat(4));
                incomeBean.setInsertTime(cursor.getLong(5));
                been.add(incomeBean);
                AmayaLog.e(TAG, "findIncomeBeans()...i=" + i++ + "--" + incomeBean.toString());
            }
            EventBus.getDefault().post(new AmayaEvent.IncomeBeansEvent(been));
        } else {
            EventBus.getDefault().post(new AmayaEvent.IncomeBeansEvent(null));

        }

    }

    private void findYanyiBeans(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            int i = 0;
            while (cursor.moveToNext()) {
                int luckId = cursor.getInt(0);
                String name = cursor.getString(1);
                String time = cursor.getString(2);
                String sendBagName = cursor.getString(3);
                float money = cursor.getFloat(4);
                int best = cursor.getInt(5);
                boolean isBest = best == 1 ? true : false;
                long insertTime = cursor.getLong(6);
                int index = cursor.getInt(7);
                String timeMD = cursor.getString(8);
                YanyiBean bean = new YanyiBean();
                bean.setLuckId(luckId);
                bean.setName(name);
                bean.setTime(time);
                bean.setMoney(money);
                bean.setSendBagName(sendBagName);
                bean.setBest(isBest);
                bean.setInsertTime(insertTime);
                bean.setBagIndex(index);
                bean.setTimeMD(timeMD);
                AmayaLog.e(TAG, "findYanyiBeans()...i=" + i++ + "--" + bean.toString());
            }
        }
    }


    public IncomeBean findSelfIncomeBean(String name, boolean close) {

        String sql = "select * from IncomeBean where name = ?";
        AmayaLog.e(TAG, "findSelfIncomeBean()...name=" + name);
        IncomeBean incomeBean = null;
        try {
            initDataBase();
            Cursor cursor = db.rawQuery(sql, new String[]{name});
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToNext();
                incomeBean = new IncomeBean();
                incomeBean.setName(cursor.getString(0));
                float inMoney = cursor.getFloat(1);
                incomeBean.setInMoney(inMoney);
                incomeBean.setOutMoney(cursor.getFloat(2));
                incomeBean.setLastInMoney(cursor.getFloat(3));
                incomeBean.setInOutMoney(cursor.getFloat(4));
                incomeBean.setInsertTime(cursor.getLong(5));
                cursor.close();
                AmayaLog.e(TAG, "findSelfIncomeBean().." + incomeBean.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (close) closeDB();
        }
        return incomeBean;
    }

    public int[] deleteIncomeDB() {
        int[] delete = new int[2];
        try {
            initDataBase();
            delete[0] = db.delete("IncomeBean", null, null);
            delete[1] = db.delete("YanyiBean", null, null);
        } catch (Exception e) {
            delete[0] = delete[1] = -1;
            e.printStackTrace();
        } finally {
            closeDB();
        }
        return delete;
    }
}
