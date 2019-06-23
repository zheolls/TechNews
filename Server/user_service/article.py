from settings import *
from datetime import *
mydb.ping(reconnect=True)

def click_article(item):
    try:
        mydb=db()
        cursor=mydb.cursor()
        sql='update article set fever=fever+1 where articleid= %s'
        cursor.execute(sql,(item['articleid'],))
        mydb.commit()
        mydb.close()
    except Exception as e:
        print(e)
def update_user_click(item):
    try:
        mydb=db()
        cursor=mydb.cursor()
        sql='select * from article_history where userid=%s and articleid=%s'
        cursor.execute(sql,(item['userid'],item['articleid'],))
        if len(cursor.fetchall())>0:
            sql='update article_history set date=%s where userid=%s and articleid=%s '
            cursor.execute(sql,(datetime.now().strftime("%Y-%m-%d")
                           ,item['userid'],item['articleid']))
        else:
            sql='insert into article_history(userid, articleid, date) VALUES (%s,%s,%s)'
            cursor.execute(sql,(item['userid'],item['articleid'],datetime.now().strftime("%Y-%m-%d")
))
            mydb.commit()

        sql='select * from user_click where userid=%s'
        cursor.execute(sql,(item['userid'],))
        if len(cursor.fetchall()):
            sql='update user_click set times=times+1 where userid= %s'
            cursor.execute(sql,(item['userid'],))
            mydb.commit()
        else:
            sql='insert into user_click(userid, times) VALUES (%s,1)'
            cursor.execute(sql,(item['userid'],))
            mydb.commit()
        mydb.close()
    except Exception as e:
        print(e)

def colloct_article(item):
    try:
        mydb=db()
        cursor=mydb.cursor()
        sql='select * from collect_article where userid=%s and articleid=%s'
        cursor.execute(sql,(item['userid'],item['articleid'],))
        if len(cursor.fetchall())>0:
            if item['check']==0:
                sql='update collect_article set date=%s where userid=%s and articleid=%s '
                cursor.execute(sql,
                               (datetime.now().strftime("%Y-%m-%d"),
                                item['userid'],item['articleid']))
                mydb.commit()
                return {'code': 0}

            elif item['check']==2:
                sql='delete from collect_article where userid=%s and articleid=%s'
                cursor.execute(sql,(item['userid'],item['articleid'],))
                mydb.commit()
                return {'code': 0}
            return {'code': code['has_collect']}
        else:
            if item['check']==1:
                sql='insert into collect_article(userid, articleid, date) VALUES (%s,%s,%s)'
                cursor.execute(sql,(item['userid'],item['articleid'],datetime.now().strftime("%Y-%m-%d")))
                mydb.commit()
                return {'code':code['collect_success']}
            else:
                return {'code':1}


    except Exception as e:
        print(e)
        return {'code':code['server_error']}

def get_collect_article(item):
    try:
        mydb=db()
        cursor=mydb.cursor()
        sql = 'select count(*) from article'
        cursor.execute(sql)
        maxpage = int(cursor.fetchall()[0][0] / (PAGE_NUM))
        if int(item['page']) > maxpage+1:
            return {'code': code['max_page']}
        sql = 'select article.articleid,url,imgurl,title,collect_article.date,source,fever,webid ' \
              'from article,collect_article,user where article.date < %s and ' \
              'article.articleid=collect_article.articleid ' \
              'and collect_article.userid=user.userid ' \
              'order by fever,date desc limit %s , %s'
        cursor.execute(sql, (item['date'],(item['page'] - 1) * PAGE_NUM, PAGE_NUM))
        results=[]
        for result in cursor.fetchall()[0:PAGE_NUM]:
            results.append({'articleid': result[0],
                            'url': result[1],
                            'imgurl': result[2],
                            'title': result[3],
                            'date': result[4].replace(tzinfo=UTC).astimezone(cst_tz).strftime("%Y-%m-%d %H:%M:%S"),
                            'source': result[5],
                            'webid': result[7],
                            'fever':result[6]})
        if len(results)==0:
            return {'code': code['max_page']}

        return {'code':code['send_news_success'],'news':results}
    except Exception as e:
        print(e)
        return {'code':code['server_error']}

def get_track(item):
    try:
        mydb=db()
        cursor=mydb.cursor()
        sql = 'select count(*) from article'
        cursor.execute(sql)
        maxpage = int(cursor.fetchall()[0][0] / (PAGE_NUM))
        if int(item['page']) > maxpage+1:
            return {'code': code['max_page']}
        sql = 'select article.articleid,url,imgurl,title,article_history.date,source,fever,webid ' \
              'from article,article_history,user where article.date < %s and ' \
              'article.articleid=article_history.articleid ' \
              'and article_history.userid=user.userid ' \
              'order by fever,date desc limit %s , %s'
        cursor.execute(sql, (item['date'],(item['page'] - 1) * PAGE_NUM, PAGE_NUM))
        results=[]


        for result in cursor.fetchall()[0:PAGE_NUM]:
            results.append({'articleid': result[0],
                            'url': result[1],
                            'imgurl': result[2],
                            'title': result[3],
                            'date': result[4].replace(tzinfo=UTC).astimezone(cst_tz).strftime("%Y-%m-%d %H:%M:%S"),
                            'source': result[5],
                            'webid': result[7],
                            'fever':result[6]})
        if len(results) == 0:
            return {'code': code['max_page']}
        return {'code':code['send_news_success'],'news':results}
    except Exception as e:
        print(e)
        return {'code':code['server_error']}

def search_article(item):
    try:
        mydb = db()
        cursor = mydb.cursor()
        sql = 'select count(*) from article'
        cursor.execute(sql)
        maxpage = int(cursor.fetchall()[0][0] / (PAGE_NUM))
        if int(item['page']) > maxpage+1:
            return {'code': code['max_page']}
        a=['%%%s%%' % item['key']]
        sql = 'select articleid,url,imgurl,title,date,source,fever,webid ' \
              'from article ' \
              "where date < %s and title like" + "%s " \
              'order by date desc limit %s , %s'
        cursor.execute(sql, (item['date'],a,(item['page'] - 1) * PAGE_NUM, PAGE_NUM))
        results = []
        for result in cursor.fetchall()[0:PAGE_NUM]:
            results.append({'articleid': result[0],
                            'url': result[1],
                            'imgurl': result[2],
                            'title': result[3],
                            'date': result[4].replace(tzinfo=UTC).astimezone(cst_tz).strftime("%Y-%m-%d %H:%M:%S"),
                            'source': result[5],
                            'webid': result[7],
                            'fever': result[6]})
        if len(results) == 0:
            return {'code': code['max_page']}

        return {'code': code['send_news_success'], 'news': results}
    except Exception as e:
        print(e)
        return {'code': code['server_error']}
