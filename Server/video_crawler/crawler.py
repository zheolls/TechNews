import datetime
import re
import time

import requests
from bs4 import BeautifulSoup

from news_crawler.crawler import get_driver
from settings import mydb

headers = {
    'host':'api.bilibili.com',
    'Accept-Encoding':'gzip, deflate',
    'Accept':'Accept: image/webp,image/apng,image/*,*/*;q=0.8',
    'Connection':'keep-alive',
    'DNT':'1',
    'Cookie':"LIVE_BUVID=AUTO9815337182827336; fts=1533718310; stardustvideo=1; sid=7elvkil6; CURRENT_QUALITY=80; im_notify_type_34972826=0; CURRENT_FNVAL=16; buvid3=73A5F3C0-26A8-4390-8CE5-0D98658674A3163687infoc; rpdid=|(J~JY|YuRlJ0J'ullY~k|k~J; UM_distinctid=16acf8f96f7220-02bf8ffa2c4905-353166-130980-16acf8f96f8165; DedeUserID=34972826; DedeUserID__ckMd5=d77ede0d1d7fe8f3; SESSDATA=0e2b09a2%2C1562419062%2C36323c61; bili_jct=ff007eaf4b5d2e393da64e641d63195c",
}

def get_head(id,count):
    try:
        driver = get_driver()
        url='https://www.bilibili.com/video/av'+str(id)+'/'
        driver.get(url)
        time.sleep(6)
        driver.implicitly_wait(30)
        js_down = "var q=document.documentElement.scrollTop=100000"
        js_up = "var q=document.documentElement.scrollTop=0"
        for i in range(3):
            driver.execute_script(js_down)
            time.sleep(2)
            driver.execute_script(js_up)
            time.sleep(1)
        soup=BeautifulSoup(driver.page_source,'lxml')
        head=soup.find('img',attrs={'class':'up-face'}).get('src')
        driver.close()
        return head
    except Exception as e:
        print(e)
        if count>3:
            return ''
        else:
            return get_head(id,count+1)

def get_data(soup,webid):
    datalist=[]
    # res=requests.get(headers['Referer'],headers=headers)
    # print(res.json())
    items = soup.find("ul", attrs={"class": "vd-list mod-2"})
    list = items.find_all('li')
    for i in list:
        try:
            vid=i.find("div",attrs={"class":"r"}).a
            title=vid.text
            vid=vid.get('href')
            vid=(re.findall(r'av[0-9]+',vid))[0][2:]
            source=i.find("div",attrs={"class":'up-info'}).a.get('title')
            watch=i.find("span",attrs={"class":'v-info-i'}).text
            if '万' in watch:
                watch=int(float(re.findall(r'[0-9.]',watch)[0])*10000)
            else:
                watch=int(watch)
            head='http:'+get_head(vid,0)
            datalist.append((vid,webid,source,datetime.datetime.utcnow(),watch,head,title))
        except Exception as e:
            print(e)
    return datalist
def video_crawler(url, webid):
    driver = get_driver()
    try:
        cursor=mydb.cursor()
        driver.get(url)
        time.sleep(6)
        driver.implicitly_wait(30)
        # driver.set_page_load_timeout(30)
        # driver.set_script_timeout(30)
        js_down = "var q=document.documentElement.scrollTop=100000"
        js_up = "var q=document.documentElement.scrollTop=0"
        driver.execute_script(js_down)
        time.sleep(2)
        driver.execute_script(js_up)
        time.sleep(1)

        soup = BeautifulSoup(driver.page_source, 'lxml')
        # cursor.execute('select articleid from article ORDER BY articleid DESC LIMIT 1')
        # result = cursor.fetchall()[0][0]

        error = 0
        resultss=get_data(soup,webid)
        for i in resultss:
            print(i)
        for i in resultss:
            try:
                sql = 'insert into video(url, webid, source, date, fever, authorimgurl, title) ' \
                      'VALUES (%s,%s,%s,%s,%s,%s,%s)'
                cursor.execute(sql,i)
                mydb.commit()
            except Exception as e:
                print(e)
                try:
                    sql='update video set fever=? where url= ?'
                    cursor.execute(sql,(i[4],i[0],))
                except Exception as e:
                    print(e)
                error=error+1
        print(datetime.datetime.now(),' 更新视频，成功:%d 个, 已存在：%d 个。' % (len(resultss)-error,error) )
    except Exception as e:
        print(str(e))
    driver.quit()

