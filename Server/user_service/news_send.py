import math
import pandas
from settings import *
from datetime import datetime
mydb.ping(reconnect=True)
def get_news(item):
    try:
        mydb=db()
        cursor=mydb.cursor()
        if item['type']=='signedin':
            sql='select count(*) from article where webid=%s'
            cursor.execute(sql,(item['webid'],))
            maxpage=cursor.fetchall()[0][0]/(PAGE_NUM*5)
            if item['page']>maxpage:
                cursor.close()
                return {'code':code['max_page']}
            user=item['userid']
            sql='select keyword,fever from user_keyword where userid= %s;'
            cursor.execute(sql,(user,))
            results=cursor.fetchall()
            list=[]
            list2=[]
            if len(results)==0:
                cursor.close()
                return get_latest_news(item)
            sql='select articleid,date,fever from article where date< %s and webid=%s order by date desc limit %s , %s'
            cursor.execute(sql,(item['date'],item['webid'],(item['page']-1)*PAGE_NUM,PAGE_NUM*5))
            news_results=cursor.fetchall()
            a1=0
            avgfever=0
            for i in news_results:
                date=i[1]
                sql='select keyword from article_keyword where articleid=%s'
                cursor.execute(sql,(i[0],))
                keyword_results=cursor.fetchall()
                #calculate fever
                fever = 0
                for j in keyword_results:
                    for k in results:
                        if j[0]==k[0]:
                            fever=fever+k[1]
                            break
                list2.append((i[0],i[1],fever,i[2]))
                a1=a1+i[2]
                avgfever+=fever
            avgarticlefever= a1/len(news_results)
            avgfever=avgfever/len(news_results)
            for i in list2:
                date=i[1]
                fever=i[2]
                articelfever=i[3]
                timefever=math.log(((datetime.now()-date).total_seconds()+3600)/3600)/math.log(60)
                fever=fever*0.7+(avgfever)*0.2*timefever+(articelfever+1)/(1+avgarticlefever)*(avgfever)*0.1
                list.append({'id':i[0],'fever':fever})
            list.sort(key=lambda a:a['fever'],reverse=True)
            results=[]
            for i in list[0:PAGE_NUM]:
                sql='select articleid,url,imgurl,title,date,source,fever,webid from article where articleid=%s'
                cursor.execute(sql,(i['id'],))
                result=cursor.fetchall()[0]
                results.append({'articleid':result[0],
                                'url':result[1],
                                'imgurl':result[2],
                                'title':result[3],
                                'date': result[4].replace(tzinfo=UTC).astimezone(cst_tz).strftime("%Y-%m-%d %H:%M:%S"),
                                'source':result[5],
                                'webid': result[7],
                                'fever':result[6]}),

            cursor.close()
            return {'news':results,'code':code['send_news_success']}
        elif item['type']=='hot':
            return get_hotest_news(item)
        else:
            return get_latest_news(item)
    except Exception as e:
        print(e)
        return {'code':code['server_error']}

def get_hotest_news(item):
    try:
        mydb=db()
        cursor=mydb.cursor()
        sql = 'select count(*) from article'
        cursor.execute(sql)
        maxpage = int(cursor.fetchall()[0][0] / (PAGE_NUM*3))
        if int(item['page']) > maxpage:
            return {'code': code['max_page']}
        sql = 'select articleid,url,imgurl,title,date,source,fever,webid from article where date < %s and webid=%s order by fever,date desc limit %s , %s'
        cursor.execute(sql, (item['date'],item['webid'],(item['page'] - 1) * PAGE_NUM, PAGE_NUM*3))
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
        return {'code':code['send_news_success'],'news':results}
    except Exception as e:
        print(e)
        return {'code':code['server_error']}



def get_latest_news(item):
    try:
        mydb=db()
        cursor=mydb.cursor()
        results=[]
        sql = 'select count(*) from article'
        cursor.execute(sql)
        maxpage = cursor.fetchall()[0][0] / PAGE_NUM
        if item['page'] > maxpage:
            return {'code': code['max_page']}

        sql = 'select articleid,url,imgurl,title,date,source,fever,webid from article  where date < %s and webid=%s order by date desc limit %s , %s'
        cursor.execute(sql, (item['date'],item['webid'],(item['page'] - 1) * PAGE_NUM, PAGE_NUM))
        for result in cursor.fetchall():

            results.append({'articleid': result[0],
                            'url': result[1],
                            'imgurl': result[2],
                            'title': result[3],
                            'date': result[4].replace(tzinfo=UTC).astimezone(cst_tz).strftime("%Y-%m-%d %H:%M:%S"),
                            'source': result[5],
                            'webid': result[7],
                            'fever':result[6]})
        return {'code':code['send_news_success'],'news':results}
    except Exception as e:
        print(e)
        return {'code':code['server_error']}

# item={
#     'type':'hot',
#     'userid':2,
#     'page':2
# }
# for i in get_news(item)['news']:
#     print(i)