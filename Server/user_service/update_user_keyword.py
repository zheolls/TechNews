from time import sleep

from settings import mydb, USER_KEYWORD_INSTANT_INTERVAL
import pandas
MAX_DATA=60
import datetime
import math
def need_instant_update():
    cursor = mydb.cursor()
    sql='select * from user_click where times>10'
    cursor.execute(sql)
    results=cursor.fetchall()
    if len(results)>0:
        return True
    else:
        return False

def user_keyword_update(action):
    cursor = mydb.cursor()
    if action==1:
        sql = 'select * from user_keyword_daily_view'
        sql2 = 'delete from user_keyword where exists(select * from user_click where user_click.userid=user_keyword.userid)'

    else:
        sql = 'select * from user_keyword_update_instant_view'
        sql2='delete from user_keyword where exists(select * from user_click where user_click.userid=user_keyword.userid and times>10)'

    cursor.execute(sql)
    results=cursor.fetchall()
    cursor.execute(sql2)
    mydb.commit()
    lists=[]
    if len(results)==0:
        print(datetime.datetime.now(),'没有可更新的用户')
        return
    for item in results:
        userid=item[0]
        keyword=item[1]
        date=item[2]
        times=item[3]
        fever=math.log((1+date)/MAX_DATA)/math.log(1/60)*times
        lists.append({'userid':userid,'keyword':keyword,'fever':fever})
    df= pandas.DataFrame(lists)
    data=df.groupby(['userid', 'keyword']).sum()
    data2=data.groupby('userid')
    user=0
    for name,item in data2:
        user=user+1
        dic=item.to_dict()['fever']
        list=[]
        for i in dic:
            list.append((name,i[1],dic[i]))
        list.sort(key=lambda a:a[2],reverse=True)
        for i in list[0:20]:
            sql='insert into user_keyword(userid, keyword, fever) VALUES (%s,%s,%s)'
            cursor.execute(sql,i)
            mydb.commit()
    if action==1:
        sql='delete from user_click'
    else:
        sql='delete from user_click where times>10'

    cursor.execute(sql)
    mydb.commit()
    print(datetime.datetime.now(),' 更新用户模型成功，共更新%d个用户' % (user))

def update_user_keyword():
    while True:
        now = datetime.datetime.now()
        if (now.hour == 1 and now.minute <= USER_KEYWORD_INSTANT_INTERVAL):
            try:
                user_keyword_update(1)
            except Exception as e:
                print(e)
        else:
            try:
                user_keyword_update(0)
            except Exception as e:
                print(e)
            sleep(60 * USER_KEYWORD_INSTANT_INTERVAL)

user_keyword_update(1)