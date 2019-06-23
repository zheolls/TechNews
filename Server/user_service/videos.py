import json
import re

import requests

from settings import *
from datetime import datetime
from video_crawler.crawler import headers


def get_url(aid,quality):
        string='http://api.bilibili.com/playurl?aid='\
               +aid+\
               '&page=1&platform=html5&quality=' \
               +str(quality)+'&vtype=mp4&type=json'
        return string




def get_videos(item):
    try:
        mydb=db()
        cursor=mydb.cursor()
        sql='select count(*) from video'
        cursor.execute(sql)
        maxpage=cursor.fetchall()[0][0]/(PAGE_NUM)
        if item['page']>maxpage+1:
            cursor.close()
            return {'code':code['max_page']}
        sql='select videoid,url,title,date,source,fever,authorimgurl from video where date< %s order by date desc limit %s , %s'
        cursor.execute(sql,(item['date'],(item['page']-1)*PAGE_NUM,PAGE_NUM))
        news_results=cursor.fetchall()
        results=[]
        for result in news_results[0:PAGE_NUM]:
            try:
                url=get_url(str(result[1]),3)
                res = requests.get(url, headers=headers)
                result2 = dict(res.json())
                url=result2['durl'][0]['url']
                # url=re.findall(r'http.+mp4',url)[0]
                print(url)
                imgurl=result2['img']
                results.append({'videoid':result[0],
                                'url':url,
                                'aid':result[1],
                                'imgurl':imgurl,
                                'title':result[2],
                                'date': result[3].replace(tzinfo=UTC).astimezone(cst_tz).strftime("%Y-%m-%d %H:%M:%S"),
                                'source':result[4],
                                'fever': result[5],
                                'authorimgurl': result[6]})
            except Exception as e:
                print(result2)

        cursor.close()
        return {'videos':results,'code':code['send_news_success']}

    except Exception as e:
        print(e)
        return {'code':code['server_error'],'error':str(e)}

# url=get_url('55375310',3)
# url=dict(requests.get(url, headers=headers).json())['durl'][0]['url']
# print(url)
# print(re.findall(r'http.+mp4',url))